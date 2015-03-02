import java.io.Serializable;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.linalg.Vector;

import com.nag.exceptions.NAGBadIntegerException;
import com.nag.routines.Routine;
import com.nag.routines.G02.G02BW;
import com.nag.routines.G02.G02BU;
import com.nag.routines.G02.G02BZ;
import com.nag.routines.F01.F01ZA;

public class NAGCorrelation {

        private int _ifail;
        private int _chunkSize = 10000;
	protected int _numVars;
        protected int _dataSize;
        protected double [] _ssq;
        protected double [] _ssqUT;
        protected double [] _correlations;
        protected double [] _correlationsUT;
	protected double[] _means;

	public int getNumVars() {
		return _numVars;
	}

	public int getDataSize() {
		return _dataSize;
	}

	public double[] getMeans() {
		return _means;
	}

	public double[] getSSQ() {
		return _ssq;
	}

	public double[] getCorrelation() {
		return _correlations;
	}

	public double[] getCorrelationsUpperTri() {
		return _correlationsUT;
	}

	static class NAGData implements Serializable {
	    NAGData(int length, double[] means, double[] ssq) {
              this.length = length;
	      this.means = means;
	      this.ssq = ssq;
	    }
            int length;
	    double[] means;
	    double[] ssq;
	}

        static class combineNAGData implements Function2<NAGData,NAGData,NAGData> {
                @Override
                public NAGData call(NAGData data1,NAGData data2) throws Exception {
                        G02BZ g02bz = new G02BZ();
                        int IFAIL = 1;
                        g02bz.eval("M", data1.means.length, data1.length, data1.means,
                                        data1.ssq, data2.length, data2.means, data2.ssq, IFAIL);
                        data1.length = (int)(g02bz.getXSW()); 
                        if(g02bz.getIFAIL()>0) {
                                System.out.println("Error with g02bz!!!");
                                System.exit(1);
                        }                        
                        return data1;
                }
	}        
	 static class ComputeVectorSSQ implements FlatMapFunction<Iterator<Vector>, NAGData> {
	    @Override
	    public Iterable<NAGData> call(Iterator<Vector> iter) throws Exception {
              List<Vector> mypoints= new ArrayList<Vector>();
              while(iter.hasNext()) {
                        mypoints.add(iter.next());                      
               }
                int length = mypoints.size();
                int numvars = mypoints.get(0).size();
                double[] x = new double[length*numvars];
                for(int i=0;i<length;i++) {
                        for(int j=0;j<numvars;j++) {
                                x[(j) * length + i] = mypoints.get(i).apply(j);
                        }
                }
                
                G02BU g02bu = new G02BU();
                double[] means = new double [numvars];
                double[] ssq = new double [(numvars*numvars+numvars)/2];
                int ifail = 1;
                double sw = 0;
                g02bu.eval("M", "U", length, numvars, x, length, x, 0.0, 
                                                        means, ssq, ifail);
                if(g02bu.getIFAIL()>0) {
                        System.out.println("Error with g02bu!!!");
                        System.exit(1);
                }                        
	      return Arrays.asList(new NAGData(length,means,ssq));
	    }
        }

	 static class ComputeLabeledPointSSQ implements 
				FlatMapFunction<Iterator<LabeledPoint>, NAGData> {
	    @Override
	    public Iterable<NAGData> call(Iterator<LabeledPoint> iter) throws Exception {
              List<LabeledPoint> mypoints= new ArrayList<LabeledPoint>();
              while(iter.hasNext()) {
                        mypoints.add(iter.next());                      
               }
                int length = mypoints.size();
                int numvars = mypoints.get(0).features().size() + 1;
                double[] x = new double[length*numvars];
                for(int i=0;i<length;i++) {
                        for(int j=0;j<numvars-1;j++) {
                                x[(j) * length + i] = mypoints.get(i).features().apply(j);
                        }
                        x[(numvars-1) * length + i] = mypoints.get(i).label();
                }
                
                G02BU g02bu = new G02BU();
                double[] means = new double [numvars];
                double[] ssq = new double [(numvars*numvars+numvars)/2];
                int ifail = 1;
                double sw = 0;
                g02bu.eval("M", "U", length, numvars, x, length, x, 0.0, 
                                                        means, ssq, ifail);
                if(g02bu.getIFAIL()>0) {
                        System.out.println("Error with g02bu!!!");
                        System.exit(1);
                }                        
	      return Arrays.asList(new NAGData(length,means,ssq));
	    }
        }

        public void LabeledPointCorrelation(JavaRDD<LabeledPoint> datapoints)
                                        throws NAGBadIntegerException {

                Routine.init();                
                _numVars = datapoints.take(1).get(0).features().size() + 1;

                _dataSize = (int) datapoints.count();                
                int numPartitions = (int) (_dataSize / _chunkSize) + 1;

                double[] emptyMeans = new double[_numVars];
                double[] emptySSQ = new double[(_numVars*_numVars+_numVars)/2];
                NAGData NAGEmpty = new NAGData(0, emptyMeans, emptySSQ);

                NAGData finalpt = datapoints.repartition(numPartitions)
                                                .mapPartitions(new ComputeLabeledPointSSQ())
                                                .aggregate(NAGEmpty, new combineNAGData(),
                                                 new combineNAGData());
                
                _ssq = convertMatrix(finalpt.ssq);
		_means = finalpt.means;

                int ifail = 1;
                G02BW g02bw = new G02BW();
                g02bw.eval(_numVars, finalpt.ssq, ifail);
                if(g02bw.getIFAIL() > 0) {
                        System.out.println("Error with NAG (g02bw) IFAIL = " + g02bw.getIFAIL());       
                        System.exit(1);                
                }

                _ifail = g02bw.getIFAIL();
		_correlationsUT = finalpt.ssq;
		_correlations = convertMatrix(_correlationsUT);
       }

	public void VectorCorrelation(JavaRDD<Vector> datapoints)
                                        throws NAGBadIntegerException {
                
                Routine.init();                
                _numVars = datapoints.take(1).get(0).size();

                _dataSize = (int) datapoints.count();                
                int numPartitions = (int) (_dataSize / _chunkSize) + 1;

                double[] emptyMeans = new double[_numVars];
                double[] emptySSQ = new double[(_numVars*_numVars+_numVars)/2];
                NAGData NAGEmpty = new NAGData(0, emptyMeans, emptySSQ);

                NAGData finalpt = datapoints.repartition(numPartitions)
                                                .mapPartitions(new ComputeVectorSSQ())
                                                .aggregate(NAGEmpty, new combineNAGData(),
                                                 new combineNAGData());
                
                _ssq = convertMatrix(finalpt.ssq);
		_means = finalpt.means;

                int ifail = 1;
                G02BW g02bw = new G02BW();
                g02bw.eval(_numVars, finalpt.ssq, ifail);
                if(g02bw.getIFAIL() > 0) {
                        System.out.println("Error with NAG (g02bw) IFAIL = " + g02bw.getIFAIL());       
                        System.exit(1);                
                }

                _ifail = g02bw.getIFAIL();
		_correlationsUT = finalpt.ssq;
		_correlations = convertMatrix(_correlationsUT);
       }

        /* Method to convert packed 1-d array into a symmetric unpack 2-d array */
        protected double[] convertMatrix(double[] input) 
                                                throws NAGBadIntegerException {
		Routine.init();
                String JOB = "U";
                String UPLO = "U";
                String DIAG = "N";
                int N = _numVars;
                int LDA = N;
                double[] B = new double[N*N];
                int ifail = 1;
                F01ZA f01za = new F01ZA(JOB, UPLO, DIAG, N, B, LDA, input, ifail);
                f01za.eval();
                if(f01za.getIFAIL()>0) {
                        System.out.println("Error with f01za!!!");
                        System.exit(1);
                }                        
		int l=0;
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++) {
				B[l]=B[j*N+i];
				l++;
			}
		}
                return B;
        }  
}

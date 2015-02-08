import java.io.Serializable;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.linalg.Vector;

import com.nag.exceptions.NAGBadIntegerException;
import com.nag.routines.Routine;
import com.nag.routines.G02.G02AB;
import com.nag.routines.G02.G02BU;
import com.nag.routines.G02.G02BZ;
import com.nag.routines.G02.G02CG;
import com.nag.routines.G02.G02BW;
import com.nag.routines.F01.F01ZA;

public class NAGLinearRegression {

	private int _numvars;
        private int _dataSize;
        private int _chunkSize = 10000;
        private double[] _con ;
        private double[] _coef = null;
        private double _Rsquared ;
        private int _ifail;
        private double [] _correlations;
        private double _time;

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

	 static class ParseSet implements FlatMapFunction<Iterator<LabeledPoint>, NAGData> {
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

        public void train(JavaRDD<LabeledPoint> datapoints)
                                        throws NAGBadIntegerException {

                Routine.init();                
                _numvars = datapoints.take(1).get(0).features().size() + 1;               

                long startTime = System.currentTimeMillis();

                _dataSize = (int) datapoints.count();                
                int numPartitions = (int) (_dataSize / _chunkSize) + 1;

                double[] emptyMeans = new double[_numvars];
                double[] emptySSQ = new double[(_numvars*_numvars+_numvars)/2];
                NAGData NAGEmpty = new NAGData(0, emptyMeans, emptySSQ);

                NAGData finalpt = datapoints.repartition(numPartitions)
                                                .mapPartitions(new ParseSet())
                                                .aggregate(NAGEmpty, new combineNAGData(),
                                                 new combineNAGData());
                
                int ifail = 1;
                int K1 = _numvars;
                int K = K1 - 1;

                double[] result = new double[13];
                double[] coef = new double[K*3];
                double[] con = new double[3];
                double[] rinv = new double[K*K];
                double[] c = new double[K*K];
                double[] wkz = new double[K*K];
                double[] ssq;

                ssq = convertMatrix(finalpt.ssq, K1);

                G02BW g02bw = new G02BW();

                g02bw.eval(_numvars, finalpt.ssq, ifail);
                if(g02bw.getIFAIL() > 0) {
                        System.out.println("Error with NAG (g02bw) IFAIL = " + g02bw.getIFAIL());       
                        System.exit(1);                
                }

                double[] pearsonR = convertMatrix(finalpt.ssq, K1);
                _correlations = convertMatrix(finalpt.ssq, K1);
				
		double[] X = nearest_corr(pearsonR, K1);

                G02CG g02cg = new G02CG();                     
                g02cg.eval(finalpt.length, K1, K, finalpt.means, ssq, K1, X, 
                                K1, result, coef, K, con, rinv, K, c, K, wkz, K, ifail);

                long endTime = System.currentTimeMillis();
                _time = endTime - startTime;
                _con = con;
                _Rsquared = result[11];
                _coef = coef;
                _ifail = g02cg.getIFAIL();
        }

	public double[] nearest_corr(double[] input, int size) 
					throws NAGBadIntegerException {
		int LDG = size, N = size, MAXITS = -1, MAXIT = -1,
			ITER = 0, FEVAL = 0, IFAIL = 1, LDX = size;
		String OPT = "A";
		double ALPHA = .0001, ERRTOL = .0001, NRMGRD = 0.0;
		double[] W = new double[1];
		double[] X = new double[input.length];
		G02AB g02ab = new G02AB(input, LDG, N, OPT, ALPHA, W, ERRTOL, MAXITS,
					MAXIT, X, LDX, ITER, FEVAL, NRMGRD, IFAIL);

		g02ab.eval();
                if(g02ab.getIFAIL() > 0) {
                        System.out.println("Error with NAG (g02ab) IFAIL = " + 
							g02ab.getIFAIL());       
                        System.exit(1);                
                }
		return X;
	}
        
        public double predict(Vector a_vector) {
                if(_coef == null) {
                        System.out.println("Factors are null, run regression first.");
                        System.exit(1);
                }
                double[] data = a_vector.toArray();
                double temp = _con[0];
                for(int i=0;i<_numvars-1;i++)
                        temp+=data[i]*_coef[i];
                return temp;
        }

        /* Method to convert packed 1-d array into unpack 2-d array */
        private double[] convertMatrix(double[] input, int n) 
                                                throws NAGBadIntegerException {
                String JOB = "U";
                String UPLO = "U";
                String DIAG = "N";
                int N = n;
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
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++) {
				B[l]=B[j*n+i];
				l++;
			}
		}
                return B;
        }

        public void writeLogFile(String fileName, List<LabeledPoint> datapoints) 
                                                                throws Exception {
                File file;
                FileWriter fw;
                BufferedWriter bw;
        
                file = new File(fileName);
                if(!file.exists()){
                        file.createNewFile();    
                }

                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                bw.write("NAG Spark Multi-Linear Regression\n");
                bw.write("*************************************************\n");
		bw.write("Number Variables: " + _numvars + "\n");
                bw.write("Total number of points: " + _dataSize + "\n");
                bw.write("Var\t\t|Coef\t\tSE\t\tt-value\n");
                bw.write("------------------------------------------------------\n");
                bw.write(String.format("Intcp\t\t|%d\t\t%.1f\t\t%.1f", (int)_con[0], 
                                                        _con[1], _con[2]));
                bw.newLine();
                for(int i=0;i<_numvars-1;i++) 
                {
                        bw.write(String.format(i+"\t\t|%.1f\t\t%.1f\t\t%.1f", _coef[i],
                                                        _coef[i+3], _coef[i+6]));
                        bw.newLine();
                }
                bw.newLine();
                bw.write("R-Squared = " + _Rsquared + "\n");
                bw.write("NAG IFAIL = " + _ifail + "\n");
                bw.write("Total time (in milliseconds): " + _time + "\n");
                bw.write("*************************************************\n");
                bw.write("Correlations:");
                bw.newLine();
                for(int i=0;i<_numvars;i++){
                        for(int j=0;j<_numvars;j++) 
                                bw.write(String.format("%.3f\t", 
                                                _correlations[j+i*_numvars]));    
                        bw.newLine();
                }

                bw.write("*************************************************\n");
                LabeledPoint point;
                bw.write(String.format("Predicting %d points\n",datapoints.size()));
                for(int i=0;i<datapoints.size();i++) {
                        point = datapoints.get(i);
                        bw.write(String.format(
                        "Prediction: %.1f Actual: %.1f\n", predict(point.features()),
                                                                point.label()));                       
                }

                bw.close();        
        } 
}

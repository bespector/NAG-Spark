import java.io.Serializable;
import java.util.regex.Pattern;
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
import org.apache.spark.api.java.JavaDoubleRDD;

import com.nag.exceptions.NAGBadIntegerException;
import com.nag.routines.Routine;
import com.nag.routines.G01.G01AT;

public class NAGSimpleStatistics {

        private int _dataSize;
        private int _chunkSize = 1000;
        private double[] _CON = null;
        private double[] _coef = null;
        private double _Rsquared ;
        private int _ifail;

        public void NAGSimpleStats(JavaDoubleRDD doublerdd, int numPartitions)
        {
                Routine.init();
                int NB, IWT, PN, IFAIL;
                double XMEAN, XSD, XSKEW, XKURT, XMIN, XMAX;
                double[] RCOMM, X;

                doublerdd.repartition(numPartitions);
                for(int i=0;i<numPartitions;i++)
                {
                        
                }
        }

//        public NAGLinearRegression(JavaRDD<LabeledPoint> points) {
               
  //      }

/*
	 static class ParseSet implements FlatMapFunction<Iterator<LabeledPoint>, NAGData> {
	    @Override
	    public Iterable<NAGData> call(Iterator<LabeledPoint> iter) throws Exception {
              int i=0;
              List<LabeledPoint> mypoints= new ArrayList<LabeledPoint>();
              while(iter.hasNext()) {
                        mypoints.add(iter.next());                      
               }
                int length = mypoints.size();
                int numvars = mypoints.get(i).features().toArray().length + 1;
                double[] x = new double[length*numvars];
                double[] features;	      
                for(i=0;i<length;i++)
                {
                        features = mypoints.get(i).features().toArray();
                        for(int j=0;j<numvars-1;j++) {
                                x[(j) * length + i] = features[j];
                        }
                        x[(numvars-1) * length + i] = mypoints.get(i).label();
                }
                
                G02BU g02bu = new G02BU();
                double[] MEANS = new double [numvars];
                double[] SSQ = new double [(numvars*numvars+numvars)/2];
                int IFAIL = 1;
                double sw = 0;
                g02bu.eval("M", "U", length, numvars, x, length, x, 0.0, 
                                                        MEANS, SSQ, IFAIL);
                if(g02bu.getIFAIL()>0)
                {
                        System.out.println("Error with g02bu!!!");
                        System.exit(1);
                }                        
	      return Arrays.asList(new NAGData(length,MEANS,SSQ));
	    }
        }

        public void writeLogFile(String fileName) throws Exception{
                File file;
                FileWriter fw;
                BufferedWriter bw;
        
                file = new File(fileName);
                if(!file.exists()){
                        file.createNewFile();    
                }

                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                bw.write("NAG Spark Multi-Linear Regression");
                bw.newLine();
                bw.write("*************************************************");
                bw.newLine();
                bw.write("Total number of points: " + _dataSize);
                bw.newLine();
                bw.write("Var\t\t|Coef\t\tSE\t\tt-value");
                bw.newLine();
                bw.write("------------------------------------------------------");
                bw.newLine();
                bw.write(String.format("Intcp\t\t|%d\t\t%.1f\t\t%.1f", (int)_CON[0], 
                                                        _CON[1], _CON[2]));
                bw.newLine();
                
                for(int i=0;i<_numvars-1;i++) 
                {
                        bw.write(String.format(i+"\t\t|%.1f\t\t%.1f\t\t%.1f", _coef[i],
                                                        _coef[i+3], _coef[i+6]));
                        bw.newLine();
                }
                bw.newLine();
                bw.write("R-Squared = " + _Rsquared);
                bw.newLine();
                bw.write("NAG IFAIL = " + _ifail);
                bw.newLine();
                bw.write("*************************************************");
                bw.newLine();                
                bw.close();        
        }
 */
}

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
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.linalg.Vector;

import com.nag.exceptions.NAGBadIntegerException;
import com.nag.routines.Routine;
import com.nag.routines.G02.G02CG;

public class NAGLinearRegression extends NAGCorrelation {

	private int _ifail;
        private double[] _con;
        private double[] _coef = null;
        private double _Rsquared;

        public void train(JavaRDD<LabeledPoint> datapoints)
                                        throws NAGBadIntegerException {

		LabeledPointCorrelation(datapoints);                
                int ifail = 1;
                int K1 = _numVars;
                int K = K1 - 1;

                double[] result = new double[13];
                double[] coef = new double[K*3];
                double[] con = new double[3];
                double[] rinv = new double[K*K];
                double[] c = new double[K*K];
                double[] wkz = new double[K*K];

                G02CG g02cg = new G02CG();                     
                g02cg.eval(_dataSize, K1, K, _means, _ssq, K1, _correlations, 
                                K1, result, coef, K, con, rinv, K, c, K, wkz, K, ifail);


                _con = con;
                _Rsquared = result[11];
                _coef = coef;
                _ifail = g02cg.getIFAIL();
        }

/*	public double[] nearest_corr(double[] input, int size) 
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
  */      
        public double predict(Vector a_vector) {
                if(_coef == null) {
                        System.out.println("Factors are null, run regression first.");
                        System.exit(1);
                }
                double[] data = a_vector.toArray();
                double temp = _con[0];
                for(int i=0;i<_numVars-1;i++)
                        temp+=data[i]*_coef[i];
                return temp;
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
		bw.write("Number Variables: " + _numVars + "\n");
                bw.write("Total number of points: " + _dataSize + "\n");
                bw.write("Var\t\t|Coef\t\tSE\t\tt-value\n");
                bw.write("------------------------------------------------------\n");
                bw.write(String.format("Intcp\t\t|%d\t\t%.1f\t\t%.1f", (int)_con[0], 
                                                        _con[1], _con[2]));
                bw.newLine();
                for(int i=0;i<_numVars-1;i++) 
                {
                        bw.write(String.format(i+"\t\t|%.1f\t\t%.1f\t\t%.1f", _coef[i],
                                                        _coef[i+3], _coef[i+6]));
                        bw.newLine();
                }
                bw.newLine();
                bw.write("R-Squared = " + _Rsquared + "\n");
                bw.write("NAG IFAIL = " + _ifail + "\n");
                bw.write("*************************************************\n");
                bw.write("Correlations:");
                bw.newLine();
                for(int i=0;i<_numVars;i++){
                        for(int j=0;j<_numVars;j++) 
                                bw.write(String.format("%.3f\t", 
                                                _correlations[j+i*_numVars]));    
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

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
import com.nag.routines.F08.F08FA;

public class NAGpca extends NAGCorrelation {

	private int _ifail;
        private double[] _eigenvalues = null;
        private double[] _eigenvectors = null;
	
	public double[] getEIGENVALUES() {
		return _eigenvalues;
	}


        public void pcaLabeledPoint(JavaRDD<LabeledPoint> datapoints)
                                        throws NAGBadIntegerException {

		LabeledPointCorrelation(datapoints);                
                int info = -1;

		String JOBZ = "V";
		String UPLO = "U";
		int N = _numVars, LDA = N;
		_eigenvalues = new double[N];
		int LWORK = 3 * N - 1;		
		double[] WORK = new double[LWORK];
		_eigenvectors = new double[N*N];
		for(int i=0;i<N*N;i++)
			_eigenvectors[i]=_correlations[i];

		F08FA f08fa = new F08FA(JOBZ, UPLO, N, _eigenvectors, LDA, 
					_eigenvalues, WORK, LWORK, info);
		f08fa.eval();

		if(f08fa.getINFO() > 0) {
			System.out.println("Error with f08fa! IFAIL = " + f08fa.getINFO());
			System.exit(1);
		}
        }
        public void pcaVector(JavaRDD<Vector> datapoints)
                                        throws NAGBadIntegerException {

		VectorCorrelation(datapoints);                
                int info = -1;

		String JOBZ = "V";
		String UPLO = "U";
		int N = _numVars, LDA = N;
		_eigenvalues = new double[N];
		int LWORK = 3 * N - 1;		
		double[] WORK = new double[LWORK];
		_eigenvectors = new double[N*N];
		for(int i=0;i<N*N;i++)
			_eigenvectors[i]=_correlations[i];

		F08FA f08fa = new F08FA(JOBZ, UPLO, N, _eigenvectors, LDA, 
					_eigenvalues, WORK, LWORK, info);
		f08fa.eval();

		if(f08fa.getINFO() > 0) {
			System.out.println("Error with f08fa! IFAIL = " + f08fa.getINFO());
			System.exit(1);
		}
        }
}

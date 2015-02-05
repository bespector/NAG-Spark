import java.io.IOException;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.linalg.Vector;

import com.nag.routines.Routine;
import com.nag.routines.E04.E04KY;
import com.nag.routines.E04.E04KY.Abstract_E04KY_FUNCT2;

public class NAGLogisticRegression {

        private static JavaRDD<LabeledPoint> _points;
        private int _N;
        private static double _subsample = 1.0;
        private double[] _factors = null;
        private boolean _intercept = true;

        static class VectorSum implements Function2<double[], double[], double[]> {
                @Override

                public double[] call(double[] a, double[] b) {
                        for (int i = 0; i < a.length; i++) {
                                a[i] += b[i];
                        }
                        return a;
                }
        }

        static class ComputeGradient implements Function<LabeledPoint, double[]> {
                private final double[] gX;
                double xb = 0.0;
                double xby;

                ComputeGradient(double[] weights) {
                        gX = weights;
                }

                @Override
                public double[] call(LabeledPoint p) {
                        double[] gradient = new double[gX.length + 1];
                        double[] data = p.features().toArray();

                        xb=0.0;
                      	for (int i = 0; i < data.length; i++)
                       		xb += data[i]*gX[i];
		
        		xby = xb * p.label();
			gradient[0] = (xby - Math.log(1.0 + Math.exp(xb)));
	        	for (int i = 0; i < data.length; i++)
	        		gradient[i+1] = data[i] * (p.label() - 1.0 / 
                                                (1.0 + Math.exp(-1.0 * xb)));
                        return gradient;    
                }
        }

        public static class OBJFUN extends E04KY.Abstract_E04KY_FUNCT2 {

                public void eval() {
                        double[] gradient = _points.sample(false, _subsample)
                                                .map(new ComputeGradient(XC))
                                                .reduce(new VectorSum());  

                        this.setFC(-1.0 * gradient[0]);
                        for(int i=0;i<N;i++)
                                GC[i] = -1.0*gradient[i+1];                
        	}
        }
        
        public   NAGLogisticRegression(JavaRDD<LabeledPoint> points)  {

                Routine.init();
                _points = points;
                _N = _points.take(1).get(0).features().size();

        }
        
        public void train() throws Exception {
                int N = _N, IBOUND = 1, LIW = _N+2, IFAIL = 1,
                        LW = Math.max(10*_N + _N*(_N-1)/2,11);
                int[] IW, IUSER;
                double[] BL, BU, X, G, W, RUSER;
                double F = 0;

                IW = new int[LIW];
                IUSER = new int[1];
                W = new double[LW];
                BL = new double[N];
                BU = new double[N];
                X = new double[N];
                G = new double[N];
                RUSER = new double[1];
                for(int i=0;i<N;i++)
                        X[i] = 0.5;

                OBJFUN objfun = new OBJFUN();                         
                E04KY e04ky = new E04KY();
                e04ky.eval(N, IBOUND, objfun, BL, BU, X, F, G, IW, LIW, W, 
                                                LW, IUSER, RUSER, IFAIL);
                for(int i=0;i<N;i++)
                        System.out.println("X[" + i + "] = " + X[i]);
                System.out.println("IFAIL =  " + e04ky.getIFAIL());
                _factors = X;
        }

        public double predict(Vector a_vector) {
                if(_factors == null) {
                        System.out.println("Factors are null, run regression first.");
                        System.exit(1);                
                }
                double[] data = a_vector.toArray();
                double prob = _factors[0];
                for(int i=0;i<_factors.length-1;i++)
                        prob+=data[i]*_factors[i+1];
                prob = 1.0/(1.0+Math.exp(-1.0*prob));
                return prob;
        }

}

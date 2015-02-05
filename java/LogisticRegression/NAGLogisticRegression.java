import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

import com.nag.routines.Routine;
import com.nag.routines.E04.E04KY;
import com.nag.routines.E04.E04KY.Abstract_E04KY_FUNCT2;

public class NAGLogisticRegression {

        private static JavaRDD<LabeledPoint> _points;
        private int _N;

        static class VectorSum implements Function2<double[], double[], double[]> {
                @Override

                public double[] call(double[] a, double[] b) {
                        double[] c = new double[5];
                        for (int i = 0; i < a.length; i++) {
                                c[i] = a[i] + b[i];
                        }
                        return c;
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
                      	for (int i = 0; i < 4; i++)
                       		xb += data[i]*gX[i];
		
        		xby = xb * p.label();
			gradient[0] = (xby - Math.log(1.0 + Math.exp(xb)));
	        	for (int i = 0; i < 4; i++)
	        		gradient[i+1] = data[i] * (p.label() - 1.0 / 
                                                (1.0 + Math.exp(-1.0 * xb)));
                        return gradient;    
                }
        }

  public static class OBJFUN extends E04KY.Abstract_E04KY_FUNCT2 {

        public void eval() {
 
                double[] gradient = _points.map(new ComputeGradient(XC)).reduce(new VectorSum());  

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
                int N = _N, IBOUND = 1, LIW = _N+2, LW = Math.max(10*_N + _N*(_N-1)/2,11), IFAIL = 1;
                int[] IW, IUSER;
                double[] BL, BU, X, G, W, RUSER;
                double F=0;
                IW = new int[LIW];
                IUSER = new int[1];
                W = new double[LW];
                BL = new double[N];
                BU = new double[N];
                X = new double[N];
                G = new double[N];
                RUSER = new double[1];
                for(int i=0;i<N;i++)
                        X[i]=.5;

                OBJFUN objfun = new OBJFUN();                         
                E04KY e04ky = new E04KY();
                e04ky.eval(N, IBOUND, objfun, BL, BU, X, F, G, 
                                                IW, LIW, W, LW, IUSER, RUSER, IFAIL);
                for(int i=0;i<N;i++)
                        System.out.println("ANSWER     " + X[i]);
                System.out.println("IFAIL ===  " + e04ky.getIFAIL());

        }
}

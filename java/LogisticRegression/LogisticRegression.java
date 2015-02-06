import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

public class LogisticRegression {

	  static class ParsePoint implements Function<String, LabeledPoint> {
	    @Override
	    public LabeledPoint call(String line) {
              String[] parts = line.split(",");
//	      String[] tok = parts[1].split(" ");
	      double[] x = new double[parts.length];
              double label = Integer.parseInt(parts[0]);
              x[0] = 1.0;
	      for (int i = 1; i < parts.length; i++) {
	        x[i] = Double.parseDouble(parts[i]);

              }	      

	      return new LabeledPoint(label, Vectors.dense(x));
	    }
	  }

	public static void main(String[] args) {
                           
                SparkConf conf = new SparkConf()
                                        .setAppName("NAG Logistic Regression Example")
                                        .setMaster("local");
                JavaSparkContext ctx = new JavaSparkContext(conf);

         	JavaRDD<String> fileContent = ctx.textFile(args[0]);	
               	JavaRDD<LabeledPoint> points = fileContent.map(new ParsePoint()).cache();
         	NAGLogisticRegression lr = new NAGLogisticRegression(points);
                try {
	               lr.train();
                       lr.writeLogFile("LogisticResults.txt", points.take(25));
                } catch (Exception e) {
                       System.out.println("Error with analysis!!");
                       e.printStackTrace();
                }  
	}
}

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

public class LinearRegression {

	  static class ParsePoint implements Function<String, LabeledPoint> {
	    @Override
	    public LabeledPoint call(String line) {
              String[] parts = line.split(",");
	      String[] tok = parts[1].split(" ");
	      double[] x = new double[tok.length];
              double label = Double.parseDouble(parts[0]);
	      for (int i = 0; i < tok.length; i++) {
	        x[i] = Double.parseDouble(tok[i]);
              }	      
	      return new LabeledPoint(label, Vectors.dense(x));
	    }
	  }

	public static void main(String[] args) {
                           
               if(args[0].equals("generateData")) {       
                        try{
                                generateData data = new generateData(args);
                                data.generate();
                        } catch (Exception e) {
        			System.out.println("Error with NAG Library!");
        			e.printStackTrace();
                                System.exit(1);
        		}             	
                } else if (args[0].equals("runRegression")) {

                        SparkConf conf = new SparkConf()
                                        .setAppName("NAG Linear Regression Example")
                                        .setMaster("local");
                        JavaSparkContext ctx = new JavaSparkContext(conf);

                	JavaRDD<String> fileContent = ctx.textFile(args[1]);	
                       	JavaRDD<LabeledPoint> points = fileContent.map(new ParsePoint()).cache();
                	NAGLinearRegression lr = new NAGLinearRegression();
                        try {
			        lr.analize(points);
                                lr.writeLogFile("Results.txt");
                        } catch (Exception e) {
                                System.out.println("Error with analysis!!");
                                e.printStackTrace();
                        }
                        
		} else
		System.out.println("Usage: generateData/runRegression folder numRecords numFiles");

	}
}

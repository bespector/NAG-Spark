import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

public class LogisticRegression {

	  static class ParsePoint implements Function<String, LabeledPoint> {
	    @Override
	    public LabeledPoint call(String line) {
              String[] parts = line.split(",");
//	      String[] tok = parts[1].split(" ");
	      double[] x = new double[parts.length-1];
              double label = Integer.parseInt(parts[0]);
	      for (int i = 0; i < parts.length-1; i++) {
	        x[i] = Double.parseDouble(parts[i+1]);
              }	      
	      return new LabeledPoint(label, Vectors.dense(x));
	    }
	  }

	public static void main(String[] args) {
                           
               if(args[0].equals("generateData")) {       
                        try{
                                //generateData data = new generateData(args);
                                //data.generate();
                        } catch (Exception e) {
        			System.out.println("Error with NAG Library!");
        			e.printStackTrace();
                                System.exit(1);
        		}             	
                } else if (args[0].equals("runRegression")) {

                        SparkConf conf = new SparkConf()
                                        .setAppName("NAG Logistic Regression Example")
                                        .setMaster("local");
                        JavaSparkContext ctx = new JavaSparkContext(conf);

                	JavaRDD<String> fileContent = ctx.textFile(args[1]);	
                       	JavaRDD<LabeledPoint> points = fileContent.map(new ParsePoint()).cache();
                	NAGLogisticRegression lr = new NAGLogisticRegression(points);
                        try {
			        lr.train();
//                                lr.writeLogFile("Results.txt");
                        } catch (Exception e) {
                                System.out.println("Error with analysis!!");
                                e.printStackTrace();
                        }
                        
		} else
		System.out.println("Usage: generateData/runRegression folder numRecords numFiles");

	}
}

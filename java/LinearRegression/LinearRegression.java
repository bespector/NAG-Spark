import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;
import org.apache.spark.mllib.regression.LinearRegressionModel;

public class LinearRegression {

 	static	File file;
	static   FileWriter fw;
        static BufferedWriter bw;
       
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
                           
                SparkConf conf = new SparkConf()
                        .setAppName("NAG Linear Regression Example")
                        .setMaster("local");
                JavaSparkContext ctx = new JavaSparkContext(conf);

        	JavaRDD<String> fileContent = ctx.textFile(args[0]);	
               	JavaRDD<LabeledPoint> datapoints = fileContent.map(new ParsePoint()).cache();
        	NAGLinearRegression nag = new NAGLinearRegression();
                try {
                        nag.train(datapoints);
                } catch (Exception e) {
                      System.out.println("Error with analysis!!");
                      e.printStackTrace();
                }
		final LinearRegressionModel model = LinearRegressionWithSGD
						.train(JavaRDD.toRDD(datapoints),100);

		try {
	                file = new File("results.txt");
        	        if(!file.exists()){
        	                file.createNewFile();    
        	        }

        	        fw = new FileWriter(file.getAbsoluteFile());
        	        bw = new BufferedWriter(fw);

			List<LabeledPoint> test = datapoints.takeSample(false,10);

			bw.write("Spark/NAG Linear Regression Results\n");
			bw.write("Actual Value\t\tNAG Value\tMLLIB Value\n");
			for(int i=0;i<10;i++) 
				bw.write(String.format("%.3f\t\t\t%.3f\t\t%.3f\t\n",
						test.get(i).label(),
						nag.predict(test.get(i).features()),
						model.predict(test.get(i).features())>-100000 ? 
						model.predict(test.get(i).features()) : -100000));
			bw.close();
      	        } catch (Exception e) {
        	              System.out.println("Error with analysis!!");
        	              e.printStackTrace();
      	        }
	}
}

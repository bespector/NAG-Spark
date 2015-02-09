import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

public class Correlation {

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
                        .setAppName("NAG Correlation Example")
                        .setMaster("local");
                JavaSparkContext ctx = new JavaSparkContext(conf);

        	JavaRDD<String> fileContent = ctx.textFile(args[0]);	
               	JavaRDD<LabeledPoint> datapoints = fileContent.map(new ParsePoint()).cache();
		NAGCorrelation corr= new NAGCorrelation();

		double[] PearsonR = null;
                try {
	       	 	corr.LabeledPointCorrelation(datapoints);
			PearsonR = corr.getCorrelation();
                } catch (Exception e) {
                      System.out.println("Error with analysis!!");
                      e.printStackTrace();
                }
		int n = corr.getNumVars();
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++)
				System.out.print(PearsonR[j+i*n]+ " ");
			System.out.print("\n");		
		}
	}
}

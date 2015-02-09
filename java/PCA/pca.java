import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

public class pca {

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
                        .setAppName("NAG PCA Example")
                        .setMaster("local");
                JavaSparkContext ctx = new JavaSparkContext(conf);

        	JavaRDD<String> fileContent = ctx.textFile(args[0]);	
               	JavaRDD<LabeledPoint> datapoints = fileContent.map(new ParsePoint()).cache();

        	NAGpca pca = new NAGpca();
                try {
                        pca.pcaLabeledPoint(datapoints);
                } catch (Exception e) {
                      System.out.println("Error with analysis!!");
                      e.printStackTrace();
                }
		double[] ev = pca.getEIGENVALUES();

		System.out.println("************************");
		System.out.println("Eigevalue\tValue");

		for(int i=0;i<pca.getNumVars();i++)
			System.out.println(i+"\t\t"+String.format("%.3f",ev[i]));
	}
}

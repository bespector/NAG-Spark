import java.io.Serializable;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

public class SimpleStatistics {

	  static class ParsePoint implements DoubleFunction<String> {
	    final Pattern SPACE = Pattern.compile(" ");
	    final Pattern COMMA = Pattern.compile(",");
	    @Override
	    public double call(String line) {
              //String[] parts = line.split(",");
	      //String[] tok = parts[1].split(" ");
	      //double[] x = new double[tok.length-1];
              //double label = Double.parseDouble(parts[0]);
	      //for (int i = 0; i < tok.length-1; i++) {
	      //  x[i] = Double.parseDouble(tok[i]);
              //}
	      
	      return 1.0;
	    }
	  }

	public static void main(String[] args) {
                        SparkConf conf = new SparkConf().setAppName("NAG Simple Stats")
                                                        .setMaster("local");
                        JavaSparkContext ctx = new JavaSparkContext(conf);

                        List<Double> data = Arrays.asList(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0);
                	JavaDoubleRDD fileContent = ctx.parallelizeDoubles(data);
 

                       }


}

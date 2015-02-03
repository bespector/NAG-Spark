import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SimpleStatistics {

	public static void main(String[] args) {

                        SparkConf conf = new SparkConf().setAppName("NAG Simple Stats")
                                                        .setMaster("local");
                        JavaSparkContext ctx = new JavaSparkContext(conf);

                        List<Double> data = Arrays.asList(1.5, 2.0, 3.5, 4.0, 
                                                5.5, 6.0, 7.5, 8.0, 9.5, 10.0);
                                                

                        JavaDoubleRDD doubleRDD = ctx.parallelizeDoubles(data); 
                        NAGSimpleStatistics ss = new NAGSimpleStatistics();

                        try {
                                ss.NAGSimpleStatistics(doubleRDD, 1);
                        } catch (Exception ex) {
                                System.out.println("Something went wrong!!");
                        }
                System.out.println("mean = " + ss.getMEAN());
                System.out.println("sd = " + ss.getSD());
                System.out.println("skew = " + ss.getSKEW());
                System.out.println("kurt = " + ss.getKURT());
                System.out.println("min = " + ss.getMIN());
                System.out.println("max = " + ss.getMAX());


        }
}

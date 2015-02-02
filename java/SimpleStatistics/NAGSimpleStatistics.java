import java.io.Serializable;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.JavaDoubleRDD;

import com.nag.exceptions.NAGBadIntegerException;
import com.nag.routines.Routine;
import com.nag.routines.G01.G01AT;

public class NAGSimpleStatistics {

        private int _dataSize;
        private int _chunkSize = 1000;
        private double[] _CON = null;
        private double[] _coef = null;
        private double _Rsquared ;
        private int _ifail;

        public void NAGSimpleStats(JavaDoubleRDD doublerdd, int numPartitions) {
                Routine.init();
                int NB, IWT, PN, IFAIL;
                double XMEAN, XSD, XSKEW, XKURT, XMIN, XMAX;
                double[] RCOMM, X;

                doublerdd.repartition(numPartitions);
                for(int i=0;i<numPartitions;i++) {
                        
                }
        }

}

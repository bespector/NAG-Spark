import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;

import com.nag.exceptions.NAGBadIntegerException;
import com.nag.routines.Routine;
import com.nag.routines.G05.G05KF;
import com.nag.routines.G05.G05SK;
import com.nag.routines.G05.G05SA;

public class generateData {

        static String folder;
        static int numRecords;
        static int numFiles;
        static double[] factors = {1000, 300, 500};
        static double[] area = {0, 5000, 13000};
        int[] STATE;        
        int GENID,SUBID,LSEED,LSTATE;        
        int [] SEED;
        int IFAIL;
        G05SK g05sk;
        G05SA g05sa;
        
        public generateData(String[] args) throws Exception {
                folder = args[1];
                numRecords = Integer.parseInt(args[2]);
                numFiles = Integer.parseInt(args[3]);
                GENID = 6;
                SUBID = 0;
                SEED = new int[6];
                for(int i=0;i<6;i++)                
                        SEED[i] = i+1;
                LSEED = 6;
                STATE = new int[61];
                LSTATE = 61;
                Routine.init();
                IFAIL = 1;
                G05KF g05kf = new G05KF();
                g05kf.eval(GENID,SUBID,SEED,LSEED,STATE,LSTATE,IFAIL);
                if(g05kf.getIFAIL() > 0) {
                        System.out.println("Error with initializing RNG (g05kf) IFAIL = " + 
                                                g05kf.getIFAIL());       
                        System.exit(1);                
                }
                g05sk = new G05SK();
                g05sa = new G05SA();
        }

        public void generate() throws Exception {
                double XMU = 0;
                double VAR = 1.0;
                double[] X = new double[2];
                double[] RATING = new double[2];                
                
                int i=0;
                int j=0;
                
                String line = "";
                while(i<numFiles){

                        j=0;
                        File file = new File("data/data_" + Integer.toString(i) 
                                                                        + ".dat");
                        if(!file.exists()){
                            file.createNewFile();    
                        }
                        FileWriter fw = new FileWriter(file.getAbsoluteFile());
                        BufferedWriter bw = new BufferedWriter(fw);
                        IFAIL = 1;
                        while(j<numRecords) {
                                g05sk.eval(2,XMU,VAR,STATE,X,IFAIL);
                                g05sa.eval(2,STATE,RATING,IFAIL);                                
                                try{         
                                      line = genID(j+i*numRecords, Math.max(X[0]*10+40,18), 
                                                5*X[1]+10, 10*RATING[0], 3*RATING[1]);
                                } catch (Exception e) 
                                {       
                                        e.printStackTrace();
                                        System.exit(1);
                                }
                                bw.write(line);
                                bw.newLine();    
                                j++;
                        }
                        bw.close();
                        i++;
                }
        }

        public String genID(int id, double age, double exp, double rating, 
                                                double industry) throws Exception {
                double base = 15000;
                exp = age - exp > 22 ? exp : age - 22;
                exp = age < 22 ? 0 : exp;
                exp = exp > 0 ? exp : 0.0;

        	double[] RANDOM = new double[1];
                g05sk.eval(1, 0, 15000, STATE, RANDOM, IFAIL);

                double salery = base + age * factors[0] + exp * factors[1] + 
                                rating * factors[2] + area[(int) industry] + RANDOM[0];

                g05sa.eval(1, STATE, RANDOM, IFAIL);
                salery = age > 18 ? salery : 0;
                salery = age < 65 ? salery : salery * 2 / 5;
                
                if(RANDOM[0]<.01)
                        salery += 50000;

                return String.format("%.1f,%.1f %.1f %.2f %d", salery, age, exp, 
                                                                rating, (int)industry);        
        }
}	

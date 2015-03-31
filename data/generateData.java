import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;

import com.nag.exceptions.NAGBadIntegerException;
import com.nag.routines.Routine;
import com.nag.routines.G05.G05KF;
import com.nag.routines.G05.G05SK;
import com.nag.routines.G05.G05SA;
import com.nag.routines.G05.G05RZ;

public class generateData {

    static String folder;
    static int numRecords;
    static int numFiles;
    int[] STATE;
    int IFAIL;
    FileWriter fw;
    BufferedWriter bw;


    public static void main(String[] args) {
        try {
            generateData generator = new generateData(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("something went wrong");
        }
    }

    public generateData(String[] args) throws Exception {

        folder = args[1];
        numRecords = Integer.parseInt(args[2]);
        numFiles = Integer.parseInt(args[3]);

        initializeNAG();

        if (args[0].equals("linearRegression") ) {
            generateLinear();
        } else if (args[0].equals("correlation") ) {
            generateCorrelation();
        } else if (args[0].equals("logisticRegression") ) {
            generateLogistic();
        } else {
            System.out.println("Data type must be linearRegression, correlation, or logisticRegression");
        }
    }

    private void initializeNAG() throws Exception {

        Routine.init();
        int GENID = 6, SUBID = 0, LSEED = 6, LSTATE = 61;
        int [] SEED = new int[6];

        for(int i=0; i<6; i++)
            SEED[i] = i+1;

        STATE = new int[LSTATE];
        IFAIL = 1;
        G05KF g05kf = new G05KF();
        g05kf.eval(GENID,SUBID,SEED,LSEED,STATE,LSTATE,IFAIL);
	
        if(g05kf.getIFAIL() > 0) {
            System.out.println("Error with initializing RNG (g05kf) IFAIL = " +
                               g05kf.getIFAIL());
            System.exit(1);
        }
	
    }

    private void openFile(int fileNum) throws Exception {
        File file = new File(folder + "/data" + fileNum + ".dat"); //Add folder as location
        if(!file.exists()) {
            file.createNewFile();
        }
        fw = new FileWriter(file.getAbsoluteFile());
        bw = new BufferedWriter(fw);
    }

    private void writeLine(String line) throws Exception {
        bw.write(line);
        bw.newLine();
    }

    private void closeFile() throws Exception {
        bw.close();
    }

    public void generateLinear() throws Exception {

        double XMU = 0;
        double VAR = 1.0;
        double[] normalRandomNumbers = new double[3];
        double[] uniformRandomNumbers = new double[3];

        G05SK g05sk = new G05SK();
        G05SA g05sa = new G05SA();

        String line = "";
        for(int i=0; i<numFiles; i++) {
            openFile(i);
            IFAIL = 1;
            for(int j=0; j<numRecords; j++) {
                g05sk.eval(3,XMU,VAR,STATE,normalRandomNumbers,IFAIL);
                g05sa.eval(3,STATE,uniformRandomNumbers,IFAIL);
                try {
                    line = genIDLin(normalRandomNumbers,uniformRandomNumbers);
                } catch (Exception e) {
                    System.out.println("something went wrong with genid");
                    e.printStackTrace();
                    System.exit(1);
                }
                writeLine(line);
            }
            closeFile();
        }
    }

    public String genIDLin(double[] normal, double []uniform) throws Exception {

        double age,exp,rating,industry, salary;
        double base = 15000;
        double[] factors = {1000, 300, 500, 0, 6000, 16000};

        age = Math.max(normal[0]*10+40,18);
        exp = 5 * normal[1]+10;
        rating = 10 * uniform[0];
        industry = 3 * uniform[1];

        exp = age - exp > 22 ? exp : age - 22;
        exp = age < 22 ? 0 : exp;
        exp = exp > 0 ? exp : 0.0;

        salary = base + age * factors[0] + exp * factors[1] +
                 rating * factors[2] + factors[3 + (int)industry]  +
                 normal[2] * 120;

        salary = age > 18 ? salary : 0;
        salary = age < 65 ? salary : salary * 2 / 5;

        if(uniform[2]<.01)
            salary += 50000;

        return String.format("%.1f,%.1f %.1f %.2f %d", salary, age, exp,
                             rating, (int)industry);
    }

    public void generateCorrelation() throws Exception {
        G05RZ g05rz = new G05RZ();
        int mode = 2, n = 1, ldx = n, m = 4, ldc = 4, lr = 21;
        double[] xmu = {1,2,-3,0};
        double[] c = {1.69, 0.39, -1.86, 0.07,0.39, 98.01, -7.07, -0.71,
                      -1.86, -7.07, 11.56, 0.03, 0.07, -0.71, 0.03, 0.01
                     } ;
        double[] r = new double[lr];
        double[] x = new double[ldx*m];

        for(int i=0; i<numFiles; i++) {
            openFile(i);
            for(int j=0; j<numRecords; j++) {
                IFAIL = 1;
                try {
                    g05rz.eval(mode, n, m, xmu, c, ldc, r, lr, STATE, x,ldx, IFAIL);
			
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("something went wrong with g05rz");
                    System.exit(1);
                }
                writeLine(String.format("%.5f,%.5f,%.5f,%.5f", x[0],x[1],x[2],x[3]));
            }
            closeFile();
        }
    }
    public void generateLogistic() throws Exception {
        G05SK g05sk = new G05SK();
        G05SA g05sa = new G05SA();
        double[] beta = {.1, .2, .4, .8};
        double[] x=new double[3];
        for(int i=0; i<numFiles; i++) {
            openFile(i);
            for(int j=0; j<numRecords; j++) {
                IFAIL=1;
                try {
                    g05sk.eval(3,0,2, STATE, x, IFAIL) ;
//			System.out.println("x[0]="+x[0]+"x[1]="+x[1]+"x[2]="+x[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("something went wrong with g05sk");
                    System.exit(1);
                }
                double linpred = beta[0] + x[0]*beta[1] + x[1]*beta[2] + x[2]*beta[3];
                double prob = 1.0/(1.0+Math.exp(-1.0*linpred));

                double[] urndm=new double[1];
                IFAIL=1;
                try {
                    g05sa.eval(1, STATE, urndm, IFAIL);
//			System.out.println("urndm="+urndm[0]);
                } catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("something went wrong with g05sa");
                    System.exit(1);
                }

                double ytest = 0.0;

                if (urndm[0] < prob) {
                    ytest = 1.0;
                }
                writeLine(String.format("%.5f,%.5f,%.5f,%.5f", ytest,x[0],x[1],x[2]));

            }
            closeFile();
        }
    }
}

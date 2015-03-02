JFLAGS = -g
JC = javac
JR = java

NAGDIR = /opt/NAG/fll6i24dcl
NAGJAVA = /home/brian/java/NAGJava
SPARKDIR = /home/brian/Downloads/spark-1.2.1

SPARKMASTER = "local"

SPARKCP = $(SPARKDIR)/assembly/target/scala-2.10/spark-assembly-1.2.1-hadoop1.0.4.jar

CLASSPATH = .:$(NAGJAVA)/jar/NAGJava.jar:$(SPARKCP):../Correlation

LD_LIBRARY_PATH=$(NAGDIR)/rtl/:$(NAGDIR)/lib/:$(NAGJAVA)/linux_x64/
export LD_LIBRARY_PATH

LinearRegression.class: LinearRegression.java NAGLinearRegression.java
	$(JC) $(JFLAGS) -cp $(CLASSPATH) NAGLinearRegression.java LinearRegression.java "../Correlation/NAGCorrelation.java"
        
run_nag_data:
	$(JR) -Dspark.master=$(SPARKMASTER) -cp $(CLASSPATH) LinearRegression "../../data/linearRegressionData" 

run_spark_data:
	$(JR) -Dspark.master=$(SPARKMASTER) -cp $(CLASSPATH) LinearRegression $(SPARKDIR)/data/mllib/ridge-data

clean:
	$(RM) *.class

JFLAGS = -g
JC = javac
JR = java

NAGDIR = /opt/NAG/fll6i24dcl
NAGJAVA = /home/brian/java/NAGJava
SPARKDIR = /home/brian/Downloads/spark-1.1.1

SPARKCP = $(SPARKDIR)/assembly/target/scala-2.10/spark-assembly-1.1.1-hadoop1.0.4.jar

CLASSPATH = .:$(NAGJAVA)/jar/NAGJava.jar:$(SPARKCP)

LD_LIBRARY_PATH=$(NAGDIR)/rtl/:$(NAGDIR)/lib/:$(NAGJAVA)/linux_x64/
export LD_LIBRARY_PATH

LinearRegression.class: LinearRegression.java NAGLinearRegression.java generateData.java
	$(JC) $(JFLAGS) -cp $(CLASSPATH) generateData.java NAGLinearRegression.java LinearRegression.java
        
# Generate 3 files, 1000 records each in the 'data' folder
generateData:
	$(JR) -cp $(CLASSPATH) LinearRegression generateData data 1000 3

# Run the regression from files in 'data' folder
run:
	$(JR) -cp $(CLASSPATH) LinearRegression runRegression "data"

clean:
	$(RM) *.class

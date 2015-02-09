JFLAGS = -g
JC = javac
JR = java

NAGDIR = /opt/NAG/fll6i24dcl
NAGJAVA = /home/brian/java/NAGJava
SPARKDIR = /home/brian/Downloads/spark-1.1.1

SPARKCP = $(SPARKDIR)/assembly/target/scala-2.10/spark-assembly-1.1.1-hadoop1.0.4.jar

CLASSPATH = .:$(NAGJAVA)/jar/NAGJava.jar:$(SPARKCP):../Correlation

LD_LIBRARY_PATH=$(NAGDIR)/rtl/:$(NAGDIR)/lib/:$(NAGJAVA)/linux_x64/
export LD_LIBRARY_PATH

pca.class: pca.java NAGpca.java
	$(JC) $(JFLAGS) -cp $(CLASSPATH) NAGpca.java pca.java "../Correlation/NAGCorrelation.java"
        
run:
	$(JR) -cp $(CLASSPATH) pca "../../data/linearRegressionData"

clean:
	$(RM) *.class

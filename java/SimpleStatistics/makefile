JFLAGS = -g
JC = javac
JR = java

NAGDIR = /opt/NAG/fll6i24dcl
NAGJAVA = /home/brian/java/NAGJava
SPARKDIR = /home/brian/Downloads/spark-1.1.1

SPARKMASTER = "local"

SPARKCP = $(SPARKDIR)/assembly/target/scala-2.10/spark-assembly-1.1.1-hadoop1.0.4.jar

CLASSPATH = .:$(NAGJAVA)/jar/NAGJava.jar:$(SPARKCP)

LD_LIBRARY_PATH=$(NAGDIR)/rtl/:$(NAGDIR)/lib/:$(NAGJAVA)/linux_x64/
export LD_LIBRARY_PATH

SimpleStatistics.class: SimpleStatistics.java NAGSimpleStatistics.java
	$(JC) $(JFLAGS) -cp $(CLASSPATH) NAGSimpleStatistics.java SimpleStatistics.java
       
run:
	$(JR) -Dspark.master=$(SPARKMASTER) -cp $(CLASSPATH) SimpleStatistics

clean:
	$(RM) *.class

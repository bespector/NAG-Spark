JFLAGS = -g
JC = javac
JR = java

NAGDIR = /opt/NAG/fll6i24dcl

NAGJAVA = /home/julie/Documents/NAG/Library/NAGJava

CLASSPATH = .:$(NAGJAVA)/jar/NAGJava.jar

LD_LIBRARY_PATH=$(NAGDIR)/rtl/:$(NAGDIR)/lib/:$(NAGJAVA)/linux_x64/
export LD_LIBRARY_PATH

generateData.class: generateData.java
	$(JC) $(JFLAGS) -cp $(CLASSPATH) generateData.java
       
run_correlation: generateData.class
	$(JR) -cp $(CLASSPATH) generateData correlation "data" 100 3

run_linear: generateData.class
	$(JR) -cp $(CLASSPATH) generateData linearRegression "data" 25 7

run_logistic: generateData.class
	$(JR) -cp $(CLASSPATH) generateData logisticRegression "data" 25 3

clean:
	$(RM) *.class

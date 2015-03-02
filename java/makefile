
SPARKMASTER = "local[4]"

NAG_JAVA = /home/brian/java/NAGJava/jar/NAGJava.jar
SPARK_DIR = /home/brian/Downloads/spark-1.2.1

build:
	mvn clean package

Correlation: build
	exec $(SPARK_DIR)/bin/spark-submit --jars $(NAG_JAVA) --class "Correlation" --master $(SPARKMASTER) target/simple-project-1.0.jar

SimpleStatistics: build
	exec $(SPARK_DIR)/bin/spark-submit --jars $(NAG_JAVA) --class "SimpleStatistics" --master $(SPARKMASTER) target/simple-project-1.0.jar

LinearRegression: build
	exec $(SPARK_DIR)/bin/spark-submit --jars $(NAG_JAVA) --class "LinearRegression" --master $(SPARKMASTER) target/simple-project-1.0.jar

LogisticRegression: build
	exec $(SPARK_DIR)/bin/spark-submit --jars $(NAG_JAVA) --class "LogisticRegression" --master $(SPARKMASTER) target/simple-project-1.0.jar

PCA: build
	exec $(SPARK_DIR)/bin/spark-submit --jars $(NAG_JAVA) --class "PCA" --master $(SPARKMASTER) target/simple-project-1.0.jar

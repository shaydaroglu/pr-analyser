#!makefile

# Set Java 21 for Maven
export JAVA_HOME := $(shell /usr/libexec/java_home -v 21)

JAR_NAME=target/pr-analyser-1.0.jar

build:
	mvn clean package

run: build
	java -jar $(JAR_NAME) $(ARGS)

clean:
	mvn clean

exec:
	java -jar $(JAR_NAME) $(ARGS)


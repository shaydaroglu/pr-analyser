# PR Analyser

## Introduction

### Description

PR Analyser is responsible for analyzing pull requests, comments, and reviews to generate user statistics.

### Prerequisites

Ensure the following stack is installed:

* Java 21
* Maven 3.9.9

## Get started

### Initial setup & Run (Manual Execution)

* Git clone this repo and cd into it
* Run `mvn clean install` to install the dependencies
* Run `mvn package` to build the application
* Run `java -jar target/pr-analyser-1.0.jar -h` to get the help message
* Run `java -jar target/pr-analyser-1.0.jar -r pr-analyser -u username -sd 2025-03-18 -ed 2025-03-18 -t Github_Access_Token`

In case mvn uses different java version, you can specify the java version by setting JAVA_HOME environment variable.


```shell
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH
```
### Running the application


For first time usage run the following command to get the help message:
  ```bash
  make run
  ```
or alternatively 
```bash
make build
make exec
```
It will set JAVA_HOME to the correct version and run the application.

Alternatively, you can run the following commands:
  ```bash
  make build
  make exec ARGS="-r pr-analyser -u username -sd 2025-03-18 -ed 2025-03-18 -t Github_Access_Token"
  ```

Example parameters to run the application:
  ```bash
  make exec ARGS="--repository-name pr-analyser --user username --start-date 2025-03-16 --end-date 2025-03-18 --access-token Github_Access_Token"
  ```
  ```bash
  make exec ARGS="-r pr-analyser -u username -sd 2025-03-18 -ed 2025-03-18 -t Github_Access_Token"
  ```

siths-server
============
SITHS is a Small Issue Tracking and Handling system

HowTo
============
1. Istall JDK 1.7 (Java 7 is required since SITHS is using Neo4j 2.0.1)
2. Install Apache Maven (Maven 3 is preferrable)
3. Compile

    mvn clean package

4. Run

    java -jar target/siths-0.1.0.jar --server.port=8080
    


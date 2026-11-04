mkdir ./target 
mvn clean
mvn package
if (( $? == 0 )) then;
    java -jar 'target/OceanNeedle-java-1.0-SNAPSHOT.jar'
fi
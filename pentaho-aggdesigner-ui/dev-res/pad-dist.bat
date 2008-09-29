REM Creates Pentaho Aggregation Designer distribution
REM Add -Dmaven.test.skip=true to skip unit tests
cd ..
mvn clean package javadoc:javadoc assembly:assembly -Dmaven.test.skip=true
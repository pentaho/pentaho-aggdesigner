# $Id: aggdes.sh 1004 2008-09-28 05:01:55Z aphillips $
# aggdes.sh - Script containing a sample invocation of the Aggregate Designer
# command line utility. Feel free to modify with your parameters.

# Directory where mondrian is installed. Customize with the location of your
# mondrian installation. If you are not using MondrianSchemaLoader, you may
# not need mondrian or many of its dependent libraries.
mondrianDir=$(cd $(dirname $0)/../../mondrian; pwd)
thisDir=$(cd $(dirname $0); pwd)
cp="${thisDir}/target/classes"
cp="${cp};${mondrianDir}/classes"
cp="${cp};${mondrianDir}/lib/eigenbase-xom.jar"
cp="${cp};${mondrianDir}/lib/eigenbase-properties.jar"
cp="${cp};${mondrianDir}/lib/eigenbase-resgen.jar"
cp="${cp};${mondrianDir}/lib/log4j.jar"
cp="${cp};${mondrianDir}/lib/commons-dbcp.jar"
cp="${cp};${mondrianDir}/lib/commons-pool.jar"
cp="${cp};${mondrianDir}/lib/commons-collections.jar"
cp="${cp};${mondrianDir}/lib/commons-vfs.jar"
cp="${cp};${mondrianDir}/lib/commons-logging.jar"
cp="${cp};${mondrianDir}/lib/commons-math.jar"
cp="${cp};${mondrianDir}/lib/javacup.jar"
java -cp "$cp" \
 org.pentaho.aggdes.Main \
 --loaderClass org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader \
 --loaderParam connectString 'Provider=mondrian;Jdbc=jdbc:odbc:MondrianFoodMart;Catalog=/open/mondrian/demo/FoodMart.xml;JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver' \
 --loaderParam cube Sales \
 --algorithmClass org.pentaho.aggdes.algorithm.impl.AdaptiveMonteCarloAlgorithm \
 --algorithmParam aggregateLimit 10 \
 --resultClass org.pentaho.aggdes.output.impl.ResultHandlerImpl \
 --resultParam tables true \
 --resultParam indexes false \
 --resultParam populate true

# Or try 
# --resultClass org.pentaho.aggdes.output.impl.TeradataResultHandler
# --algorithmParam timeLimitSeconds 3 \

# End aggdes.sh

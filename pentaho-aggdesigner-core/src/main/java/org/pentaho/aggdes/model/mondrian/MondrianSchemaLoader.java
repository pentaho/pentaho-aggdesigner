/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU General Public License, version 2 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
*
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.model.mondrian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mondrian.olap.DriverManager;
import mondrian.olap.Util;
import mondrian.olap.Util.PropertyList;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapStar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.algorithm.impl.AlgorithmImpl;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils.ValidationException;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.SchemaLoader;
import org.pentaho.aggdes.model.ValidationMessage;
import org.pentaho.aggdes.model.mondrian.validate.MondrianSchemaValidator;
import org.pentaho.aggdes.model.mondrian.validate.ValidationHelper;

/**
 * Loads the schema needed to drive the aggregate designer algorithm
 * from a mondrian schema file.
 *
 * <p>The implementation loads a star cube from a mondrian cube.
 */
public class MondrianSchemaLoader implements SchemaLoader {

    private static final Log logger = LogFactory.getLog(MondrianSchemaLoader.class);
 
    public Schema createSchema(Map<Parameter, Object> parameterValues) {
        String connectString =
            (String) parameterValues.get(
                MondrianSchemaLoaderParameter.connectString);
        String cubeName =
            (String) parameterValues.get(
                MondrianSchemaLoaderParameter.cube);
        final RolapConnection connection =
            (RolapConnection)DriverManager.getConnection(connectString, null);
        final mondrian.olap.Schema schema = connection.getSchema();
        
        final RolapCube cube = (RolapCube) schema.lookupCube(cubeName, true);
        return new MondrianSchema(connection, cube);
    }

    public List<ValidationMessage> validateSchema(Map<Parameter, Object> parameterValues) {
      String connectString =
        (String) parameterValues.get(
            MondrianSchemaLoaderParameter.connectString);
      String cubeName =
        (String) parameterValues.get(
            MondrianSchemaLoaderParameter.cube);
      
      PropertyList propertyList = Util.parseConnectString(connectString);
      
      String jdbcDrivers = propertyList.get("JdbcDrivers");
      if (StringUtils.isBlank(jdbcDrivers)) {
        throw new RuntimeException("missing 'JdbcDrivers' in connect string");
      }      
      
      String jdbc = propertyList.get("Jdbc");
      if (StringUtils.isBlank(jdbcDrivers)) {
        throw new RuntimeException("missing 'Jdbc' in connect string");
      }
      
      String catalog = propertyList.get("Catalog");
      if (StringUtils.isBlank(jdbcDrivers)) {
        throw new RuntimeException("missing 'Catalog' in connect string");
      }

      String jdbcUser = propertyList.get("JdbcUser");
      
      String jdbcPassword = propertyList.get("JdbcPassword");
      
      List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
      
      try {
        List<MondrianSchemaValidator> validators = loadValidators(parameterValues);
        
        Class.forName(jdbcDrivers); //$NON-NLS-1$
  
        java.sql.Connection conn = java.sql.DriverManager.getConnection(jdbc,  //$NON-NLS-1$
            jdbcUser, jdbcPassword); //$NON-NLS-1$ //$NON-NLS-2$
        
        messages = ValidationHelper.validateCube(catalog, cubeName, conn, validators); //$NON-NLS-1$
        
        conn.close();
      } catch (Exception e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e);
        }
        ValidationMessage msg = new ValidationMessage(ValidationMessage.Type.ERROR, e.getClass().getName() + ": " + e.getLocalizedMessage());
        messages.add(msg);
      }
      return messages;
    }
    
    protected List<MondrianSchemaValidator> loadValidators(Map<Parameter, Object> parameterValues) throws 
      ClassNotFoundException, InstantiationException, IllegalAccessException {
      String validatorClassString = (String) parameterValues.get(
          MondrianSchemaLoaderParameter.validators);
      
      if (null == validatorClassString || "".equals(validatorClassString)) { //$NON-NLS-1$
        return Collections.emptyList();
      }
      
      String[] validatorClassNames = validatorClassString.split(","); //$NON-NLS-1$
      
      List<MondrianSchemaValidator> validators = new ArrayList<MondrianSchemaValidator>();
      
      for (String className : validatorClassNames) {
        Class<?> clazz = Class.forName(className);
        if (!MondrianSchemaValidator.class.isAssignableFrom(clazz)) {
            throw new ValidationException(
                null,
                "Class '" + className
                    + "' does not implement required interface 'MondrianSchemaValidator'");
        }
        validators.add(MondrianSchemaValidator.class.cast(clazz.newInstance()));
      }
      return validators;
    }

    public List<Parameter> getParameters() {
        return Arrays.asList(
            (Parameter []) MondrianSchemaLoaderParameter.values());
    }

    public String getName() {
        return AlgorithmImpl.getBaseName(getClass());
    }

    /**
     * Enumeration of parameters accepted by MondrianSchemaLoader.
     */
    public enum MondrianSchemaLoaderParameter implements Parameter {
        connectString("Mondrian connect string", true, Type.STRING),
        cube("Name of cube", true, Type.STRING),
        validators("Comma-separated list of validators", false, Type.STRING);
        
        private final String description;
        private final boolean required;
        private final Type type;

        MondrianSchemaLoaderParameter(
            String description, boolean required, Type type)
        {
            this.description = description;
            this.required = required;
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public Type getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name();
        }
    }

    /**
     * Estimates the number of rows in an aggregate table, given the
     * cardinalities of its columns (assumed independent) and the number of
     * rows in the fact table.
     *
     * @param cardinalities Array of column cardinalities
     * @param factCount Number of rows in fact table
     * @return Estimated number of rows in aggregate table
     */
    public static double estimateAggregateCount(
        double[] cardinalities, double factCount)
    {
        // A table with columns K2, K5, K10, K100
        // has 2 * 5 * 10 * 100 = 10,000 possible rows.
        // If there are 1,000 rows in the fact table, how many rows do we
        // expect to find in the aggregate table A(K2, K5, K10)?

        // Probability that a given row, say A(K2=1, K5=1, K10=1)
        // does not occur in the fact table =
        //   Probability that a given row is equal to that row ^
        //   Number of rows in fact table.

        double comboCount = 1.0;
        for (double cardinality : cardinalities) {
            comboCount *= cardinality;
        }
        return estimateAggregateCount(comboCount, factCount);
    }

    /**
     * Estimates the number of rows in an aggregate table, given the
     * product of the cardinalities of its columns (assumed independent) and the
     * number of rows in the fact table.
     *
     * @param comboCount Product of column cardinalities
     * @param factCount Number of rows in fact table
     * @return Estimated number of rows in aggregate table
     */
    public static double estimateAggregateCount(
        double comboCount, double factCount) {
        if (comboCount > factCount * 10.0) {
            // If there's a huge number of combinations, '1.0 - rowProb'
            // tends to underflow to 1.0, and we get the wrong answer. So, just
            // return factCount.
            return factCount;
        }

        // Probability that a given row in the fact table is a given
        // combination of attribute values.
        double rowProb = 1.0 / comboCount;

        // Probability that a given combination of attribute values does not
        // exist in the fact table.
        double aggProb = Math.pow(1.0 - rowProb, factCount);

        // Estimated number of rows in the agg table =
        //   Number of combinations *
        //   (1 - Probability that combination does not occur)
        return comboCount - (comboCount * aggProb);
    }

    static double estimateSpaceForColumn(final RolapStar.Column column) {
        switch (column.getDatatype()) {
        case Boolean:
            return 1;
        case Date:
            return 4;
        case Integer:
            return 4;
        case Numeric:
            return 4;
        case String:
            return 20;
        case Time:
            return 4;
        case Timestamp:
            return 8;
        default:
            throw Util.unexpected(column.getDatatype());
        }
    }
}

// End MondrianSchemaLoader.java

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.algorithm.util;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.algorithm.Progress;
import org.pentaho.aggdes.model.Component;
import org.pentaho.aggdes.model.Parameter;

/**
 * Utility methods for retrieving and validating the algorithm's command-line arguments.
 * 
 * TODO mlowery Remove these method definitions from Main.
 * 
 * @author mlowery
 */
public class ArgumentUtils {

  public static Map<Parameter, Object> validateParameters(Component component, Map<String, String> rawParams) {
    final List<Parameter> parameterList = component.getParameters();
    final Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    final Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
    for (Parameter parameter : parameterList) {
      parameters.put(parameter.getName(), parameter);
    }

    // Populate parameter list, and validate types.
    for (Map.Entry<String, String> entry : rawParams.entrySet()) {
      final String name = entry.getKey();
      final Parameter parameter = parameters.get(name);
      if (parameter == null) {
        throw new ValidationException(component, "Unknown parameter '" + name + "' for component "
            + component.getName());
      }
      final String value = entry.getValue();
      switch (parameter.getType()) {
        case STRING:
          parameterValues.put(parameter, value);
          break;
        case INTEGER:
          try {
            int intValue = Integer.parseInt(value);
            parameterValues.put(parameter, intValue);
          } catch (NumberFormatException e) {
            throw new ValidationException(component, "Cannot convert parameter '" + name + "' to integer");
          }
          break;
        case DOUBLE:
          try {
            double doubleValue = Double.parseDouble(value);
            parameterValues.put(parameter, doubleValue);
          } catch (NumberFormatException e) {
            throw new ValidationException(component, "Cannot convert parameter '" + name + "' to double");
          }
          break;
        case BOOLEAN:
          boolean booleanValue = Boolean.parseBoolean(value);
          parameterValues.put(parameter, booleanValue);
          break;
        default:
          throw new IllegalArgumentException(parameter.getType() + "");
      }
    }

    // Check that mandatory parameters are specified.
    for (Parameter parameter : parameterList) {
      if (parameter.isRequired() && parameterValues.get(parameter) == null) {
        throw new ValidationException(component, "Missing value for required parameter '" + parameter.getName()
            + "' of component " + component.getName());
      }
    }
    return parameterValues;
  }

  /**
   * Implementation of {@link Progress} that prints to a print writer.
   */
  public static class TextProgress implements Progress {
      private final PrintWriter pw;

      public TextProgress(PrintWriter pw) {
          this.pw = pw;
      }

      public void report(
          String message, double complete)
      {
          int percent = (int) (complete * 100.0);
          pw.println(percent + "% complete"
              + (message == null ? "" : ("; " + message)));
      }
  }

  /**
   * Thrown on error parsing or validating command-line arguments.
   *
   * <p>Optional parameter <code>component</code> specifies which component
   * was being validated and allows us to give detailed information
   * about the parameters expected by that component.</p>
   */
  public static class ValidationException extends RuntimeException {
    
      private static final long serialVersionUID = 5181014746393411408L;
    
      private final Component component;

      public ValidationException(Component component, String message) {
          super(message);
          this.component = component;
      }
      
      public Component getComponent() {
        return component;
      }
  }
}

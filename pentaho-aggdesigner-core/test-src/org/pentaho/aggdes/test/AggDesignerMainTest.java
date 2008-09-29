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
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
package org.pentaho.aggdes.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.Main;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils.ValidationException;
import org.pentaho.aggdes.model.Component;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.Parameter.Type;
import org.pentaho.aggdes.output.ResultHandler;

import junit.framework.TestCase;

/**
 * Test the Main command line version
 * of the Agg Designer.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class AggDesignerMainTest extends TestCase {

  public static class ResultHandlerStub implements ResultHandler {

    public void handle(Map<Parameter, Object> parameterValues, Schema schema, Result result) {
      System.out.println("ResultHandlerStub handle called");
      
    }

    public String getName() {
      // TODO Auto-generated method stub
      return null;
    }

    public List<Parameter> getParameters() {
      return Collections.EMPTY_LIST;
    }
    
  }
  
  public void testUsage() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream pw = new PrintStream(baos);
    PrintStream orig = System.out;
    System.setOut(pw);
    Main.main(new String[]{});
    
    String results = baos.toString();
    assertTrue(results.indexOf("Usage: java") >= 0);
    System.setOut(orig);
  }
  
  public void testInvalidParam() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream pw = new PrintStream(baos);
    PrintStream orig = System.out;
    System.setOut(pw);
    Main.main(new String[]{
        "--loaderClass", 
        "org.pentaho.aggdes.test.algorithm.impl.SchemaLoaderStub",
        "--loaderParam",
        "cube", "Sales",
        "--algorithmClass", 
        "org.pentaho.aggdes.test.algorithm.impl.AlgorithmStub",
        "--algorithmParam",
        "notValidParam1", "not_yet_validated",
        "--resultClass",
        "org.pentaho.aggdes.output.impl.ResultHandlerImpl",
        "--resultParam",
        "notValidParam2", "not_yet_validated",

    });
    
    String results = baos.toString();
    assertTrue(results.indexOf("Unknown parameter 'notValidParam1'") >= 0);
    assertTrue(results.indexOf("execTime (INTEGER) Description") >= 0);
    System.setOut(orig);
  }
  
  public void testMissingLoaderComponent() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream pw = new PrintStream(baos);
    PrintStream orig = System.out;
    System.setOut(pw);
    Main.main(new String[]{
        "--algorithmClass", 
        "org.pentaho.aggdes.test.algorithm.impl.AlgorithmStub",
        "--algorithmParam",
        "notValidParam1", "not_yet_validated",
        "--resultClass",
        "org.pentaho.aggdes.output.impl.ResultHandlerImpl",
        "--resultParam",
        "notValidParam2", "not_yet_validated",

    });
    
    String results = baos.toString();
    assertTrue(results.indexOf("Missing required component. Please specify '--loaderClass' argument") >= 0);
    System.setOut(orig);
  }
  
  public void testMissingAlgorithmComponent() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream pw = new PrintStream(baos);
    PrintStream orig = System.out;
    System.setOut(pw);
    Main.main(new String[]{
        "--loaderClass", 
        "org.pentaho.aggdes.test.algorithm.impl.SchemaLoaderStub",
        "--loaderParam",
        "cube", "Sales",
        "--resultClass",
        "org.pentaho.aggdes.output.impl.ResultHandlerImpl",
        "--resultParam",
        "notValidParam2", "not_yet_validated",
    });
    
    String results = baos.toString();
    assertTrue(results.indexOf("Missing required component. Please specify '--algorithmClass' argument") >= 0);
    System.setOut(orig);
  }
  
  public void testMissingResultComponent() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream pw = new PrintStream(baos);
    PrintStream orig = System.out;
    System.setOut(pw);
    Main.main(new String[]{
        "--loaderClass", 
        "org.pentaho.aggdes.test.algorithm.impl.SchemaLoaderStub",
        "--loaderParam",
        "cube", "Sales",
        "--algorithmClass", 
        "org.pentaho.aggdes.test.algorithm.impl.AlgorithmStub",
    });
    
    String results = baos.toString();
    assertTrue(results.indexOf("Missing required component. Please specify '--resultClass' argument") >= 0);
    System.setOut(orig);
  }
  
  public void testRunAlgo() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream pw = new PrintStream(baos);
    PrintStream orig = System.out;
    System.setOut(pw);
    Main.main(new String[]{
        "--loaderClass", 
        "org.pentaho.aggdes.test.algorithm.impl.SchemaLoaderStub",
        "--loaderParam",
        "cube", "Sales",
        "--algorithmClass", 
        "org.pentaho.aggdes.test.algorithm.impl.AlgorithmStub",
        "--resultClass",
        "org.pentaho.aggdes.test.AggDesignerMainTest$ResultHandlerStub",
    });
    
    String results = baos.toString();
    assertTrue(results.indexOf("ResultHandlerStub handle called") >= 0);
    System.setOut(orig);
  }
  
  static class ComponentStub implements Component {
    List<Parameter> parameters;
    public ComponentStub() {
      Parameter string = new Parameter() {
        public String getDescription() {
          return "Description";
        }
        public String getName() {
          return "string";
        }
        public Type getType() {
          return Type.STRING;
        }
        public boolean isRequired() {
          return false;
        }
      };
      Parameter integer = new Parameter() {
        public String getDescription() {
          return "Description";
        }
        public String getName() {
          return "integer";
        }
        public Type getType() {
          return Type.INTEGER;
        }
        public boolean isRequired() {
          return false;
        }
      };
      Parameter doub = new Parameter() {
        public String getDescription() {
          return "Description";
        }
        public String getName() {
          return "double";
        }
        public Type getType() {
          return Type.DOUBLE;
        }
        public boolean isRequired() {
          return false;
        }
      };
      Parameter bool = new Parameter() {
        public String getDescription() {
          return "Description";
        }
        public String getName() {
          return "boolean";
        }
        public Type getType() {
          return Type.BOOLEAN;
        }
        public boolean isRequired() {
          return false;
        }
      };
      List<Parameter> list = new ArrayList<Parameter>();
      list.add(string);
      list.add(integer);
      list.add(doub);
      list.add(bool);
      parameters = list;
    }
    
    public String getName() {
      return "ComponentStub";
    }
    
    public List<Parameter> getParameters() {
      return parameters;
    }
    
  }
  
  public void testArgumentUtils() {
    ComponentStub component = new ComponentStub();
    Map<String, String> rawParams = new HashMap<String, String>();
    
    // test no params 
    
    Map<Parameter, Object> params = ArgumentUtils.validateParameters(component, rawParams);
    assertEquals(params.size(), 0);
    
    // test param types
    
    // integer
    
    rawParams.clear();
    rawParams.put("integer", "2");
    
    params = ArgumentUtils.validateParameters(component, rawParams);
    
    assertEquals(params.size(), 1);
    assertEquals(params.get(component.getParameters().get(1)), 2);
    
    try {
      rawParams.clear();
      rawParams.put("integer", "x");
      ArgumentUtils.validateParameters(component, rawParams);
      fail();
    } catch (ValidationException e) {
      assertEquals(e.getMessage(), "Cannot convert parameter 'integer' to integer");
    }
    
    // double
    rawParams.clear();
    rawParams.put("double", "2.1");
    
    params = ArgumentUtils.validateParameters(component, rawParams);
    
    assertEquals(params.size(), 1);
    assertEquals(params.get(component.getParameters().get(2)), 2.1);
    
    try {
      rawParams.clear();
      rawParams.put("double", "x");
      ArgumentUtils.validateParameters(component, rawParams);
      fail();
    } catch (ValidationException e) {
      assertEquals(e.getMessage(), "Cannot convert parameter 'double' to double");
    }
    
    // boolean
    rawParams.clear();
    rawParams.put("boolean", "true");
    
    params = ArgumentUtils.validateParameters(component, rawParams);
    
    assertEquals(params.size(), 1);
    assertEquals(params.get(component.getParameters().get(3)), true);
    
    rawParams.clear();
    rawParams.put("boolean", "x");
    
    params = ArgumentUtils.validateParameters(component, rawParams);
    
    assertEquals(params.size(), 1);
    assertEquals(params.get(component.getParameters().get(3)), false);
    
    // test required params
    
    Parameter required =new Parameter() {
      public String getDescription() {
        return "Description";
      }
      public String getName() {
        return "required";
      }
      public Type getType() {
        return Type.BOOLEAN;
      }
      public boolean isRequired() {
        return true;
      }
    };
    
    component.getParameters().add(required);
    
    rawParams.clear();
    rawParams.put("required", "true");
    
    params = ArgumentUtils.validateParameters(component, rawParams);
    
    assertEquals(params.size(), 1);
    assertEquals(params.get(required), Boolean.TRUE);

    try {
      rawParams.clear();
      ArgumentUtils.validateParameters(component, rawParams);
      fail();
    } catch (ValidationException e) {
      assertEquals(e.getMessage(), "Missing value for required parameter 'required' of component ComponentStub");
    }
    
    
  }
}

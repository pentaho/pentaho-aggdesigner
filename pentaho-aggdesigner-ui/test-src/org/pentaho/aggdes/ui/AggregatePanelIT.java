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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.ui;

import static org.pentaho.aggdes.test.util.TestUtils.getTestProperty;
import static org.pentaho.aggdes.test.util.TestUtils.registerDriver;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulRunner;
import org.pentaho.ui.xul.swing.SwingXulLoader;
import org.pentaho.ui.xul.swing.SwingXulRunner;

public class AggregatePanelIT extends TestCase {
    protected String connectString;

    public void setUp() throws Exception {
      // load resources/test.properties
      System.out.println("SETUP");
      connectString = getTestProperty("test.mondrian.foodmart.connectString", //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.provider"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.jdbc"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.username"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.password"), //$NON-NLS-1$
              getTestProperty("test.mondrian.foodmart.connectString.catalog")); //$NON-NLS-1$

      System.out.println(connectString);
      registerDriver(getTestProperty("test.jdbc.driver.classpath"), getTestProperty("test.jdbc.driver.classname")); //$NON-NLS-1$//$NON-NLS-2$
    }

    public static void main(String args[]) throws Exception {
        AggregatePanelIT test = new AggregatePanelIT();
        test.setUp();
        test.testAggPanel();
    }

    @Ignore
    public void testAggPanel() {
        MondrianSchemaLoader loader = new MondrianSchemaLoader();
        Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
        System.out.println("CONN STR: " + connectString);
        parameterValues.put(loader.getParameters().get(0), connectString);
        parameterValues.put(loader.getParameters().get(1), "Sales");
        Schema schema = loader.createSchema(parameterValues);
        
        try{
            
            UIAggregateImpl impl = new UIAggregateImpl();
            impl.setName("my name");
            impl.setDescription("my description");
            
            
           XulDomContainer container = new SwingXulLoader().loadXul(
                "org/pentaho/aggdes/ui/customDesignerPanel.xul"
           );
           XulRunner runner = new SwingXulRunner();

        runner.addContainer(container);
        container.getEventHandler("eventHandler").setData(impl);
        container.getEventHandler("eventHandler").setData(schema);
        
        runner.initialize();
        runner.start();
        
       } catch(Exception e){
         System.out.println(e.getMessage());
         e.printStackTrace(System.out);
       }
    }
    
}

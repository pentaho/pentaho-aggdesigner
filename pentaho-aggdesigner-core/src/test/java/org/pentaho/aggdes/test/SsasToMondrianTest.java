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

package org.pentaho.aggdes.test;

import static org.pentaho.aggdes.test.util.TestUtils.deregisterDriver;
import static org.pentaho.aggdes.test.util.TestUtils.getTestProperty;
import static org.pentaho.aggdes.test.util.TestUtils.registerDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import junit.framework.TestCase;
import mondrian.olap.DriverManager;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.aggdes.model.ssas.ConversionUtil;

public class SsasToMondrianTest extends TestCase {

  @BeforeClass
  public static void setUpDriver() throws Exception {
    registerDriver(getTestProperty("test.jdbc.driver.classpath"), getTestProperty("test.jdbc.driver.classname")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @AfterClass
  public static void tearDownDriver() throws Exception {
    deregisterDriver(getTestProperty("test.jdbc.driver.classpath"), getTestProperty("test.jdbc.driver.classname")); //$NON-NLS-1$ //$NON-NLS-2$
  }

//  @Test
//  public void testAtAGlance() throws Exception {
//    // read in foodmart
//    Document doc = ConversionUtil.parseAssl(getClass().getResourceAsStream("/analysis_server_output.xml")); //$NON-NLS-1$
//    ConversionUtil.atAGlance(doc);
//
//    // read in adventureworks
//    doc = ConversionUtil.parseAssl(getClass().getResourceAsStream("/ssas-dump-via-mgmt-studio-click.xml")); //$NON-NLS-1$
//    ConversionUtil.atAGlance(doc);
//
//  }

  @Test
  public void testSchemaConversion() throws Exception {

    List<Document> docs = ConversionUtil.generateMondrianDocsFromSSASSchema(getClass().getResourceAsStream(
        "/combined_ssas.xml")); //$NON-NLS-1$

    PrintWriter pw = new PrintWriter(new FileWriter("output_foodmart.xml")); //$NON-NLS-1$
    pw.println(docs.get(0).asXML());
    pw.close();

    pw = new PrintWriter(new FileWriter("output_adventureworks.xml")); //$NON-NLS-1$
    pw.println(docs.get(1).asXML());
    pw.close();
  }

  @Test
  public void testSSASMgmtXmlaExportConversion() throws Exception {

    List<Document> docs = ConversionUtil.generateMondrianDocsFromSSASSchema(getClass().getResourceAsStream(
        "/ssas-dump-via-mgmt-studio-xmla.xml")); //$NON-NLS-1$

    PrintWriter pw = new PrintWriter(new FileWriter("output_foodmart_export.xml")); //$NON-NLS-1$
    pw.println(docs.get(0).asXML());
    pw.close();
  }

  
  @Test
  public void loadConvertedSchemaInMondrian() throws Exception {
    // temp file to hold conversion output
    File tmpFile = File.createTempFile("output_foodmart_", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$

    // do conversion on SSAS dump of foodmart
    List<Document> docs = ConversionUtil.generateMondrianDocsFromSSASSchema(getClass().getResourceAsStream(
        "/analysis_server_output.xml")); //$NON-NLS-1$

    // save conversion to file
    FileUtils.writeStringToFile(tmpFile, docs.get(0).asXML());

    // load file into Mondrian
    String connectString = getTestProperty("test.mondrian.foodmart.connectString", //$NON-NLS-1$
        getTestProperty("test.mondrian.foodmart.connectString.provider"), //$NON-NLS-1$
        getTestProperty("test.mondrian.foodmart.connectString.jdbc"), //$NON-NLS-1$
        getTestProperty("test.mondrian.foodmart.connectString.username"), //$NON-NLS-1$
        getTestProperty("test.mondrian.foodmart.connectString.password"), //$NON-NLS-1$
        tmpFile.getPath());

    DriverManager.getConnection(connectString, null).getSchema();
  }
  
  @Test
  public void checkConvertedSchema() throws Exception {
    // do conversion on SSAS dump of foodmart
    List<Document> docs = ConversionUtil.generateMondrianDocsFromSSASSchema(getClass().getResourceAsStream(
        "/ssas-dump-via-mgmt-studio-click.xml")); //$NON-NLS-1$

    Document schemaDoc = docs.get(0);
    
    System.out.println(docs.get(0).asXML());
    
    assertNotNull(schemaDoc.selectSingleNode("/Schema"));
    assertTrue(schemaDoc.selectNodes("/Schema/Cube").size() > 0);
    assertNotNull(schemaDoc.selectSingleNode("/Schema/Cube[@name='Internet Sales']"));
  }

}

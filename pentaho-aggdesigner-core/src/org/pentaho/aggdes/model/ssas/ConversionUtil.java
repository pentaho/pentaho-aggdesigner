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
package org.pentaho.aggdes.model.ssas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pentaho.aggdes.AggDesignerException;

/**
 * Converts SQL Server Analysis Services (SSAS) dump to model expected by Pentaho Aggregation Designer.
 * 
 * Note: ASSL is Analysis Services Scripting Language which makes up the bulk of a SSAS dump.
 * 
 * TODO: Support Composite Keys
 * TODO: Support Views
 *       - A special field in the DataSourceView "QueryDefinition" may or may
 *         not be relevant to this option
 *         http://technet.microsoft.com/en-us/library/ms174812.aspx
 * TODO: Investigate Attribute Datatype value
 * TODO: Support All Level enabled feature
 * TODO: Support Shared Dimensions?
 * TODO: Investigate Cube Internet Sales, Dimension Internet Sales Order Details, for degenerate status
 * TODO: Investigate unique names between attributes and hierarchies
 * 
 * @author mlowery
 */
public class ConversionUtil {

  private static final Log logger = LogFactory.getLog(ConversionUtil.class);

  /**
   * This object is used to represent the ROLAP Star Schema Tables within
   * Analysis Services
   */
  private static class Table {
      String logicalName;
      String dbName;
      String primaryKey;
      boolean rootTable = false;
      List<Join> childJoins = new ArrayList<Join>();
      Join parentJoin;
      List<String> columns = new ArrayList<String>();
      
      public String toString() {
          return "[Table: logical="+ logicalName + "; dbName=" + dbName + "; primaryKey=" + primaryKey + "]";
      }
      
      public Table getJoinToFactTable(String factTable) {
          Table t = this;
          while (true) {
              if (t.parentJoin == null) {
                  return null;
              }
              if (t.parentJoin.parentTable.logicalName.equals(factTable)) {
                  return t;
              }
              t = t.parentJoin.parentTable;
          }
      }
      
      public boolean ancestorsHaveColumn(String column) {
        if (columns.contains(column)) {
            return true;
        } else {
            if (parentJoin != null) {
                return parentJoin.parentTable.ancestorsHaveColumn(column);
            }
        }
        return false;
      }
      
      public Table getRoot() {
          Table t = this;
          while (t.parentJoin != null) {
              t = t.parentJoin.parentTable;
          }
          return t;
      }
      public Table clone() {
          Table table = new Table(logicalName, dbName, primaryKey);
          table.columns.addAll(this.columns);
          return table;
      }
      public Table cloneTree() {
          Table clonetable = clone();
          for (Join join : childJoins) {
              Table childTable = join.childTable.cloneTree();
              Join clonejoin = new Join(join.parentTable, join.parentKey, childTable, join.childKey);
              clonetable.childJoins.add(clonejoin);
          }
          return clonetable;
      }
      
      public Table(String logicalName, String dbName, String primaryKey) {
          this.logicalName = logicalName;
          this.dbName = dbName;
          this.primaryKey = primaryKey;
      }
      
      public List<Table> findTable(String name) {
          // traverse all child joins, return all contexts
          List<Join> joins = new ArrayList<Join>(childJoins);
          List<Table> found = new ArrayList<Table>();
          while (joins.size() > 0) {
              Join join = joins.remove(0);
              if (join.childTable.logicalName.equals(name)) {
                  found.add(join.childTable);
              } else {
                  joins.addAll(join.childTable.childJoins);
              }
          }
          return found;
      }
      
      public Table findBranchWithTables(List<String> tableNames, String factTableName, String factForeignKey) {
          // return the deepest table in the list
          // for each table, find uses and see if they are the deepest
          // of the bunch
          for (String name : tableNames) {
              List<Table> tables = findTable(name);

              for (Table table : tables) {
                  // march towards star, see if we found all the other tables
                  if (table.parentJoin == null) {
                      continue;
                  }
                  Table start = table.parentJoin.parentTable;
                  boolean matchFound = true;
                  for (String addlName : tableNames) {
                      if (!addlName.equals(name)) {
                          boolean found = false;
                          while (start.parentJoin != null) {
                              if (start.logicalName.equals(addlName)) {
                                  found = true;
                                  break;
                              } else {
                                  start = start.parentJoin.parentTable;
                              }
                          }
                          if (!found) {
                              matchFound = false;
                              break;
                          }
                      }
                  }
                  if (matchFound) {
                      Table joinTable = table.getJoinToFactTable(factTableName);
                      // verify that the join is the correct one
                      if (joinTable.parentJoin.parentKey.equals(factForeignKey)) {
                          return table;
                      }
                  }
              }
          }
          return null;
      }
  }
  
  /**
   * This object is used to represent the ROLAP Star Schema Joins within
   * Analysis Services
   */
  private static class Join {
      // child
      String childKey;
      Table childTable;
      // parent
      String parentKey;
      Table parentTable;

      public Join(Table parentTable, String parentKey, Table childTable, String childKey) {
          this.parentTable = parentTable;
          this.parentKey = parentKey;
          this.childTable = childTable;
          this.childKey = childKey;
      }
      
      public String toString() {
          return "[Join: parent=" + parentTable.logicalName + ";pkey="+parentKey+"; child=" + childTable.logicalName+"; ckey="+childKey+"]";
      }
  }

  /**
   * This is a utility function that locates the closest table to root table
   * in the table names list.
   * 
   * @param table the leaf to start from 
   * @param tableNames the list of tables to find
   * @return the closest to the root table
   */
  private static Table findParentTable(Table table, List<String> tableNames) {
      List<String> tableNameList = new ArrayList<String>(tableNames);
      while (true) {
          if (tableNameList.contains(table.logicalName)) {
              tableNameList.remove(table.logicalName);
              if (tableNameList.size() == 0) {
                  return table;
              }
          }
          table = table.parentJoin.parentTable;
      }
  }
  
  private static boolean containsIgnoreCase(List<String> list, String val) {
    for (String str : list) {
      if (str.equalsIgnoreCase(val)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * this finds and clones a tree of related tables for use with implicit stars
   * 
   * @param tableNames the list of tables to find
   * @param allTables the list of all known tables
   * @param factForeignKey the fact foreign key to join on
   */
  private static Table findFragmentWithTables(List<String> tableNames, List<Table> allTables, String factForeignKey) {
      for (String name : tableNames) {
          List<Table> tables = findTables(allTables, name);
          // this isn't a valid assumption... there may be an implicit table in the middle.
          if (tableNames.size() == 1 && containsIgnoreCase(tables.get(0).columns, factForeignKey)) {
              return tables.get(0).clone();
          }
          // for each match
          for (Table table : tables) {
              // march towards star, see if we found all the other tables
              boolean matchFound = true;
              for (String addlName : tableNames) {
                  Table start = table.parentJoin.parentTable;                  
                  if (!addlName.equals(name)) {
                      boolean found = false;
                      while (start.parentJoin != null) {
                          if (start.logicalName.equals(addlName)) {
                              found = true;
                              break;
                          } else {
                              start = start.parentJoin.parentTable;
                          }
                      }
                      if (!found) {
                          matchFound = false;
                          break;
                      }
                  }
              }
              if (matchFound) {
                  // the root may not be valid, we step down until we found the parent
                  Table parent = findParentTable(table, tableNames);
                  if (parent.ancestorsHaveColumn(factForeignKey)) {
                      // clone the tree down to the ancestor level where the key appears
                      Table toClone = table;
                      Table toReturn = null;
                      Join childJoin = null;
                      Table childClone = null;
                      boolean first = true;
                      boolean reachedParent = false;
                      do {
                          Table clone = toClone.clone();
                          if (first) {
                              toReturn = clone;
                              first = false;
                          }
                          if (toClone == parent) {
                              reachedParent = true;
                          }
                          if (childClone != null) {
                              Join clonedJoin = new Join(clone, childJoin.parentKey, childClone, childJoin.childKey);
                              childClone.parentJoin = clonedJoin;
                          }
                          childClone = clone;
                          childJoin = toClone.parentJoin;
                          toClone = toClone.parentJoin.parentTable;
                      } while (!reachedParent || !containsIgnoreCase(childClone.columns, factForeignKey));
                      return toReturn;
                  }
              }
          }
      }
      return null;
  }

  /**
   * this is a utility function that finds all the tables with a logical name
   * 
   * @param allTables the list of all known tables
   * @param logicalTableName the name of the table to find
   * @return a list of tables that match the logical name
   */
  private static List<Table> findTables(List<Table> allTables, String logicalTableName) {
      List<Table> list = new ArrayList<Table>();
      for (Table table : allTables) {
          if (table.logicalName.equals(logicalTableName)) {
              list.add(table);
          }
      }
      return list;
  }
  
  /**
   * finds a single table with the logical table name
   * 
   * @param allTables the list of all known tables
   * @param logicalTableName the name of the table to find
   * @return a single found table
   */
  private static Table findTable(List<Table> allTables, String logicalTableName) {
      for (Table table : allTables) {
          if (table.logicalName.equals(logicalTableName)) {
              return table;
          }
      }
      return null;
  }

  
  /**
   * TODO: review this method for correctness. it may be spitting out duplicate tables
   * 
   * this method parses SSAS's DataSourceView and generates a set of star schemas and 
   * join fragments
   *  
   * @param ssasDataSourceView dom4j model of SSAS's DataSourceView
   * @return 
   */ 
  @SuppressWarnings("unchecked")
  private static List<Table> generateStarModels(Element ssasDataSourceView) {
      // find all fact tables, then go from there to generate stars
      List tables = ssasDataSourceView.selectNodes("xs:complexType/xs:choice/xs:element");
      List<Table> starTables = new ArrayList<Table>();
      for (int i = 0; i < tables.size(); i++) {
          Element table = (Element)tables.get(i);
          String name = table.attributeValue("name");
          String dbname = table.attributeValue("DbTableName");
          Element primaryKey = (Element)ssasDataSourceView.selectSingleNode("xs:unique[xs:selector/@xpath='.//" + name + "']");
          String primaryKeyName = null;
          if (primaryKey != null) {
              primaryKeyName = ((Element)primaryKey.selectSingleNode("xs:field")).attributeValue("xpath");// primaryKey.attributeValue("name");
          }
          Table tableModel = new Table(name, dbname, primaryKeyName);
          
          // get columns
          List columns = table.selectNodes("xs:complexType/xs:sequence/xs:element");
          for (int j = 0; j < columns.size(); j++) {
              Element column = (Element)columns.get(j);
              tableModel.columns.add(column.attributeValue("name"));
          }
          
          starTables.add(tableModel);
      }
      
      List keyrefs = ssasDataSourceView.selectNodes("xs:keyref");
      for (int i = 0; i < keyrefs.size(); i++) {
          Element keyref = (Element)keyrefs.get(i);
          String refer = keyref.attributeValue("refer");
          String parentTable = ((Element)keyref.selectSingleNode("xs:selector")).attributeValue("xpath").substring(3);
          String parentKey = ((Element)keyref.selectSingleNode("xs:field")).attributeValue("xpath");
          
          Element unique = (Element)ssasDataSourceView.selectSingleNode("xs:unique[@name='" + refer + "']");
          String childTable = ((Element)unique.selectSingleNode("xs:selector")).attributeValue("xpath").substring(3);
          String childKey = ((Element)unique.selectSingleNode("xs:field")).attributeValue("xpath");
          
          // keyref contains parent table
          // unique contains child table
          // bind the two.  If child table already has a parent table, make a duplicate table and update both as future parents
          
          //for (Table childTableModel : starTables) {
          List<Table> origTables = new ArrayList<Table>(starTables);
          for (int j = 0; j < origTables.size(); j++) {
              Table childTableModel = origTables.get(j);
              if (childTableModel.logicalName.equals(childTable)) {
                  //for (Table parentTableModel : starTables) {
                  for (int k = 0; k < starTables.size(); k++) {
                      Table parentTableModel = starTables.get(k);
                      if (parentTableModel.logicalName.equals(parentTable) &&
                              !parentTableModel.logicalName.equals(childTableModel.logicalName) // skip parent / child hierarchies
                      ) {
                          logger.debug("Adding Join: " + parentTable + "("+parentKey+") to " + childTable +"("+childKey+")");
                          
                          // clone the child and its tree if it already has a parent
                          if (childTableModel.parentJoin != null) {
                              childTableModel = childTableModel.cloneTree();
                              starTables.add(childTableModel);
                          }

                          Join join = new Join(parentTableModel, parentKey, childTableModel, childKey);
                          childTableModel.parentJoin = join;
                          parentTableModel.childJoins.add(join);
                      }
                  }
              }
          }
      }
      
      List relationships = ssasDataSourceView.selectNodes("../xs:annotation/xs:appinfo/msdata:Relationship");
      // <msdata:Relationship name="FK_FactSalesQuota_DimEmployee" msdata:parent="dbo_DimEmployee" 
      // msdata:child="FactSalesQuota" msdata:parentkey="EmployeeKey" msdata:childkey="EmployeeKey"/>

      for (int i = 0; i < relationships.size(); i++) {
          // we used parent for fact and dimension for child, ssas uses the opposite
          Element relationship = (Element)relationships.get(i);
          String childTable = relationship.attributeValue("parent");
          String childKey = relationship.attributeValue("parentkey");
          String parentTable = relationship.attributeValue("child");
          String parentKey = relationship.attributeValue("childkey");
          
          List<Table> origTables = new ArrayList<Table>(starTables);
          for (int j = 0; j < origTables.size(); j++) {
              Table childTableModel = origTables.get(j);
              if (childTableModel.logicalName.equals(childTable)) {
                  //for (Table parentTableModel : starTables) {
                  for (int k = 0; k < starTables.size(); k++) {
                      Table parentTableModel = starTables.get(k);
                      if (parentTableModel.logicalName.equals(parentTable) &&
                              !parentTableModel.logicalName.equals(childTableModel.logicalName) // skip parent / child hierarchies
                      ) {
                          logger.debug("Adding Join: " + parentTable + "("+parentKey+") to " + childTable +"("+childKey+")");
                          
                          // clone the child and its tree if it already has a parent
                          if (childTableModel.parentJoin != null) {
                              childTableModel = childTableModel.cloneTree();
                              starTables.add(childTableModel);
                          }

                          Join join = new Join(parentTableModel, parentKey, childTableModel, childKey);
                          childTableModel.parentJoin = join;
                          parentTableModel.childJoins.add(join);
                      }
                  }
              }
          }
      }
      return starTables;
  }
  
  public static List<Document> generateMondrianDocsFromSSASSchema(final InputStream input) throws DocumentException, IOException, AggDesignerException {

      Document ssasDocument = parseAssl(input);
      
      // issue: if we have multi-line text, there is a problem with identing names / etc
      // solution: clean up the dom before traversal
      List allElements = ssasDocument.selectNodes("//*");
      for (int i = 0; i < allElements.size(); i++) {
          Element element = (Element)allElements.get(i);
          element.setText(element.getText().replaceAll("[\\s]+", " ").trim());
      }

      List ssasDatabases = ssasDocument.selectNodes("//assl:Database");
      List<Document> mondrianDocs = new ArrayList<Document>(ssasDatabases.size()); 
      for (int i = 0; i < ssasDatabases.size(); i++) {
          Document mondrianDoc = DocumentFactory.getInstance().createDocument();
          Element mondrianSchema = DocumentFactory.getInstance().createElement("Schema");
          mondrianDoc.add(mondrianSchema);
          Element ssasDatabase = (Element)ssasDatabases.get(i);
          mondrianSchema.add(DocumentFactory.getInstance().createAttribute(mondrianSchema, "name", getXPathNodeText(ssasDatabase, "assl:Name")));
          populateCubes(mondrianSchema, ssasDatabase);
          
          mondrianDocs.add(mondrianDoc);
      }
      
      return mondrianDocs;
  }

  private static boolean isVirtualMeasureGroup(Element measureGroup) {
      return measureGroup.selectSingleNode("assl:Source[@xsi:type='MeasureGroupBinding']") != null;
  }
  
  private static void populateCubes(Element mondrianSchema, Element ssasDatabase) throws AggDesignerException {
      List allMeasureGroups = ssasDatabase.selectNodes("assl:Cubes/assl:Cube/assl:MeasureGroups/assl:MeasureGroup");
      for (int i = 0; i < allMeasureGroups.size(); i++) {
          Element ssasMeasureGroup = (Element)allMeasureGroups.get(i);
          String measureGroupName = getXPathNodeText(ssasMeasureGroup, "assl:Name");
          if (isVirtualMeasureGroup(ssasMeasureGroup)) {
              String cubeName = getXPathNodeText(ssasMeasureGroup, "../../assl:Name");
              logger.debug("Skipping SSAS Virtual Cube, Measure Group " + cubeName + ", " + measureGroupName);
          } else {
              Element mondrianCube = DocumentFactory.getInstance().createElement("Cube");
              mondrianCube.addAttribute("name", measureGroupName);
              
              String dataSourceViewID = getXPathNodeText(ssasMeasureGroup, "../../assl:Source/assl:DataSourceViewID");
              Element ssasDataSourceView = (Element)ssasDatabase.selectSingleNode("assl:DataSourceViews/assl:DataSourceView[translate(assl:ID, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"+dataSourceViewID.toLowerCase()+"']/assl:Schema/xs:schema/xs:element");
              // we could cache these to increase performance
              List<Table> allTables = generateStarModels(ssasDataSourceView);
              
              // add fact table
              String factTableName = getXPathNodeText(ssasMeasureGroup, "assl:Measures/assl:Measure/assl:Source/assl:Source/assl:TableID");

              Table factTable = findTable(allTables, factTableName);
              if (factTable == null) {
                  System.out.println("AHH");
              }
              Element fact = DocumentFactory.getInstance().createElement("Table");
              fact.addAttribute("name", factTable.dbName);
              mondrianCube.add(fact);
              
              // add dimensions
              populateDimensions(mondrianCube, ssasDatabase, ssasMeasureGroup, factTable, allTables);
              
              // add measures
              populateCubeMeasures(mondrianCube, ssasMeasureGroup, factTable, measureGroupName);
              
              // add cube
              mondrianSchema.add(mondrianCube);
          }
      }
  }
  private static void populateDimensions(
          Element mondrianCube, 
          Element ssasDatabase, 
          Element ssasMeasureGroup, 
          Table factTable, 
          List<Table> allTables) throws AggDesignerException {
      // add dimensions
      List measureDimensions = ssasMeasureGroup.selectNodes("assl:Dimensions/assl:Dimension");
      
      for (int j = 0; j < measureDimensions.size(); j++) {
          
          Element measureDimension = (Element)measureDimensions.get(j);
          String cubeDimensionId = getXPathNodeText(measureDimension, "assl:CubeDimensionID");
          Element cubeDimension = (Element)ssasMeasureGroup.selectSingleNode("../../assl:Dimensions/assl:Dimension[assl:ID='"+cubeDimensionId + "']");
          String databaseDimensionId = getXPathNodeText(cubeDimension, "assl:DimensionID");
          Element databaseDimension = (Element)ssasDatabase.selectSingleNode("assl:Dimensions/assl:Dimension[assl:ID='" + databaseDimensionId + "']");
          
          Element mondrianDimension = DocumentFactory.getInstance().createElement("Dimension");
          mondrianDimension.addAttribute("name", getXPathNodeText(cubeDimension, "assl:Name"));

          // locate the key attribute
          Element keyAttribute = (Element)databaseDimension.selectSingleNode("assl:Attributes/assl:Attribute[assl:Usage='Key']");
          String keyAttributeID = getXPathNodeText(keyAttribute, "assl:ID");
          
          // get foreign key
          
          // first look in the dimension object within the measures
          Element foreignKeyObject = (Element)measureDimension.selectSingleNode("assl:Attributes/assl:Attribute[assl:AttributeID='"+keyAttributeID+"']/assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID");
          String foreignKey = null;
          if (foreignKeyObject != null) {
              foreignKey = foreignKeyObject.getTextTrim();
          } else {
              foreignKey = getXPathNodeText(keyAttribute,"assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID");
          }
          mondrianDimension.addAttribute("foreignKey", foreignKey);

          // get hierarchies
          populateHierarchies(mondrianDimension, cubeDimension, databaseDimension, keyAttribute, factTable, allTables, foreignKey);
          
          mondrianCube.add(mondrianDimension);
      }
  }
  
  private static void populateHierarchies(
          Element mondrianDimension, 
          Element ssasCubeDimension, 
          Element ssasDatabaseDimension, 
          Element ssasDimensionKeyAttribute, 
          Table factTable, 
          List<Table> allTables,
          String factForeignKey
      ) throws AggDesignerException {
      
      // first do parent child hierarchies
      // for each attribute in cube dimension, see if it's database dimension attribute is USAGE PARENT
      // SSAS 2005 only supports one parent child hierarchy per dimension
      List cubeAttributes = ssasCubeDimension.selectNodes("assl:Attributes/assl:Attribute");
      for (int i = 0; i < cubeAttributes.size(); i++) {
          Element cubeAttribute = (Element)cubeAttributes.get(i);
          // retrieve database attribute
          String attribID = getXPathNodeText(cubeAttribute, "assl:AttributeID");
          Element databaseAttribute = (Element)ssasDatabaseDimension.selectSingleNode("assl:Attributes/assl:Attribute[assl:ID='"+attribID+"']");

          Element usageElement = (Element)databaseAttribute.selectSingleNode("assl:Usage");
          if (usageElement != null && "Parent".equals(usageElement.getTextTrim())) {
              populateParentChildHierarchy(mondrianDimension, databaseAttribute, ssasDimensionKeyAttribute, ssasDatabaseDimension, factTable, factForeignKey, allTables, attribID);
          }
      }
      
      // handle the traditional hierarchies
      
      List hierarchies = ssasCubeDimension.selectNodes("assl:Hierarchies/assl:Hierarchy");
      for (int k = 0; k < hierarchies.size(); k++) {
          Element hierarchy = (Element)hierarchies.get(k);
          String databaseHierarchyID = getXPathNodeText(hierarchy, "assl:HierarchyID");
          Element databaseHierarchy = (Element)ssasDatabaseDimension.selectSingleNode("assl:Hierarchies/assl:Hierarchy[assl:ID='" + databaseHierarchyID + "']");
          
          if (databaseHierarchy == null) {
              throw new AggDesignerException("Failed to locate hierarchy " + databaseHierarchyID);
          }
          
          Element mondrianHierarchy = DocumentFactory.getInstance().createElement("Hierarchy");

          mondrianHierarchy.addAttribute("name", getXPathNodeText(databaseHierarchy, "assl:Name"));
          mondrianHierarchy.addAttribute("primaryKey", getXPathNodeText(ssasDimensionKeyAttribute,"assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID"));
          Element allMemberName = (Element)databaseHierarchy.selectSingleNode("assl:AllMemberName");
          if (allMemberName != null && allMemberName.getTextTrim().length() != 0) {
              mondrianHierarchy.addAttribute("allMemberName", allMemberName.getTextTrim());
              mondrianHierarchy.addAttribute("hasAll", "true");
          } else {
              mondrianHierarchy.addAttribute("hasAll", "false");
          }
          // determine if this hierarchy is a snow flake
          // we can tell this by looking at the levels
          
          // preprocess levels to determine snowflake-ness
          List ssasLevels = databaseHierarchy.selectNodes("assl:Levels/assl:Level");
          
          List<String> tables = new ArrayList<String>();
          for (int l = 0; l < ssasLevels.size(); l++) {
              Element level = (Element)ssasLevels.get(l);
              String sourceAttribID = getXPathNodeText(level, "assl:SourceAttributeID");
              Element sourceAttribute = (Element)ssasDatabaseDimension.selectSingleNode("assl:Attributes/assl:Attribute[assl:ID='"+sourceAttribID + "']");
              String levelTableID = getXPathNodeText(sourceAttribute, "assl:NameColumn/assl:Source/assl:TableID");
              if (!tables.contains(levelTableID)) {
                  // insert the table in the correct order
                  tables.add(0, levelTableID);
              }
          }
          
          // skip if degenerate dimension
          if (tables.size() != 1 || !tables.get(0).equals(factTable.logicalName)) {
              populateHierarchyRelation(mondrianHierarchy, tables, ssasDatabaseDimension, factTable, allTables, databaseHierarchyID, factForeignKey);
          } else {
              mondrianHierarchy.add(DocumentFactory.getInstance().createComment("Degenerate Hierarchy"));
          }
          
          // render levels
          populateHierarchyLevels(mondrianHierarchy, ssasLevels, ssasDatabaseDimension, allTables, tables.size() > 1);
          
          mondrianDimension.add(mondrianHierarchy);
      }
      
      // finally, do attribute hierarchies
      populateAttributeHierarchies(mondrianDimension, cubeAttributes, ssasDatabaseDimension, ssasDimensionKeyAttribute, factTable, factForeignKey, allTables);

  }
  
  /**
   * generates <Hierarchy> mondrian tags 
   */
  private static void populateHierarchyRelation(
          Element mondrianHierarchy, 
          List<String> tables,
          Element ssasDatabaseDimension,
          Table factTable,
          List<Table> allTables,
          String ssasHierarchyID,
          String factForeignKey
      ) throws AggDesignerException {
      // we should specify the foreign key here also
      Table currentTable = factTable.findBranchWithTables(tables, factTable.logicalName, factForeignKey);
      if (currentTable == null) {
          currentTable = findFragmentWithTables(tables, allTables, factForeignKey);
          if (currentTable == null) {
              throw new AggDesignerException("Error: " + ssasHierarchyID + " star schema branch not found.  " + factTable.logicalName + "." + factForeignKey);
          }
          // we have an implicit star join
          currentTable.getRoot().parentJoin = new Join(factTable, factForeignKey, currentTable, currentTable.primaryKey);
          logger.debug("IMPLICIT STAR JOIN FOR " + currentTable.logicalName + "." + currentTable.primaryKey + " to " + factTable.logicalName + "." + factForeignKey);          
      }
      Element currentHierarchyRelation =  DocumentFactory.getInstance().createElement("Table");
      // expand tables so that explicit naming of tables when rendering levels is known
      if (!tables.contains(currentTable.logicalName)) {
          tables.add(currentTable.logicalName);
      }
      currentHierarchyRelation.addAttribute("name", currentTable.dbName);
      
      while (!currentTable.parentJoin.parentTable.logicalName.equals(factTable.logicalName)) {
          Element hierarchyRelation = DocumentFactory.getInstance().createElement("Join");
          Element tableRelation = DocumentFactory.getInstance().createElement("Table");
          tableRelation.addAttribute("name", currentTable.parentJoin.parentTable.dbName);
          if (!tables.contains(currentTable.parentJoin.parentTable.logicalName)) {
              tables.add(currentTable.parentJoin.parentTable.logicalName);
          }
          
          hierarchyRelation.addAttribute("leftKey", currentTable.parentJoin.parentKey);
          hierarchyRelation.addAttribute("rightKey", currentTable.parentJoin.childKey);
          
          hierarchyRelation.add(tableRelation);
          hierarchyRelation.add(currentHierarchyRelation);
          
          currentHierarchyRelation = hierarchyRelation;
          currentTable = currentTable.parentJoin.parentTable;
      }
      
      if (currentHierarchyRelation.getName().equals("Join")) {
          mondrianHierarchy.addAttribute("primaryKeyTable", currentTable.dbName);
      }
      mondrianHierarchy.add(currentHierarchyRelation);
  }
  
  /**
   * generates <Level> mondrian tags 
   */
  private static void populateHierarchyLevels(
          Element mondrianHierarchy, 
          List ssasLevels, 
          Element ssasDatabaseDimension, 
          List<Table> allTables,
          boolean includeTableName
      ) throws AggDesignerException {
      
      for (int l = 0; l < ssasLevels.size(); l++) {
          Element level = (Element)ssasLevels.get(l);
          Element mondrianLevel = DocumentFactory.getInstance().createElement("Level");
          mondrianLevel.addAttribute("name", getXPathNodeText(level, "assl:Name"));
          
          String sourceAttribID = getXPathNodeText(level, "assl:SourceAttributeID");
          Element sourceAttribute = (Element)ssasDatabaseDimension.selectSingleNode("assl:Attributes/assl:Attribute[assl:ID='"+sourceAttribID + "']");
          
          Element keyUniquenessGuarantee = (Element)sourceAttribute.selectSingleNode("assl:KeyUniquenessGuarantee");
          boolean keyUniqueness = false;
          if (keyUniquenessGuarantee != null) {
              keyUniqueness = "true".equals(keyUniquenessGuarantee.getTextTrim());
          }
          mondrianLevel.addAttribute("uniqueMembers", "" + keyUniqueness);
          if (includeTableName) {
              String levelTableID = getXPathNodeText(sourceAttribute, "assl:NameColumn/assl:Source/assl:TableID");
              mondrianLevel.addAttribute("table", findTables(allTables, levelTableID).get(0).dbName); // tableName);
          }
          String keyColumn = getXPathNodeText(sourceAttribute, "assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID");
          String nameColumn = getXPathNodeText(sourceAttribute, "assl:NameColumn/assl:Source/assl:ColumnID");
          if (keyColumn == null || keyColumn.equals(nameColumn)) {
              mondrianLevel.addAttribute("column", nameColumn);
          } else {
              mondrianLevel.addAttribute("column", keyColumn);
              mondrianLevel.addAttribute("nameColumn", nameColumn);
          }
          String datatype = getXPathNodeText(sourceAttribute, "assl:NameColumn/assl:DataType");
          
          // TODO: create example with numeric column, 
          // this code is more stubbed then tested
          if (datatype.equals("Numeric")) {
              mondrianLevel.addAttribute("type", "Numeric");
          }
          mondrianHierarchy.add(mondrianLevel);
      }
  }
  
  /**
   * generates parent child hierarchy
   */
  private static void populateParentChildHierarchy(
          Element mondrianDimension,
          Element databaseAttribute,
          Element ssasDimensionKeyAttribute,
          Element ssasDatabaseDimension,
          Table factTable,
          String factForeignKey,
          List<Table> allTables,
          String attributeID
  ) throws AggDesignerException {
      mondrianDimension.add(DocumentFactory.getInstance().createComment("Parent Child Hierarchy"));
      Element mondrianHierarchy = DocumentFactory.getInstance().createElement("Hierarchy");
      mondrianHierarchy.addAttribute("name", getXPathNodeText(databaseAttribute, "assl:Name"));
      String keyColumnID = getXPathNodeText(ssasDimensionKeyAttribute,"assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID");
      mondrianHierarchy.addAttribute("primaryKey", keyColumnID);

      // not certain on where to get the all member name for parent / child rels
      // ssas seems to use "All" for the default name
      // Element allMemberName = (Element)databaseHierarchy.selectSingleNode("assl:AllMemberName");
      // if (allMemberName != null) {
      mondrianHierarchy.addAttribute("allMemberName", "All");
      mondrianHierarchy.addAttribute("hasAll", "true");
      // }

      List<String> tables = new ArrayList<String>();
      tables.add(getXPathNodeText(ssasDimensionKeyAttribute,"assl:KeyColumns/assl:KeyColumn/assl:Source/assl:TableID"));
      
      populateHierarchyRelation(mondrianHierarchy, tables, ssasDatabaseDimension, factTable, allTables, attributeID, factForeignKey);
      
      Element mondrianLevel = DocumentFactory.getInstance().createElement("Level");
      // <Level name="Employee Id" type="Numeric" uniqueMembers="true" column="employee_id" 
      //        parentColumn="supervisor_id" nameColumn="full_name" nullParentValue="0">

      // for now, use the hierarchy name for the level name
      mondrianLevel.addAttribute("name", getXPathNodeText(databaseAttribute, "assl:Name"));

      // mondrianLevel.addAttribute("type", "Numeric");
      mondrianLevel.addAttribute("uniqueMembers", "true");
      // NameColumn
      String parentID = getXPathNodeText(databaseAttribute, "assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID");
      String columnID = getXPathNodeText(databaseAttribute, "assl:NameColumn/assl:Source/assl:ColumnID");
      mondrianLevel.addAttribute("column", keyColumnID);
      mondrianLevel.addAttribute("parentColumn", parentID);
      mondrianLevel.addAttribute("nameColumn", columnID);
      
      // do we need ordinal col?
      
      // from http://msdn2.microsoft.com/en-us/library/ms174935.aspx
      // User Defined Hierarchies
      // By default, any member whose parent key equals its own member key, null, 0 (zero), 
      // or a value absent from the column for member keys is assumed to be a member of the 
      // top level (excluding the (All) level).
      
      mondrianLevel.addAttribute("nullParentValue", "0");
      mondrianHierarchy.add(mondrianLevel);
      mondrianDimension.add(mondrianHierarchy);
  }
  
  /**
   * generates ssas attribute hierarchies
   */
  private static void populateAttributeHierarchies(
          Element mondrianDimension,
          List cubeAttributes,
          Element ssasDatabaseDimension,
          Element ssasDimensionKeyAttribute,
          Table factTable,
          String factForeignKey,
          List<Table> allTables
  ) throws AggDesignerException {
      mondrianDimension.add(DocumentFactory.getInstance().createComment("Attribute Hierarchies"));
      
      for (int i = 0; i < cubeAttributes.size(); i++) {
          Element cubeAttribute = (Element)cubeAttributes.get(i);
          // retrieve database attribute
          String attribID = getXPathNodeText(cubeAttribute, "assl:AttributeID");
          Element databaseAttribute = (Element)ssasDatabaseDimension.selectSingleNode("assl:Attributes/assl:Attribute[assl:ID='"+attribID+"']");
          // note: databaseAttribute also has an AttributeHierarchyEnabled flag
          
          // should we also check "visible"?
          if (cubeAttribute.selectSingleNode("assl:AttributeHierarchyEnabled") != null &&
                  "true".equals(getXPathNodeText(cubeAttribute, "assl:AttributeHierarchyEnabled"))
          ) {
              Element usageElement = (Element)databaseAttribute.selectSingleNode("assl:Usage");
              if (usageElement != null && "Parent".equals(usageElement.getTextTrim())) {
                  // skip parent hierarchies
                  continue;
              }
              
              Element mondrianHierarchy = DocumentFactory.getInstance().createElement("Hierarchy");
              String attribName = getXPathNodeText(databaseAttribute, "assl:Name");
              mondrianHierarchy.addAttribute("name", attribName);
              mondrianHierarchy.addAttribute("primaryKey", getXPathNodeText(ssasDimensionKeyAttribute,"assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID"));

              // AttributeAllMemberName
              Element allMemberName = (Element)ssasDatabaseDimension.selectSingleNode("assl:AttributeAllMemberName");
              if (allMemberName != null && allMemberName.getTextTrim().length() != 0) {
                  mondrianHierarchy.addAttribute("allMemberName", allMemberName.getTextTrim());
                  mondrianHierarchy.addAttribute("hasAll", "true");
              } else {
                  mondrianHierarchy.addAttribute("hasAll", "false");
              }
              
              List<String> tables = new ArrayList<String>();
              tables.add(getXPathNodeText(databaseAttribute,"assl:KeyColumns/assl:KeyColumn/assl:Source/assl:TableID"));
              
              // skip if degenerate dimension
              if (tables.size() != 1 || !tables.get(0).equals(factTable.logicalName)) {
                  populateHierarchyRelation(mondrianHierarchy, tables, ssasDatabaseDimension, factTable, allTables, attribID, factForeignKey);
              }
              
              Element mondrianLevel = DocumentFactory.getInstance().createElement("Level");
              mondrianLevel.addAttribute("name", attribName);
                            
              Element keyUniquenessGuarantee = (Element)databaseAttribute.selectSingleNode("assl:KeyUniquenessGuarantee");
              boolean keyUniqueness = false;
              if (keyUniquenessGuarantee != null) {
                  keyUniqueness = "true".equals(keyUniquenessGuarantee.getTextTrim());
              }
              mondrianLevel.addAttribute("uniqueMembers", "" + keyUniqueness);
              if (tables.size() > 1) {
                  String levelTableID = getXPathNodeText(databaseAttribute, "assl:NameColumn/assl:Source/assl:TableID");
                  mondrianLevel.addAttribute("table", findTables(allTables, levelTableID).get(0).dbName); // tableName);
              }
              String keyColumn = getXPathNodeText(databaseAttribute, "assl:KeyColumns/assl:KeyColumn/assl:Source/assl:ColumnID");
              String nameColumn = getXPathNodeText(databaseAttribute, "assl:NameColumn/assl:Source/assl:ColumnID");
              if (keyColumn == null || keyColumn.equals(nameColumn)) {
                  mondrianLevel.addAttribute("column", nameColumn);
              } else {
                  mondrianLevel.addAttribute("column", keyColumn);
                  mondrianLevel.addAttribute("nameColumn", nameColumn);
              }
              String datatype = getXPathNodeText(databaseAttribute, "assl:NameColumn/assl:DataType");
              
              // TODO: create example with numeric column, 
              // this code is more stubbed then tested
              if (datatype.equals("Numeric")) {
                  mondrianLevel.addAttribute("type", "Numeric");
              }
              mondrianHierarchy.add(mondrianLevel);
              mondrianDimension.add(mondrianHierarchy);
          }

      }
  }
  /**
   * generates <Measure> mondrian tags 
   */
  private static void populateCubeMeasures(
          Element mondrianCube, 
          Element ssasMeasureGroup, 
          Table factTable, 
          String cubeName
      ) throws AggDesignerException {
      
      List allMeasures = ssasMeasureGroup.selectNodes("assl:Measures/assl:Measure");
      for (int j = 0; j < allMeasures.size(); j++) {
          Element measure = (Element)allMeasures.get(j);

          // assert Source/Source xsi:type="ColumnBinding"
          if (measure.selectSingleNode("assl:Source/assl:Source[@xsi:type='ColumnBinding']") == null &&
              measure.selectSingleNode("assl:Source/assl:Source[@xsi:type='RowBinding']") == null)                          
          {
              logger.warn("SKIPPING MEASURE, INVALID MEASURE IN CUBE " + cubeName + " : " + measure.asXML());
              continue;
          }
          
          Element mondrianMeasure = DocumentFactory.getInstance().createElement("Measure");
          String measureName = getXPathNodeText(measure, "assl:Name");
          mondrianMeasure.addAttribute("name", measureName);
          logger.trace("MEASURE: " + measureName);
          String aggType = "sum";
          Element aggFunction = (Element)measure.selectSingleNode("assl:AggregateFunction");
          if (aggFunction != null) {
              aggType = aggFunction.getTextTrim().toLowerCase();
          }
          if (aggType.equals("distinctcount")) {
              aggType = "distinct-count";
          }
          mondrianMeasure.addAttribute("aggregator", aggType);
          if (measure.selectSingleNode("assl:Source/assl:Source[@xsi:type='ColumnBinding']") != null) {
              mondrianMeasure.addAttribute("column", getXPathNodeText(measure, "assl:Source/assl:Source/assl:ColumnID"));
          } else {
              // select the first fact column in the star
              mondrianMeasure.addAttribute("column", factTable.columns.get(0));
              
          }
          mondrianCube.add(mondrianMeasure);
      }
  }
  
  
  /**
   * Returns document from file.
   * @param url path to SSAS dump
   * @return document
   * @throws DocumentException problem parsing document
   */
  public static Document parseAssl(final URL url) throws DocumentException, IOException {
      return parseAssl(url.openStream());
  }
  
  public static Document parseAssl(final InputStream input) throws DocumentException, IOException {
      Map<String, String> uris = new HashMap<String, String>();
      uris.put("assl", "http://schemas.microsoft.com/analysisservices/2003/engine");
      uris.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      uris.put("xs", "http://www.w3.org/2001/XMLSchema");
      uris.put("msprop","urn:schemas-microsoft-com:xml-msprop");
      uris.put("msdata","urn:schemas-microsoft-com:xml-msdata");
      DocumentFactory factory = new DocumentFactory();
      factory.setXPathNamespaceURIs(uris);
      SAXReader reader = new SAXReader();
      reader.setDocumentFactory(factory);

      // get bytes from InputStream and cache them so they can be read multiple times
      byte[] bytes = IOUtils.toByteArray(input);
      
      ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
      
      // try default encoding first, then fall back on iso-8859-1
      Document document = null;
      try {
        logger.debug("attempting to parse assuming utf-8 encoding");
        document = reader.read(byteInput);
      } catch (DocumentException e) {
        // retry
        reader.setEncoding("iso-8859-1");
        // exception will propagate if it fails to read with iso-8859-1
        logger.debug("parse failed; attempting to parse assuming iso=8859-1 encoding");
        // start over from the first byte by creating a new stream
        byteInput = new ByteArrayInputStream(bytes);
        document = reader.read(byteInput);
      }
      return document;
  }
  
  public static String getXPathNodeText(Node parent, String xpath) throws AggDesignerException {
      Element element = (Element)parent.selectSingleNode(xpath);
      if (element == null) {
          throw new AggDesignerException("no element found for xpath '" + xpath +"'");
      }
      return ((Element)parent.selectSingleNode(xpath)).getTextTrim();
  }
}

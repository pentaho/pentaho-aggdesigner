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


package org.pentaho.aggdes.ui.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.thoughtworks.xstream.io.xml.AbstractXmlDriver;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.di.core.database.DatabaseMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class handles the Marshalling and Unmarshalling
 * of the current Workspace
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class SerializationService {
  
  private final String SERIALIZATION_VERSION = "1.0";
  
  private ConnectionModel connectionModel;

  private AggList aggList;
  
  public String[] getConnectionAndAggListElements(String xml) throws AggDesignerException {
    try {
      String xmlElements[] = new String[3];
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
      int c = -1;
      for (int i = 0; i < doc.getDocumentElement().getChildNodes().getLength(); i++) {
        Node node = doc.getDocumentElement().getChildNodes().item(i);
        if (node instanceof Element) {
          if (c == 3) {
            throw new RuntimeException("Failed to parseWorkspace, extra elements found");
          }
          Source source = new DOMSource(node);
          StringWriter writer = new StringWriter();
          Result result = new StreamResult(writer);
          Transformer xformer = TransformerFactory.newInstance().newTransformer();
          xformer.transform(source, result);
          if (c == -1) {
            String versionInfo = writer.toString();
            XStream xstream = new XStream(new DomDriver());
            String serializationVersion = (String)xstream.fromXML(versionInfo);
            if (!serializationVersion.equals(SERIALIZATION_VERSION)) {
              throw new AggDesignerException(Messages.getString("SerializationService.UnrecognizedVersion", SERIALIZATION_VERSION, serializationVersion));
            } 
            c++;
          } else {
            xmlElements[c++] = writer.toString();
          }
        }
      }
      return xmlElements;
    } catch (IOException e) {
      e.printStackTrace();
     // replace with aggdes exception
      throw new RuntimeException("Failed to parse Workspace");
    } catch (Exception e) {
      if (e instanceof AggDesignerException) {
        AggDesignerException ex = (AggDesignerException)e;
        throw ex; 
      } else {
        e.printStackTrace();
        throw new RuntimeException("Failed to parse Workspace");
      }
    }
  }
  
    public XStream getXStream(Schema schema) {
    XStream xstream = createXStreamWithAllowedTypes( new DomDriver(), DatabaseMeta.class, MondrianFileSchemaModel.class, AggListImpl.class, Schema.class );

    xstream.registerConverter(new DatabaseMetaConverter());
    xstream.registerConverter(new AggListConverter(aggList));
    xstream.registerConverter(new AttributeConverter(schema));
    xstream.registerConverter(new MeasureConverter(schema));
    return xstream;
  }
  
  public String serializeWorkspace(Schema schema) {   
    // update the checksum
    connectionModel.getSelectedSchemaModel().setSchemaChecksum(
        connectionModel.getSelectedSchemaModel().recalculateSchemaChecksum());
    
    List<Object> systemObjects = new ArrayList<Object>();
    AggList aggList = getAggList(); 
    XStream xstream = getXStream(schema);
    systemObjects.add(SERIALIZATION_VERSION);
    systemObjects.add(connectionModel.getDatabaseMeta());
    systemObjects.add(connectionModel.getSelectedSchemaModel());
    systemObjects.add(aggList);
    return xstream.toXML(systemObjects);
  }
  
  public void deserializeConnection(Schema schema, String rdbmsXml, String schemaXml) {
    XStream xstream = getXStream(schema);
    DatabaseMeta databaseMeta = (DatabaseMeta)xstream.fromXML(rdbmsXml);
    SchemaModel schemaModel = (SchemaModel)xstream.fromXML(schemaXml);
    
    //save off cubeName since setSelectedSchemaModel will clear it out
    String cubeName = schemaModel.getCubeName();
    
    connectionModel.setDatabaseMeta(databaseMeta);
    connectionModel.setSelectedSchemaModel(schemaModel);
    
    connectionModel.setCubeName(cubeName);
  }
  
  public void deserializeAggList(Schema schema, String xml, XStream xstream) {
    xstream.fromXML(xml);
  }
  public void deserializeAggList(Schema schema, String xml) {
    XStream xstream = getXStream( schema );
    deserializeAggList( schema, xml, xstream );
  }

  public void setConnectionModel(ConnectionModel connectionModel) {
  
    this.connectionModel = connectionModel;
  }

  public AggList getAggList() {
  
    return aggList;
  }

  public void setAggList(AggList aggList) {
  
    this.aggList = aggList;
  }

  public static XStream createXStreamWithAllowedTypes( AbstractXmlDriver driver, Class ... classes ) {
    XStream xstream = driver == null ? new XStream() : new XStream( driver );
    if( classes != null ) {
      xstream.allowTypes( classes );
    }
    return xstream;
  }
}

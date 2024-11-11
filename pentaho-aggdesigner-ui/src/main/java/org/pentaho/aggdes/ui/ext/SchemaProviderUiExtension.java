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


package org.pentaho.aggdes.ui.ext;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.ui.xul.impl.XulEventHandler;

/**
 * This interface defines the behavior of the Aggregate Designer's Schema providers.
 * Implementations of this class provide the user interface definition via XUL
 * overlays and supporting methods for obtaining a {@link org.pentaho.aggdes.model.Schema} 
 * definition.
 * 
 * @author NBaker
 */
public interface SchemaProviderUiExtension extends UiExtension, XulEventHandler {

  /**
   * Returns whether or not this instance is the current selected provider
   * 
   * @return flag indicating selected status
   */
  public boolean isSelected();

  /**
   * Sets the selected state of the provider.
   * 
   * @param selected flag indicating selected state
   */
  public void setSelected(boolean selected);
  
  /**
   * Returns a {@link org.pentaho.aggdes.model.Schema}  object based on the passed in cube name.
   * 
   * @param cubeName String name of the cube definition to load
   * @return Schema object.
   * @throws AggDesignerException
   */
  public Schema loadSchema(String cubeName) throws AggDesignerException;
  
  
  /**
   * Your schema provider ui extension must notify the application when the user has entered a complete
   * set of input.  The application will enable an Apply button in the OLAP connections field set when
   * the selected schema provider extension {@link #isSchemaDefined()} returns TRUE.  It is not enough
   * that your implementation return true from this method.  Your extension must fire a property change
   * for the "schemaDefined" property in two cases:
   * <ol>
   * <li>when the user input changes (e.g. when the text changes in a text field)
   * <li>when this extension has been selected (so you will be firing the existing value of the schemaDefined property)
   * </ol>
   * @return a flag to let the application know it can proceed with this extension's data
   * @see PropertyChangeListener#firePropertyChange
   */
  public boolean isSchemaDefined();

  /**
   * Returns a list of cubes in the current Schema
   * 
   * @return a list of cube names
   * @throws AggDesignerException
   */
  public List<String> getCubeNames() throws AggDesignerException;
  
  /**
   * Returns the backing {@link org.pentaho.aggdes.model.SchemaModel} object for this 
   * SchemaProvider.
   * 
   * @return the backing {@link org.pentaho.aggdes.model.SchemaModel}
   * 
   */
  public SchemaModel getSchemaModel();
  
  /**
   * Sets the backing {@link org.pentaho.aggdes.model.SchemaModel} object for this SchemaProvider.
   * This is called by the serialization service when loading saved workspaces.
   * 
   * @param model SchemaModel object to load.
   */
  public void setSchemaModel(SchemaModel model);
  
  /**
   * Used by the deserialization service to locate an approprite SchemaProvider for a SchemaModel.
   * Implementors need to return true if the supplied SchemaModel is theirs.
   * 
   * @param schemaModel
   * @return flag indicating whether this SchemaProvider supports the supplied SchemaModel
   */
  public boolean supportsSchemaModel(SchemaModel schemaModel);
  
  /**
   * Resets the state of the SchemaProvider including it's user interface.
   * 
   */
  public void reset();
}


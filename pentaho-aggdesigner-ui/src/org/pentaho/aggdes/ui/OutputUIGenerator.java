/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.ui;

import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.ui.xul.dom.Document;

/**
 * Interface defining the behavior of OutputUIGenerator objects.
 *
 * <p>Implementations of this class provide the ability to add custom
 * UI definitions for particular
 * {@link org.pentaho.aggdes.output.Output Output} types.  Output
 * objects are associated with implementors of this interface via
 * {@link OutputUIService#getUIGenerator(Output)}
 *
 * @param <A> Implementation of the UIAggregate interface
 * @see org.pentaho.aggdes.output.Output
 * @see OutputUIService
 */
public interface OutputUIGenerator<A extends UIAggregate> {

  /**
   * Returns whether this OutputUIGenerator can provide UI support for the given Output object
   *
   * @param outputInstance instance of the Output interface
   * @return true/false
   */
  public boolean accept(Output outputInstance);


  /**
   * Loads any needed user interface controls into the application. The preferred method being via
   * Xul Overlays applied to the by document.addOverlay(docFile.xul)
   *
   * @param doc Xul Document. Used to access UI elements to save their state.
   * @throws AggDesignerException
   */
  public void loadUI(Document doc) throws AggDesignerException;

  /**
   * Removes any custom UI elements previously added to the application. Preferred method being
   * document.removeOverlay(docFile.xul)
   *
   * @param doc Xul Document. Used to access UI elements to save their state.
   * @throws AggDesignerException
   */
  public void removeUI(Document doc) throws AggDesignerException;

  /**
   * Called by the Aggregate Designer whenever a user requests an aggregate be saved. The implementing class
   * is responsible for persisting custom parameters within the UIAggregate instance.
   *
   * @param agg UIAggregate instance to save
   */
  public void saveData(A agg, Document doc);

  /**
   * Called after UI creation when an aggregate is loaded. Implementing classes are responsible
   * for loading custom parameters into the UI form elements when this method is called.
   *
   * @param agg UIAggregate to load into the UI
   * @param doc Xul Document. Used to access UI elements to save their state.
   * @param schema Schema representation object
   */
  public void loadData(A agg, Document doc, Schema schema);

}

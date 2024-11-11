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


package org.pentaho.aggdes.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.ui.xul.XulEventSource;

public class EventRecorder {

  private Map<String, Object> eventValues = new HashMap<String, Object>();
  
  private boolean logging;

  public boolean isLogging() {
    return logging;
  }

  public void setLogging(boolean logging) {
    this.logging = logging;
  }

  public Object getLastValue(String propertyName) {
    return eventValues.get(propertyName);
  }

  public void record(XulEventSource source) {
    source.addPropertyChangeListener(new PropertyChangeListener() {


      public void propertyChange(PropertyChangeEvent evt) {
        if (logging) {
          System.out.println("Recording event for property \"" + evt.getPropertyName() + "\" with value ["
              + evt.getNewValue() + ']');
        }
        eventValues.put(evt.getPropertyName(), evt.getNewValue());
      }
    });
  }

  public void reset() {
    eventValues.clear();
  }

}

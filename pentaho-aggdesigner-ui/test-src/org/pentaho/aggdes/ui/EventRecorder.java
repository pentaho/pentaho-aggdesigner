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

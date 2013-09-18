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

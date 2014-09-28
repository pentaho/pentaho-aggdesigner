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

package org.pentaho.aggdes.ui.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A generic version of <code>PropertyChangeSupport</code>.
 *
 * <p>Encourages thread-safe listener management and event firing. Use
 * <code>getListeners</code> in your "notify listeners" methods.
 *
 * <p>TODO move this class into pentaho commons
 */
public class EventSupport<T> {
  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(EventSupport.class);

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public EventSupport() {
    super();
  }

  // ~ Methods =========================================================================================================

  private HashSet<T> listeners = new HashSet<T>();

  public synchronized void addListener(final T listener) {
    listeners.add(listener);
  }

  public synchronized void removeListener(final T listener) {
    listeners.remove(listener);
  }

  public Set<T> getListeners() {
    Set<T> targets;
    synchronized (this) {
      targets = (Set<T>) listeners.clone();
    }
    return targets;
  }

}

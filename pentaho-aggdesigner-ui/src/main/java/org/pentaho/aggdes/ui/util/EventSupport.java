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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A generic version of <code>PropertyChangeSupport</code>. Encourages thread-safe listener management and event
 * firing. Use <code>getListeners</code> in your "notify listeners" methods.
 *
 * TODO move this class into pentaho commons
 *
 * @author mlowery
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

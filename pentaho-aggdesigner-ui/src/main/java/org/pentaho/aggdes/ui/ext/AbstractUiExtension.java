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

import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

public abstract class AbstractUiExtension extends AbstractXulEventHandler implements UiExtension {

  /* (non-Javadoc)
   * @see org.pentaho.aggdes.ui.ext.ExtendedUserInterfaceP#onLoad()
   */
  public abstract void onLoad();
  
  /* (non-Javadoc)
   * @see org.pentaho.aggdes.ui.ext.ExtendedUserInterfaceP#onLoad()
   */
  public abstract void onUnload();
  
  /* (non-Javadoc)
   * @see org.pentaho.aggdes.ui.ext.ExtendedUserInterfaceP#getOverlayPath()
   */
  public abstract String getOverlayPath();

}

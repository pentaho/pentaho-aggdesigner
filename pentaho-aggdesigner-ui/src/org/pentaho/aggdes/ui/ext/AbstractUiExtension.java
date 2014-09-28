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

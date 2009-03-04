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
package org.pentaho.aggdes.ui.xulstubs;

import org.pentaho.ui.xul.mock.XulDialogAdapter;

public class XulDialogStub extends XulDialogAdapter {

  private Object lock = new Object();

  private boolean visible;

  public void hide() {
    synchronized (lock) {
      lock.notify();
      this.visible = false;
    }
  }

  public boolean isHidden() {
    return !visible;
  }

  public void setVisible(boolean visible) {
    if (visible) {
      show();
    } else {
      hide();
    }
  }

  public void show() {
    synchronized (lock) {
      this.visible = true;
      try {
        lock.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  public String getOnblur() {
    return null;
  }

  public void onDomReady() {
    // TODO Auto-generated method stub
    
  }
}
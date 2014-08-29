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

package org.pentaho.aggdes.ui.xulstubs;

import org.pentaho.ui.xul.binding.BindingProvider;
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

  public Boolean getResizable() {
    return null;
  }

  public void setResizable(Boolean resizable) {

  }

  public void setModal(Boolean modal) {

  }

  public void suppressLayout(boolean suppress) {

  }
  
  public void setManagedObject(Object managed) {}

	public void setAppicon(String icon) {
		// TODO Auto-generated method stub
		
	}

	public String getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDrageffect() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOndrag() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOndrop() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPopup() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSpacing() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setContext(String id) {
		// TODO Auto-generated method stub
		
	}

	public void setDrageffect(String drageffect) {
		// TODO Auto-generated method stub
		
	}

	public void setMenu(String id) {
		// TODO Auto-generated method stub
		
	}

	public void setOndrag(String ondrag) {
		// TODO Auto-generated method stub
		
	}

	public void setOndrop(String ondrop) {
		// TODO Auto-generated method stub
		
	}

	public void setPopup(String id) {
		// TODO Auto-generated method stub
		
	}

	public void setSpacing(int spacing) {
		// TODO Auto-generated method stub
		
	}

  public boolean isPack() {
    return false;
  }
	
  public void setPack(boolean pack) {
  }

  public void setBindingProvider(BindingProvider bindingProvider) {
  }

	public String getDropvetoer() {
		return null;
	}

	public void setDropvetoer(String dropVetoMethod) {
	}
}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.aggdes.ui.xulstubs;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomException;
import org.pentaho.ui.xul.binding.BindingProvider;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.dom.Attribute;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.dom.Namespace;

public class XulMessageBoxStub implements XulMessageBox {

  public static List<XulMessageBox> openedMessageBoxes = new ArrayList<XulMessageBox>();
  public static int returnCode = 0;
  
  String message;
  
  public Object[] getButtons() { return null; }
  public int getHeight() { return 0; }
  public Object getIcon() { return null; }
  public String getMessage() { return message; }
  public String getTitle() { return null; }
  public int getWidth() { return 0; }
  public int open() { 
    openedMessageBoxes.add(this);
    return returnCode; 
  }
  public void setButtons(Object[] buttons) {}
  public void setHeight(int height) {}
  public void setIcon(Object icon) {}
  public void setMessage(String message) {
    this.message = message;
  }
  public void setScrollable(boolean scroll) {}
  public void setTitle(String title) {}
  public void setWidth(int width) {}
  public void addPropertyChangeListener(PropertyChangeListener listener) {}
  public int getFlex() {return 0; }
  public String getID() {return null; }
  public String getId() {return null; }
  public Object getManagedObject() {return null; }
  public void setManagedObject(Object managed) {}
  public String getName() {return null; }
  public boolean isDisabled() {return false; }
  public void removePropertyChangeListener(PropertyChangeListener listener) {}
  public void setDisabled(boolean disabled) {}
  public void setFlex(int flex) {}
  public void setID(String id) {}
  public void setId(String id) {}
  public void setOnblur(String method) {}
  public void addChild(Element element) {}
  public void addChildAt(Element element, int idx) {}
  public String getAttributeValue(String attributeName) { return null; }
  public List<Attribute> getAttributes() {return null; }
  public List<XulComponent> getChildNodes() {return null; }
  public Document getDocument() {return null; }
  public XulComponent getElementById(String id) {return null; }
  public XulComponent getElementByXPath(String path) {return null; }
  public Object getElementObject() { return null; }
  public List<XulComponent> getElementsByTagName(String tagName) { return null; }
  public XulComponent getFirstChild() { return null; }
  public Namespace getNamespace() { return null; }
  public XulComponent getParent() { return null; }
  public String getText() { return null; }
  public void removeChild(Element element) {}
  public void replaceChild(XulComponent oldElement, XulComponent newElement) throws XulDomException {}
  public void setAttribute(Attribute attribute) {}
  public void setAttribute(String name, String value) {}
  public void setAttributes(List<Attribute> attribute) {}
  public void setNamespace(String prefix, String uri) {}
  public void setModalParent(Object parent) {}
  public String getTooltiptext() {return null;}
  public void setTooltiptext(String tooltip) {}
  public String getBgcolor() {return null;}
  public void setBgcolor(String bgcolor) {}
  public int getPadding() {return -1;}
  public void setPadding(int padding) {}
  public String getOnblur() {return null;}
  public void adoptAttributes(XulComponent component) {
    
        // TODO Auto-generated method stub 
      
  }
  public String getInsertafter() {
    return null;
  }
  public String getInsertbefore() {
    return null;
  }
  public int getPosition() {
    return 0;
  }
  public boolean getRemoveelement() {
    return false;
  }
  public boolean isVisible() {
    return true;
  }
  public void setInsertafter(String id) {
    
        // TODO Auto-generated method stub 
      
  }
  public void setInsertbefore(String id) {
    
        // TODO Auto-generated method stub 
      
  }
  public void setPosition(int pos) {
    
        // TODO Auto-generated method stub 
      
  }
  public void setRemoveelement(boolean flag) {
    
        // TODO Auto-generated method stub 
      
  }
  public void setVisible(boolean visible) {
    
        // TODO Auto-generated method stub 
      
  }
  public void onDomReady() {
    // TODO Auto-generated method stub
    
  }

  public void setAcceptLabel(String label) {
  }

  public String getAlign() {
    return null;
  }

  public void setAlign(String align) {
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

  public void setBindingProvider(BindingProvider bindingProvider) {
    
  }
	public String getDropvetoer() {
		return null;
	}
	public void setDropvetoer(String dropVetoMethod) {
	}
}

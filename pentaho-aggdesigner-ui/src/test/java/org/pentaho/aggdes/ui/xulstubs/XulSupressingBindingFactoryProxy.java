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

import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingExceptionHandler;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBinding;
import org.pentaho.ui.xul.binding.Binding.Type;
import org.pentaho.ui.xul.dom.Document;


public class XulSupressingBindingFactoryProxy implements BindingFactory {
  
  private static BindingTestBean a = new BindingTestBean();
  private static BindingTestBean b = new BindingTestBean();
  private static final Binding DUMMY_BINDING = new DefaultBinding(a, "property1", b, "property2");
  
  {
    DUMMY_BINDING.initialize();
  }
  
  private BindingFactory proxiedBindingFactory;
  
  public void setProxiedBindingFactory(BindingFactory proxiedBindingFactory) {
    this.proxiedBindingFactory = proxiedBindingFactory;
  }

  public Binding createBinding(String sourceId, String sourceAttr, String targetId, String targetAttr,
      BindingConvertor... converters) {
    return DUMMY_BINDING;
  }

  public Binding createBinding(Object source, String sourceAttr, String targetId, String targetAttr,
      BindingConvertor... converters) {
    return DUMMY_BINDING;
  }

  public Binding createBinding(String sourceId, String sourceAttr, Object target, String targetAttr,
      BindingConvertor... converters) {
    return DUMMY_BINDING;
  }

  public Binding createBinding(Object source, String sourceAttr, Object target, String targetAttr,
      BindingConvertor... converters) {
    return proxiedBindingFactory.createBinding(source, sourceAttr, target, targetAttr, converters);
  }
  
  public void setBindingType(Type type) {
    proxiedBindingFactory.setBindingType(type);
  }

  public void setDocument(Document document) {
    //do nothing, we are ignoring all xul-specific behavior
  }

	public void setExceptionHandler(BindingExceptionHandler arg0) {
		// TODO Auto-generated method stub
		
	}

}    

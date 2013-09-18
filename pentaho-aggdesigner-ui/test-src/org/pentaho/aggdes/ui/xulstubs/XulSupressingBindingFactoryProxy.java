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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

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

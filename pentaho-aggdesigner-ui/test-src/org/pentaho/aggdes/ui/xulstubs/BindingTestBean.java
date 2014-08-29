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

import java.util.List;

import org.pentaho.ui.xul.XulEventSourceAdapter;

public class BindingTestBean extends XulEventSourceAdapter {

  private String property1, property2;

  private List<String> listProperty;
  
  private List<BindingTestBean> bindingBeans;

  public List<BindingTestBean> getBindingBeans() {
    return bindingBeans;
  }

  public void setBindingBeans(List<BindingTestBean> bindingBeans) {
    this.bindingBeans = bindingBeans;
    firePropertyChange("bindingBeans", null, bindingBeans);
  }

  public List<String> getListProperty() {
    return listProperty;
  }

  public void setListProperty(List<String> listProperty) {
    this.listProperty = listProperty;
    firePropertyChange("listProperty", null, listProperty);
  }

  public String getProperty1() {
    return property1;
  }

  public void setProperty1(String property1) {
    Object oldVal = this.property1;
    this.property1 = property1;
    firePropertyChange("property1", oldVal, property1);
  }

  public String getProperty2() {
    return property2;
  }

  public void setProperty2(String property2) {
    Object oldVal = this.property2;
    this.property2 = property2;
    firePropertyChange("property2", oldVal, property1);
  }
}

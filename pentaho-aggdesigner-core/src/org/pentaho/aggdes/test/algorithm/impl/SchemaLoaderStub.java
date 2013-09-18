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

package org.pentaho.aggdes.test.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.SchemaLoader;
import org.pentaho.aggdes.model.ValidationMessage;

/**
 * Stub implementation for <code>SchemaLoader</code>. Provides canned answers and some methods are not implemented.
 * 
 * @author mlowery
 */
public class SchemaLoaderStub implements SchemaLoader {

  public Schema createSchema(Map<Parameter, Object> parameterValues) {
    return new SchemaStub();
  }

  public List<ValidationMessage> validateSchema(Map<Parameter, Object> parameterValues) {
    return Collections.emptyList();
  }

  public String getName() {
    return getClass().getSimpleName();
  }

  public List<Parameter> getParameters() {
    Parameter param = new Parameter() {

      public String getDescription() {
        return "Description";
      }

      public String getName() {
        return "cube";
      }

      public Type getType() {
        return Type.STRING;
      }

      public boolean isRequired() {
        return false;
      }
      
    };
    List<Parameter> list = new ArrayList<Parameter>();
    list.add(param);
    return list;
  }

}

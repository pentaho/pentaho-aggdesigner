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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.model;

import java.util.List;
import java.util.Map;


/**
 * Aggregate designer component that loads a schema.
 *
 * @author jhyde
 * @version $Id: SchemaLoader.java 61 2008-03-17 05:34:55Z jhyde $
 * @since Mar 14, 2008
 */
public interface SchemaLoader extends Component {
    /**
     * Creates a Schema.
     *
     * @param parameterValues Map of parameter values
     * @return Schema
     */
    Schema createSchema(Map<Parameter, Object> parameterValues);
    
    /**
     * Validates a Schema.
     * 
     * @param parameterValues Map of parameter values
     * @return list of validation messages
     */
    List<ValidationMessage> validateSchema(Map<Parameter, Object> parameterValues);
}

// End SchemaLoader.java

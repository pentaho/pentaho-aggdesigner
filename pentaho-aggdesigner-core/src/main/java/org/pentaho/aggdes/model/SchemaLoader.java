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

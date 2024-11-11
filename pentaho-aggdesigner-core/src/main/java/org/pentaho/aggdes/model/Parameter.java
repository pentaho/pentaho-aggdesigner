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

/**
 * Definition of a parameter for a component of an aggregate designer
 * algorithm.
 *
 * <p>Implementations of components typically declare the parameters that they
 * accept using an <code>enum</code> that also implements this interface.
 *
 * @author jhyde
 * @version $Id: Parameter.java 61 2008-03-17 05:34:55Z jhyde $
 * @since Mar 14, 2008
 */
public interface Parameter {
    /**
         * Returns whether the parameter is required.
     *
     * @return whether Parameter is required
     */
    boolean isRequired();

    /**
         * Returns the of this Parameter.
     *
     * @return type of this parameter.
     */
    Type getType();

    /**
     * Returns the description of this Parameter.
     *
     * @return description of Parameter
     */
    String getDescription();

    /**
     * Returns the name of this Parameter.
     *
     * @return name of this Parameter
     */
    String getName();

    enum Type {
        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN
    }
}

// End Parameter.java

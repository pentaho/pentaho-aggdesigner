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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

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

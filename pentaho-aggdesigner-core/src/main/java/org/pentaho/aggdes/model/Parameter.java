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

package org.pentaho.aggdes.model;

/**
 * Definition of a parameter for a component of an aggregate designer
 * algorithm.
 *
 * <p>Implementations of components typically declare the parameters that they
 * accept using an <code>enum</code> that also implements this interface.
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

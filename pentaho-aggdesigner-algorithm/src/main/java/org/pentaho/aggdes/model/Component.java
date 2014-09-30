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

import java.util.List;


/**
 * Component of an algorithm.
 *
 * <p>Components include: schema loader, algorithm.
 */
public interface Component {
    /**
     * Returns a name for this component.
     *
     * @return name for this component
     */
    String getName();

    /**
     * Declares the parameters that this component accepts.
     *
     * @return list of parameters that this component accepts
     */
    List<Parameter> getParameters();
}

// End Component.java

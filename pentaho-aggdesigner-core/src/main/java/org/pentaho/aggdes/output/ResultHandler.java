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

package org.pentaho.aggdes.output;

import org.pentaho.aggdes.algorithm.*;
import org.pentaho.aggdes.model.Component;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;

import java.util.Map;
import java.util.List;

/**
 * Handles the results of a run of the aggregate designer algorithm.
 *
 * <p>A handler is particularly useful if you are running the algorithm in
 * batch mode, for example from the {@link org.pentaho.aggdes.Main}
 * command-line tool. If you are invoking the algorithm programmatically, you
 * can take the {@link org.pentaho.aggdes.algorithm.Result}
 * object and process it as you wish.
 *
 * <p>{@code ResultHandler} implements
 * {@link org.pentaho.aggdes.model.Component} so that it has the same
 * understanding of parameters as other components used by the command-line
 * tool.
 *
 * @author jhyde
 * @version $Id: ResultHandler.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 15, 2008
 */
public interface ResultHandler extends Component {
    /**
     * Handles a result of a run of the aggregate design algorithm. Appends
     * commands to the list.
     *
     * @param parameterValues Parameter values
     * @param schema Schema
     * @param result Result
     */
    void handle(
        Map<Parameter, Object> parameterValues,
        Schema schema, Result result);
}

// End ResultHandler.java

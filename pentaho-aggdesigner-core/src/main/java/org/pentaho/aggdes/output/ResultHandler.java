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

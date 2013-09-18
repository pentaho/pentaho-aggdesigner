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

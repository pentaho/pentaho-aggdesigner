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

package org.pentaho.aggdes.algorithm;

import java.util.*;
import java.io.PrintWriter;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Component;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;

/**
 * Algorithm that takes a star schema and recommends a set of aggregate tables.
 *
 * @author jhyde
 * @version $Id: Algorithm.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Apr 25, 2006
 */
public interface Algorithm extends Component {

    /**
     * Runs the optimization algorithm to produces a set of recommended
     * aggregate tables.
     *
     * <p>The allowable parameters are specified by the
     * {@link #getParameters()} method.
     *
     * <p>Returns <code>null</code> if another thread
     * called {@link #cancel()} and there is no useful result.
     *
     * @param schema Schema
     *
     * @param parameterValues Parameter values
     *
     * @param progress Progress callback
     * 
     * @return Set of recommended aggregate tables, or null if another thread
     * called {@link #cancel()} and there is no useful result
     */
    Result run(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        Progress progress);

    /**
     * Cancels a run.
     *
     * <p>Call this method from another thread.
     *
     * <p>Some algorithms are able to return a result if they are canceled
     * before they run to completion; if so, the {@link #run} method will
     * return that result; otherwise {@link #run} returns null.
     */
    void cancel();

    /**
     * Creates an Aggregate.
     *
     * @param schema Schema object
     * @param attributeList List of attributes
     * @return Aggregate
     */
    Aggregate createAggregate(Schema schema, List<Attribute> attributeList);

    /**
     * Computes cost/benefit metrics for a list of Aggregates.
     *
     * <p>The aggregates must have been created using the
     * {@link #createAggregate(java.util.List)} method.
     *
     * <p>The order of the list is important: the benefit of the i<sup>th</sup>
     * aggregate is its benefit over the previous aggregates 0, ... i - 1.
     *
     * <p>This method is not thread safe; you must not call it while calling
     * another method on this Algorithm object. The Algorithm object may or
     * may not have been previously used to generate a set of aggregates
     * (see {@link #run}).
     *
     * @param schema Schema
     *
     * @param parameterValues Parameter values
     *
     * @param aggregateList List of aggregates
     *
     * @return list of cost/benefit for each aggregate in the list
     */
    List<CostBenefit> computeAggregateCosts(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        List<Aggregate> aggregateList);

    /**
     * Enumeration of parameters common to all algorithms.
     */
    enum ParameterEnum implements Parameter {
        timeLimitSeconds(
            "Maximum time, in seconds, to run the algorithm. After this time,"
                + " the algorithm returns the best solution it has found so"
                + " far.", false, Type.INTEGER),

        aggregateLimit(
            "Maximum number of aggregates to create", false, Type.INTEGER),

        costLimit(
            "Maximum total cost of the aggregates produced.", false, Type.DOUBLE);

        private final String description;
        private final boolean required;
        private final Type type;

        ParameterEnum(
            String description, boolean required, Type type)
        {
            this.description = description;
            this.required = required;
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public Type getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name();
        }
    }

    /**
     * Collection of metrics describing the cost and benefit of instantiating
     * a particular {@link Aggregate}.
     *
     * <p>The cost metrics relate to the space and time investment required to
     * add the aggregate to the system. The current cost metrics are
     * ({@link #getRowCount()}, {@link #getSpace()} and
     * {@link #getLoadTime()}), and their interpretation is obvious.
     *
     * <p>The benefit metrics apply are the incremental benefit to the system
     * for this aggregate existing. Generally benefits are all about reduced
     * query time. Thus for aggregate #n, the benefit is the
     * benefit having aggregates {1, ..., n - 1, n} compared to the benefit of
     * having aggregates {1, ..., n - 1}. It is clear that the order of
     * aggregates is important.
     *
     * <p>Benefit metrics also assume a particular query load. The query load
     * may be drawn from past observed queries; or a theoretical load assuming,
     * say, that all queries with N attributes are equally likely; or a mixture
     * of the two. In any case, the query load is a theoretical model, because
     * the actual queries can not be known in advance, and tends to be implicit
     * from a particular choice of algorithm.
     *
     * <p>The sole benefit metric at this time is
     * {@link #getSavedQueryRowCount}.
     */
    interface CostBenefit {
        /**
         * Returns an estimate of the number of rows in this aggregate.
         *
         * @return estimated number of rows
         */
        double getRowCount();

        /**
         * Returns an estimate of the number of bytes required to store this
         * aggregate on disk. This includes space for secondary structures such
         * as indexes.
         *
         * @return estated number of bytes
         */
        double getSpace();

        /**
         * Returns an estimate of the number of seconds required to load this
         * aggregate.
         *
         * <p>This estimate is for a full load of an aggregate from empty; a
         * related metric, not currently supported, would describe the effort
         * required to incrementally maintain the aggregate during typical
         * operation.
         *
         * @return estimated load time
         */
        double getLoadTime();

        /**
         * Returns the number of rows that do not need to be read in a typical
         * query because this aggregate exists.
         *
         * <p>Suppose that there are 6 possible queries, and only 2 of them
         * could use this aggregate.
         *
         * <table>
         * <tr>
         *   <th>Query</th>
         *   <th>Rows</th>
         *   <th>Rows read without aggregate</th>
         *   <th>Rows read with aggregate<th>
         *   <th>Incremental Benefit</th>
         * </tr>
         * <tr><td>Q1</td><td> 100</td><td> 100</td><td> 100</td><td>   0</td></tr>
         * <tr><td>Q2</td><td> 200</td><td> 200</td><td>  40</td><td> 160</td></tr>
         * <tr><td>Q3</td><td> 300</td><td> 300</td><td> 300</td><td>   0</td></tr>
         * <tr><td>Q4</td><td>1000</td><td> 200</td><td>  10</td><td> 190</td></tr>
         * <tr><td>Q5</td><td> 500</td><td>  25</td><td>  25</td><td>   0</td></tr>
         * </table>
         *
         * <p>Queries Q1 and Q3 are not helped by this or any aggregate; their
         * benefit is 0. Query Q5 is helped by a previous aggregate, but not
         * further helped by this one; its benefit is 0. This aggregate helps
         * reduce Q2 from 200 rows to 40 rows, giving a benefit of 160 rows.
         * An aggregate has improved Q2 from 1000 rows to 200, and this
         * aggregate further improves the row count to 10, giving a benefit of
         * 190.</p>
         *
         * <p>The expected benefit of this aggreate is the average benefit over
         * all queries. For this example we assume that all queries are equally
         * likely, so the expected benefit is (0 + 160 + 0 + 190 + 0)/ 5 = 70
         * rows per query.
         *
         * @return number of row reads saved by this aggregate
         */
        double getSavedQueryRowCount();

        /**
         * Describes this cost/benefit metric.
         *
         * @param pw Print writer
         */
        void describe(PrintWriter pw);
    }
}

// End Algorithm.java

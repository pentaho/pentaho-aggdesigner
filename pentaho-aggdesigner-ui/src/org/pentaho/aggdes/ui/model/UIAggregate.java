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

package org.pentaho.aggdes.ui.model;

import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.output.Output;

/**
 * This interface extends core model Aggregates and allows for custom
 * creation of aggregates.
 */
public interface UIAggregate extends Aggregate {

    /**
     * Aggregate Name
     *
     * @return name
     */
    public String getName();

    /**
     * set the Aggregate name
     *
     * @param name aggregate name
     */
    public void setName(String name);

    /**
     * set the Aggregate description
     *
     * @param description
     */
    public void setDescription(String description);

    /**
     * set the attributes for this aggregate
     *
     * @param attributes list of attributes
     */
    public void setAttributes(List<Attribute> attributes);

    /**
     * set the measures for this aggregate
     *
     * @param attributes list of attributes
     */
    public void setMeasures(List<Measure> measures);

    /**
     * set the calculated estimated row count
     *
     * @param estimateRowCount the estimated row count of this aggregate
     */
    public void setEstimateRowCount(double estimateRowCount);

    /**
     * set the calculated estimated space
     *
     * @param estimateSpace the estimated space used by this aggregate
     */
    public void setEstimateSpace(double estimateSpace);

    /**
     * set the list of outputs related to this aggregate
     *
     * @param output the output pojo object
     */
    public void setOutput(Output output);

    /**
     * get the list of outputs related to this aggregate
     *
     * @return output list
     */
    public Output getOutput();

    /**
     * return true if this is an algorithm recommended aggregate
     * @return
     */
    public boolean isAlgoAgg();

    /**
     * sets the algorithm aggregate flag
     *
     * @param algoAgg
     */
    public void setAlgoAgg(boolean algoAgg);

    /**
     * set the enabled flag for this aggregate
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled);

    /**
     * gets the enabled state of this aggregate
     *
     * @return enabled state
     */
    public boolean getEnabled();
}

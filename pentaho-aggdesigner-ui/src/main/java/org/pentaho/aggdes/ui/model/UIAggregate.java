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

package org.pentaho.aggdes.ui.model;

import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.output.Output;

/**
 * This interface extends core model Aggregates and allows for custom
 * creation of aggregates. 
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
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

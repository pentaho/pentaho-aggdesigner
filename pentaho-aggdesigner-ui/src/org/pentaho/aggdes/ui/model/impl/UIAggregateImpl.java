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

package org.pentaho.aggdes.ui.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.ui.model.UIAggregate;

/**
 * This class defines a custom Aggregate
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class UIAggregateImpl implements UIAggregate {

    boolean algoAgg = false;
    String name;
    String description;
    List<Attribute> attributes;
    Output output;
    List<Measure> measures;
    private boolean enabled = true;
    double estimateRowCount = 0;
    double estimateSpace = 0;
    
    public UIAggregateImpl() {
      this("", "", new ArrayList<Attribute>(), new ArrayList<Measure>());
    }
    
    public UIAggregateImpl(String name, String description, List<Attribute> attributes) {
        this(name, description, attributes, new ArrayList<Measure>());
    }
    
    public UIAggregateImpl(String name, String description, List<Attribute> attributes, List<Measure> measures) {
      this.name =  name;
      this.description = description;
      this.attributes = attributes;
      this.measures = measures;
    }
    
    public String getName() {
        return name;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEstimateRowCount(double estimateRowCount) {
      this.estimateRowCount = estimateRowCount;
    }
    
    public double estimateRowCount() {
        return estimateRowCount;
    }

    public void setEstimateSpace(double estimateSpace) {
      this.estimateSpace = estimateSpace;
    }
    
    public double estimateSpace() {
        return estimateSpace;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public List<Measure> getMeasures() {
        return measures;
    }
    
    public void setMeasures(List<Measure> measures) {
      this.measures = measures;
  }

    public String getDescription() {
        return description;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public String getCandidateTableName() {
        return name;
    }

    public boolean isAlgoAgg() {
        return algoAgg;
    }

    public void setAlgoAgg(boolean algoAgg) {
        this.algoAgg = algoAgg;
    }

    public boolean getEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

}

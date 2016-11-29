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

package org.pentaho.aggdes.test.util;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.algorithm.*;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;

public class TestAggregate implements Aggregate {
    List<Attribute> attributeList = new ArrayList<Attribute>();
    List<Measure> measureList = new ArrayList<Measure>();
    String candidateTableName;

    public TestAggregate() {
    }
    
    public void addAttribute(Attribute attrib) {
        attributeList.add(attrib);
    }
    
    public void addMeasure(Measure measure) {
        measureList.add(measure);
    }
    
    public double estimateRowCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double estimateSpace() {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<Attribute> getAttributes() {
        // TODO Auto-generated method stub
        return attributeList;
    }

    public List<Measure> getMeasures() {
        // TODO Auto-generated method stub
        return measureList;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCandidateTableName() {
        return candidateTableName;
    }
    
    public void setCandidateTableName(String candidateTableName) {
        this.candidateTableName = candidateTableName;
    }
}

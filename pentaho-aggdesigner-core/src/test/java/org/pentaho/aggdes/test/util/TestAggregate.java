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

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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.model.Aggregate;

public class TestResult implements Result {
    List<Aggregate> agglist = new ArrayList<Aggregate>();
    
    public TestResult() {
    }
    
    public void addAggregate(Aggregate aggregate) {
        agglist.add(aggregate);
    }
    
    public void describe(PrintWriter pw) {
        // TODO Auto-generated method stub
    }

    public List<Aggregate> getAggregates() {
        return agglist;
    }

    public List<Algorithm.CostBenefit> getCostBenefits() {
        throw new UnsupportedOperationException();
    }
}

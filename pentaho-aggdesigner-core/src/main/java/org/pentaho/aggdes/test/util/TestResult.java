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

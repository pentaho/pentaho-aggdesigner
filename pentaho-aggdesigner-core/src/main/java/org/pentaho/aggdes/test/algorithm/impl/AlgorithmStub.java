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

package org.pentaho.aggdes.test.algorithm.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.algorithm.Progress;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dialect;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.Table;

/**
 * Stub implementation for <code>Algorithm</code>. Provides canned answers and some methods are not implemented.
 * 
 * @author mlowery
 */
public class AlgorithmStub implements Algorithm {

  private boolean canceled;

  public void cancel() {
    canceled = true;
  }

  public Aggregate createAggregate(Schema schema, List<Attribute> attributeList) {
    return null;
  }

  public List<CostBenefit> computeAggregateCosts(
    Schema schema,
    Map<Parameter, Object> parameterValues,
    List<Aggregate> aggregateList)
  {
    return null;
  }

    public Result run(Schema schema, Map<Parameter, Object> parameterValues, Progress progress) {
    canceled = false;
    int i = 0;
    while (!canceled && i < 3) {
      try {
        System.out.println("algorithm running");
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        i++;
      }
    }
    if (canceled) {
      System.out.println("algorithm canceled");
      return null;
    } else {
      System.out.println("algorithm ended normally");
      return new ResultStub();
    }
    
  }

  public String getName() {
    return getClass().getSimpleName();
  }

  public List<Parameter> getParameters() {
    Parameter param = new Parameter() {

      public String getDescription() {
        return "Description";
      }

      public String getName() {
        return "execTime";
      }

      public Type getType() {
        return Type.INTEGER;
      }

      public boolean isRequired() {
        return false;
      }
      
    };
    List<Parameter> list = new ArrayList<Parameter>();
    list.add(param);
    return list;
  }

  public static class ResultStub implements Result {

    public void describe(PrintWriter pw) {
      // TODO Auto-generated method stub

    }

    public List<Aggregate> getAggregates() {
      List<Aggregate> aggs = new ArrayList<Aggregate>();
      aggs.add(new AggregateStub());
      aggs.add(new AggregateStub());
      aggs.add(new AggregateStub());
      return aggs;
    }

    public List<CostBenefit> getCostBenefits() {
      return null;
    }
  }

  public static class AggregateStub implements Aggregate {

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
      return null;
    }

    public String getDescription() {
      return "description" + Math.random();
    }

    public List<Measure> getMeasures() {
      List<Measure> measures = new ArrayList<Measure>();
      measures.add(new MeasureStub());
      measures.add(new MeasureStub());
      return measures;
    }

    public String getCandidateTableName() {
        // TODO Auto-generated method stub
        return null;
    }

  }

  public static class MeasureStub implements Measure {

    public boolean isDistinct() {
      // TODO Auto-generated method stub
      return false;
    }

    public double estimateSpace() {
      // TODO Auto-generated method stub
      return 0;
    }

    public String getCandidateColumnName() {
      // TODO Auto-generated method stub
      return null;
    }

    public String getDatatype(Dialect dialect) {
      // TODO Auto-generated method stub
      return null;
    }

    public String getLabel() {
      return "label" + Math.random();
    }

    public Table getTable() {
      // TODO Auto-generated method stub
      return null;
    }

    public String toString() {
      return getLabel();
    }

    public List<Attribute> getAncestorAttributes() {
      // TODO Auto-generated method stub
      return null;
    }
  }
}

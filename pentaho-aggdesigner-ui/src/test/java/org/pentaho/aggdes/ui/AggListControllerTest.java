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

package org.pentaho.aggdes.ui;

import java.awt.Image;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.ui.ext.AlgorithmUiExtension;
import org.pentaho.aggdes.ui.form.controller.AggListController;
import org.pentaho.aggdes.ui.form.model.AggregateSummaryModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.components.XulImage;
import org.pentaho.ui.xul.dom.Document;

@RunWith(JMock.class)
public class AggListControllerTest {

  private static final Log logger = LogFactory.getLog(AggListControllerTest.class);

  private Mockery context;

  private Document doc;

  private XulDomContainer container;

  private AggListController controller;

  private AggList aggList;

  private OutputService outputService;

  private Workspace workspace;

  private ConnectionModel connModel;

  private Schema schema;

  private AlgorithmUiExtension uiExt;
  
  private Algorithm algo;
  
  private AggregateSummaryModel aggregateSummaryModel;

  @Before
  public void setUp() throws Exception {
    controller = new AggListController();
    context = new JUnit4Mockery() {
      {
        // only here to mock types that are not interfaces
        setImposteriser(ClassImposteriser.INSTANCE);
      }
    };
    doc = context.mock(Document.class);
    container = context.mock(XulDomContainer.class);
    outputService = context.mock(OutputService.class);
    workspace = context.mock(Workspace.class);
    connModel = context.mock(ConnectionModel.class);
    schema = context.mock(Schema.class);
    uiExt = context.mock(AlgorithmUiExtension.class);
    algo = context.mock(Algorithm.class);
    aggregateSummaryModel = context.mock(AggregateSummaryModel.class);

    aggList = new AggListImpl();

    controller.setOutputService(outputService);
    controller.setAggList(aggList);
    controller.setAlgorithmUiExtension(uiExt);
    controller.setAlgorithm(algo);

    // need some expectations here as setXulDomContainer calls getDocumentRoot on the container
    context.checking(new Expectations() {
      {
        one(container).getDocumentRoot();
        will(returnValue(doc));
      }
    });

    controller.setXulDomContainer(container);
    controller.setWorkspace(workspace);
    controller.setConnectionModel(connModel);
    controller.setAggregateSummaryModel(aggregateSummaryModel);
    
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testChangeInAggregates() {
    UIAggregateImpl agg1 = new UIAggregateImpl();
    agg1.setEnabled(true);
    agg1.setEstimateRowCount(4000);
    agg1.setEstimateSpace(22000);
    UIAggregateImpl agg2 = new UIAggregateImpl();
    agg2.setEnabled(true);
    agg2.setEstimateRowCount(4000);
    agg2.setEstimateSpace(22000);
    

    final XulImage img = context.mock(XulImage.class);
    
    
    aggList.addAgg(agg1);
    aggList.addAgg(agg2);
    context.checking(new Expectations() {
      {
        one(workspace).setWorkspaceUpToDate(false);
        one(connModel).setSchemaUpToDate(false);
        one(connModel).getSchema();
        will(returnValue(schema));
        ignoring(connModel).getSchema();
        will(returnValue(schema));
        one(uiExt).getAlgorithmParameters();
        one(algo).getParameters();
        will(returnValue(Collections.emptyList()));
        one(algo).computeAggregateCosts(with(equal(schema)), with(any(Map.class)), with(any(List.class)));
        exactly(2).of(algo).createAggregate(with(equal(schema)), with(any(List.class)));
        one(aggregateSummaryModel).setSelectedAggregateCount(with(any(String.class)));
        one(aggregateSummaryModel).setSelectedAggregateRows(with(any(String.class)));
        one(aggregateSummaryModel).setSelectedAggregateSpace(with(any(String.class)));
        one(aggregateSummaryModel).setSelectedAggregateLoadTime(with(any(String.class)));
        one(doc).getElementById(with(equal("chart")));
        will(returnValue(img));
        one(img).setSrc(with(any(Image.class)));
      }
    });
    controller.changeInAggregates();
  }

}

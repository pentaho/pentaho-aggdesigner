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

package org.pentaho.aggdes.ui;


import java.awt.Image;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.model.Aggregate;
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

@RunWith(MockitoJUnitRunner.class)
public class AggListControllerTest {

  private static final Log logger = LogFactory.getLog(AggListControllerTest.class);

  @Mock
  private Document doc;

  @Mock
  private XulDomContainer container;

  @Mock
  private OutputService outputService;

  @Mock
  private Workspace workspace;

  @Mock
  private ConnectionModel connModel;

  @Mock
  private Schema schema;

  @Mock
  private AlgorithmUiExtension uiExt;

  @Mock
  private Algorithm algo;

  @Mock
  private AggregateSummaryModel aggregateSummaryModel;

  private AggListController controller;

  private AggList aggList;

  @Before
  public void setUp() throws Exception {
    controller = new AggListController();
    doc = Mockito.mock(Document.class);
    container = Mockito.mock(XulDomContainer.class);
    outputService = Mockito.mock(OutputService.class);
    workspace = Mockito.mock(Workspace.class);
    connModel = Mockito.mock(ConnectionModel.class);
    schema = Mockito.mock(Schema.class);
    uiExt = Mockito.mock(AlgorithmUiExtension.class);
    algo = Mockito.mock(Algorithm.class);
    aggregateSummaryModel = Mockito.mock(AggregateSummaryModel.class);

    aggList = new AggListImpl();

    controller.setOutputService(outputService);
    controller.setAggList(aggList);
    controller.setAlgorithmUiExtension(uiExt);
    controller.setAlgorithm(algo);

    Mockito.when(container.getDocumentRoot()).thenReturn(doc);

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

    XulImage img = Mockito.mock(XulImage.class);

    aggList.addAgg(agg1);
    aggList.addAgg(agg2);

    Mockito.doNothing().when(workspace).setWorkspaceUpToDate(false);
    Mockito.doNothing().when(connModel).setSchemaUpToDate(false);
    Mockito.when(connModel.getSchema()).thenReturn(schema);
    Mockito.when(uiExt.getAlgorithmParameters()).thenReturn(Collections.emptyMap());
    Mockito.when(algo.getParameters()).thenReturn(Collections.emptyList());

//    Mockito.when(algo.computeAggregateCosts(schema, Collections.emptyMap(), Collections.emptyList())).thenReturn(Collections.emptyList());
    Mockito.when(algo.createAggregate(schema, Collections.emptyList())).thenReturn(Mockito.mock( Aggregate.class));

    Mockito.doNothing().when(aggregateSummaryModel).setSelectedAggregateCount(Mockito.anyString());
    Mockito.doNothing().when(aggregateSummaryModel).setSelectedAggregateRows(Mockito.anyString());
    Mockito.doNothing().when(aggregateSummaryModel).setSelectedAggregateSpace(Mockito.anyString());
    Mockito.doNothing().when(aggregateSummaryModel).setSelectedAggregateLoadTime(Mockito.anyString());

    Mockito.when(doc.getElementById("chart")).thenReturn(img);

    controller.changeInAggregates();

    Mockito.verify(workspace).setWorkspaceUpToDate(false);
    Mockito.verify(connModel).setSchemaUpToDate(false);
    Mockito.verify(connModel, Mockito.times(5)).getSchema();
    Mockito.verify(uiExt).getAlgorithmParameters();
    Mockito.verify(algo).getParameters();

  /*  algorithm.computeAggregateCosts(
      Mock for Schema, hashCode: 851033362,
      {},
    [Mock for Aggregate, hashCode: 905940937, Mock for Aggregate, hashCode: 905940937]
);*/

//    Mockito.verify(algo).computeAggregateCosts(schema, Collections.emptyMap(), Collections.emptyList());
    Mockito.verify(algo, Mockito.times(2)).createAggregate(schema, Collections.emptyList());
    Mockito.verify(aggregateSummaryModel).setSelectedAggregateCount(Mockito.anyString());
    Mockito.verify(aggregateSummaryModel).setSelectedAggregateRows(Mockito.anyString());
    Mockito.verify(aggregateSummaryModel).setSelectedAggregateSpace(Mockito.anyString());
    Mockito.verify(aggregateSummaryModel).setSelectedAggregateLoadTime(Mockito.anyString());
    Mockito.verify(doc).getElementById("chart");
    Mockito.verify(img).setSrc(Mockito.any(Image.class));
  }

}

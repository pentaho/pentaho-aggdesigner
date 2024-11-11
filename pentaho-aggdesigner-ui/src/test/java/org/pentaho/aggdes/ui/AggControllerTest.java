/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.ui;

import junit.framework.TestCase;
import org.pentaho.aggdes.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.OutputFactory;
import org.pentaho.aggdes.output.OutputValidationException;
import org.pentaho.aggdes.output.impl.AggregateTableOutputFactory;
import org.pentaho.aggdes.output.impl.OutputServiceImpl;
import org.pentaho.aggdes.ui.form.controller.AggController;
import org.pentaho.aggdes.ui.form.model.AggModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModelImpl;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.aggdes.ui.xulstubs.XulDomContainerStub;

import java.util.ArrayList;
import java.util.List;

public class AggControllerTest extends TestCase {

  public int syncToAggCalled;
  public int aggChangedCalled;
  public int generateDefaultOutputCalled;
  public int setThinAggCalled;

  public void testAggController() {
    // onload
    // apply
    // aggChanged(int)
    // resetAgg

    AggModel aggModel = new AggModel() {
      public void synchToAgg() {
        syncToAggCalled++;
      }

      public void setThinAgg( UIAggregate thinAgg ) {
        super.setThinAgg( thinAgg );
        setThinAggCalled++;
      }
    };
    AggListImpl aggList = new AggListImpl() {
      public void aggChanged( UIAggregate agg ) {
        // super.aggChanged(agg);
        aggChangedCalled++;
      }
    };
    AggController controller = new AggController();
    Schema schema = new SchemaStub();
    ConnectionModelImpl connectionModel = new ConnectionModelImpl();
    connectionModel.setSchema( schema );
    OutputServiceImpl outputService = new OutputServiceImpl() {
      public Output generateDefaultOutput( Aggregate aggregate ) throws OutputValidationException {
        generateDefaultOutputCalled++;
        return null;
      }
    };
    outputService.init( schema );
    List<OutputFactory> outputFactories = new ArrayList<OutputFactory>();
    outputFactories.add( new AggregateTableOutputFactory() );
    outputService.setOutputFactories( outputFactories );

    aggModel.setConnectionModel( connectionModel );
    assertNotNull( aggModel.getConnectionModel() );

    // setup controller
    controller.setAggModel( aggModel );
    controller.setAggList( aggList );
    controller.setOutputService( outputService );

    XulDomContainerStub xulDomContainer = new XulDomContainerStub();
    controller.setXulDomContainer( xulDomContainer );
    aggModel.getThinAgg().setName( "temp_name" );
    aggModel.setCurrentUiExtension( null );

    // test onLoad
    //FIXME: fix this test
    //    DefaultBindingFactory bindingFactory = new DefaultBindingFactory();
    //    bindingFactory.setDocument(xulDomContainer.getDocumentRoot());
    //    XulSupressingBindingFactoryProxy proxyFac = new XulSupressingBindingFactoryProxy();
    //    proxyFac.setProxiedBindingFactory(bindingFactory);
    //
    //    controller.setBindingFactory(proxyFac);
    //    controller.onLoad();
    //
    //    // verify bindings exist
    //    assertEquals(xulDomContainer.bindings.size(), 3);
    //    assertEquals(((DocumentStub)xulDomContainer.getDocumentRoot()).bindings.size(), 9);
    //
    //    // verify thinagg has been initialized to a new UI Agg
    //    assertNotNull(aggModel.getThinAgg());
    //    assertFalse(aggModel.getThinAgg().getName().equals("temp_name"));

    // tpdo: verify listener exists by triggering event and seeing it's effects

    // test apply

    controller.apply();
    assertEquals( syncToAggCalled, 1 );
    assertEquals( aggChangedCalled, 1 );
    assertEquals( generateDefaultOutputCalled, 1 );

    // test resetAgg
    setThinAggCalled = 0;
    controller.reset();
    assertEquals( setThinAggCalled, 1 );

  }
}

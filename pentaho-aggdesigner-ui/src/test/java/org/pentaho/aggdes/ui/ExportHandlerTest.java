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

package org.pentaho.aggdes.ui;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import org.pentaho.aggdes.output.CreateScriptGenerator;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.output.PopulateScriptGenerator;
import org.pentaho.aggdes.output.SchemaGenerator;
import org.pentaho.aggdes.ui.exec.SqlExecutor;
import org.pentaho.aggdes.ui.exec.SqlExecutor.ExecutorCallback;
import org.pentaho.aggdes.ui.exec.impl.JdbcTemplateSqlExecutorTest;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaModel;
import org.pentaho.aggdes.ui.form.controller.ExportHandler;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.components.XulFileDialog;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.components.XulFileDialog.RETURN_CODE;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.containers.XulWindow;
import org.pentaho.ui.xul.dom.Document;

@RunWith(JMock.class)
public class ExportHandlerTest {

  private static final Log logger = LogFactory.getLog(ExportHandlerTest.class);

  private Mockery context;

  private Document doc;

  private XulDomContainer container;

  private ExportHandler controller;

  private AggList aggList;

  private OutputService outputService;

  private SqlExecutor executor;

  private String tmpFilePath;

  @Before
  public void setUp() throws Exception {
    controller = new ExportHandler();
    context = new JUnit4Mockery() {
      {
        // only here to mock types that are not interfaces
        setImposteriser(ClassImposteriser.INSTANCE);
      }
    };
    doc = context.mock(Document.class);
    container = context.mock(XulDomContainer.class);
    outputService = context.mock(OutputService.class);
    executor = context.mock(SqlExecutor.class);

    aggList = new AggListImpl();

    controller.setOutputService(outputService);
    controller.setAggList(aggList);
    controller.setDdlDmlExecutor(executor);

    // need some expectations here as setXulDomContainer calls getDocumentRoot on the container
    context.checking(new Expectations() {
      {
        one(container).getDocumentRoot();
        will(returnValue(doc));
      }
    });

    controller.setXulDomContainer(container);

  }

  @After
  public void tearDown() throws Exception {
    if (tmpFilePath != null) {
      File f = new File(tmpFilePath);
      if (f.exists()) {
        f.delete();
      }
    }
  }

  @Test
  public void testOpenDialogNoAggs() throws Exception {
    final XulMessageBox msgBox = context.mock(XulMessageBox.class);
    context.checking(new Expectations() {
      {
        // show error messagebox stating no aggs
        one(doc).createElement(with(equal("messagebox")));
        will(returnValue(msgBox));
        ignoring(msgBox).setMessage(with(any(String.class)));
        ignoring(msgBox).setTitle(with(any(String.class)));
        one(msgBox).open();
      }
    });
    controller.openDialog();
  }

  @Test
  public void testOpenDialogNoEnabledAggs() throws Exception {
    final XulMessageBox msgBox = context.mock(XulMessageBox.class);
    context.checking(new Expectations() {
      {
        // show error messagebox stating no aggs
        one(doc).createElement(with(equal("messagebox")));
        will(returnValue(msgBox));
        ignoring(msgBox).setMessage(with(any(String.class)));
        ignoring(msgBox).setTitle(with(any(String.class)));
        one(msgBox).open();
      }
    });
    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled(false);
    aggList.addAgg(agg);
    controller.openDialog();
  }

  @Test
  public void testOpenDialogWithAggs() throws Exception {
    final XulDialog diag = context.mock(XulDialog.class);
    context.checking(new Expectations() {
      {
        // show export dialog
        one(doc).getElementById(with(any(String.class)));
        will(returnValue(diag));
        one(diag).show();
      }
    });
    aggList.addAgg(new UIAggregateImpl());
    controller.openDialog();
  }

  @Test
  public void testShowPreviewWithAggs() throws Exception {
    final XulDialog diag = context.mock(XulDialog.class);
    final XulTextbox textbox = context.mock(XulTextbox.class);
    final XulComponent ignored = context.mock(XulComponent.class);
    context.checking(new Expectations() {
      {
        // get preview dialog
        one(doc).getElementById(with(any(String.class)));
        will(returnValue(diag));
        // get ddl field
        one(doc).getElementById(with(any(String.class)));
        will(returnValue(textbox));
        one(outputService).getFullArtifact(with(any(List.class)), with(equal(CreateScriptGenerator.class)));
        one(textbox).setValue(with(any(String.class)));
        // get dml field
        one(doc).getElementById(with(any(String.class)));
        will(returnValue(textbox));
        // get dml tab (if exists)
        one(doc).getElementById(with(any(String.class)));
        // returnValue doesn't matter as long as not null
        will(returnValue(ignored));
        one(outputService).getFullArtifact(with(any(List.class)), with(equal(PopulateScriptGenerator.class)));
        one(textbox).setValue(with(any(String.class)));
        // get olap field
        one(doc).getElementById(with(any(String.class)));
        will(returnValue(textbox));
        one(outputService).getFullArtifact(with(any(List.class)), with(equal(SchemaGenerator.class)));
        one(textbox).setValue(with(any(String.class)));
        one(diag).show();
      }
    });
    aggList.addAgg(new UIAggregateImpl());
    controller.showPreview();
  }

  /**
   * Tests all of the copy* methods.
   */
  @Test
  public void testCopyToClipboard() throws Exception {
    final XulTextbox textbox = context.mock(XulTextbox.class);
    final XulWindow window = context.mock(XulWindow.class);
    final String stringToCopy = "ignored"; //$NON-NLS-1$
    context.checking(new Expectations() {
      {
        exactly(3).of(doc).getElementById(with(any(String.class)));
        will(returnValue(textbox));
        exactly(3).of(textbox).getValue();
        will(returnValue(stringToCopy));
        exactly(3).of(doc).getRootElement();
        will(returnValue(window));
        exactly(3).of(window).copy(with(equal(stringToCopy)));
      }
    });
    controller.copyDdlToClipboard();
    controller.copyDmlToClipboard();
    controller.copyToClipboardMultiDimPreview();
  }

  @Test
  public void testStartExecuteDdl() {
    final XulDialog diag = context.mock(XulDialog.class);
    context.checking(new Expectations() {
      {
        // get progress dialog
        one(doc).getElementById(with(any(String.class)));
        will(returnValue(diag));
        one(executor).execute(with(any(new String[0].getClass())), with(any(ExecutorCallback.class)));
        one(diag).show();
      }
    });
    controller.startExecuteDdl();

    // WG: The reason this test case was failing has to do with the Thread in startExecuteDdl.  
    // This test passes if given enough time to call execute() within the thread. as a temporary
    // solution, I added a one second sleep at the end of this test to give the thread time to run.

    // see this article for ideas on how to resolve this:
    // http://www.jmock.org/threads.html

    try {
      Thread.sleep(1000);
    } catch (Exception e) {
    }
  }

  @Test
  public void testStartExecuteDml() {
    final XulDialog diag = context.mock(XulDialog.class);
    context.checking(new Expectations() {
      {
        // get progress dialog
        exactly(1).of(doc).getElementById(with(any(String.class)));
        will(returnValue(diag));
        String[] sql = new String[0];
        System.err.println(sql.getClass());
        System.err.println(String.class);
        System.err.println(new String[1].getClass());
        exactly(1).of(executor).execute(with(any(new String[0].getClass())), with(any(ExecutorCallback.class)));
        one(diag).show();
      }
    });
    controller.startExecuteDml();

    // WG: The reason this test case was failing has to do with the Thread in startExecuteDml.  
    // This test passes if given enough time to call execute() within the thread. as a temporary
    // solution, I added a one second sleep at the end of this test to give the thread time to run.

    // see this article for ideas on how to resolve this:
    // http://www.jmock.org/threads.html

    try {
      Thread.sleep(1000);
    } catch (Exception e) {
    }

  }

  @Test
  public void testSaveDdl() throws Exception {
    final XulFileDialog fileBox = context.mock(XulFileDialog.class);
    final XulDialog diag = context.mock(XulDialog.class);
    final File tmpFile = File.createTempFile("whatever", ".sql");
    // save path so that it can be deleted after test
    tmpFilePath = tmpFile.getAbsolutePath();
    context.checking(new Expectations() {
      {
        // show error messagebox stating no aggs
        one(doc).createElement(with(equal("filedialog")));
        will(returnValue(fileBox));
        ignoring(doc).getElementById(with(any(String.class)));
        will(returnValue(diag));
        ignoring(diag).getRootObject();
        will(returnValue(null));
        one(fileBox).setModalParent(with(any(Object.class)));
        one(fileBox).showSaveDialog();
        will(returnValue(RETURN_CODE.OK));
        one(fileBox).getFile();
        will(returnValue(tmpFile));
      }
    });
    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled(false);
    aggList.addAgg(agg);
    controller.saveDdl();
    System.out.println(FileUtils.readFileToString(new File(tmpFilePath)));
  }

  @Test
  public void testSaveDml() throws Exception {
    final XulFileDialog fileBox = context.mock(XulFileDialog.class);
    final XulDialog diag = context.mock(XulDialog.class);
    final File tmpFile = File.createTempFile("whatever", ".sql");
    // save path so that it can be deleted after test
    tmpFilePath = tmpFile.getAbsolutePath();
    context.checking(new Expectations() {
      {
        // show error messagebox stating no aggs
        one(doc).createElement(with(equal("filedialog")));
        will(returnValue(fileBox));
        ignoring(doc).getElementById(with(any(String.class)));
        will(returnValue(diag));
        ignoring(diag).getRootObject();
        will(returnValue(null));
        one(fileBox).setModalParent(with(any(Object.class)));
        one(fileBox).showSaveDialog();
        will(returnValue(RETURN_CODE.OK));
        one(fileBox).getFile();
        will(returnValue(tmpFile));
      }
    });
    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled(false);
    aggList.addAgg(agg);
    controller.saveDml();
    System.out.println(FileUtils.readFileToString(new File(tmpFilePath)));
  }

  @Test
  public void testSaveOlap() throws Exception {
    final XulFileDialog fileBox = context.mock(XulFileDialog.class);
    final XulDialog diag = context.mock(XulDialog.class);
    final ConnectionModel connModel = context.mock(ConnectionModel.class);
    final MondrianFileSchemaModel schemaModel = context.mock(MondrianFileSchemaModel.class);
    final File tmpFile = File.createTempFile("whatever", ".sql");
    // save path so that it can be deleted after test
    tmpFilePath = tmpFile.getAbsolutePath();
    controller.setConnectionModel(connModel);
    context.checking(new Expectations() {
      {
        // show error messagebox stating no aggs
        one(doc).createElement(with(equal("filedialog")));
        will(returnValue(fileBox));
        ignoring(doc).getElementById(with(any(String.class)));
        will(returnValue(diag));
        ignoring(diag).getRootObject();
        will(returnValue(null));
        one(fileBox).setModalParent(with(any(Object.class)));
        one(fileBox).showSaveDialog(with(any(File.class)));
        will(returnValue(RETURN_CODE.OK));
        one(fileBox).getFile();
        will(returnValue(tmpFile));
        exactly(2).of(connModel).getSelectedSchemaModel();
        will(returnValue(schemaModel));
        one(schemaModel).getMondrianSchemaFilename();
        will(returnValue(tmpFile.getAbsolutePath()));
        one(outputService).getFullArtifact(with(any(List.class)), with(equal(SchemaGenerator.class)));
        one(schemaModel).setMondrianSchemaFilename(with(any(String.class)));
        one(connModel).setSchemaUpToDate(true);
      }
    });
    UIAggregate agg = new UIAggregateImpl();
    agg.setEnabled(false);
    aggList.addAgg(agg);
    controller.saveOlap();
    System.out.println(FileUtils.readFileToString(new File(tmpFilePath)));
  }

}

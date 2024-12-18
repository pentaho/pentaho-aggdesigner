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

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulLoader;
import org.pentaho.ui.xul.XulRunner;
import org.pentaho.ui.xul.impl.XulEventHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UIMain {

  private static final Log logger = LogFactory.getLog(UIMain.class);

  private UIConfiguration configuration;
  private XulLoader xulLoader;
  private XulRunner xulRunner;

  /**
   * The main startup of the Aggregation Designer
   * @param args
   */
  public static void main(String[] args) {
    try {
    	KettleClientEnvironment.init();
    	ApplicationContext context = new ClassPathXmlApplicationContext(
        new String[] {"applicationContext.xml", "plugins.xml"}); //$NON-NLS-1$ //$NON-NLS-2$

      UIMain uiMain = (UIMain) context.getBean("uiMain"); //$NON-NLS-1$
      uiMain.start(context);
    } catch (Throwable t) {
      logger.error("uncaught exception in main", t);
      System.exit(1);
    }
  }

  @SuppressWarnings("unchecked")
  public void start(ApplicationContext context) throws XulException {
    XulDomContainer container;

    //check to see if they've specified an alternate resource bundle
    String bundleStr = configuration.getResourceBundle();
    ResourceBundle bundle = null;
    if(bundleStr != null){
      try{
        bundle = ResourceBundle.getBundle(bundleStr);
      } catch (MissingResourceException e){
        logger.error("Could not load Resource Bundle: "+bundleStr); //$NON-NLS-1$
      }
    }

    //Set the look and feel based on configuration
    setLAF();

    if(bundle != null){
      container = xulLoader.loadXul("org/pentaho/aggdes/ui/resources/mainFrame.xul", bundle); //$NON-NLS-1$
    } else {
      container = xulLoader.loadXul("org/pentaho/aggdes/ui/resources/mainFrame.xul"); //$NON-NLS-1$
    }

    //generically register all Spring-initialized XulEventHandlers
    Map handlerMap = context.getBeansOfType(XulEventHandler.class);
    for(Object handler : handlerMap.values()) {
      container.addEventHandler((XulEventHandler)handler);
    }

    xulRunner.addContainer(container);
    xulRunner.initialize();
    xulRunner.start();
  }

  public void setConfiguration(UIConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setXulLoader(XulLoader xulLoader) {
    this.xulLoader = xulLoader;
  }

  public void setXulRunner(XulRunner xulRunner) {
    this.xulRunner = xulRunner;
  }

  private void setLAF() {

    try{
      String laf = configuration.getLookAndFeel();

      if(laf.equalsIgnoreCase("system")){
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } else if(laf.equalsIgnoreCase("metal")) {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } else if(!StringUtils.isEmpty(laf)){
        UIManager.setLookAndFeel(laf);
      } else {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      }

    } catch (Exception e){
      logger.warn("error setting look and feel",e); //$NON-NLS-1$
    }

  }
}

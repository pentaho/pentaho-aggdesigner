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

package org.pentaho.aggdes.test.util;

import mondrian.olap.Util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class TestUtils {

    public static final String nl = System.getProperty("line.separator");

  // ~ Static fields/initializers ============================================

  private static Properties testProperties;

  private static Map<String, Driver> registeredDrivers = new HashMap<>();

  // ~ Constructors ==========================================================

  private TestUtils() {
  }

  // ~ Methods ===============================================================

  static {
    testProperties = new Properties();
    try {
      testProperties.load(TestUtils.class.getResourceAsStream("/test.properties"));
      InputStream overrides = TestUtils.class.getResourceAsStream("/testoverride.properties");
      if(overrides != null) {
        testProperties.load(overrides);
      }
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static String getTestProperty(final String key, final Object... args) {
    return MessageFormat.format(
      System.getProperty(
        key,
        testProperties.getProperty( key ) ),
      args );
  }

  public static void registerDriver(final String jdbcDriverClasspath, final String jdbcDriverClassname)
      throws Exception {
    Driver newDriver;
    if (jdbcDriverClasspath != null && !jdbcDriverClasspath.isEmpty() ) {
      URLClassLoader urlLoader = new URLClassLoader(new URL[] { new URL(jdbcDriverClasspath) });
      Driver d = (Driver) Class.forName(jdbcDriverClassname, true, urlLoader).newInstance();
      newDriver = new DriverShim(d);
    } else {
      newDriver = (Driver) Class.forName(jdbcDriverClassname).newInstance();
    }
    DriverManager.registerDriver(newDriver);
    registeredDrivers.put(jdbcDriverClasspath + ":" + jdbcDriverClassname, newDriver);
  }

  public static void deregisterDriver(final String jdbcDriverClasspath, final String jdbcDriverClassname)
      throws Exception {
    Driver registeredDriver = registeredDrivers.get(jdbcDriverClasspath + ":" + jdbcDriverClassname);
    if (null != registeredDriver) {
      DriverManager.deregisterDriver(registeredDriver);
    }
  }

  // using http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
  // jdbc loading example
  private static class DriverShim implements Driver {
    private final Driver driver;

    DriverShim(Driver d) {
      this.driver = d;
    }

    public boolean acceptsURL(String u) throws SQLException {
      return this.driver.acceptsURL(u);
    }

    public Connection connect(String u, Properties p) throws SQLException {
      return this.driver.connect(u, p);
    }

    public int getMajorVersion() {
      return this.driver.getMajorVersion();
    }

    public int getMinorVersion() {
      return this.driver.getMinorVersion();
    }

    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
      return this.driver.getPropertyInfo(u, p);
    }

    public boolean jdbcCompliant() {
      return this.driver.jdbcCompliant();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return null;
    }
  }

  /**
   * Converts a string constant into locale-specific line endings.
   */
  public static String fold(String string) {
      if (!nl.equals("\n")) {
          string = Util.replace(string, "\n", nl);
      }
      return string;
  }
}

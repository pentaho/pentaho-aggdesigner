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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.test.util;

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

import mondrian.olap.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestUtils {

    public static final String nl = System.getProperty("line.separator");
    
  // ~ Static fields/initializers ============================================

  private static final Log logger = LogFactory.getLog(TestUtils.class);

  private static Properties testProperties;

  private static Map<String, Driver> registeredDrivers = new HashMap<String, Driver>();

  // ~ Constructors ==========================================================

  private TestUtils() {
  }

  // ~ Methods ===============================================================

  static {
    testProperties = new Properties();
    try {
      testProperties.load(TestUtils.class.getResourceAsStream("/test.properties")); //$NON-NLS-1$
      InputStream overrides = TestUtils.class.getResourceAsStream("/testoverride.properties");  //$NON-NLS-1$
      if(overrides != null) {
        testProperties.load(overrides);
      }
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static String getTestProperty(final String key, final Object... args) {
    return MessageFormat.format(testProperties.getProperty(key), args);
  }

  public static void registerDriver(final String jdbcDriverClasspath, final String jdbcDriverClassname)
      throws Exception {
    Driver newDriver;
    if (jdbcDriverClasspath != null && !jdbcDriverClasspath.equals("")) {
      URLClassLoader urlLoader = new URLClassLoader(new URL[] { new URL(jdbcDriverClasspath) });
      Driver d = (Driver) Class.forName(jdbcDriverClassname, true, urlLoader).newInstance();
      newDriver = new DriverShim(d);
    } else {
      newDriver = (Driver) Class.forName(jdbcDriverClassname).newInstance();
    }
    DriverManager.registerDriver(newDriver);
    registeredDrivers.put(jdbcDriverClasspath + ":" + jdbcDriverClassname, newDriver); //$NON-NLS-1$
  }

  public static void deregisterDriver(final String jdbcDriverClasspath, final String jdbcDriverClassname)
      throws Exception {
    Driver registeredDriver = registeredDrivers.get(jdbcDriverClasspath + ":" + jdbcDriverClassname); //$NON-NLS-1$
    if (null != registeredDriver) {
      DriverManager.deregisterDriver(registeredDriver);
    }
  }

  // using http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
  // jdbc loading example
  private static class DriverShim implements Driver {
    private Driver driver;

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

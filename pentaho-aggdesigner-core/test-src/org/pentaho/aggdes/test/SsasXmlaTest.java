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

package org.pentaho.aggdes.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.pentaho.aggdes.test.util.TestUtils;
import org.pentaho.aggdes.util.XmlaUtil;

// note: i had to run "ethtool -K eth0 sg off rx off tx off tso off" on fedora to talk to my
// windowz vmware instance
public class SsasXmlaTest extends TestCase {

  @Test @Ignore
  public void test() throws IOException {
    String request = XmlaUtil.generateXmlaDiscoverRequest("DISCOVER_XML_METADATA");
    PrintWriter pw = new PrintWriter(new FileWriter("combined_ssas.xml"));
    String xmlaUrl = TestUtils.getTestProperty("test.ssas.xmlaUrl");
    if (xmlaUrl == null || xmlaUrl.trim().length() == 0) {
      fail("xmla url not set.  set 'test.ssas.xmlaUrl' in testoverride.properties to enable this test.");
    } else {
      pw.println(new String(XmlaUtil.executeXmlaRequest(new URL(xmlaUrl), request)));
      pw.close();
    }
  }
}

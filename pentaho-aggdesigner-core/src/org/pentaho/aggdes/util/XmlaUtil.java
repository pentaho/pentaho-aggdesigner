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
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
package org.pentaho.aggdes.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import mondrian.util.Base64;

/**
 * this is a utility class which makes simple XMLA discover requests
 * note: most of this code was copied from olap4j's XMLA implementation.
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 */
public class XmlaUtil {
    
    /**
     * Generates a discover request.

     * @param requestType request type
     * @return XMLA request
     */
    public static String generateXmlaDiscoverRequest(String requestType) {
        final String encoding = "UTF-8";
        final StringBuilder buf = new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n" 
                + "<SOAP-ENV:Envelope\n"
                + "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "    SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"
                + "  <SOAP-ENV:Body>\n"
                + "    <Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n"
                + "        SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"
                + "    <RequestType>");
        buf.append(requestType);
        buf.append("</RequestType>\n"
                + "    <Restrictions>\n"
                + "      <RestrictionList/>\n"
                + "    </Restrictions>\n"
                + "    <Properties>\n"
                + "      <PropertyList/>\n"
                + "    </Properties>\n"
                + "    </Discover>\n"
                + "</SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>");
        return buf.toString();
    }

    /**
     * executes an xmla request.  supports basic auth
     * 
     * @param url url of xmla server
     * @param request
     * @return
     * @throws IOException
     */
    public static byte[] executeXmlaRequest(URL url, String request) throws IOException {
        // Open connection to manipulate the properties
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("content-type", "text/xml");

        // Encode credentials for basic authentication
        if (url.getUserInfo() != null) {
            String encoding =
                Base64.encodeBytes(url.getUserInfo().getBytes(), 0);
            urlConnection.setRequestProperty(
                "Authorization", "Basic " + encoding);
        }

        // Send data (i.e. POST). Use same encoding as specified in the
        // header.
        final String encoding = "UTF-8";
        urlConnection.getOutputStream().write(request.getBytes(encoding));

        // Get the response, again assuming default encoding.
        InputStream is = urlConnection.getInputStream();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int count;
        while ((count = is.read(buf)) > 0) {
            baos.write(buf, 0, count);
        }
        return baos.toByteArray();
    }
}

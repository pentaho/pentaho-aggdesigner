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


package org.pentaho.aggdes.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import mondrian.util.Base64;

/**
 * this is a utility class which makes simple XMLA discover requests
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class XmlaUtil {
    
    /**
     * Generates a discover request.
     * 
     * @param requestType request type
     * @return XMLA request
     */
    public static String generateXmlaDiscoverRequest(String requestType) {
        final StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
           .append("<SOAP-ENV:Envelope\n")
           .append(" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n")
           .append(" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n")
           .append(" <SOAP-ENV:Body>\n")
           .append("  <Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n")
           .append("   SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n")
           .append("   <RequestType>").append(requestType).append("</RequestType>\n")
           .append("   <Restrictions>\n")
           .append("    <RestrictionList/>\n")
           .append("   </Restrictions>\n")
           .append("   <Properties>\n")
           .append("    <PropertyList/>\n")
           .append("   </Properties>\n")
           .append("  </Discover>\n")
           .append(" </SOAP-ENV:Body>\n")
           .append("</SOAP-ENV:Envelope>");
        return buf.toString();
    }

    /**
     * Execute Xmla Request
     * 
     * @param url xmla server path
     * @param request soap request
     * @return output
     * @throws IOException
     */
    public static byte[] executeXmlaRequest(URL url, String request) throws IOException {
        // create a url connection
        final URLConnection urlConn = url.openConnection();
        urlConn.setDoOutput(true);
        final String contentTypeKey = "content-type";
        final String contentTypeValue = "text/xml";
        urlConn.setRequestProperty(contentTypeKey, contentTypeValue);

        // basic auth
        if (url.getUserInfo() != null) {
            final byte[] userInfoBytes = url.getUserInfo().getBytes();
            final String basicAuthKey = "Authorization";
            final String basicAuthVal = "Basic " +  Base64.encodeBytes(userInfoBytes, 0);
            urlConn.setRequestProperty(basicAuthKey, basicAuthVal);
        }
        
        final byte[] requestBytes = request.getBytes("UTF-8");
        urlConn.getOutputStream().write(requestBytes);

        // write the response to a byte array, and return the array
        InputStream is = urlConn.getInputStream();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int count = is.read(buf);
        while (count > 0) {
            baos.write(buf, 0, count);
            count = is.read(buf);
        }
        return baos.toByteArray();
    }
}

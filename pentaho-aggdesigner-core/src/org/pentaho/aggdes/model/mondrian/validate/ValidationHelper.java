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

package org.pentaho.aggdes.model.mondrian.validate;

import static org.pentaho.aggdes.model.ValidationMessage.Type.ERROR;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import mondrian.olap.MondrianDef;
import mondrian.olap.MondrianDef.Cube;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eigenbase.xom.Parser;
import org.eigenbase.xom.XOMException;
import org.eigenbase.xom.XOMUtil;
import org.pentaho.aggdes.model.ValidationMessage;

/**
 * @author mlowery
 */
public class ValidationHelper {

  private static final Log logger = LogFactory.getLog(ValidationHelper.class);

  public static String messagesToString(List<ValidationMessage> messages) {
    Collections.sort(messages);

    StringBuilder buf = new StringBuilder();
    for (ValidationMessage message : messages) {
      buf.append(message.getType())
          .append("\t").append(message.getMessage()).append(System.getProperty("line.separator")); //$NON-NLS-1$
    }
    return buf.toString();
  }

  public static List<ValidationMessage> validateCube(String catalogUrl, String cubeName, java.sql.Connection conn,
      List<MondrianSchemaValidator> validators) {
    MondrianDef.Schema schema = loadSchema(catalogUrl);
    MondrianDef.Cube cube = getCubeByName(schema, cubeName);
    MondrianSchemaValidatorManager man = new MondrianSchemaValidatorManager();
    man.setValidators(validators);
    List<ValidationMessage> messages = man.validateCube(schema, cube, conn);
    return messages;
  }

  public static MondrianDef.Schema loadSchema(String catalogUrl) {
    try {
      Parser xmlParser = XOMUtil.createDefaultParser();
      logger.debug("catalogUrl: " + catalogUrl);
      return new MondrianDef.Schema(xmlParser.parse(new URL(catalogUrl)));
    } catch (XOMException e) {
      logger.error("an exception occurred; returning null", e);
    } catch (MalformedURLException e) {
      logger.error("an exception occurred", e);
    }
    return null;
  }

  public static Cube getCubeByName(MondrianDef.Schema schema, String name) {
    for (Cube cube : schema.cubes) {
      if (cube.name.equals(name)) {
        return cube;
      }
    }
    return null;
  }

  public static boolean hasErrors(List<ValidationMessage> messages) {
    for (ValidationMessage message : messages) {
      if (message.getType() == ERROR) {
        return true;
      }
    }
    return false;
  }

}

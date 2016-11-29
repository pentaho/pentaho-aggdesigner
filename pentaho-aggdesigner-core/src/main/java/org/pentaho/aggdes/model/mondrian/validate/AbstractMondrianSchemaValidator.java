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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.model.mondrian.validate;

import static org.pentaho.aggdes.model.ValidationMessage.Type.ERROR;

import java.util.List;

import org.pentaho.aggdes.model.ValidationMessage;
import org.pentaho.aggdes.model.ValidationMessage.Type;
import org.pentaho.aggdes.model.mondrian.Messages;
import org.pentaho.aggdes.model.mondrian.validate.MondrianSchemaValidator;

public abstract class AbstractMondrianSchemaValidator implements MondrianSchemaValidator {

  /**
   * Wraps an Exception in a ValidationMessage.
   */
  protected ValidationMessage wrapException(Exception e) {
    return new ValidationMessage(ERROR, e.getMessage());
  }

  /**
   * Adds message to list and does any other list-related activities in preparation for an immediate exit of the 
   * validation process.
   */
  protected List<ValidationMessage> fatal(ValidationMessage message, List<ValidationMessage> messages) {
    // TODO mlowery sort the list
    messages.add(message);
    return messages;
  }

  protected List<ValidationMessage> fatal(Exception e, List<ValidationMessage> messages) {
    return this.fatal(wrapException(e), messages);
  }

  protected void append(List<ValidationMessage> messages, Type type, String keySuffix, Object... params) {
    messages.add(new ValidationMessage(type, Messages.getString("SimpleMondrianSchemaValidator." + keySuffix, params))); //$NON-NLS-1$
  }

}

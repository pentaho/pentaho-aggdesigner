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

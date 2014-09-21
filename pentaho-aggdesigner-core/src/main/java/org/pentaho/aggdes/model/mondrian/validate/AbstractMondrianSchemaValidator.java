/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
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

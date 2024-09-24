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

package org.pentaho.aggdes.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ValidationMessage implements Comparable<ValidationMessage> {

  public enum Type {
    OK, WARNING, ERROR
  };

  private Type type;

  private String message;

  public ValidationMessage(Type type, String message) {
    super();
    this.type = type;
    this.message = message;
  }

  public Type getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("type", type).append("message", "\"" + message + "\"").toString(); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  public int compareTo(ValidationMessage other) {
    // first check type
    if (this.type != other.type) {
      if (this.type == Type.ERROR) {
        return -1;
      } else if (this.type == Type.WARNING) {
        if (other.type == Type.OK) {
          return -1;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    } else {
      return this.message.compareTo(other.message);
    }
  }
}

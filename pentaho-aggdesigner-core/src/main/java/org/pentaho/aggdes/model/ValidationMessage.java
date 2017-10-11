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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

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

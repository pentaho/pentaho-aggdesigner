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

package org.pentaho.aggdes.ui.exec;

import java.util.List;

/**
 * Provides a service to install DDLExecutionCompleteCallback implementations. 
 * These are callback mechanisms between pentaho-aggdesigner-ui and plugins.
 */
public class DDLExecutionCallbackService {
    private List<DDLExecutionCompleteCallback> ddlCallbacks;

    public void setDdlCallbacks(List<DDLExecutionCompleteCallback> ddlCallbacks) {
        this.ddlCallbacks = ddlCallbacks;
    }
    
    public List<DDLExecutionCompleteCallback> getDdlCallbacks() {
        return ddlCallbacks;
    }
}

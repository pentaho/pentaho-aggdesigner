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

package org.pentaho.aggdes.output;

import org.pentaho.aggdes.model.Aggregate;

/**
 * An output object is the UI layer's view into Commands.  Outputs should be
 * serializable and store any necessary attributes required for customization
 * of a Command.  Output Attributes will appear in the UI and may be editable
 * by DBAs. 
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public interface Output {

    /**
     * the parent aggregate of this output
     * 
     * @return aggregate
     */
    public Aggregate getAggregate();

}

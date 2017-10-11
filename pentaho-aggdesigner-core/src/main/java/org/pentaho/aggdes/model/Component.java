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

import java.util.List;


/**
 * Component of an algorithm.
 *
 * <p>Components include: schema loader, algorithm.
 *
 * @author jhyde
 * @version $Id: Component.java 63 2008-03-17 08:37:39Z jhyde $
 * @since Mar 14, 2008
 */
public interface Component {
    /**
     * Returns a name for this component.
     *
     * @return name for this component
     */
    String getName();

    /**
     * Declares the parameters that this component accepts.
     *
     * @return list of parameters that this component accepts
     */
    List<Parameter> getParameters();
}

// End Component.java

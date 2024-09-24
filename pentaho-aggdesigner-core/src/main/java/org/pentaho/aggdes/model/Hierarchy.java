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

import java.util.List;


/**
 * OLAP Cube Hierarchy, Child of Dimension
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public interface Hierarchy {
    
    /**
     * Name of the Hierarchy
     * 
     * @return name
     */
    public String getName();
    
    /**
     * List of Levels
     * 
     * @return levels
     */
    public List<? extends Level> getLevels();
    
}

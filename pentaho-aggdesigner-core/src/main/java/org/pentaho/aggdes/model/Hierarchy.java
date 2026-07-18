/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.model;


/**
 * OLAP Cube Level, Child of Hierarchy
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public interface Level {
    
    /**
     * Name of the Level
     *
     * @return name
     */
    public String getName();
    
    /**
     * Attribute associated with level if available
     * 
     * Note that All Levels will not have an attribute associated with them.
     * 
     * @return attribute
     */
    public Attribute getAttribute();
    
    
    /**
     * returns the parent level, null if root level
     * 
     * @return level
     */
    public Level getParent();
}

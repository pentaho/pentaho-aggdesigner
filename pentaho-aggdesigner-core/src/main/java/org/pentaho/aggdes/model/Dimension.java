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

import java.util.List;


/**
 * OLAP Cube Dimension
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public interface Dimension {
    
    /**
     * Name of the Dimension
     * 
     * @return name
     */
    public String getName();
    
    /**
     * List of Hierarchies
     * 
     * @return hierarchies
     */
    public List<? extends Hierarchy> getHierarchies();
}

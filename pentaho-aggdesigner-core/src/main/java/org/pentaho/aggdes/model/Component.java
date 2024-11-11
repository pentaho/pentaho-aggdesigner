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

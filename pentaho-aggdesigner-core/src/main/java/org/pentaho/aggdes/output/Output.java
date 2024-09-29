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

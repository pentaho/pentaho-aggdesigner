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

import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Schema;

/**
 * this factory interface allows the creation of outputs,
 * such as AggregateTableOutput, MaterialzedViewOutput, 
 * and/or AJIOutput objects.
 * It should register with the OutputService.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public interface OutputFactory {
    
    /**
     * returns true if the factory can create an output.  
     * in the future, this call may include parameters for
     * overriding default behaviors.
     * 
     * @param schema the current schema object
     * 
     * @return true if this factory applies
     */
    public boolean canCreateOutput(Schema schema);
    
    /**
     * this is the output implementation class this factory generates.
     * this call is used by the Output Service to resolve the correct list of
     * supported generators
     * 
     * @return output class type
     */
    public Class<? extends Output> getOutputClass();
    
    /**
     * create an output object, populating it with default values.
     * 
     * @param schema schema object
     * @param aggregate aggregate object
     * @return new output object
     */
    public Output createOutput(Schema schema, Aggregate aggregate);
    
    /**
     * create a list of output objects, populating them with default values.
     * 
     * @param schema schema object
     * @param aggregates aggregate list
     * @return new output object list
     */
    public List<Output> createOutputs(Schema schema, List<Aggregate> aggregates);
}

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

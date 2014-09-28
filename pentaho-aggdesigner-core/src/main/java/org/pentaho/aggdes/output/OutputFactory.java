/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.output;

import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Schema;

/**
 * Factory that allows the creation of outputs,
 * such as AggregateTableOutput, MaterialzedViewOutput,
 * and/or AJIOutput objects.
 *
 * <p>It should register with the OutputService.
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

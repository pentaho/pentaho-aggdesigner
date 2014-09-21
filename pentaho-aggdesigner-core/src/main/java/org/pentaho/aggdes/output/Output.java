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

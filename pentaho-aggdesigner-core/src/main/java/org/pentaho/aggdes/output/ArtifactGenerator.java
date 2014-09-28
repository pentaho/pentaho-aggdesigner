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

import org.pentaho.aggdes.model.Schema;

/**
 * Artifact Generators generate outputs such as DDL, DML, or Schema snippits
 * based on Output Java beans.  Artifact Generators should register with the
 * OutputService.
 */
public interface ArtifactGenerator {
    /**
     * returns a list of supported output classes that this
     * generator can render
     *
     * @return a list of class objects
     */
    public Class[] getSupportedOutputClasses();

    /**
     * returns true if the output implementation is fully compatible
     * with this artifact generator
     *
     *
     * @param schema schema object
     * @param output output object
     *
     * @return true if compatible
     */
    public boolean canGenerate(Schema schema, Output output);

    /**
     * this method gets called by the output service only if output is
     * supported and canGenerate returns true
     *
     * @param schema schema object
     * @param output output object
     *
     * @return artifact
     */
    public String generate(Schema schema, Output output);

    /**
     * this method generates a full output of an artifact, which may include additional
     * items not found in the individual outputs.
     *
     * @param schema schema object
     * @param outputs list of outputs
     *
     * @return string
     */
    public String generateFull(Schema schema, List<? extends Output> outputs);
}

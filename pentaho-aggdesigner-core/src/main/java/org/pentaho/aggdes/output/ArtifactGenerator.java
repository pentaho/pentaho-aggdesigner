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


package org.pentaho.aggdes.output;

import java.util.List;

import org.pentaho.aggdes.model.Schema;

/**
 * Artifact Generators generate outputs such as DDL, DML, or Schema snippits
 * based on Output Java beans.  Artifact Generators should register with the
 * OutputService.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
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

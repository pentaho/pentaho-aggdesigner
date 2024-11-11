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

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Schema;

/**
 * The output service API defines how a client tool interacts with the output 
 * service layer.  This includes creating outputs, and also generating
 * full artifacts.
 * 
 * An example of an output is the create table script.  This script is created
 * by the service layer and returned to the UI layer for rendering.  When the 
 * DBA is ready to generate the full create table script, a call into getFullArtifact()
 * is done.
 * 
 * @author Will Gorman(wgorman@pentaho.com)
 *
 */
public interface OutputService {
    
    /**
     * returns an output of a specific type for an aggregate
     * 
     * note that in the future this call may require additional
     * parameters, to override the default behavior
     * 
     * @param aggregate aggregate object
     * @param outputType 
     * @return
     */
    public Output generateDefaultOutput(Aggregate aggregate) throws OutputValidationException;
    
    /**
     * returns a list of supported artifact generator classes
     * that the output service supports.
     * 
     * @return list of artifact generator classes supported.
     */
    public Class[] getSupportedArtifactGeneratorClasses();
    
    /**
     * returns the artifact of an output.  This should be called if output attributes
     * have changed.
     *
     * @param output output object
     * @return artifact string
     */
    public String getArtifact(Output output, Class<? extends ArtifactGenerator> artifactGenerator) throws OutputValidationException;
    
    /**
     * returns the full artifact of a list of outputs of a single output type. 
     * 
     * @param outputs a list of outputs
     * @return artifact string
     */
    public String getFullArtifact(List<? extends Output> outputs, Class<? extends ArtifactGenerator> artifactGenerator) throws OutputValidationException;

    /**
     * set the state of this service (eventually this service will be stateless, but for now..)
     * @param schema olap schema
     */
    public void init(Schema schema);
}

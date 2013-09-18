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
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
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

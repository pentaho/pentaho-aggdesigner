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
package org.pentaho.aggdes.output.impl;

import java.util.List;

import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.ArtifactGenerator;
import org.pentaho.aggdes.output.Output;

public abstract class AbstractGenerator implements ArtifactGenerator {

    /**
     * this is a common method shared by most generators
     */
    public String generateFull(Schema schema, List<? extends Output> outputs) {
        StringBuilder sb = new StringBuilder();
        for (Output output : outputs) {
            sb.append(generate(schema, output));
        }
        return sb.toString();
    }
}

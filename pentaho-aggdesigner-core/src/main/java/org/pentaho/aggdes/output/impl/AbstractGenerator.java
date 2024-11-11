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

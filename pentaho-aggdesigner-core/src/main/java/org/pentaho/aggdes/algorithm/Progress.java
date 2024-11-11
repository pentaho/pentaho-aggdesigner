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


package org.pentaho.aggdes.algorithm;

/**
 * Callback to report progress.
 */
public interface Progress {
    /**
     * Emits a progress message.
     *
     * @param message Message
     * @param complete Fraction complete (a number between 0 and 1).
     */
    void report(
        String message,
        double complete);
}

// End Progress.java

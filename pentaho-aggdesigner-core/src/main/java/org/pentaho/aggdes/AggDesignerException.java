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

package org.pentaho.aggdes;

/**
 * The main agg designer exception class
 * 
 * @author Will Gorman (wgorman@pentaho.org)
 *
 */
public class AggDesignerException extends Exception {

    private static final long serialVersionUID = -2572088196639316329L;

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public AggDesignerException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public AggDesignerException(String message, Throwable cause) {
        super(message, cause);
    }

    
    /**
     * Constructs a new exception by wrapping another Throwable object
     * 
     * @param e Throwable objec to wrap
     */
    public AggDesignerException(Throwable e) {
      super(e);
    }
    
}

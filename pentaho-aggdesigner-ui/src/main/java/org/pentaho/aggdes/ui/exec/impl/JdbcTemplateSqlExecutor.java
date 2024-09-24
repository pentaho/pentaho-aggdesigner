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

package org.pentaho.aggdes.ui.exec.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.exec.SqlExecutor;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class JdbcTemplateSqlExecutor implements SqlExecutor {

  private static final Log logger = LogFactory.getLog( JdbcTemplateSqlExecutor.class );
  private static final String NL = System.getProperty( "line.separator" );
  private ConnectionModel connectionModel;

  public void setConnectionModel( ConnectionModel connectionModel ) {
    this.connectionModel = connectionModel;
  }

  public String[] removeCommentsAndSemicolons( Schema schema, String[] sql ) {
    String[] newsql = new String[sql.length];
    for ( int i = 0; i < sql.length; i++ ) {
      newsql[i] = removeCommentsAndSemicolons( schema, sql[i] );
    }
    return newsql;
  }

  public String removeCommentsAndSemicolons( Schema schema, String sql ) {
    if ( sql == null ) {
      return null;
    }
    String trimmed = sql.trim(  );
    String commentStart = null;
    StringBuilder sb = new StringBuilder(  );
    schema.getDialect(  ).comment( sb, "" );
    commentStart = sb.toString(  );
    // remove NL if necessary
    if ( commentStart.indexOf( NL ) >= 0 ) {
      commentStart = commentStart.substring( 0, commentStart.indexOf( NL ) );
    }
    String[ ] lines  = trimmed.split( NL );
    StringBuilder newSql = new StringBuilder(  );
    boolean newLineNeeded = false;
    for ( int i = 0; i < lines.length; i++ ) {
      if ( !lines[i].startsWith( commentStart ) ) {
        if ( newLineNeeded ) {
          newSql.append( NL );
        }
        newSql.append( lines[i] );
        newLineNeeded = true;
      }
    }
    String noCommentSql = newSql.toString(  );
    if ( noCommentSql.endsWith( ";" ) ) {
      noCommentSql = noCommentSql.substring( 0, noCommentSql.length(  ) - 1 );
    }
    logger.debug( "clean sql: --[" + noCommentSql + "]--" );
    return noCommentSql;
  }

  public void execute( final String[] sql, final ExecutorCallback callback ) throws DataAccessException {
    Exception exceptionDuringExecute = null;
    DatabaseMeta dbMeta = connectionModel.getDatabaseMeta(  );
    String url = null;
    try {
      url = dbMeta.getURL(  );
    } catch ( KettleDatabaseException e ) {
      throw new DataAccessException( "DatabaseMeta problem", e ) {
        private static final long serialVersionUID = -3457360074729938909L;
      };
    }
    // create the datasource
    SingleConnectionDataSource ds = new SingleConnectionDataSource(  url, dbMeta.getUsername(  ), dbMeta
        .getPassword(  ), false );
    ds.setDriverClassName( dbMeta.getDriverClass(  ) );

    // create the jdbc template
    final JdbcTemplate jt = new JdbcTemplate( ds );

    // create the transaction manager
    DataSourceTransactionManager tsMan = new DataSourceTransactionManager( ds );

    // create the transaction template
    TransactionTemplate txTemplate = new TransactionTemplate( tsMan );

    // set behavior
    txTemplate.setPropagationBehavior( DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW );
    final String[ ] noCommentSql = removeCommentsAndSemicolons( connectionModel.getSchema(  ), sql );
    try {
      // run the code in a transaction
      txTemplate.execute( new TransactionCallbackWithoutResult(  ) {
        public void doInTransactionWithoutResult( TransactionStatus status ) {
          jt.batchUpdate( noCommentSql );
        }
      } );
    } catch ( DataAccessException e ) {
      if ( logger.isErrorEnabled(  ) ) {
        logger.error( "data access exception", e );
      }
      exceptionDuringExecute = e;
    }
    callback.executionComplete( exceptionDuringExecute );

  }

}

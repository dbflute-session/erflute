package org.dbflute.erflute.editor.model.dbexport.db;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ExportToDBManager implements IRunnableWithProgress {

    private static Logger logger = Logger.getLogger(ExportToDBManager.class.getName());

    protected Connection con;
    private String ddl;
    private Exception exception;
    private String errorSql;

    public ExportToDBManager() {
    }

    public void init(Connection con, String ddl) throws SQLException {
        this.con = con;
        con.setAutoCommit(false);
        this.ddl = ddl;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            final String[] ddls = ddl.split(";[\r\n]+");

            monitor.beginTask(DisplayMessages.getMessage("dialog.message.drop.table"), ddls.length);

            for (int i = 0; i < ddls.length; i++) {
                String message = ddls[i];
                final int index = message.indexOf("\r\n");
                if (index != -1) {
                    message = message.substring(0, index);
                }

                monitor.subTask("(" + (i + 1) + "/" + ddls.length + ") " + message);

                executeDDL(ddls[i]);
                monitor.worked(1);

                if (monitor.isCanceled()) {
                    throw new InterruptedException("Cancel has been requested.");
                }
            }

            con.commit();
        } catch (final InterruptedException e) {
            throw e;
        } catch (final Exception e) {
            this.exception = e;
        }

        monitor.done();
    }

    private void executeDDL(String ddl) throws SQLException {
        Statement stmt = null;
        try {
            logger.info(ddl);
            stmt = con.createStatement();
            stmt.execute(ddl);
        } catch (final SQLException e) {
            Activator.error(e);
            this.errorSql = ddl;
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public Exception getException() {
        return exception;
    }

    public String getErrorSql() {
        return errorSql;
    }
}

package org.dbflute.erflute.db;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.impl.access.AccessEclipseDBManager;
import org.dbflute.erflute.db.impl.db2.DB2EclipseDBManager;
import org.dbflute.erflute.db.impl.h2.H2EclipseDBManager;
import org.dbflute.erflute.db.impl.hsqldb.HSQLDBEclipseDBManager;
import org.dbflute.erflute.db.impl.mysql.MySQLEclipseDBManager;
import org.dbflute.erflute.db.impl.oracle.OracleEclipseDBManager;
import org.dbflute.erflute.db.impl.postgres.PostgresEclipseDBManager;
import org.dbflute.erflute.db.impl.sqlite.SQLiteEclipseDBManager;
import org.dbflute.erflute.db.impl.sqlserver.SqlServerEclipseDBManager;
import org.dbflute.erflute.db.impl.sqlserver2008.SqlServer2008EclipseDBManager;
import org.dbflute.erflute.db.impl.standard_sql.StandardSQLEclipseDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;

public class EclipseDBManagerFactory {

    private static final List<EclipseDBManager> DB_LIST = new ArrayList<>();

    static {
        new StandardSQLEclipseDBManager();
        new DB2EclipseDBManager();
        new H2EclipseDBManager();
        new HSQLDBEclipseDBManager();
        new AccessEclipseDBManager();
        new MySQLEclipseDBManager();
        new OracleEclipseDBManager();
        new PostgresEclipseDBManager();
        new SQLiteEclipseDBManager();
        new SqlServerEclipseDBManager();
        new SqlServer2008EclipseDBManager();
    }

    static void addDB(EclipseDBManager manager) {
        DB_LIST.add(manager);
    }

    public static EclipseDBManager getEclipseDBManager(String database) {
        for (final EclipseDBManager manager : DB_LIST) {
            if (manager.getId().equals(database)) {
                return manager;
            }
        }

        throw new IllegalArgumentException(DisplayMessages.getMessage("error.database.is.not.supported") + database);
    }

    public static EclipseDBManager getEclipseDBManager(ERDiagram diagram) {
        return getEclipseDBManager(diagram.getDatabase());
    }
}

package org.dbflute.erflute.db.impl.sqlserver2008;

import org.dbflute.erflute.db.impl.sqlserver.SqlServerDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;

public class SqlServer2008DBManager extends SqlServerDBManager {

    public static final String ID = "SQLServer 2008";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new SqlServer2008DDLCreator(diagram, semicolon);
    }

}

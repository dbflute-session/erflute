package org.dbflute.erflute.db.impl.oracle12c;

import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;

public class Oracle12cDBManager extends OracleDBManager {

    public static final String ID = "Oracle12c";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new Oracle12cDDLCreator(diagram, semicolon);
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA };
    }
}

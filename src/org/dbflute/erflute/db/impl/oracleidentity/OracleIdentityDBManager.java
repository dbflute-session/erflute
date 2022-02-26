package org.dbflute.erflute.db.impl.oracleidentity;

import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;

public class OracleIdentityDBManager extends OracleDBManager {

    public static final String ID = "Oracle(Identity)";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new OracleIdentityDDLCreator(diagram, semicolon);
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA };
    }
}

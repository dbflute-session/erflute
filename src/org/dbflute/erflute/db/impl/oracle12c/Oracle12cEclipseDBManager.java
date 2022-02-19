package org.dbflute.erflute.db.impl.oracle12c;

import org.dbflute.erflute.db.impl.oracle.OracleEclipseDBManager;

public class Oracle12cEclipseDBManager extends OracleEclipseDBManager {

    @Override
    public String getId() {
        return Oracle12cDBManager.ID;
    }
}

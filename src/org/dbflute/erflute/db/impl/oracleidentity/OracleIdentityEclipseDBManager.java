package org.dbflute.erflute.db.impl.oracleidentity;

import org.dbflute.erflute.db.impl.oracle.OracleEclipseDBManager;

public class OracleIdentityEclipseDBManager extends OracleEclipseDBManager {

    @Override
    public String getId() {
        return OracleIdentityDBManager.ID;
    }
}

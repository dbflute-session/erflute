package org.dbflute.erflute.db.impl.sqlserver;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.db.sqltype.SqlTypeManagerBase;

public class SqlServerSqlTypeManager extends SqlTypeManagerBase {

    public int getByteLength(SqlType type, Integer length, Integer decimal) {
        return 0;
    }
}

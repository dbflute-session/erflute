package org.dbflute.erflute.db.impl.standard_sql;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.db.sqltype.SqlTypeManagerBase;

public class StandardSQLSqlTypeManager extends SqlTypeManagerBase {

    public int getByteLength(SqlType type, Integer length, Integer decimal) {
        return 0;
    }

}

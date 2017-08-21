package org.dbflute.erflute.db.impl.oracle;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.db.sqltype.SqlTypeManagerBase;

public class OracleSqlTypeManager extends SqlTypeManagerBase {

    @Override
    public int getByteLength(SqlType type, Integer length, Integer decimal) {
        if (type == null) {
            return 0;
        }

        return 0;
    }
}

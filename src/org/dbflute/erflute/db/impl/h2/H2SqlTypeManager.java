package org.dbflute.erflute.db.impl.h2;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.db.sqltype.SqlTypeManagerBase;

public class H2SqlTypeManager extends SqlTypeManagerBase {

    @Override
    public int getByteLength(SqlType type, Integer length, Integer decimal) {
        return 0;
    }
}

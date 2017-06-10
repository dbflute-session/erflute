package org.dbflute.erflute.db.sqltype;

public interface SqlTypeManager {

    int getByteLength(SqlType type, Integer length, Integer decimal);
}

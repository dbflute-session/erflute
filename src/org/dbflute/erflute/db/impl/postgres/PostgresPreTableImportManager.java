package org.dbflute.erflute.db.impl.postgres;

import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;

public class PostgresPreTableImportManager extends PreImportFromDBManager {

    @Override
    protected String getTableNameWithSchema(String schema, String tableName) {
        return this.dbSetting.getTableNameWithSchema("\"" + tableName + "\"", schema);
    }

}

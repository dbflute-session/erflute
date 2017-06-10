package org.dbflute.erflute.db.impl.standard_sql;

import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManagerBase;

public class StandardSQLTableImportManager extends ImportFromDBManagerBase {

    @Override
    protected String getViewDefinitionSQL(String schema) {
        return null;
    }
}

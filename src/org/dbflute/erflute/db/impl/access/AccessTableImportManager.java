package org.dbflute.erflute.db.impl.access;

import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManagerBase;

public class AccessTableImportManager extends ImportFromDBManagerBase {

    @Override
    protected String getViewDefinitionSQL(String schema) {
        return null;
    }
}

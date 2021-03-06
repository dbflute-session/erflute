package org.dbflute.erflute.db.impl.sqlite;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManagerBase;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class SQLiteTableImportManager extends ImportFromDBManagerBase {

    @Override
    protected String getViewDefinitionSQL(String schema) {
        return null;
    }

    @Override
    protected List<ERIndex> getIndexes(ERTable table, DatabaseMetaData metaData, List<PrimaryKeyData> primaryKeys) throws SQLException {
        return new ArrayList<>();
    }

    @Override
    protected void setForeignKeys(List<ERTable> list) throws SQLException {
        // SQLite note yet implemented
    }

    @Override
    protected Map<String, ColumnData> getColumnDataMap(String tableNameWithSchema, String tableName, String schema)
            throws SQLException, InterruptedException {
        this.cashColumnDataX(null, tableName, null, null);

        return super.getColumnDataMap(tableNameWithSchema, tableName, schema);
    }
}

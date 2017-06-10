package org.dbflute.erflute.db.impl.sqlite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.DBManagerBase;
import org.dbflute.erflute.db.sqltype.SqlTypeManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.db.PreTableExportManager;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManager;
import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SQLiteDBManager extends DBManagerBase {

    public static final String ID = "SQLite";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    protected String getURL() {
        return "jdbc:sqlite:<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 0;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new SQLiteSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof SQLiteTableProperties) {
            return tableProperties;
        }

        return new SQLiteTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new SQLiteDDLCreator(diagram, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(ERTable table) {
        final List<String> list = new ArrayList<>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_SCHEMA, SUPPORT_AUTO_INCREMENT };
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new SQLiteTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new SQLitePreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new SQLitePreTableExportManager();
    }

    @Override
    public boolean doesNeedURLServerName() {
        return false;
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return null;
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(TablespaceProperties tablespaceProperties) {
        return null;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] { "CURRENT_TIMESTAMP" };
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return BigDecimal.ZERO;
    }
}

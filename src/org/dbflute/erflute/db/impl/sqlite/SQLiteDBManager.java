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

    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:sqlite:<DB NAME>";
    }

    public int getDefaultPort() {
        return 0;
    }

    public SqlTypeManager getSqlTypeManager() {
        return new SQLiteSqlTypeManager();
    }

    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof SQLiteTableProperties) {
            return tableProperties;
        }

        return new SQLiteTableProperties();
    }

    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new SQLiteDDLCreator(diagram, semicolon);
    }

    public List<String> getIndexTypeList(ERTable table) {
        List<String> list = new ArrayList<String>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_SCHEMA, SUPPORT_AUTO_INCREMENT };
    }

    public ImportFromDBManager getTableImportManager() {
        return new SQLiteTableImportManager();
    }

    public PreImportFromDBManager getPreTableImportManager() {
        return new SQLitePreTableImportManager();
    }

    public PreTableExportManager getPreTableExportManager() {
        return new SQLitePreTableExportManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesNeedURLServerName() {
        return false;
    }

    public TablespaceProperties createTablespaceProperties() {
        return null;
    }

    public TablespaceProperties checkTablespaceProperties(TablespaceProperties tablespaceProperties) {
        return null;
    }

    public String[] getCurrentTimeValue() {
        return new String[] { "CURRENT_TIMESTAMP" };
    }

    public BigDecimal getSequenceMaxValue() {
        return BigDecimal.ZERO;
    }

}

package org.dbflute.erflute.db.impl.access;

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

public class AccessDBManager extends DBManagerBase {

    public static final String ID = "MSAccess";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDriverClassName() {
        return "sun.jdbc.odbc.JdbcOdbcDriver";
    }

    @Override
    protected String getURL() {
        return "jdbc:odbc:<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 0;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new AccessSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof AccessTableProperties) {
            return tableProperties;
        }

        return new AccessTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new AccessDDLCreator(diagram, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(ERTable table) {
        final List<String> list = new ArrayList<>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_AUTO_INCREMENT_SETTING };
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new AccessTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new AccessPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new AccessPreTableExportManager();
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] { "GETDATE()", "CURRENT_TIMESTAMP" };
    }

    @Override
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<>();

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(TablespaceProperties tablespaceProperties) {
        return null;
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return null;
    }

    @Override
    public boolean doesNeedURLServerName() {
        return false;
    }
}

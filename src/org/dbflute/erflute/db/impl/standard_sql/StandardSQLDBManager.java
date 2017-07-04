package org.dbflute.erflute.db.impl.standard_sql;

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

public class StandardSQLDBManager extends DBManagerBase {

    public static final String ID = "StandardSQL";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDriverClassName() {
        return "";
    }

    @Override
    protected String getURL() {
        return "";
    }

    @Override
    public int getDefaultPort() {
        return 0;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new StandardSQLSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof StandardSQLTableProperties) {
            return tableProperties;
        }

        return new StandardSQLTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new StandardSQLDDLCreator(diagram, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(ERTable table) {
        final List<String> list = new ArrayList<>();
        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_AUTO_INCREMENT_SETTING, SUPPORT_SCHEMA, SUPPORT_SEQUENCE,
                SUPPORT_SEQUENCE_NOCACHE };
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new StandardSQLTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new StandardSQLPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new StandardSQLPreTableExportManager();
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
        return null;
    }
}

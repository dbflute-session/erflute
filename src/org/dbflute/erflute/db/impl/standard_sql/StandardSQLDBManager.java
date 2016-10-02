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

    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "";
    }

    public int getDefaultPort() {
        return 0;
    }

    public SqlTypeManager getSqlTypeManager() {
        return new StandardSQLSqlTypeManager();
    }

    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof StandardSQLTableProperties) {
            return tableProperties;
        }

        return new StandardSQLTableProperties();
    }

    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new StandardSQLDDLCreator(diagram, semicolon);
    }

    public List<String> getIndexTypeList(ERTable table) {
        List<String> list = new ArrayList<String>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_AUTO_INCREMENT_SETTING, SUPPORT_SCHEMA, SUPPORT_SEQUENCE };
    }

    public ImportFromDBManager getTableImportManager() {
        return new StandardSQLTableImportManager();
    }

    public PreImportFromDBManager getPreTableImportManager() {
        return new StandardSQLPreTableImportManager();
    }

    public PreTableExportManager getPreTableExportManager() {
        return new StandardSQLPreTableExportManager();
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
        return null;
    }
}

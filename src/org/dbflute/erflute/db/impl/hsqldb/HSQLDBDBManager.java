package org.dbflute.erflute.db.impl.hsqldb;

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

public class HSQLDBDBManager extends DBManagerBase {

    public static final String ID = "HSQLDB";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDriverClassName() {
        return "org.hsqldb.jdbcDriver";
    }

    @Override
    protected String getURL() {
        return "jdbc:hsqldb:hsql://<SERVER NAME>:<PORT>/<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 9001;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new HSQLDBSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof HSQLDBTableProperties) {
            return tableProperties;
        }

        return new HSQLDBTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new HSQLDBDDLCreator(diagram, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(ERTable table) {
        final List<String> list = new ArrayList<>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_SCHEMA, SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE };
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new HSQLDBTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new HSQLDBPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new HSQLDBPreTableExportManager();
    }

    @Override
    public boolean doesNeedURLDatabaseName() {
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
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<>();

        list.add("information_schema");
        list.add("system_lobs");

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }
}

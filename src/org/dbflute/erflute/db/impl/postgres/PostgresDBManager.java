package org.dbflute.erflute.db.impl.postgres;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.DBManagerBase;
import org.dbflute.erflute.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.dbflute.erflute.db.sqltype.SqlTypeManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.db.PreTableExportManager;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManager;
import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class PostgresDBManager extends DBManagerBase {

    public static final String ID = "PostgreSQL";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String getURL() {
        return "jdbc:postgresql://<SERVER NAME>:<PORT>/<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 5432;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new PostgresSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof PostgresTableProperties) {
            return tableProperties;
        }

        return new PostgresTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new PostgresDDLCreator(diagram, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(ERTable table) {
        final List<String> list = new ArrayList<>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_AUTO_INCREMENT_SETTING, SUPPORT_SCHEMA, SUPPORT_SEQUENCE };
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new PostgresTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new PostgresPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new PostgresPreTableExportManager();
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return new PostgresTablespaceProperties();
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(TablespaceProperties tablespaceProperties) {

        if (!(tablespaceProperties instanceof PostgresTablespaceProperties)) {
            return new PostgresTablespaceProperties();
        }

        return tablespaceProperties;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] { "CURRENT_TIMESTAMP", "now()" };
    }

    @Override
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<>();

        list.add("information_schema");
        list.add("pg_catalog");
        list.add("pg_toast_temp_1");

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return BigDecimal.valueOf(Long.MAX_VALUE);
    }
}

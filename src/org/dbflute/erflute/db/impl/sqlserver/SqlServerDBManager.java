package org.dbflute.erflute.db.impl.sqlserver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.DBManagerBase;
import org.dbflute.erflute.db.impl.sqlserver.tablespace.SqlServerTablespaceProperties;
import org.dbflute.erflute.db.sqltype.SqlTypeManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.db.PreTableExportManager;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManager;
import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SqlServerDBManager extends DBManagerBase {

    public static final String ID = "SQLServer";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDriverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    protected String getURL() {
        return "jdbc:sqlserver://<SERVER NAME>:<PORT>;database=<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 1433;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new SqlServerSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof SqlServerTableProperties) {
            return tableProperties;
        }

        return new SqlServerTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new SqlServerDDLCreator(diagram, semicolon);
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
        return new SqlServerTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new SqlServerPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new SqlServerPreTableExportManager();
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return new SqlServerTablespaceProperties();
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(TablespaceProperties tablespaceProperties) {

        if (!(tablespaceProperties instanceof SqlServerTablespaceProperties)) {
            return new SqlServerTablespaceProperties();
        }

        return tablespaceProperties;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] { "GETDATE()", "CURRENT_TIMESTAMP" };
    }

    @Override
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<>();

        list.add("db_accessadmin");
        list.add("db_backupoperator");
        list.add("db_datareader");
        list.add("db_datawriter");
        list.add("db_ddladmin");
        list.add("db_denydatareader");
        list.add("db_denydatawriter");
        list.add("db_owner");
        list.add("db_securityadmin");

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }
}

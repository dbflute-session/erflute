package org.dbflute.erflute.db.impl.db2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.DBManagerBase;
import org.dbflute.erflute.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.dbflute.erflute.db.sqltype.SqlTypeManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.db.PreTableExportManager;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManager;
import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class DB2DBManager extends DBManagerBase {

    public static final String ID = "DB2";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDriverClassName() {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    @Override
    protected String getURL() {
        return "jdbc:db2://<SERVER NAME>:<PORT>/<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 50000;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new DB2SqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof DB2TableProperties) {
            return tableProperties;
        }

        return new DB2TableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new DB2DDLCreator(diagram, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(ERTable table) {
        final List<String> list = new ArrayList<>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA, SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE };
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new DB2TableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new DB2PreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new DB2PreTableExportManager();
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return new DB2TablespaceProperties();
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(TablespaceProperties tablespaceProperties) {
        if (!(tablespaceProperties instanceof DB2TablespaceProperties)) {
            return new DB2TablespaceProperties();
        }

        return tablespaceProperties;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] { "CURRENT TIMESTAMP" };
    }

    @Override
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<>();
        list.add("nullid");
        list.add("sqlj");
        list.add("syscat");
        list.add("sysfun");
        list.add("sysibm");
        list.add("sysibmadm");
        list.add("sysibminternal");
        list.add("sysibmts");
        list.add("sysproc");
        list.add("syspublic");
        list.add("sysstat");
        list.add("systools");

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }
}

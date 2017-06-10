package org.dbflute.erflute.db.impl.h2;

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

public class H2DBManager extends DBManagerBase {

    public static final String ID = "H2";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:h2:tcp://<SERVER NAME>:<PORT>/<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 9092;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new H2SqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof H2TableProperties) {
            return tableProperties;
        }

        return new H2TableProperties();
    }

    @Override
    public List<String> getIndexTypeList(ERTable table) {
        final List<String> list = new ArrayList<>();

        list.add("BTREE");

        return list;
    }

    @Override
    public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
        return new H2DDLCreator(diagram, semicolon);
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] { SUPPORT_SCHEMA, SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE };
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new H2TableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new H2PreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new H2PreTableExportManager();
    }

    /**
     * {@inheritDoc}
     */
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

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }
}

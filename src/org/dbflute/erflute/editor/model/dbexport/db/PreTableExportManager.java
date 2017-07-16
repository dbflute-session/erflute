package org.dbflute.erflute.editor.model.dbexport.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLTarget;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.dbflute.erflute.editor.model.settings.Environment;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class PreTableExportManager {

    protected Connection con;
    protected DatabaseMetaData metaData;
    protected DBSettings dbSetting;
    private ERDiagram diagram;
    private Exception exception;
    private String errorSql;
    private String ddl;
    private Environment environment;
    private String ifExistsOption;
    private Set<String> newViewNames;
    protected Set<String> newTableNames;
    private Set<String> newSequenceNames;

    public void init(Connection con, DBSettings dbSetting, ERDiagram diagram, Environment environment) throws SQLException {
        this.con = con;
        this.dbSetting = dbSetting;
        this.diagram = diagram;
        this.environment = environment;
        this.metaData = con.getMetaData();
        this.ifExistsOption = DBManagerFactory.getDBManager(diagram).getDDLCreator(diagram, false).getIfExistsOption();
        prepareNewNames();
    }

    protected void prepareNewNames() {
        this.newTableNames = new HashSet<>();
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            newTableNames.add(dbSetting.getTableNameWithSchema(
                    table.getPhysicalName(), table.getTableViewProperties().getSchema()));
        }
        this.newViewNames = new HashSet<>();
        for (final ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            newViewNames.add(dbSetting.getTableNameWithSchema(view.getPhysicalName(), view.getTableViewProperties().getSchema()));
        }
        this.newSequenceNames = new HashSet<>();
        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            newSequenceNames.add(dbSetting.getTableNameWithSchema(sequence.getName(), sequence.getSchema()));
        }
    }

    public void run() {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append(dropViews());
            sb.append(dropForeignKeys());
            sb.append(dropTables());
            sb.append(dropSequences());
            sb.append(executeDDL());
            this.ddl = sb.toString();
        } catch (final Exception e) {
            this.exception = e;
        }
    }

    private String dropSequences() throws SQLException {
        final StringBuilder ddl = new StringBuilder();
        ResultSet sequenceSet = null;
        try {
            sequenceSet = metaData.getTables(null, null, null, new String[] { "SEQUENCE" });
            while (sequenceSet.next()) {
                String name = sequenceSet.getString("TABLE_NAME");
                final String schema = sequenceSet.getString("TABLE_SCHEM");
                name = dbSetting.getTableNameWithSchema(name, schema);
                if (newSequenceNames == null || newSequenceNames.contains(name)) {
                    ddl.append(dropSequence(name));
                    ddl.append("\r\n");
                }
            }
        } finally {
            if (sequenceSet != null) {
                sequenceSet.close();
            }
        }
        return ddl.toString();
    }

    private String dropSequence(String sequenceName) throws SQLException {
        final String sql = "DROP SEQUENCE " + ifExistsOption + sequenceName + ";";
        return sql;
    }

    private String dropViews() throws SQLException {
        final StringBuilder ddl = new StringBuilder();
        ResultSet viewSet = null;
        try {
            viewSet = metaData.getTables(null, null, null, new String[] { "VIEW" });
            while (viewSet.next()) {
                String name = viewSet.getString("TABLE_NAME");
                final String schema = viewSet.getString("TABLE_SCHEM");
                name = dbSetting.getTableNameWithSchema(name, schema);
                if (newViewNames == null || newViewNames.contains(name)) {
                    ddl.append(dropView(name));
                    ddl.append("\r\n");
                }
            }
        } finally {
            if (viewSet != null) {
                viewSet.close();
            }
        }
        return ddl.toString();
    }

    private String dropView(String viewName) throws SQLException {
        final String sql = "DROP VIEW " + ifExistsOption + viewName + ";";
        return sql;
    }

    protected String dropForeignKeys() throws SQLException {
        final StringBuilder ddl = new StringBuilder();
        ResultSet foreignKeySet = null;
        try {
            foreignKeySet = metaData.getImportedKeys(null, null, null);
            final Set<String> fkNameSet = new HashSet<>();
            while (foreignKeySet.next()) {
                final String constraintName = foreignKeySet.getString("FK_NAME");
                if (fkNameSet.contains(constraintName)) {
                    continue;
                }
                fkNameSet.add(constraintName);
                String tableName = foreignKeySet.getString("FKTABLE_NAME");
                final String schema = foreignKeySet.getString("FKTABLE_SCHEM");
                tableName = dbSetting.getTableNameWithSchema(tableName, schema);
                if (newTableNames == null || newTableNames.contains(tableName)) {
                    ddl.append(dropForeignKey(tableName, constraintName));
                    ddl.append("\r\n");
                }
            }
        } finally {
            if (foreignKeySet != null) {
                foreignKeySet.close();
            }
        }
        return ddl.toString();
    }

    private String dropForeignKey(String tableName, String constraintName) throws SQLException {
        final String sql = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName + ";";
        return sql;
    }

    private String dropTables() throws SQLException, InterruptedException {
        final StringBuilder ddl = new StringBuilder();
        ResultSet tableSet = null;
        try {
            tableSet = metaData.getTables(null, null, null, new String[] { "TABLE" });
            while (tableSet.next()) {
                String tableName = tableSet.getString("TABLE_NAME");
                final String schema = tableSet.getString("TABLE_SCHEM");
                tableName = dbSetting.getTableNameWithSchema(tableName, schema);
                if (newTableNames == null || newTableNames.contains(tableName)) {
                    try {
                        checkTableExist(con, tableName);
                    } catch (final SQLException e) {
                        continue;
                    }
                    ddl.append(dropTable(tableName));
                    ddl.append("\r\n");
                }
            }
        } finally {
            if (tableSet != null) {
                tableSet.close();
            }
        }
        return ddl.toString();
    }

    private String dropTable(String tableName) throws SQLException {
        final String sql = "DROP TABLE " + ifExistsOption + tableName + ";";
        return sql;
    }

    private String executeDDL() throws SQLException {
        final DDLCreator ddlCreator = DBManagerFactory.getDBManager(diagram).getDDLCreator(diagram, true);
        ddlCreator.init(environment, new DDLTarget());
        return ddlCreator.prepareCreateDDL(diagram);
    }

    protected void checkTableExist(Connection con, String tableNameWithSchema) throws SQLException {
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Exception getException() {
        return exception;
    }

    public String getErrorSql() {
        return errorSql;
    }

    public String getDdl() {
        return ddl;
    }
}

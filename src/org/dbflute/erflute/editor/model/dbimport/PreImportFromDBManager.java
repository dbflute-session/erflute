package org.dbflute.erflute.editor.model.dbimport;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DBSettings;

public abstract class PreImportFromDBManager {

    private static Logger logger = Logger.getLogger(PreImportFromDBManager.class.getName());

    protected Connection con;

    private DatabaseMetaData metaData;

    protected DBSettings dbSetting;

    private DBObjectSet importObjects;

    protected List<String> schemaList;

    private Exception exception;

    public void init(Connection con, DBSettings dbSetting, ERDiagram diagram, List<String> schemaList) throws SQLException {
        this.con = con;
        this.dbSetting = dbSetting;

        this.metaData = con.getMetaData();

        this.importObjects = new DBObjectSet();
        this.schemaList = schemaList;
    }

    public void run() {
        try {
            this.importObjects.addAll(this.importTables());
            this.importObjects.addAll(this.importSequences());
            this.importObjects.addAll(this.importViews());
            this.importObjects.addAll(this.importTriggers());

        } catch (final Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            this.exception = e;
        }
    }

    protected List<DBObject> importTables() throws SQLException {
        return this.importObjects(new String[] { "TABLE", "SYSTEM TABLE", "SYSTEM TOAST TABLE", "TEMPORARY TABLE" }, DBObject.TYPE_TABLE);
    }

    protected List<DBObject> importSequences() throws SQLException {
        return this.importObjects(new String[] { "SEQUENCE" }, DBObject.TYPE_SEQUENCE);
    }

    protected List<DBObject> importViews() throws SQLException {
        return this.importObjects(new String[] { "VIEW", "SYSTEM VIEW" }, DBObject.TYPE_VIEW);
    }

    protected List<DBObject> importTriggers() throws SQLException {
        return this.importObjects(new String[] { "TRIGGER" }, DBObject.TYPE_TRIGGER);
    }

    private List<DBObject> importObjects(String[] types, String dbObjectType) throws SQLException {
        final List<DBObject> list = new ArrayList<>();

        ResultSet resultSet = null;

        if (this.schemaList.isEmpty()) {
            this.schemaList.add(null);
        }

        for (final String schemaPattern : this.schemaList) {
            try {
                resultSet = metaData.getTables(null, schemaPattern, null, types);

                while (resultSet.next()) {
                    final String schema = resultSet.getString("TABLE_SCHEM");
                    final String name = resultSet.getString("TABLE_NAME");

                    if (DBObject.TYPE_TABLE.equals(dbObjectType)) {
                        try {
                            this.getAutoIncrementColumnName(con, schema, name);

                        } catch (final SQLException e) {
                            e.printStackTrace();
                            // テーブル情報が取得できない場合（他のユーザの所有物などの場合）、
                            // このテーブルは使用しない。
                            continue;
                        }
                    }

                    final DBObject dbObject = new DBObject(schema, name, dbObjectType);
                    list.add(dbObject);
                }
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
            }
        }

        return list;
    }

    private String getAutoIncrementColumnName(Connection con, String schema, String tableName) throws SQLException {
        final String autoIncrementColumnName = null;

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT 1 FROM " + this.getTableNameWithSchema(schema, tableName));

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

        return autoIncrementColumnName;
    }

    protected String getTableNameWithSchema(String schema, String tableName) {
        return this.dbSetting.getTableNameWithSchema(tableName, schema);
    }

    public DBObjectSet getImportObjects() {
        return this.importObjects;
    }

    public Exception getException() {
        return exception;
    }
}

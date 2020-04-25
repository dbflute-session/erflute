package org.dbflute.erflute.db.impl.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.dbimport.DBObject;
import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;

public class MySQLPreTableImportManager extends PreImportFromDBManager {

    @Override
    protected List<DBObject> importObjects(String[] types, String dbObjectType) throws SQLException {
        final List<DBObject> list = new ArrayList<>();

        ResultSet resultSet = null;

        if (schemaList.isEmpty()) {
            schemaList.add(null);
        }

        final String catalog = (8 <= metaData.getDriverMajorVersion()) ? dbSetting.getDatabase() : null;
        for (final String schemaPattern : schemaList) {
            try {
                resultSet = metaData.getTables(catalog, schemaPattern, null, types);

                while (resultSet.next()) {
                    final String schema = resultSet.getString("TABLE_SCHEM");
                    final String name = resultSet.getString("TABLE_NAME");

                    if (DBObject.TYPE_TABLE.equals(dbObjectType)) {
                        try {
                            getAutoIncrementColumnName(con, schema, name);
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
}

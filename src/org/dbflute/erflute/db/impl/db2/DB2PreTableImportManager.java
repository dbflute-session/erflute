package org.dbflute.erflute.db.impl.db2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.dbimport.DBObject;
import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;

public class DB2PreTableImportManager extends PreImportFromDBManager {

    @Override
    protected List<DBObject> importSequences() throws SQLException {
        final List<DBObject> list = new ArrayList<>();

        ResultSet resultSet = null;
        PreparedStatement stmt = null;

        if (schemaList.isEmpty()) {
            schemaList.add(null);
        }

        for (final String schemaPattern : schemaList) {
            try {
                if (schemaPattern == null) {
                    stmt = con.prepareStatement("SELECT SEQSCHEMA, SEQNAME FROM SYSCAT.SEQUENCES");
                } else {
                    stmt = con.prepareStatement("SELECT SEQSCHEMA, SEQNAME FROM SYSCAT.SEQUENCES WHERE SEQSCHEMA = ?");
                    stmt.setString(1, schemaPattern);
                }

                resultSet = stmt.executeQuery();

                while (resultSet.next()) {
                    final String schema = resultSet.getString("SEQSCHEMA");
                    final String name = resultSet.getString("SEQNAME");

                    final DBObject dbObject = new DBObject(schema, name, DBObject.TYPE_SEQUENCE);
                    list.add(dbObject);
                }
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            }
        }

        return list;
    }
}

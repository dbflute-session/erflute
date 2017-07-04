package org.dbflute.erflute.db.impl.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManagerBase;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;

public class H2TableImportManager extends ImportFromDBManagerBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getViewDefinitionSQL(String schema) {
        return "SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ";
    }

    @Override
    protected ColumnData createColumnData(ResultSet columnSet) throws SQLException {
        final ColumnData columnData = super.createColumnData(columnSet);
        final String type = columnData.type.toLowerCase();

        if (type.startsWith("timestamp")) {
            columnData.size = columnData.decimalDegits;
        }

        return columnData;
    }

    // TODO for identity column
    // private String getRestrictType(String tableName, String schema,
    // ColumnData columnData) throws SQLException {
    // String type = null;
    //
    // PreparedStatement ps = null;
    // ResultSet rs = null;
    //
    // try {
    // ps =
    // con.prepareStatement("select sequence_name from INFORMATION_SCHEMA.COLUMNS "
    // + " where table_name = ? "
    // + " and table_schema = ? "
    // + " and column_name = ?");
    //
    // ps.setString(1, tableName);
    // ps.setString(2, schema);
    // ps.setString(3, columnData.columnName);
    //
    // rs = ps.executeQuery();
    //
    // if (rs.next()) {
    // if (!Check.isEmpty(rs.getString("sequence_name"))) {
    // type = "identity";
    // }
    // }
    //
    // } finally {
    // if (rs != null) {
    // rs.close();
    // }
    // if (ps != null) {
    // ps.close();
    // }
    // }
    //
    // return type;
    // }

    @Override
    protected Sequence importSequence(String schema, String sequenceName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA = ? AND SEQUENCE_NAME = ?");
            stmt.setString(1, schema);
            stmt.setString(2, sequenceName);

            rs = stmt.executeQuery();

            if (rs.next()) {
                final Sequence sequence = new Sequence();

                sequence.setName(sequenceName);
                sequence.setSchema(schema);
                sequence.setIncrement(rs.getInt("INCREMENT"));
                sequence.setCache(rs.getInt("CACHE"));

                return sequence;
            }

            return null;
        } finally {
            close(rs);
            close(stmt);
        }
    }
}

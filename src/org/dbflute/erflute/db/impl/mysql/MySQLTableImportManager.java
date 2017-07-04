package org.dbflute.erflute.db.impl.mysql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManagerBase;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class MySQLTableImportManager extends ImportFromDBManagerBase {

    @Override
    protected String getViewDefinitionSQL(String schema) {
        if (schema != null) {
            return "SELECT view_definition FROM information_schema.views WHERE table_schema = ? AND table_name = ?";
        } else {
            return "SELECT view_definition FROM information_schema.views WHERE table_name = ?";
        }
    }

    @Override
    protected List<ERIndex> getIndexes(ERTable table, DatabaseMetaData metaData, List<PrimaryKeyData> primaryKeys) throws SQLException {
        final List<ERIndex> indexes = super.getIndexes(table, metaData, primaryKeys);

        for (final Iterator<ERIndex> iter = indexes.iterator(); iter.hasNext();) {
            final ERIndex index = iter.next();

            if ("PRIMARY".equalsIgnoreCase(index.getName())) {
                iter.remove();
            }
        }

        return indexes;
    }

    @Override
    protected String getConstraintName(PrimaryKeyData data) {
        return null;
    }

    @Override
    protected void cashOtherColumnData(String tableName, String schema, ColumnData columnData) throws SQLException {
        final String tableNameWithSchema = dbSetting.getTableNameWithSchema(tableName, schema);

        final SqlType sqlType = SqlType.valueOfId(columnData.type);

        if (sqlType != null && sqlType.doesNeedArgs()) {
            final String restrictType = getRestrictType(tableNameWithSchema, columnData);

            final Pattern p = Pattern.compile(columnData.type.toLowerCase() + "\\((.*)\\)");
            final Matcher m = p.matcher(restrictType);

            if (m.matches()) {
                columnData.enumData = m.group(1);
            }
        } else if (columnData.type.equals("year")) {
            final String restrictType = getRestrictType(tableNameWithSchema, columnData);
            columnData.type = restrictType;
        }
    }

    private String getRestrictType(String tableNameWithSchema, ColumnData columnData) throws SQLException {
        String type = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement("SHOW COLUMNS FROM `" + tableNameWithSchema + "` LIKE ?");

            ps.setString(1, columnData.columnName);
            rs = ps.executeQuery();

            if (rs.next()) {
                type = rs.getString("Type");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }

        return type;
    }

    @Override
    protected ColumnData createColumnData(ResultSet columnSet) throws SQLException {
        final ColumnData columnData = super.createColumnData(columnSet);
        final String type = columnData.type.toLowerCase();

        if (type.startsWith("decimal")) {
            if (columnData.size == 10 && columnData.decimalDegits == 0) {
                columnData.size = 0;
            }
        } else if (type.startsWith("double")) {
            if (columnData.size == 22 && columnData.decimalDegits == 0) {
                columnData.size = 0;
            }
        } else if (type.startsWith("float")) {
            if (columnData.size == 12 && columnData.decimalDegits == 0) {
                columnData.size = 0;
            }
        }
        return columnData;
    }
}

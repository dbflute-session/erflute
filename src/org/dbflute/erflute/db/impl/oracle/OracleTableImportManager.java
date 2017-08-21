package org.dbflute.erflute.db.impl.oracle;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dbflute.erflute.editor.model.dbimport.DBObject;
import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManagerBase;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.eclipse.core.runtime.IProgressMonitor;

public class OracleTableImportManager extends ImportFromDBManagerBase {

    private static Logger logger = Logger.getLogger(OracleTableImportManager.class.getName());
    private static final Pattern INTERVAL_YEAR_TO_MONTH_PATTERN = Pattern.compile("interval year\\((.)\\) to month");
    private static final Pattern INTERVAL_DAY_TO_SECCOND_PATTERN = Pattern.compile("interval day\\((.)\\) to second\\((.)\\)");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("timestamp\\((.)\\).*");

    @Override
    protected void cashColumnData(List<DBObject> dbObjectList, IProgressMonitor monitor) throws SQLException, InterruptedException {
        // p1us2er0 exclude schemas such as SYS, SYSTEM (2017/06/08)
        final List<String> schemaList = dbObjectList.stream()
                .filter(dbObject -> DBObject.TYPE_TABLE.equals(dbObject.getType()))
                .map(dbObject -> dbObject.getSchema())
                .distinct()
                .collect(Collectors.toList());
        for (final String schema : schemaList) {
            cashColumnDataX(schema, null, dbObjectList, monitor);
        }
        final String sql = "SELECT OWNER, TABLE_NAME, COLUMN_NAME, COMMENTS FROM SYS.ALL_COL_COMMENTS WHERE COMMENTS IS NOT NULL"
                + schemaList.stream().map(schema -> "?").collect(Collectors.joining(", ", " AND OWNER IN (", ")"));
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            IntStream.range(0, schemaList.size()).forEach(index -> {
                try {
                    stmt.setString(index + 1, schemaList.get(index));
                } catch (final SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    final String schema = rs.getString("OWNER");
                    final String columnName = rs.getString("COLUMN_NAME");
                    final String comments = rs.getString("COMMENTS");

                    tableName = dbSetting.getTableNameWithSchema(tableName, schema);

                    final Map<String, ColumnData> cash = columnDataCash.get(tableName);
                    if (cash != null) {
                        final ColumnData columnData = cash.get(columnName);
                        if (columnData != null) {
                            columnData.description = comments;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void cashTableComment(IProgressMonitor monitor) throws SQLException, InterruptedException {
        final String sql = "SELECT OWNER, TABLE_NAME, COMMENTS FROM SYS.ALL_TAB_COMMENTS WHERE COMMENTS IS NOT NULL";
        try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");

                final String schema = rs.getString("OWNER");
                final String comments = rs.getString("COMMENTS");

                tableName = dbSetting.getTableNameWithSchema(tableName, schema);

                tableCommentMap.put(tableName, comments);
            }
        }
    }

    @Override
    protected String getViewDefinitionSQL(String schema) {
        if (schema != null) {
            return "SELECT TEXT FROM ALL_VIEWS WHERE OWNER = ? AND VIEW_NAME = ?";
        } else {
            return "SELECT TEXT FROM ALL_VIEWS WHERE VIEW_NAME = ?";
        }
    }

    @Override
    protected Sequence importSequence(String schema, String sequenceName) throws SQLException {
        PreparedStatement stmt = null;
        try {
            if (schema != null) {
                stmt = con.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_OWNER = ? AND SEQUENCE_NAME = ?");
                stmt.setString(1, schema);
                stmt.setString(2, sequenceName);
            } else {
                stmt = con.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_NAME = ?");
                stmt.setString(1, sequenceName);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    final Sequence sequence = new Sequence();

                    sequence.setName(sequenceName);
                    sequence.setSchema(schema);
                    sequence.setIncrement(rs.getInt("INCREMENT_BY"));
                    final BigDecimal minValue = rs.getBigDecimal("MIN_VALUE");
                    sequence.setMinValue(minValue.longValue());
                    final BigDecimal maxValue = rs.getBigDecimal("MAX_VALUE");
                    sequence.setMaxValue(maxValue);
                    final BigDecimal lastNumber = rs.getBigDecimal("LAST_NUMBER");
                    sequence.setStart(lastNumber.longValue());
                    sequence.setCache(rs.getInt("CACHE_SIZE"));

                    final String cycle = rs.getString("CYCLE_FLAG").toLowerCase();
                    if ("y".equals(cycle)) {
                        sequence.setCycle(true);
                    } else {
                        sequence.setCycle(false);
                    }

                    return sequence;
                }
            }
            return null;
        } finally {
            close(stmt);
        }
    }

    @Override
    protected Trigger importTrigger(String schema, String name) throws SQLException {
        PreparedStatement stmt = null;
        try {
            if (schema != null) {
                stmt = con.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE OWNER = ? AND TRIGGER_NAME = ?");
                stmt.setString(1, schema);
                stmt.setString(2, name);
            } else {
                stmt = con.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE TRIGGER_NAME = ?");
                stmt.setString(1, name);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    final Trigger trigger = new Trigger();

                    trigger.setName(name);
                    trigger.setSchema(schema);
                    trigger.setDescription(rs.getString("DESCRIPTION"));
                    trigger.setSql(rs.getString("TRIGGER_BODY"));

                    return trigger;
                }
            }
            return null;
        } finally {
            close(stmt);
        }
    }

    public static boolean isValidObjectName(String s) {
        return s.matches("([a-zA-Z]{1}\\w*(\\$|\\#)*\\w*)|(\".*)");
    }

    @Override
    protected List<ERIndex> getIndexes(ERTable table, DatabaseMetaData metaData, List<PrimaryKeyData> primaryKeys) throws SQLException {
        if (!isValidObjectName(table.getPhysicalName())) {
            logger.info("is not valid object name : " + table.getPhysicalName());
            return new ArrayList<>();
        }

        try {
            return super.getIndexes(table, metaData, primaryKeys);
        } catch (final SQLException e) {
            if (e.getErrorCode() == 38029) {
                logger.info(table.getPhysicalName() + " : " + e.getMessage());
                return new ArrayList<>();
            }
            throw e;
        }
    }

    @Override
    protected int getLength(String type, int size) {
        final int startIndex = type.indexOf("(");
        if (startIndex > 0) {
            final int endIndex = type.indexOf(")", startIndex + 1);
            if (endIndex != -1) {
                final String str = type.substring(startIndex + 1, endIndex);
                return Integer.parseInt(str);
            }
        }
        return size;
    }

    @Override
    protected List<ERTable> importSynonyms() throws SQLException, InterruptedException {
        final List<ERTable> list = new ArrayList<>();
        return list;
    }

    @Override
    protected ColumnData createColumnData(ResultSet columnSet) throws SQLException {
        final ColumnData columnData = super.createColumnData(columnSet);
        final String type = columnData.type.toLowerCase();

        if (type.equals("number")) {
            if (columnData.size == 22 && columnData.decimalDegits == 0) {
                columnData.size = 0;
            }
        } else if (type.equals("float")) {
            if (columnData.size == 126 && columnData.decimalDegits == 0) {
                columnData.size = 0;
            }
        } else if (type.equals("urowid")) {
            if (columnData.size == 4000) {
                columnData.size = 0;
            }
        } else if (type.equals("anydata")) {
            columnData.size = 0;
        } else {
            final Matcher yearToMonthMatcber = INTERVAL_YEAR_TO_MONTH_PATTERN.matcher(columnData.type);
            final Matcher dayToSecondMatcber = INTERVAL_DAY_TO_SECCOND_PATTERN.matcher(columnData.type);
            final Matcher timestampMatcber = TIMESTAMP_PATTERN.matcher(columnData.type);

            if (yearToMonthMatcber.matches()) {
                columnData.type = "interval year to month";

                if (columnData.size == 2) {
                    columnData.size = 0;
                }
            } else if (dayToSecondMatcber.matches()) {
                columnData.type = "interval day to second";

                if (columnData.size == 2 && columnData.decimalDegits == 6) {
                    columnData.size = 0;
                    columnData.decimalDegits = 0;
                }
            } else if (timestampMatcber.matches()) {
                columnData.type = columnData.type.replaceAll("\\(.\\)", "");
                columnData.size = 0;

                if (columnData.decimalDegits == 6) {
                    columnData.size = 0;

                } else {
                    columnData.size = columnData.decimalDegits;
                }

                columnData.decimalDegits = 0;
            }
        }
        return columnData;
    }
}

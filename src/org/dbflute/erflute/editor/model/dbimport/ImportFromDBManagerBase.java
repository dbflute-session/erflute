package org.dbflute.erflute.editor.model.dbimport;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public abstract class ImportFromDBManagerBase implements ImportFromDBManager, IRunnableWithProgress {

    private static Logger logger = Logger.getLogger(ImportFromDBManagerBase.class.getName());
    private static final boolean LOG_SQL_TYPE = false;
    private static final Pattern AS_PATTERN = Pattern.compile("(.+) [aA][sS] (.+)");

    protected Connection con;
    protected DatabaseMetaData metaData;
    protected DBSettings dbSetting;
    private ERDiagram diagram;
    private List<DBObject> dbObjectList;
    private final Map<String, ERTable> tableMap;
    protected Map<String, String> tableCommentMap;
    protected Map<String, Map<String, ColumnData>> columnDataCash;
    private Map<String, List<ForeignKeyData>> tableForeignKeyDataMap;
    private final Map<UniqueWord, Word> dictionary;
    private List<ERTable> importedTables;
    private List<Sequence> importedSequences;
    private List<Trigger> importedTriggers;
    private List<Tablespace> importedTablespaces;
    private List<ERView> importedViews;
    private Exception exception;
    private boolean useCommentAsLogicalName;
    private boolean mergeWord;

    protected static class ColumnData {
        public String columnName;
        public String type;
        public int size;
        public int decimalDegits;
        public int nullable;
        public String defaultValue;
        public String description;
        public String constraint;
        public String enumData;

        @Override
        public String toString() {
            return "ColumnData [columnName=" + columnName + ", type=" + type + ", size=" + size + ", decimalDegits=" + decimalDegits + "]";
        }
    }

    private static class ForeignKeyData {
        private String name;
        private String sourceTableName;
        private String sourceSchemaName;
        private String sourceColumnName;
        private String targetTableName;
        private String targetSchemaName;
        private String targetColumnName;
        private short updateRule;
        private short deleteRule;
    }

    protected static class PrimaryKeyData {
        private String columnName;
        private String constraintName;
    }

    public ImportFromDBManagerBase() {
        this.tableMap = new HashMap<>();
        this.tableCommentMap = new HashMap<>();
        this.columnDataCash = new HashMap<>();
        this.tableForeignKeyDataMap = new HashMap<>();
        this.dictionary = new HashMap<>();
    }

    @Override
    public void init(Connection con, DBSettings dbSetting, ERDiagram diagram, List<DBObject> dbObjectList,
            boolean useCommentAsLogicalName, boolean mergeWord) throws SQLException {
        this.con = con;
        this.dbSetting = dbSetting;
        this.diagram = diagram;
        this.dbObjectList = dbObjectList;
        this.useCommentAsLogicalName = useCommentAsLogicalName;
        this.mergeWord = mergeWord;
        this.metaData = con.getMetaData();
        if (this.mergeWord) {
            for (final Word word : diagram.getDiagramContents().getDictionary().getWordList()) {
                dictionary.put(new UniqueWord(word), word);
            }
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            if (monitor != null) {
                monitor.beginTask(DisplayMessages.getMessage("dialog.message.import.table"), dbObjectList.size());
            }

            this.importedSequences = importSequences(dbObjectList);
            this.importedTriggers = importTriggers(dbObjectList);
            this.importedTablespaces = importTablespaces(dbObjectList);
            this.importedTables = importTables(dbObjectList, monitor);
            this.importedTables.addAll(importSynonyms());

            setForeignKeys(importedTables);

            this.importedViews = importViews(dbObjectList);
        } catch (final InterruptedException e) {
            throw e;
        } catch (final Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            this.exception = e;
        }

        if (monitor != null) {
            monitor.done();
        }
    }

    protected void cashColumnData(List<DBObject> dbObjectList, IProgressMonitor monitor) throws SQLException, InterruptedException {
        cashColumnDataX(null, null, dbObjectList, monitor);
    }

    protected void cashColumnDataX(String schemaPattern, String tableName, List<DBObject> dbObjectList, IProgressMonitor monitor)
            throws SQLException, InterruptedException {
        try (ResultSet columnSet = metaData.getColumns(null, schemaPattern, tableName, null)) {
            while (columnSet.next()) {
                tableName = columnSet.getString("TABLE_NAME");
                final String schema = columnSet.getString("TABLE_SCHEM");

                final String tableNameWithSchema = dbSetting.getTableNameWithSchema(tableName, schema);
                monitor.subTask("reading : " + tableNameWithSchema);

                Map<String, ColumnData> cash = columnDataCash.get(tableNameWithSchema);
                if (cash == null) {
                    cash = new LinkedHashMap<>();
                    columnDataCash.put(tableNameWithSchema, cash);
                }

                final ColumnData columnData = createColumnData(columnSet);

                cashOtherColumnData(tableName, schema, columnData);

                cash.put(columnData.columnName, columnData);

                if (monitor.isCanceled()) {
                    throw new InterruptedException("Cancel has been requested.");
                }
            }
        }
    }

    protected ColumnData createColumnData(ResultSet columnSet) throws SQLException {
        final ColumnData columnData = new ColumnData();
        columnData.columnName = columnSet.getString("COLUMN_NAME");
        columnData.type = columnSet.getString("TYPE_NAME").toLowerCase();
        columnData.size = columnSet.getInt("COLUMN_SIZE");
        columnData.decimalDegits = columnSet.getInt("DECIMAL_DIGITS");
        columnData.nullable = columnSet.getInt("NULLABLE");
        columnData.defaultValue = columnSet.getString("COLUMN_DEF");

        if (columnData.defaultValue != null) {
            if ("bit".equals(columnData.type)) {
                final byte[] bits = columnData.defaultValue.getBytes();

                columnData.defaultValue = "";

                for (int i = 0; i < bits.length; i++) {
                    columnData.defaultValue += bits[i];
                }
            }
        }

        columnData.description = columnSet.getString("REMARKS");

        return columnData;
    }

    protected void cashOtherColumnData(String tableName, String schema, ColumnData columnData) throws SQLException {
    }

    protected void cashTableComment(IProgressMonitor monitor) throws SQLException, InterruptedException {
    }

    private List<Sequence> importSequences(List<DBObject> dbObjectList) throws SQLException {
        final List<Sequence> list = new ArrayList<>();

        for (final DBObject dbObject : dbObjectList) {
            if (DBObject.TYPE_SEQUENCE.equals(dbObject.getType())) {
                final String schema = dbObject.getSchema();
                final String name = dbObject.getName();

                final Sequence sequence = importSequence(schema, name);

                if (sequence != null) {
                    list.add(sequence);
                }
            }
        }

        return list;
    }

    protected Sequence importSequence(String schema, String sequenceName) throws SQLException {
        final String sequenceNameWithSchema = getTableNameWithSchema(schema, sequenceName);
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM " + sequenceNameWithSchema);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                final Sequence sequence = new Sequence();

                sequence.setName(sequenceName);
                sequence.setSchema(schema);
                sequence.setIncrement(rs.getInt("INCREMENT_BY"));
                sequence.setMinValue(rs.getLong("MIN_VALUE"));

                final BigDecimal maxValue = rs.getBigDecimal("MAX_VALUE");

                sequence.setMaxValue(maxValue);
                sequence.setStart(rs.getLong("LAST_VALUE"));
                sequence.setCache(rs.getInt("CACHE_VALUE"));
                sequence.setCycle(rs.getBoolean("IS_CYCLED"));

                return sequence;
            }
            return null;
        }
    }

    private List<Trigger> importTriggers(List<DBObject> dbObjectList) throws SQLException {
        final List<Trigger> list = new ArrayList<>();

        for (final DBObject dbObject : dbObjectList) {
            if (DBObject.TYPE_TRIGGER.equals(dbObject.getType())) {
                final String schema = dbObject.getSchema();
                final String name = dbObject.getName();

                final Trigger trigger = importTrigger(schema, name);

                if (trigger != null) {
                    list.add(trigger);
                }
            }
        }

        return list;
    }

    protected Trigger importTrigger(String schema, String triggerName) throws SQLException {
        return null;
    }

    protected List<ERTable> importTables(List<DBObject> dbObjectList, IProgressMonitor monitor) throws SQLException, InterruptedException {
        final List<ERTable> list = new ArrayList<>();

        cashTableComment(monitor);
        cashColumnData(dbObjectList, monitor);

        int i = 0;

        for (final DBObject dbObject : dbObjectList) {
            if (DBObject.TYPE_TABLE.equals(dbObject.getType())) {
                i++;

                final String tableName = dbObject.getName();
                final String schema = dbObject.getSchema();
                final String tableNameWithSchema = dbSetting.getTableNameWithSchema(tableName, schema);
                if (monitor != null) {
                    monitor.subTask("(" + i + "/" + dbObjectList.size() + ") " + tableNameWithSchema);
                    monitor.worked(1);
                }

                final ERTable table = importTable(tableNameWithSchema, tableName, schema);

                if (table != null) {
                    list.add(table);
                }
            }

            if (monitor != null && monitor.isCanceled()) {
                throw new InterruptedException("Cancel has been requested.");
            }
        }

        return list;
    }

    protected List<ERTable> importSynonyms() throws SQLException, InterruptedException {
        return new ArrayList<>();
    }

    protected String getConstraintName(PrimaryKeyData data) {
        return data.constraintName;
    }

    protected ERTable importTable(String tableNameWithSchema, String tableName, String schema) throws SQLException, InterruptedException {
        String autoIncrementColumnName = null;
        try {
            autoIncrementColumnName = getAutoIncrementColumnName(con, getTableNameWithSchema(schema, tableName));
        } catch (final SQLException e) {
            return null;
        }

        final ERTable table = new ERTable();
        final TableViewProperties tableProperties = table.getTableViewProperties(dbSetting.getDbsystem());
        tableProperties.setSchema(schema);

        table.setPhysicalName(tableName);
        final String description = tableCommentMap.get(tableNameWithSchema);
        //description = ID:ID
        if (useCommentAsLogicalName && !Check.isEmpty(description)) {
            final int pos = description.indexOf(':');
            if (pos >= 0) {
                table.setLogicalName(description.substring(0, pos).replaceAll("[\r\n]", ""));
                table.setDescription(description.substring(pos + 1));
            } else {
                table.setLogicalName(description.replaceAll("[\r\n]", ""));
            }
        }

        final List<PrimaryKeyData> primaryKeys = getPrimaryKeys(table, metaData);
        if (!primaryKeys.isEmpty()) {
            table.setPrimaryKeyName(getConstraintName(primaryKeys.get(0)));
        }

        final List<ERIndex> indexes = getIndexes(table, metaData, primaryKeys);

        final List<ERColumn> columns =
                getColumns(tableNameWithSchema, tableName, schema, indexes, primaryKeys, autoIncrementColumnName);

        table.setColumns(columns);
        table.setIndexes(indexes);

        tableMap.put(tableNameWithSchema, table);

        for (final ERIndex index : indexes) {
            setIndexColumn(table, index);
        }

        return table;
    }

    protected String getTableNameWithSchema(String schema, String tableName) {
        return dbSetting.getTableNameWithSchema(tableName, schema);
    }

    protected void setForeignKeys(List<ERTable> list) throws SQLException {
        cashForeignKeyData();

        for (final ERTable target : list) {
            if (tableForeignKeyDataMap != null) {
                setForeignKeysUsingCash(target);
            } else {
                setForeignKeys(target);
            }
        }
    }

    private String getAutoIncrementColumnName(Connection con, String tableNameWithSchema) throws SQLException {
        String autoIncrementColumnName = null;
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableNameWithSchema)) {
            final ResultSetMetaData md = rs.getMetaData();

            for (int i = 0; i < md.getColumnCount(); i++) {
                if (md.isAutoIncrement(i + 1)) {
                    autoIncrementColumnName = md.getColumnName(i + 1);
                    break;
                }
            }
        }

        return autoIncrementColumnName;
    }

    protected List<ERIndex> getIndexes(ERTable table, DatabaseMetaData metaData, List<PrimaryKeyData> primaryKeys) throws SQLException {
        final List<ERIndex> indexes = new ArrayList<>();
        final Map<String, ERIndex> indexMap = new HashMap<>();

        try (ResultSet indexSet = metaData.getIndexInfo(null,
                table.getTableViewProperties(dbSetting.getDbsystem()).getSchema(),
                table.getPhysicalName(), false, true)) {
            while (indexSet.next()) {
                final String name = indexSet.getString("INDEX_NAME");
                if (name == null) {
                    continue;
                }

                ERIndex index = indexMap.get(name);

                if (index == null) {
                    final boolean nonUnique = indexSet.getBoolean("NON_UNIQUE");
                    String type = null;
                    final short indexType = indexSet.getShort("TYPE");
                    if (indexType == DatabaseMetaData.tableIndexOther) {
                        type = "BTREE";
                    }

                    index = new ERIndex(table, name, nonUnique, type, null);

                    indexMap.put(name, index);
                    indexes.add(index);
                }

                String columnName = indexSet.getString("COLUMN_NAME");
                final String ascDesc = indexSet.getString("ASC_OR_DESC");

                if (columnName.startsWith("\"") && columnName.endsWith("\"")) {
                    columnName = columnName.substring(1, columnName.length() - 1);
                }

                // p1us2er0 May be null. (2017/09/12)
                // https://docs.oracle.com/javase/jp/8/docs/api/java/sql/DatabaseMetaData.html#getIndexInfo-java.lang.String-java.lang.String-java.lang.String-boolean-boolean-
                index.addColumnName(columnName, "D".equals(ascDesc));
            }
        } catch (final SQLException e) {
            throw e;
        }

        for (final Iterator<ERIndex> iter = indexes.iterator(); iter.hasNext();) {
            final ERIndex index = iter.next();
            final List<String> indexColumns = index.getColumnNames();

            if (indexColumns.size() == primaryKeys.size()) {
                boolean equals = true;

                for (int i = 0; i < indexColumns.size(); i++) {
                    if (!indexColumns.get(i).equals(primaryKeys.get(i).columnName)) {
                        equals = false;
                        break;
                    }
                }

                if (equals) {
                    iter.remove();
                }
            }
        }

        return indexes;
    }

    private void setIndexColumn(ERTable erTable, ERIndex index) {
        for (final String columnName : index.getColumnNames()) {
            for (final ERColumn column : erTable.getColumns()) {
                if (column instanceof NormalColumn) {
                    final NormalColumn normalColumn = (NormalColumn) column;

                    if (normalColumn.getPhysicalName().equals(columnName)) {
                        index.addColumn(normalColumn);
                        break;
                    }
                }
            }
        }
    }

    private List<PrimaryKeyData> getPrimaryKeys(ERTable table, DatabaseMetaData metaData) throws SQLException {
        final List<PrimaryKeyData> primaryKeys = new ArrayList<>();

        try (ResultSet primaryKeySet = metaData.getPrimaryKeys(null,
                table.getTableViewProperties(dbSetting.getDbsystem()).getSchema(),
                table.getPhysicalName())) {
            while (primaryKeySet.next()) {
                final PrimaryKeyData data = new PrimaryKeyData();
                data.columnName = primaryKeySet.getString("COLUMN_NAME");
                data.constraintName = primaryKeySet.getString("PK_NAME");
                primaryKeys.add(data);
            }
        } catch (final SQLException e) {
            // Microsoft Access does not support getPrimaryKeys
        }

        return primaryKeys;
    }

    protected Map<String, ColumnData> getColumnDataMap(String tableNameWithSchema, String tableName, String schema)
            throws SQLException, InterruptedException {
        return columnDataCash.get(tableNameWithSchema);
    }

    private List<ERColumn> getColumns(String tableNameWithSchema, String tableName, String schema, List<ERIndex> indexes,
            List<PrimaryKeyData> primaryKeys, String autoIncrementColumnName) throws SQLException, InterruptedException {
        final List<ERColumn> columns = new ArrayList<>();

        final Map<String, ColumnData> columnDataMap = getColumnDataMap(tableNameWithSchema, tableName, schema);
        if (columnDataMap == null) {
            return new ArrayList<>();
        }

        final Collection<ColumnData> columnSet = columnDataMap.values();

        for (final ColumnData columnData : columnSet) {
            final String columnName = columnData.columnName;
            String type = columnData.type;

            boolean array = false;
            Integer arrayDimension = null;
            boolean unsigned = false;

            final int unsignedIndex = type.indexOf(" UNSIGNED");
            if (unsignedIndex != -1) {
                unsigned = true;
                type = type.substring(0, unsignedIndex);
            }

            final int arrayStartIndex = type.indexOf("[");
            if (arrayStartIndex != -1) {
                array = true;
                final String str = type.substring(arrayStartIndex + 1, type.indexOf("]"));
                arrayDimension = Integer.parseInt(str);
                type = type.substring(0, arrayStartIndex);
            }

            final int size = getLength(type, columnData.size);
            final Integer length = new Integer(size);

            final SqlType sqlType = SqlType.valueOf(dbSetting.getDbsystem(), type, size);
            if (sqlType == null || LOG_SQL_TYPE) {
                logger.info(columnName + ": " + type + ", " + size + ", " + columnData.decimalDegits);
            }

            final int decimalDegits = columnData.decimalDegits;
            final Integer decimal = new Integer(decimalDegits);

            boolean notNull = false;
            if (columnData.nullable == DatabaseMetaData.columnNoNulls) {
                notNull = true;
            }

            String defaultValue = Format.null2blank(columnData.defaultValue);
            if (sqlType != null) {
                if (SqlType.SQL_TYPE_ID_SERIAL.equals(sqlType.getId()) || SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(sqlType.getId())) {
                    defaultValue = "";
                }
            }

            String description = Format.null2blank(columnData.description);
            final String constraint = Format.null2blank(columnData.constraint);

            boolean primaryKey = false;
            for (final PrimaryKeyData primaryKeyData : primaryKeys) {
                if (columnName.equals(primaryKeyData.columnName)) {
                    primaryKey = true;
                    break;
                }
            }

            final boolean uniqueKey = isUniqueKey(columnName, indexes, primaryKeys);

            final boolean autoIncrement = columnName.equalsIgnoreCase(autoIncrementColumnName);

            String logicalName = null;
            //description = ID:ID
            if (useCommentAsLogicalName && !Check.isEmpty(description)) {
                final int pos = description.indexOf(':');
                if (pos >= 0) {
                    logicalName = description.substring(0, pos).replaceAll("[\r\n]", "");
                    description = description.substring(pos + 1);
                } else {
                    logicalName = description.replaceAll("[\r\n]", "");
                    description = "";
                }
            }

            final String args = columnData.enumData;
            final TypeData typeData = new TypeData(length, decimal, array, arrayDimension, unsigned, args, false);

            Word word = new Word(columnName, logicalName, sqlType, typeData, description, this.diagram.getDatabase());
            final UniqueWord uniqueWord = new UniqueWord(word);

            if (dictionary.get(uniqueWord) != null) {
                word = dictionary.get(uniqueWord);
            } else {
                dictionary.put(uniqueWord, word);
            }

            final NormalColumn column =
                    new NormalColumn(word, notNull, primaryKey, uniqueKey, autoIncrement, defaultValue, constraint, null, null, null);
            columns.add(column);
        }

        return columns;
    }

    private boolean isUniqueKey(String columnName, List<ERIndex> indexes, List<PrimaryKeyData> primaryKeys) {
        String primaryKey = null;

        if (primaryKeys.size() == 1) {
            primaryKey = primaryKeys.get(0).columnName;
        }

        if (columnName == null) {
            return false;
        }

        for (final ERIndex index : indexes) {
            final List<String> columnNames = index.getColumnNames();
            if (columnNames.size() == 1) {
                final String indexColumnName = columnNames.get(0);
                if (columnName.equals(indexColumnName)) {
                    if (!index.isNonUnique()) {
                        if (!columnName.equals(primaryKey)) {
                            indexes.remove(index);
                            return true;
                        }
                        return false;
                    }
                }
            }
        }

        return false;
    }

    private boolean isCyclicForeignKye(ForeignKeyData foreignKeyData) {
        if (foreignKeyData.sourceSchemaName == null) {
            if (foreignKeyData.targetSchemaName != null) {
                return false;
            }
        } else if (!foreignKeyData.sourceSchemaName.equals(foreignKeyData.targetSchemaName)) {
            return false;
        }

        if (!foreignKeyData.sourceTableName.equals(foreignKeyData.targetTableName)) {
            return false;
        }

        if (!foreignKeyData.sourceColumnName.equals(foreignKeyData.targetColumnName)) {
            return false;
        }

        return true;
    }

    private void cashForeignKeyData() throws SQLException {
        try (ResultSet foreignKeySet = metaData.getImportedKeys(null, null, null)) {
            while (foreignKeySet.next()) {
                final ForeignKeyData foreignKeyData = new ForeignKeyData();

                foreignKeyData.name = foreignKeySet.getString("FK_NAME");
                foreignKeyData.sourceSchemaName = foreignKeySet.getString("PKTABLE_SCHEM");
                foreignKeyData.sourceTableName = foreignKeySet.getString("PKTABLE_NAME");
                foreignKeyData.sourceColumnName = foreignKeySet.getString("PKCOLUMN_NAME");
                foreignKeyData.targetSchemaName = foreignKeySet.getString("FKTABLE_SCHEM");
                foreignKeyData.targetTableName = foreignKeySet.getString("FKTABLE_NAME");
                foreignKeyData.targetColumnName = foreignKeySet.getString("FKCOLUMN_NAME");
                foreignKeyData.updateRule = foreignKeySet.getShort("UPDATE_RULE");
                foreignKeyData.deleteRule = foreignKeySet.getShort("DELETE_RULE");

                if (isCyclicForeignKye(foreignKeyData)) {
                    continue;
                }

                final String key = dbSetting.getTableNameWithSchema(foreignKeyData.targetTableName, foreignKeyData.targetSchemaName);
                List<ForeignKeyData> foreignKeyDataList = tableForeignKeyDataMap.get(key);
                if (foreignKeyDataList == null) {
                    foreignKeyDataList = new ArrayList<>();
                    tableForeignKeyDataMap.put(key, foreignKeyDataList);
                }

                foreignKeyDataList.add(foreignKeyData);
            }
        } catch (final SQLException e) {
            tableForeignKeyDataMap = null;
        }
    }

    private void setForeignKeysUsingCash(ERTable target) throws SQLException {
        String tableName = target.getPhysicalName();
        final String schema = target.getTableViewProperties(dbSetting.getDbsystem()).getSchema();

        tableName = dbSetting.getTableNameWithSchema(tableName, schema);

        final List<ForeignKeyData> foreignKeyList = tableForeignKeyDataMap.get(tableName);

        if (foreignKeyList == null) {
            return;
        }

        final Map<String, List<ForeignKeyData>> sameNameForeignKeyDataMap = collectSameNameForeignKeyData(foreignKeyList);

        for (final Map.Entry<String, List<ForeignKeyData>> entry : sameNameForeignKeyDataMap.entrySet()) {
            createRelation(target, entry.getValue());
        }
    }

    private void setForeignKeys(ERTable target) throws SQLException {
        final String tableName = target.getPhysicalName();
        final String schemaName = target.getTableViewProperties(dbSetting.getDbsystem()).getSchema();

        try (ResultSet foreignKeySet = metaData.getImportedKeys(null, schemaName, tableName)) {
            final List<ForeignKeyData> foreignKeyList = new ArrayList<>();

            while (foreignKeySet.next()) {
                final ForeignKeyData foreignKeyData = new ForeignKeyData();

                foreignKeyData.name = foreignKeySet.getString("FK_NAME");
                foreignKeyData.sourceTableName = foreignKeySet.getString("PKTABLE_NAME");
                foreignKeyData.sourceSchemaName = foreignKeySet.getString("PKTABLE_SCHEM");
                foreignKeyData.sourceColumnName = foreignKeySet.getString("PKCOLUMN_NAME");
                foreignKeyData.targetSchemaName = foreignKeySet.getString("FKTABLE_SCHEM");
                foreignKeyData.targetColumnName = foreignKeySet.getString("FKCOLUMN_NAME");
                foreignKeyData.updateRule = foreignKeySet.getShort("UPDATE_RULE");
                foreignKeyData.deleteRule = foreignKeySet.getShort("DELETE_RULE");

                foreignKeyList.add(foreignKeyData);
            }

            if (foreignKeyList.isEmpty()) {
                return;
            }

            final Map<String, List<ForeignKeyData>> sameNameForeignKeyDataMap = collectSameNameForeignKeyData(foreignKeyList);

            for (final Map.Entry<String, List<ForeignKeyData>> entry : sameNameForeignKeyDataMap.entrySet()) {
                createRelation(target, entry.getValue());
            }
        } catch (final SQLException e) {
            // microsoft access does not support getImportedKeys
        }
    }

    private Map<String, List<ForeignKeyData>> collectSameNameForeignKeyData(List<ForeignKeyData> foreignKeyList) {
        final Map<String, List<ForeignKeyData>> map = new HashMap<>();

        for (final ForeignKeyData foreignKyeData : foreignKeyList) {
            List<ForeignKeyData> list = map.get(foreignKyeData.name);
            if (list == null) {
                list = new ArrayList<>();
                map.put(foreignKyeData.name, list);
            }

            list.add(foreignKyeData);
        }

        return map;
    }

    private Relationship createRelation(ERTable target, List<ForeignKeyData> foreignKeyDataList) {
        final ForeignKeyData representativeData = foreignKeyDataList.get(0);

        String sourceTableName = representativeData.sourceTableName;
        final String sourceSchemaName = representativeData.sourceSchemaName;

        sourceTableName = dbSetting.getTableNameWithSchema(sourceTableName, sourceSchemaName);

        final ERTable source = tableMap.get(sourceTableName);
        if (source == null) {
            return null;
        }

        boolean referenceForPK = true;

        final List<NormalColumn> primaryKeys = source.getPrimaryKeys();
        if (primaryKeys.size() != foreignKeyDataList.size()) {
            referenceForPK = false;
        }

        final Map<NormalColumn, NormalColumn> referenceMap = new HashMap<>();

        for (final ForeignKeyData foreignKeyData : foreignKeyDataList) {
            NormalColumn sourceColumn = null;

            for (final NormalColumn normalColumn : source.getNormalColumns()) {
                if (normalColumn.getPhysicalName().equals(foreignKeyData.sourceColumnName)) {
                    sourceColumn = normalColumn;
                    break;
                }
            }

            if (sourceColumn == null) {
                return null;
            }

            if (!sourceColumn.isPrimaryKey()) {
                referenceForPK = false;
            }

            NormalColumn targetColumn = null;

            for (final NormalColumn normalColumn : target.getNormalColumns()) {
                if (normalColumn.getPhysicalName().equals(foreignKeyData.targetColumnName)) {
                    targetColumn = normalColumn;
                    break;
                }
            }

            if (targetColumn == null) {
                return null;
            }

            referenceMap.put(sourceColumn, targetColumn);
        }

        CompoundUniqueKey referredComplexUniqueKey = null;
        NormalColumn referredSimpleUniqueColumn = null;

        if (!referenceForPK) {
            if (referenceMap.size() > 1) {
                referredComplexUniqueKey = new CompoundUniqueKey("");
                for (final NormalColumn column : referenceMap.keySet()) {
                    referredComplexUniqueKey.addColumn(column);
                }
                source.getCompoundUniqueKeyList().add(referredComplexUniqueKey);
            } else {
                referredSimpleUniqueColumn = referenceMap.keySet().iterator().next();
            }
        }

        final Relationship relation = new Relationship(referenceForPK, referredComplexUniqueKey, referredSimpleUniqueColumn);
        relation.setForeignKeyName(representativeData.name);
        relation.setSourceWalker(source);
        relation.setTargetWithoutForeignKey(target);

        String onUpdateAction = null;
        if (representativeData.updateRule == DatabaseMetaData.importedKeyCascade) {
            onUpdateAction = "CASCADE";
        } else if (representativeData.updateRule == DatabaseMetaData.importedKeyRestrict) {
            onUpdateAction = "RESTRICT";
        } else if (representativeData.updateRule == DatabaseMetaData.importedKeyNoAction) {
            onUpdateAction = "NO ACTION";
        } else if (representativeData.updateRule == DatabaseMetaData.importedKeySetDefault) {
            onUpdateAction = "SET DEFAULT";
        } else if (representativeData.updateRule == DatabaseMetaData.importedKeySetNull) {
            onUpdateAction = "SET NULL";
        } else {
            onUpdateAction = "";
        }

        relation.setOnUpdateAction(onUpdateAction);

        String onDeleteAction = null;
        if (representativeData.deleteRule == DatabaseMetaData.importedKeyCascade) {
            onDeleteAction = "CASCADE";
        } else if (representativeData.deleteRule == DatabaseMetaData.importedKeyRestrict) {
            onDeleteAction = "RESTRICT";
        } else if (representativeData.deleteRule == DatabaseMetaData.importedKeyNoAction) {
            onDeleteAction = "NO ACTION";
        } else if (representativeData.deleteRule == DatabaseMetaData.importedKeySetDefault) {
            onDeleteAction = "SET DEFAULT";
        } else if (representativeData.deleteRule == DatabaseMetaData.importedKeySetNull) {
            onDeleteAction = "SET NULL";
        } else {
            onDeleteAction = "";
        }

        relation.setOnDeleteAction(onDeleteAction);

        for (final Map.Entry<NormalColumn, NormalColumn> entry : referenceMap.entrySet()) {
            entry.getValue().addReference(entry.getKey(), relation);
        }

        return relation;
    }

    public List<ERTable> getImportedTables() {
        return importedTables;
    }

    public List<Sequence> getImportedSequences() {
        return importedSequences;
    }

    public List<ERView> getImportedViews() {
        return importedViews;
    }

    private List<ERView> importViews(List<DBObject> dbObjectList) throws SQLException {
        final List<ERView> list = new ArrayList<>();

        for (final DBObject dbObject : dbObjectList) {
            if (DBObject.TYPE_VIEW.equals(dbObject.getType())) {
                final String schema = dbObject.getSchema();
                final String name = dbObject.getName();

                final ERView view = importView(schema, name);

                if (view != null) {
                    list.add(view);
                }
            }
        }

        return list;
    }

    protected ERView importView(String schema, String viewName) throws SQLException {
        final String sql = getViewDefinitionSQL(schema);
        if (sql == null) {
            return null;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            if (schema != null) {
                stmt.setString(1, schema);
                stmt.setString(2, viewName);
            } else {
                stmt.setString(1, viewName);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    final ERView view = new ERView();
                    view.setPhysicalName(viewName);
                    view.setLogicalName(viewName);
                    final String definitionSQL = rs.getString(1);
                    view.setSql(definitionSQL);
                    view.getTableViewProperties().setSchema(schema);
                    final List<ERColumn> columnList = getViewColumnList(definitionSQL);
                    view.setColumns(columnList);
                    return view;
                }
            }
            return null;
        }
    }

    protected abstract String getViewDefinitionSQL(String schema);

    private List<ERColumn> getViewColumnList(String sql) {
        final List<ERColumn> columnList = new ArrayList<>();

        final String upperSql = sql.toUpperCase().replaceAll("\r?\n", " ").replaceAll("  +", " ");
        final int selectIndex = upperSql.indexOf("SELECT ");
        final int fromIndex = upperSql.indexOf(" FROM ");

        if (selectIndex == -1) {
            return null;
        }

        String columnsPart = null;
        String fromPart = null;
        if (fromIndex != -1) {
            columnsPart = sql.substring(selectIndex + "SELECT ".length(), fromIndex);
            fromPart = sql.substring(fromIndex + " FROM ".length());

        } else {
            columnsPart = sql.substring(selectIndex + "SELECT ".length());
            fromPart = "";
        }

        final int whereIndex = fromPart.toUpperCase().indexOf(" WHERE ");
        if (whereIndex != -1) {
            fromPart = fromPart.substring(0, whereIndex);
        }

        final Map<String, String> aliasTableMap = new HashMap<>();
        final StringTokenizer fromTokenizer = new StringTokenizer(fromPart, ",");
        while (fromTokenizer.hasMoreTokens()) {
            String tableName = fromTokenizer.nextToken().trim();

            tableName.replaceAll(" AS", "");
            tableName.replaceAll(" as", "");
            tableName.replaceAll(" As", "");
            tableName.replaceAll(" aS", "");

            String tableAlias = null;

            final int asIndex = tableName.toUpperCase().indexOf(" ");
            if (asIndex != -1) {
                tableAlias = tableName.substring(asIndex + 1).trim();
                tableName = tableName.substring(0, asIndex).trim();

                final int dotIndex = tableName.indexOf(".");
                if (dotIndex != -1) {
                    tableName = tableName.substring(dotIndex + 1);
                }

                aliasTableMap.put(tableAlias, tableName);
            }
        }

        final StringTokenizer columnTokenizer = new StringTokenizer(columnsPart, ",");
        String previousColumn = null;
        while (columnTokenizer.hasMoreTokens()) {
            String columnName = columnTokenizer.nextToken();

            if (previousColumn != null) {
                columnName = previousColumn + "," + columnName;
                previousColumn = null;
            }

            if (columnName.split("\\(").length > columnName.split("\\)").length) {
                previousColumn = columnName;
                continue;
            }

            columnName = columnName.trim();
            columnName = columnName.replaceAll("\"", "");

            String columnAlias = null;
            final Matcher matcher = AS_PATTERN.matcher(columnName);
            if (matcher.matches()) {
                columnAlias = matcher.toMatchResult().group(2).trim();
                columnName = matcher.toMatchResult().group(1).trim();
            } else {
                final int asIndex = columnName.indexOf(" ");
                if (asIndex != -1) {
                    columnAlias = columnName.substring(asIndex + 1).trim();
                    columnName = columnName.substring(0, asIndex).trim();
                }
            }

            int dotIndex = columnName.indexOf(".");
            String tableName = null;
            if (dotIndex != -1) {
                String aliasTableName = columnName.substring(0, dotIndex);
                columnName = columnName.substring(dotIndex + 1);

                dotIndex = columnName.indexOf(".");
                if (dotIndex != -1) {
                    aliasTableName = columnName.substring(0, dotIndex);
                    columnName = columnName.substring(dotIndex + 1);
                }

                tableName = aliasTableMap.get(aliasTableName);

                if (tableName == null) {
                    tableName = aliasTableName;
                }
            }

            if (columnAlias == null) {
                columnAlias = columnName;
            }

            NormalColumn targetColumn = null;

            if (columnName != null) {
                if (tableName != null) {
                    tableName = tableName.toLowerCase();
                }
                columnName = columnName.toLowerCase();

                if (!"*".equals(columnName)) {
                    for (final ERTable table : importedTables) {
                        if (tableName == null
                                || (table.getPhysicalName() != null && tableName.equals(table.getPhysicalName().toLowerCase()))) {
                            for (final NormalColumn column : table.getExpandedColumns()) {
                                if (column.getPhysicalName() != null && columnName.equals(column.getPhysicalName().toLowerCase())) {
                                    targetColumn = column;
                                    break;
                                }
                            }

                            if (targetColumn != null) {
                                break;
                            }
                        }
                    }

                    addColumnToView(columnList, targetColumn, columnAlias);
                } else {
                    for (final ERTable table : importedTables) {
                        if (tableName == null
                                || (table.getPhysicalName() != null && tableName.equals(table.getPhysicalName().toLowerCase()))) {
                            for (final NormalColumn column : table.getExpandedColumns()) {
                                addColumnToView(columnList, column, null);
                            }
                        }
                    }
                }
            }
        }

        return columnList;
    }

    private void addColumnToView(List<ERColumn> columnList, NormalColumn targetColumn, String columnAlias) {
        Word word = null;
        if (targetColumn != null) {
            word = new Word(targetColumn.getWord());
            if (columnAlias != null) {
                word.setPhysicalName(columnAlias);
            }
        } else {
            final TypeData emptyTypeData = new TypeData(null, null, false, null, false, null, false);
            word = new Word(columnAlias, columnAlias, null, emptyTypeData, null, null);
        }
        final UniqueWord uniqueWord = new UniqueWord(word);
        if (dictionary.get(uniqueWord) != null) {
            word = dictionary.get(uniqueWord);
        } else {
            dictionary.put(uniqueWord, word);
        }
        final NormalColumn column = new NormalColumn(word, false, false, false, false, null, null, null, null, null);
        columnList.add(column);
    }

    public List<Tablespace> getImportedTablespaces() {
        return importedTablespaces;
    }

    private List<Tablespace> importTablespaces(List<DBObject> dbObjectList) throws SQLException {
        final List<Tablespace> list = new ArrayList<>();
        for (final DBObject dbObject : dbObjectList) {
            if (DBObject.TYPE_TABLESPACE.equals(dbObject.getType())) {
                final String name = dbObject.getName();
                final Tablespace tablespace = importTablespace(name);
                if (tablespace != null) {
                    list.add(tablespace);
                }
            }
        }

        return list;
    }

    public List<Trigger> getImportedTriggers() {
        return importedTriggers;
    }

    protected Tablespace importTablespace(String tablespaceName) throws SQLException {
        return null;
    }

    public Exception getException() {
        return exception;
    }

    protected int getLength(String type, int size) {
        return size;
    }

    protected void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    protected void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }
}

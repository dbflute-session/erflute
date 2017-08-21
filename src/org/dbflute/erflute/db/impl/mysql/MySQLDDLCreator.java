package org.dbflute.erflute.db.impl.mysql;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class MySQLDDLCreator extends DDLCreator {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public MySQLDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    // ===================================================================================
    //                                                                            Drop DDL
    //                                                                            ========
    @Override
    public String prepareDropDDL(ERDiagram diagram) {
        final String dropDDL = super.prepareDropDDL(diagram);
        if (dropDDL.isEmpty()) {
            return dropDDL;
        }
        final StringBuilder ddl = new StringBuilder();
        ddl.append("SET SESSION FOREIGN_KEY_CHECKS=0");
        if (semicolon) {
            ddl.append(";");
        }
        ddl.append("\r\n");
        ddl.append(dropDDL);
        return ddl.toString();
    }

    @Override
    public String doBuildDropIndex(ERIndex index, ERTable table) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("DROP INDEX ");
        // MySQL supports "IF EXISTS" for only table so comment out here for now
        // https://github.com/dbflute-session/erflute/pull/34
        //ddl.append(getIfExistsOption());
        ddl.append(filter(index.getName()));
        ddl.append(" ON ");
        ddl.append(filter(table.getNameWithSchema(getDiagram().getDatabase())));
        if (semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                   Create Tablespace
    //                                                                   =================
    @Override
    protected String doBuildCreateTablespace(Tablespace tablespace) {
        final MySQLTablespaceProperties tablespaceProperties =
                (MySQLTablespaceProperties) tablespace.getProperties(environment, getDiagram());
        final StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLESPACE ");
        ddl.append(filter(tablespace.getName()));
        ddl.append("\r\n");
        ddl.append(" ADD DATAFILE '");
        ddl.append(tablespaceProperties.getDataFile());
        ddl.append("'\r\n");
        ddl.append(" USE LOGFILE GROUP ");
        ddl.append(tablespaceProperties.getLogFileGroup());
        ddl.append("\r\n");
        if (!Check.isEmpty(tablespaceProperties.getExtentSize())) {
            ddl.append(" EXTENT_SIZE ");
            ddl.append(tablespaceProperties.getExtentSize());
            ddl.append("\r\n");
        }
        ddl.append(" INITIAL_SIZE ");
        ddl.append(tablespaceProperties.getInitialSize());
        ddl.append("\r\n");
        ddl.append(" ENGINE ");
        ddl.append(tablespaceProperties.getEngine());
        ddl.append("\r\n");
        if (semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                        Create Table
    //                                                                        ============
    @Override
    protected String buildColumnPart(NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();
        final String description = normalColumn.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineColumnComment) {
            ddl.append("\t-- ");
            ddl.append(description.replaceAll("\n", "\n\t-- "));
            ddl.append("\r\n");
        }
        ddl.append("\t");
        ddl.append(filter(normalColumn.getPhysicalName()));
        ddl.append(" ");
        ddl.append(filter(Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), getDiagram().getDatabase())));
        if (!Check.isEmpty(normalColumn.getCharacterSet())) {
            ddl.append(" CHARACTER SET ");
            ddl.append(normalColumn.getCharacterSet());
            if (!Check.isEmpty(normalColumn.getCollation())) {
                ddl.append(" COLLATE ");
                ddl.append(normalColumn.getCollation());
            }
        }
        if (!Check.isEmpty(normalColumn.getDefaultValue())) {
            String defaultValue = normalColumn.getDefaultValue();
            if (DisplayMessages.getMessage("label.current.date.time").equals(defaultValue)) {
                defaultValue = getDBManager().getCurrentTimeValue()[0];
            }
            ddl.append(" DEFAULT ");
            if (doesNeedQuoteDefaultValue(normalColumn)) {
                ddl.append("'");
                ddl.append(Format.escapeSQL(defaultValue));
                ddl.append("'");
            } else {
                ddl.append(defaultValue);
            }
        }
        // from https://github.com/naoki-iwami/ermaster-b/commit/048b7fa7ffa3d6fd34c2918b44e73c7615aa2bfe
        String constraint = Format.null2blank(normalColumn.getConstraint());
        if ("BINARY".equalsIgnoreCase(constraint)) {
            ddl.append(" ");
            ddl.append(constraint);
            constraint = "";
        }
        if (normalColumn.isNotNull()) {
            ddl.append(" NOT NULL");
        }
        if (!"".equals(constraint)) {
            ddl.append(" ");
            ddl.append(constraint);
        }
        if (normalColumn.isUniqueKey()) {
            if (!Check.isEmpty(normalColumn.getUniqueKeyName())) {
                ddl.append(" CONSTRAINT ");
                ddl.append(normalColumn.getUniqueKeyName());
            }
            ddl.append(" UNIQUE");
        }
        if (normalColumn.isAutoIncrement()) {
            ddl.append(" AUTO_INCREMENT");
        }
        if (ddlTarget.createComment) {
            final String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);
            if (!Check.isEmpty(comment)) {
                ddl.append(" COMMENT '");
                ddl.append(comment.replaceAll("'", "''"));
                ddl.append("'");
            }
        }
        return ddl.toString();
    }

    @Override
    protected boolean doesNeedQuoteDefaultValue(NormalColumn normalColumn) {
        if (!super.doesNeedQuoteDefaultValue(normalColumn)) {
            return false;
        }
        if ("CURRENT_TIMESTAMP".equalsIgnoreCase(normalColumn.getDefaultValue().trim())) {
            return false;
        }
        return true;
    }

    @Override
    protected String getPrimaryKeyLength(ERTable table, NormalColumn primaryKey) {
        final SqlType type = primaryKey.getType();
        if (type != null && type.isFullTextIndexable() && !type.isNeedLength(getDiagram().getDatabase())) {
            Integer length = null;
            MySQLTableProperties tableProperties = (MySQLTableProperties) table.getTableViewProperties();
            length = tableProperties.getPrimaryKeyLengthOfText();
            if (length == null) {
                tableProperties = (MySQLTableProperties) getDiagram().getDiagramContents().getSettings().getTableViewProperties();
                length = tableProperties.getPrimaryKeyLengthOfText();
            }
            return "(" + length + ")";
        }
        return "";
    }

    @Override
    public String buildTableOptionPart(ERTable table) {
        final MySQLTableProperties commonTableProperties =
                (MySQLTableProperties) getDiagram().getDiagramContents().getSettings().getTableViewProperties();
        final MySQLTableProperties tableProperties = (MySQLTableProperties) table.getTableViewProperties();
        String engine = tableProperties.getStorageEngine();
        if (Check.isEmpty(engine)) {
            engine = commonTableProperties.getStorageEngine();
        }
        String characterSet = tableProperties.getCharacterSet();
        if (Check.isEmpty(characterSet)) {
            characterSet = commonTableProperties.getCharacterSet();
        }
        final String collation = tableProperties.getCollation();
        if (Check.isEmpty(collation)) {
            characterSet = commonTableProperties.getCharacterSet();
        }
        final StringBuilder postDDL = new StringBuilder();
        if (!Check.isEmpty(engine)) {
            postDDL.append(" ENGINE = ");
            postDDL.append(engine);
        }
        if (ddlTarget.createComment) {
            final String comment = filterComment(table.getLogicalName(), table.getDescription(), false);
            if (!Check.isEmpty(comment)) {
                postDDL.append(" COMMENT = '");
                postDDL.append(comment.replaceAll("'", "''"));
                postDDL.append("'");
            }
        }
        if (!Check.isEmpty(characterSet)) {
            postDDL.append(" DEFAULT CHARACTER SET ");
            postDDL.append(characterSet);
            if (!Check.isEmpty(collation)) {
                postDDL.append(" COLLATE ");
                postDDL.append(collation);
            }
        }
        postDDL.append(super.buildTableOptionPart(table));
        return postDDL.toString();
    }

    // ===================================================================================
    //                                                                        Create Index
    //                                                                        ============
    @Override
    public String doBuildCreateIndex(ERIndex index, ERTable table) {
        final StringBuilder ddl = new StringBuilder();
        final String description = index.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(description.replaceAll("\n", "\n-- "));
            ddl.append("\r\n");
        }
        ddl.append("CREATE ");
        if (!index.isNonUnique()) {
            ddl.append("UNIQUE ");
        }
        ddl.append("INDEX ");
        ddl.append(filter(index.getName()));
        if (index.getType() != null && !index.getType().trim().equals("")) {
            ddl.append(" USING ");
            ddl.append(index.getType().trim());
        }
        ddl.append(" ON ");
        ddl.append(filter(table.getNameWithSchema(getDiagram().getDatabase())));
        ddl.append(" (");
        boolean first = true;
        int i = 0;
        final List<Boolean> descs = index.getDescs();
        for (final NormalColumn column : index.getColumns()) {
            if (!first) {
                ddl.append(", ");
            }
            ddl.append(filter(column.getPhysicalName()));
            if (getDBManager().isSupported(DBManager.SUPPORT_DESC_INDEX)) {
                if (descs.size() > i) {
                    final Boolean desc = descs.get(i);
                    if (Boolean.TRUE.equals(desc)) {
                        ddl.append(" DESC");
                    } else {
                        ddl.append(" ASC");
                    }
                }
            }
            first = false;
            i++;
        }
        ddl.append(")");
        if (semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    @Override
    protected String filterComment(String logicalName, String description, boolean column) {
        String comment = null;
        if (ddlTarget.commentValueLogicalNameDescription) {
            comment = Format.null2blank(logicalName);
            if (!Check.isEmpty(description)) {
                comment = comment + " : " + Format.null2blank(description);
            }
        } else if (ddlTarget.commentValueLogicalName) {
            comment = Format.null2blank(logicalName);
        } else {
            comment = Format.null2blank(description);
        }
        if (ddlTarget.commentReplaceLineFeed) {
            comment = comment.replaceAll("\r\n", Format.null2blank(ddlTarget.commentReplaceString));
            comment = comment.replaceAll("\r", Format.null2blank(ddlTarget.commentReplaceString));
            comment = comment.replaceAll("\n", Format.null2blank(ddlTarget.commentReplaceString));
        }
        // DB comment size has been increased since MySQL-5.5.3 like this:
        //   table  : 60 -> 2048
        //   column : 255 -> 1024
        // *http://dev.mysql.com/doc/refman/5.5/en/create-table.html
        //
        // actual test result in Server version: 5.5.19-log MySQL Community Server (GPL)
        //   Caused by: java.sql.SQLException: Comment for table 'member' is too long (max = 2048)
        //   Caused by: java.sql.SQLException: Comment for field 'REGISTER_DATETIME' is too long (max = 1024)
        //
        // ERMaster-b is forked product
        // so no problem basically for new version
        int maxLength = 2048;
        if (column) {
            maxLength = 1024;
        }
        if (comment.length() > maxLength) {
            comment = comment.substring(0, maxLength);
        }
        return comment;
    }

    @Override
    public String getIfExistsOption() {
        return "IF EXISTS "; // MySQL supports "IF EXISTS" for "drop table"
    }
}

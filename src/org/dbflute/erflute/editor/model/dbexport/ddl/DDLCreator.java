package org.dbflute.erflute.editor.model.dbexport.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.model.settings.Settings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class DDLCreator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String LN = "\r\n";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ERDiagram diagram;
    protected boolean semicolon;
    protected Environment environment;
    protected DDLTarget ddlTarget;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DDLCreator(ERDiagram diagram, boolean semicolon) {
        this.diagram = diagram;
        this.semicolon = semicolon;
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void init(Environment environment, DDLTarget ddlTarget) {
        this.environment = environment;
        this.ddlTarget = ddlTarget;
    }

    // ===================================================================================
    //                                                                    Prepare Drop DDL
    //                                                                    ================
    public String prepareDropDDL(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        if (ddlTarget.dropIndex) {
            ddl.append(buildDropIndexes(diagram));
        }
        if (ddlTarget.dropView) {
            ddl.append(buildDropViews(diagram));
        }
        if (ddlTarget.dropTrigger) {
            ddl.append(buildDropTriggers(diagram));
        }
        if (ddlTarget.dropTable) {
            ddl.append(buildDropTables(diagram));
        }
        if (ddlTarget.dropSequence && DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            ddl.append(buildDropSequences(diagram));
        }
        if (ddlTarget.dropTablespace) {
            ddl.append(buildDropTablespaces(diagram));
        }
        return ddl.toString();
    }

    private String buildDropViews(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            if (first) {
                ddl.append("\r\n/* Drop Views */\r\n\r\n");
                first = false;
            }
            ddl.append(this.doBuildDropView(view));
            ddl.append(LN);
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildDropView(ERView view) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("DROP VIEW ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(this.getNameWithSchema(view.getTableViewProperties().getSchema(), view.getPhysicalName())));
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    private String buildDropTriggers(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            if (first) {
                ddl.append("\r\n/* Drop Triggers */\r\n\r\n");
                first = false;
            }
            ddl.append(this.doBuildDropTrigger(trigger));
            ddl.append(LN);
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildDropTrigger(Trigger trigger) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("DROP TRIGGER ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(trigger.getName()));
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    private String buildDropTables(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        final Set<TableView> doneTables = new HashSet<TableView>();
        boolean first = true;
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (diagram.getCurrentCategory() != null && !diagram.getCurrentCategory().contains(table)) {
                continue;
            }
            if (first) {
                ddl.append("\r\n/* Drop Tables */\r\n\r\n");
                first = false;
            }
            if (!doneTables.contains(table)) {
                ddl.append(this.doBuildDropTable(table, doneTables));
            }
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildDropTable(TableView table, Set<TableView> doneTables) {
        final StringBuilder ddl = new StringBuilder();
        doneTables.add(table);
        for (final Relationship relation : table.getOutgoingRelationshipList()) {
            final TableView targetTableView = relation.getTargetTableView();
            if (!doneTables.contains(targetTableView)) {
                doneTables.add(targetTableView);
                final String targetTableDDL = this.doBuildDropTable(targetTableView, doneTables);
                ddl.append(targetTableDDL);
            }
        }
        ddl.append("DROP TABLE ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(table.getNameWithSchema(diagram.getDatabase())));
        if (this.semicolon) {
            ddl.append(";");
        }
        ddl.append(LN);
        return ddl.toString();
    }

    private String buildDropSequences(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            if (first) {
                ddl.append("\r\n/* Drop Sequences */\r\n\r\n");
                first = false;
            }
            ddl.append(doBuildDropSequence(sequence));
            ddl.append(LN);
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildDropSequence(Sequence sequence) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("DROP ");
        ddl.append("SEQUENCE ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(this.getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    private String buildDropTablespaces(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        if (this.getDBManager().createTablespaceProperties() != null) {
            for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet()) {
                if (first) {
                    ddl.append("\r\n/* Drop Tablespaces */\r\n\r\n");
                    first = false;
                }
                ddl.append(this.doBuildDropTablespace(tablespace));
                ddl.append(LN);
                ddl.append(LN);
                ddl.append(LN);
            }
        }

        return ddl.toString();
    }

    protected String doBuildDropTablespace(Tablespace tablespace) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("DROP ");
        ddl.append("TABLESPACE ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(tablespace.getName()));
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    private String buildDropIndexes(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (diagram.getCurrentCategory() != null && !diagram.getCurrentCategory().contains(table)) {
                continue;
            }
            for (final ERIndex index : table.getIndexes()) {
                if (first) {
                    ddl.append("\r\n/* Drop Indexes */\r\n\r\n");
                    first = false;
                }
                ddl.append(this.doBuildDropIndex(index, table));
                ddl.append(LN);
            }
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildDropIndex(ERIndex index, ERTable table) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("DROP INDEX ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(index.getName()));
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                  Prepare Create DDL
    //                                                                  ==================
    public String prepareCreateDDL(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        if (this.ddlTarget.createTablespace) {
            ddl.append(buildCreateTablespaces(diagram));
        }
        if (this.ddlTarget.createSequence && DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            ddl.append(buildCreateSequences(diagram));
        }
        if (this.ddlTarget.createTable) {
            ddl.append(buildCreateTables(diagram));
        }
        if (this.ddlTarget.createIndex) {
            ddl.append(buildCreateIndexes(diagram));
        }
        if (this.ddlTarget.createForeignKey) {
            ddl.append(buildCreateForeignKeys(diagram));
        }
        if (this.ddlTarget.createTrigger) {
            ddl.append(buildCreateTriggers(diagram));
        }
        if (this.ddlTarget.createView) {
            ddl.append(buildCreateViews(diagram));
        }
        if (this.ddlTarget.createComment) {
            ddl.append(buildCreateComment(diagram));
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                   Create Tablespace
    //                                                                   =================
    private String buildCreateTablespaces(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        if (this.getDBManager().createTablespaceProperties() != null) {
            for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet()) {
                if (first) {
                    ddl.append("\r\n/* Create Tablespaces */\r\n\r\n");
                    first = false;
                }
                final String description = tablespace.getDescription();
                if (this.semicolon && !Check.isEmpty(description) && this.ddlTarget.inlineTableComment) {
                    ddl.append("-- ");
                    ddl.append(description.replaceAll("\n", "\n-- "));
                    ddl.append(LN);
                }
                ddl.append(doBuildCreateTablespace(tablespace));
                ddl.append(LN);
                ddl.append(LN);
                ddl.append(LN);
            }
        }
        return ddl.toString();
    }

    protected abstract String doBuildCreateTablespace(Tablespace object);

    // ===================================================================================
    //                                                                     Create Sequence
    //                                                                     ===============
    private String buildCreateSequences(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        final List<String> autoSequenceNames =
                diagram.getDiagramContents().getDiagramWalkers().getTableSet().getAutoSequenceNames(diagram.getDatabase());
        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            final String sequenceName = this.getNameWithSchema(sequence.getSchema(), sequence.getName()).toUpperCase();
            if (autoSequenceNames.contains(sequenceName)) {
                continue;
            }
            if (first) {
                ddl.append("\r\n/* Create Sequences */\r\n\r\n");
                first = false;
            }
            ddl.append(doBuildCreateSequence(sequence));
            ddl.append(LN);
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildCreateSequence(Sequence sequence) {
        final StringBuilder ddl = new StringBuilder();
        final String description = sequence.getDescription();
        if (this.semicolon && !Check.isEmpty(description) && this.ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(description.replaceAll("\n", "\n-- "));
            ddl.append(LN);
        }
        ddl.append("CREATE ");
        ddl.append("SEQUENCE ");
        ddl.append(filter(this.getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (sequence.getIncrement() != null) {
            ddl.append(" INCREMENT ");
            ddl.append(sequence.getIncrement());
        }
        if (sequence.getMinValue() != null) {
            ddl.append(" MINVALUE ");
            ddl.append(sequence.getMinValue());
        }
        if (sequence.getMaxValue() != null) {
            ddl.append(" MAXVALUE ");
            ddl.append(sequence.getMaxValue());
        }
        if (sequence.getStart() != null) {
            ddl.append(" START ");
            ddl.append(sequence.getStart());
        }
        if (sequence.getCache() != null) {
            ddl.append(" CACHE ");
            ddl.append(sequence.getCache());
        }
        if (sequence.isCycle()) {
            ddl.append(" CYCLE");
        }
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                        Create Table
    //                                                                        ============
    private String buildCreateTables(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final ERTable table : this.getTablesForCreateDDL()) {
            if (diagram.getCurrentCategory() != null && !diagram.getCurrentCategory().contains(table)) {
                continue;
            }
            if (first) {
                ddl.append("\r\n/* Create Tables */\r\n\r\n");
                first = false;
            }
            ddl.append(doBuildCreateTable(table));
            ddl.append(LN);
            ddl.append(LN);
            ddl.append(LN);
            ddl.append(getTableSettingDDL(table));
        }
        return ddl.toString();
    }

    protected Iterable<ERTable> getTablesForCreateDDL() {
        return diagram.getDiagramContents().getDiagramWalkers().getTableSet();
    }

    protected String getTableSettingDDL(ERTable table) {
        return "";
    }

    // -----------------------------------------------------
    //                                      Create one Table
    //                                      ----------------
    protected String doBuildCreateTable(ERTable table) {
        final StringBuilder ddl = new StringBuilder();
        final String tableDescription = table.getDescription();
        if (this.semicolon && !Check.isEmpty(tableDescription) && this.ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(tableDescription.replaceAll("\n", "\n-- "));
            ddl.append(LN);
        }
        ddl.append("CREATE TABLE ");
        ddl.append(filter(table.getNameWithSchema(diagram.getDatabase())));
        ddl.append("\r\n(\r\n");
        boolean first = true;
        for (final ERColumn column : table.getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;
                if (!first) {
                    ddl.append(",\r\n");
                }
                ddl.append(buildColumnPart(normalColumn));
                first = false;
            } else {
                final ColumnGroup columnGroup = (ColumnGroup) column;
                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    if (!first) {
                        ddl.append(",\r\n");
                    }
                    ddl.append(buildColumnPart(normalColumn));
                    first = false;
                }
            }
        }
        ddl.append(buildPrimaryKeyPart(table));
        final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
        for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
            ddl.append(",\r\n");
            ddl.append("\t");
            if (!Check.isEmpty(complexUniqueKey.getUniqueKeyName())) {
                ddl.append("CONSTRAINT ");
                ddl.append(complexUniqueKey.getUniqueKeyName());
                ddl.append(" ");
            }
            ddl.append("UNIQUE (");
            first = true;
            for (final NormalColumn column : complexUniqueKey.getColumnList()) {
                if (!first) {
                    ddl.append(", ");
                }
                ddl.append(filter(column.getPhysicalName()));
                first = false;
            }
            ddl.append(")");
        }
        String constraint = Format.null2blank(table.getConstraint()).trim();
        if (!"".equals(constraint)) {
            constraint = constraint.replaceAll(LN, "\r\n\t");
            ddl.append(",\r\n");
            ddl.append("\t");
            ddl.append(constraint);
        }
        ddl.append(LN);
        ddl.append(")");
        ddl.append(this.buildTableOptionPart(table));
        final String option = Format.null2blank(table.getOption()).trim();
        if (!"".equals(option)) {
            ddl.append(LN);
            ddl.append(option);
        }
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // -----------------------------------------------------
    //                                          Table Column
    //                                          ------------
    protected String buildColumnPart(NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();
        final String description = normalColumn.getDescription();
        if (this.semicolon && !Check.isEmpty(description) && this.ddlTarget.inlineColumnComment) {
            ddl.append("\t-- ");
            ddl.append(description.replaceAll("\n", "\n\t-- "));
            ddl.append(LN);
        }
        ddl.append("\t");
        ddl.append(filter(normalColumn.getPhysicalName()));
        ddl.append(" ");
        ddl.append(filter(Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), diagram.getDatabase())));
        if (!Check.isEmpty(normalColumn.getDefaultValue())) {
            String defaultValue = normalColumn.getDefaultValue();
            if (DisplayMessages.getMessage("label.current.date.time").equals(defaultValue)) {
                defaultValue = this.getDBManager().getCurrentTimeValue()[0];
            }
            ddl.append(" DEFAULT ");
            if (this.doesNeedQuoteDefaultValue(normalColumn)) {
                ddl.append("'");
                ddl.append(Format.escapeSQL(defaultValue));
                ddl.append("'");
            } else {
                ddl.append(defaultValue);
            }
        }
        if (normalColumn.isNotNull()) {
            ddl.append(" NOT NULL");
        }
        if (normalColumn.isUniqueKey()) {
            if (!Check.isEmpty(normalColumn.getUniqueKeyName())) {
                ddl.append(" CONSTRAINT ");
                ddl.append(normalColumn.getUniqueKeyName());
            }
            ddl.append(" UNIQUE");
        }
        final String constraint = Format.null2blank(normalColumn.getConstraint());
        if (!"".equals(constraint)) {
            ddl.append(" ");
            ddl.append(constraint);
        }
        return ddl.toString();
    }

    protected boolean doesNeedQuoteDefaultValue(NormalColumn normalColumn) {
        if (normalColumn.getType().isNumber()) {
            return false;
        }
        if (normalColumn.getType().isTimestamp()) {
            if (!Character.isDigit(normalColumn.getDefaultValue().toCharArray()[0])) {
                return false;
            }
        }
        return true;
    }

    // -----------------------------------------------------
    //                                           Primary Key
    //                                           -----------
    protected String buildPrimaryKeyPart(ERTable table) {
        final StringBuilder ddl = new StringBuilder();
        final List<NormalColumn> primaryKeys = table.getPrimaryKeys();
        if (primaryKeys.size() != 0) {
            ddl.append(",\r\n");
            ddl.append("\t");
            if (!Check.isEmpty(table.getPrimaryKeyName())) {
                ddl.append("CONSTRAINT ");
                ddl.append(table.getPrimaryKeyName());
                ddl.append(" ");
            }
            ddl.append("PRIMARY KEY (");
            boolean first = true;
            for (final NormalColumn primaryKey : primaryKeys) {
                if (!first) {
                    ddl.append(", ");
                }
                ddl.append(filter(primaryKey.getPhysicalName()));
                ddl.append(getPrimaryKeyLength(table, primaryKey));
                first = false;
            }
            ddl.append(")");
        }
        return ddl.toString();
    }

    protected String getPrimaryKeyLength(ERTable table, NormalColumn primaryKey) {
        return "";
    }

    // -----------------------------------------------------
    //                                          Table Option
    //                                          ------------
    protected String buildTableOptionPart(ERTable table) {
        final TableViewProperties commonTableProperties = this.getDiagram().getDiagramContents().getSettings().getTableViewProperties();
        final TableProperties tableProperties = (TableProperties) table.getTableViewProperties();
        Tablespace tableSpace = tableProperties.getTableSpace();
        if (tableSpace == null) {
            tableSpace = commonTableProperties.getTableSpace();
        }
        final StringBuilder postDDL = new StringBuilder();
        if (tableSpace != null) {
            postDDL.append(" TABLESPACE ");
            postDDL.append(tableSpace.getName());
        }
        return postDDL.toString();
    }

    // ===================================================================================
    //                                                                        Create Index
    //                                                                        ============
    private String buildCreateIndexes(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (diagram.getCurrentCategory() != null && !diagram.getCurrentCategory().contains(table)) {
                continue;
            }
            for (final ERIndex index : table.getIndexes()) {
                if (first) {
                    ddl.append("\r\n/* Create Indexes */\r\n\r\n");
                    first = false;
                }
                ddl.append(doBuildCreateIndex(index, table));
                ddl.append(LN);
            }
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildCreateIndex(ERIndex index, ERTable table) {
        final StringBuilder ddl = new StringBuilder();
        final String description = index.getDescription();
        if (this.semicolon && !Check.isEmpty(description) && this.ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(description.replaceAll("\n", "\n-- "));
            ddl.append(LN);
        }
        ddl.append("CREATE ");
        if (!index.isNonUnique()) {
            ddl.append("UNIQUE ");
        }
        ddl.append("INDEX ");
        ddl.append(filter(index.getName()));
        ddl.append(" ON ");
        ddl.append(filter(table.getNameWithSchema(diagram.getDatabase())));
        if (index.getType() != null && !index.getType().trim().equals("")) {
            ddl.append(" USING ");
            ddl.append(index.getType().trim());
        }
        ddl.append(" (");
        boolean first = true;
        int i = 0;
        final List<Boolean> descs = index.getDescs();
        for (final NormalColumn column : index.getColumns()) {
            if (!first) {
                ddl.append(", ");
            }
            ddl.append(filter(column.getPhysicalName()));
            if (this.getDBManager().isSupported(DBManager.SUPPORT_DESC_INDEX)) {
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
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                   Create ForeignKey
    //                                                                   =================
    protected String buildCreateForeignKeys(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (diagram.getCurrentCategory() != null && !diagram.getCurrentCategory().contains(table)) {
                continue;
            }
            for (final Relationship relation : table.getOutgoingRelationshipList()) {
                if (first) {
                    ddl.append("\r\n/* Create Foreign Keys */\r\n\r\n");
                    first = false;
                }
                ddl.append(doBuildCreateForeignKey(relation));
                ddl.append(LN);
                ddl.append(LN);
                ddl.append(LN);
            }
        }
        return ddl.toString();
    }

    protected String doBuildCreateForeignKey(Relationship relationship) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("ALTER TABLE ");
        ddl.append(filter(relationship.getTargetTableView().getNameWithSchema(diagram.getDatabase())));
        ddl.append(LN);
        ddl.append("\tADD ");
        if (relationship.getForeignKeyName() != null && !relationship.getForeignKeyName().trim().equals("")) {
            ddl.append("CONSTRAINT ");
            ddl.append(filter(relationship.getForeignKeyName()));
            ddl.append(" ");
        }
        ddl.append("FOREIGN KEY (");
        boolean first = true;
        for (final NormalColumn column : relationship.getForeignKeyColumns()) {
            if (!first) {
                ddl.append(", ");
            }
            ddl.append(filter(column.getPhysicalName()));
            first = false;
        }
        ddl.append(")\r\n");
        ddl.append("\tREFERENCES ");
        ddl.append(filter(relationship.getSourceTableView().getNameWithSchema(diagram.getDatabase())));
        ddl.append(" (");
        first = true;
        for (final NormalColumn foreignKeyColumn : relationship.getForeignKeyColumns()) {
            if (!first) {
                ddl.append(", ");
            }
            ddl.append(filter(foreignKeyColumn.getReferencedColumn(relationship).getPhysicalName()));
            first = false;
        }
        ddl.append(")\r\n");
        ddl.append("\tON UPDATE ");
        ddl.append(filter(relationship.getOnUpdateAction()));
        ddl.append(LN);
        ddl.append("\tON DELETE ");
        ddl.append(filter(relationship.getOnDeleteAction()));
        ddl.append(LN);
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                      Create Trigger
    //                                                                      ==============
    private String buildCreateTriggers(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            if (first) {
                ddl.append("\r\n/* Create Triggers */\r\n\r\n");
                first = false;
            }
            ddl.append(doBuildCreateTrigger(trigger));
            ddl.append(LN);
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildCreateTrigger(Trigger trigger) {
        final StringBuilder ddl = new StringBuilder();
        final String description = trigger.getDescription();
        if (this.semicolon && !Check.isEmpty(description) && this.ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(description.replaceAll("\n", "\n-- "));
            ddl.append(LN);
        }
        ddl.append("CREATE TRIGGER ");
        ddl.append(filter(getNameWithSchema(trigger.getSchema(), trigger.getName())));
        ddl.append(" ");
        ddl.append(filter(trigger.getSql()));
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                         Create View
    //                                                                         ===========
    private String buildCreateViews(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            if (first) {
                ddl.append("\r\n/* Create Views */\r\n\r\n");
                first = false;
            }
            ddl.append(doBuildCreateView(view));
            ddl.append(LN);
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected String doBuildCreateView(ERView view) {
        final StringBuilder ddl = new StringBuilder();
        final String description = view.getDescription();
        if (this.semicolon && !Check.isEmpty(description) && this.ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(description.replaceAll("\n", "\n-- "));
            ddl.append(LN);
        }
        ddl.append("CREATE VIEW ");
        ddl.append(filter(this.getNameWithSchema(view.getTableViewProperties().getSchema(), view.getPhysicalName())));
        ddl.append(" AS ");
        String sql = filter(view.getSql());
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        ddl.append(sql);
        if (this.semicolon) {
            ddl.append(";");
        }
        return ddl.toString();
    }

    // ===================================================================================
    //                                                                      Create Comment
    //                                                                      ==============
    private String buildCreateComment(ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        boolean first = true;
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (diagram.getCurrentCategory() != null && !diagram.getCurrentCategory().contains(table)) {
                continue;
            }
            final List<String> commentDDLList = doBuildCreateComment(table);
            if (!commentDDLList.isEmpty()) {
                if (first) {
                    ddl.append("\r\n/* Comments */\r\n\r\n");
                    first = false;
                }
                for (final String commentDDL : commentDDLList) {
                    ddl.append(commentDDL);
                    ddl.append(LN);
                }
            }
        }
        if (!first) {
            ddl.append(LN);
            ddl.append(LN);
        }
        return ddl.toString();
    }

    protected List<String> doBuildCreateComment(ERTable table) {
        return new ArrayList<String>();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected String getNameWithSchema(String schema, String name) {
        final StringBuilder sb = new StringBuilder();
        if (Check.isEmpty(schema)) {
            schema = this.getDiagram().getDiagramContents().getSettings().getTableViewProperties().getSchema();
        }
        if (!Check.isEmpty(schema)) {
            sb.append(schema);
            sb.append(".");
        }
        sb.append(name);
        return sb.toString();
    }

    public String getIfExistsOption() { // called by e.g. PreTableExportManager
        return "";
    }

    protected String filter(String str) {
        if (str == null) {
            return "";
        }
        final Settings settings = diagram.getDiagramContents().getSettings();
        if (settings.isCapital()) {
            return str.toUpperCase();
        }
        return str;
    }

    protected String filterComment(String logicalName, String description, boolean column) {
        String comment = null;
        if (this.ddlTarget.commentValueLogicalNameDescription) {
            comment = Format.null2blank(logicalName);
            if (!Check.isEmpty(description)) {
                comment = comment + " : " + Format.null2blank(description);
            }
        } else if (this.ddlTarget.commentValueLogicalName) {
            comment = Format.null2blank(logicalName);
        } else {
            comment = Format.null2blank(description);
        }
        if (ddlTarget.commentReplaceLineFeed) {
            comment = comment.replaceAll(LN, Format.null2blank(ddlTarget.commentReplaceString));
            comment = comment.replaceAll("\r", Format.null2blank(ddlTarget.commentReplaceString));
            comment = comment.replaceAll("\n", Format.null2blank(ddlTarget.commentReplaceString));
        }
        return comment;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected DBManager getDBManager() {
        return DBManagerFactory.getDBManager(diagram);
    }

    protected ERDiagram getDiagram() {
        return diagram;
    }
}

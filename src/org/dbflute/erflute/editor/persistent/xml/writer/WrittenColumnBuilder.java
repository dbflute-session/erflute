package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenColumnBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenSequenceBuilder sequenceBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenColumnBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenSequenceBuilder sequenceBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.sequenceBuilder = sequenceBuilder;
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    public String buildColumns(List<ERColumn> columns, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<columns>\n");
        for (final ERColumn column : columns) {
            if (column instanceof ColumnGroup) {
                xml.append(tab(setupColumnsColumnGroup((ColumnGroup) column, context)));
            } else if (column instanceof NormalColumn) {
                xml.append(tab(setupNormalColumn((NormalColumn) column, context)));
            }
        }
        xml.append("</columns>\n");
        return xml.toString();
    }

    private String setupColumnsColumnGroup(ColumnGroup columnGroup, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_group>").append(context.columnGroupMap.get(columnGroup)).append("</column_group>\n");
        return xml.toString();
    }

    private String setupNormalColumn(NormalColumn normalColumn, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<normal_column>\n");
        // #for_erflute not persist word info to immobilize XML
        //final Integer wordId = context.wordMap.get(normalColumn.getWord());
        //if (wordId != null) {
        //    xml.append("\t<word_id>").append(wordId).append("</word_id>\n");
        //}
        final Word word = normalColumn.getWord();
        setupName(normalColumn, xml, word); // name first to be read-able XML e.g. physical_name
        setupType(normalColumn, xml, word); // e.g. type, length
        if (context != null) { // needed? by jflute
            setupRelationship(normalColumn, context, xml);
        }
        setupComment(normalColumn, xml, word); // e.g. description
        if (context != null) { // needed? by jflute
            setupId(normalColumn, context, xml);
        }
        setupGeneralConstraint(normalColumn, xml);
        setupCharacterSettings(normalColumn, xml);
        // nobody read in old age: referenced_column and relation are saved instead
        //xml.append("\t<foreign_key>").append(normalColumn.isForeignKey()).append("</foreign_key>\n");
        setupAutoIncrementSetting(normalColumn, xml);
        xml.append("</normal_column>\n");
        return xml.toString();
    }

    private void setupName(NormalColumn normalColumn, StringBuilder xml, Word word) {
        // not write if empty or false to slim XML
        final String physicalName = derivePhysicalName(normalColumn, word);
        final String logicalName = word != null ? word.getLogicalName() : normalColumn.getForeignKeyLogicalName();
        xml.append("\t<physical_name>").append(escape(physicalName)).append("</physical_name>\n");
        if (Srl.is_NotNull_and_NotEmpty(logicalName)) { // not required
            xml.append("\t<logical_name>").append(escape(logicalName)).append("</logical_name>\n");
        }
    }

    private String derivePhysicalName(NormalColumn normalColumn, Word word) {
        if (word != null) {
            return word.getPhysicalName();
        }
        final String foreignKeyPhysicalName = normalColumn.getForeignKeyPhysicalName();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(foreignKeyPhysicalName)) {
            return foreignKeyPhysicalName;
        }
        final NormalColumn firstReferencedColumn = normalColumn.getFirstReferredColumn();
        if (firstReferencedColumn != null) {
            return firstReferencedColumn.getPhysicalName();
        } else { // no way but you can save
            return "#error:unknownColumn";
        }
    }

    private void setupType(NormalColumn normalColumn, final StringBuilder xml, final Word word) {
        if (!normalColumn.isForeignKey()) { // FK column depends on target PK
            xml.append("\t<type>").append(prepareType(normalColumn)).append("</type>\n");
            setupTypeData(xml, word);
        }
    }

    private String prepareType(NormalColumn normalColumn) {
        String type = "";
        final SqlType sqlType = normalColumn.getType();
        if (sqlType != null) {
            type = sqlType.getId();
        }
        return type;
    }

    private void setupTypeData(StringBuilder xml, Word word) {
        // not write if empty or false to slim XML
        final TypeData typeData = word.getTypeData();
        final Integer length = word != null ? typeData.getLength() : null;
        final Integer decimal = word != null ? typeData.getDecimal() : null;
        final boolean array = word != null ? typeData.isArray() : null;
        final Integer arrayDimension = word != null ? typeData.getArrayDimension() : null;
        final boolean unsigned = word != null ? typeData.isUnsigned() : null;
        final String args = word != null ? typeData.getArgs() : null;
        if (length != null) {
            xml.append("\t<length>").append(escape(length)).append("</length>\n");
        }
        if (decimal != null) {
            xml.append("\t<decimal>").append(escape(decimal)).append("</decimal>\n");
        }
        if (array) {
            xml.append("\t<array>").append(escape(array)).append("</array>\n");
        }
        if (arrayDimension != null) {
            xml.append("\t<arrayDimension>").append(escape(arrayDimension)).append("</arrayDimension>\n");
        }
        if (unsigned) {
            xml.append("\t<unsigned>").append(escape(unsigned)).append("</unsigned>\n");
        }
        if (Srl.is_NotNull_and_NotEmpty(args)) {
            xml.append("\t<args>").append(escape(args)).append("</args>\n");
        }
        // TODO jflute xxxxxxxxxxx (2016/10/28)
        if (true) {
            xml.append("\t<char_semantics>").append(true).append("</char_semantics>\n");
        }
    }

    private void setupComment(NormalColumn normalColumn, final StringBuilder xml, final Word word) {
        final String description = word != null ? word.getDescription() : normalColumn.getForeignKeyDescription();
        if (Srl.is_NotNull_and_NotEmpty(description)) {
            xml.append("\t<description>").append(escape(description)).append("</description>\n");
        }
    }

    private void setupId(NormalColumn normalColumn, PersistentContext context, final StringBuilder xml) {
        // not use serial ID to immobilize XML
        //xml.append("\t<id>").append(context.columnMap.get(normalColumn)).append("</id>\n");
    }

    private void setupRelationship(NormalColumn normalColumn, PersistentContext context, final StringBuilder xml) {
        for (final NormalColumn referencedColumn : normalColumn.getReferencedColumnList()) {
            final String columnId = Format.toString(context.columnMap.get(referencedColumn));
            xml.append("\t<referred_column>").append(columnId).append("</referred_column>\n"); // #for_erflute rename to 'referred'
        }
        for (final Relationship relation : normalColumn.getRelationshipList()) {
            final String relationId = context.connectionMap.get(relation);
            xml.append("\t<relationship>").append(relationId).append("</relationship>\n"); // #for_erflute rename to relationship
        }
    }

    private void setupGeneralConstraint(NormalColumn normalColumn, final StringBuilder xml) {
        // not write if empty or false to slim XML
        final boolean notNull = normalColumn.isNotNull();
        if (notNull) {
            xml.append("\t<not_null>").append(notNull).append("</not_null>\n");
        }
        final boolean primaryKey = normalColumn.isPrimaryKey();
        if (primaryKey) {
            xml.append("\t<primary_key>").append(primaryKey).append("</primary_key>\n");
        }
        final boolean uniqueKey = normalColumn.isUniqueKey();
        if (uniqueKey) {
            xml.append("\t<unique_key>").append(uniqueKey).append("</unique_key>\n");
        }
        final boolean autoIncrement = normalColumn.isAutoIncrement();
        if (autoIncrement) {
            xml.append("\t<auto_increment>").append(autoIncrement).append("</auto_increment>\n");
        }
        final String defaultValue = normalColumn.getDefaultValue();
        if (Srl.is_NotNull_and_NotEmpty(defaultValue)) {
            xml.append("\t<default_value>").append(escape(defaultValue)).append("</default_value>\n");
        }
        final String constraint = normalColumn.getConstraint();
        if (Srl.is_NotNull_and_NotEmpty(constraint)) {
            xml.append("\t<constraint>").append(escape(constraint)).append("</constraint>\n");
        }
        final String uniqueKeyName = normalColumn.getUniqueKeyName();
        if (Srl.is_NotNull_and_NotEmpty(uniqueKeyName)) {
            xml.append("\t<unique_key_name>").append(escape(uniqueKeyName)).append("</unique_key_name>\n");
        }
    }

    private void setupCharacterSettings(NormalColumn normalColumn, final StringBuilder xml) {
        final String characterSet = normalColumn.getCharacterSet();
        if (Srl.is_NotNull_and_NotEmpty(characterSet)) {
            xml.append("\t<character_set>").append(escape(characterSet)).append("</character_set>\n");
        }
        final String collation = normalColumn.getCollation();
        if (Srl.is_NotNull_and_NotEmpty(collation)) {
            xml.append("\t<collation>").append(escape(collation)).append("</collation>\n");
        }
    }

    private void setupAutoIncrementSetting(NormalColumn normalColumn, final StringBuilder xml) {
        final Sequence autoIncrementSetting = normalColumn.getAutoIncrementSetting();
        if (normalColumn.isAutoIncrement() && !autoIncrementSetting.isEmpty()) {
            xml.append(tab(sequenceBuilder.buildSequence(autoIncrementSetting)));
        }
    }

    // ===================================================================================
    //                                                                       Column Groups
    //                                                                       =============
    public String buildColumnGroups(ColumnGroupSet columnGroups, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_groups>\n");
        for (final ColumnGroup columnGroup : columnGroups) {
            xml.append(tab(doBuildColumnGroup(columnGroup, context)));
        }
        xml.append("</column_groups>\n");
        return xml.toString();
    }

    private String doBuildColumnGroup(ColumnGroup columnGroup, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_group>\n");
        // #for_erflute column group name is unique by validator
        //final String groupId = context.columnGroupMap.get(columnGroup);
        //xml.append("\t<column_group_id>").append(groupId).append("</column_group_id>\n");
        xml.append("\t<column_group_name>").append(escape(columnGroup.getGroupName())).append("</column_group_name>\n"); // me too
        xml.append("\t<columns>\n");
        for (final NormalColumn column : columnGroup.getColumns()) {
            xml.append(tab(tab(setupNormalColumn(column, context))));
        }
        xml.append("\t</columns>\n");
        xml.append("</column_group>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }

    private String escape(Object obj) {
        return assistLogic.escape(obj);
    }
}
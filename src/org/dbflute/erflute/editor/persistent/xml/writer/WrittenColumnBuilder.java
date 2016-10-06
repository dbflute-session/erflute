package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
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
                xml.append(tab(doBuildColumnsColumnGroup((ColumnGroup) column, context)));
            } else if (column instanceof NormalColumn) {
                xml.append(tab(doBuildNormalColumn((NormalColumn) column, context)));
            }
        }
        xml.append("</columns>\n");
        return xml.toString();
    }

    private String doBuildColumnsColumnGroup(ColumnGroup columnGroup, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_group>").append(context.columnGroupMap.get(columnGroup)).append("</column_group>\n");
        return xml.toString();
    }

    private String doBuildNormalColumn(NormalColumn normalColumn, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<normal_column>\n");
        if (context != null) {
            final Integer wordId = context.wordMap.get(normalColumn.getWord());
            if (wordId != null) {
                xml.append("\t<word_id>").append(wordId).append("</word_id>\n");
            }
            xml.append("\t<id>").append(context.columnMap.get(normalColumn)).append("</id>\n");
            for (final NormalColumn referencedColumn : normalColumn.getReferencedColumnList()) {
                xml.append("\t<referenced_column>")
                        .append(Format.toString(context.columnMap.get(referencedColumn)))
                        .append("</referenced_column>\n");
            }
            for (final Relationship relation : normalColumn.getRelationshipList()) {
                xml.append("\t<relation>").append(context.connectionMap.get(relation)).append("</relation>\n");
            }
        }

        final String description = normalColumn.getForeignKeyDescription();
        final String logicalName = normalColumn.getForeignKeyLogicalName();
        final String physicalName = normalColumn.getForeignKeyPhysicalName();
        final SqlType sqlType = normalColumn.getType();

        xml.append("\t<description>").append(escape(description)).append("</description>\n");
        xml.append("\t<unique_key_name>").append(escape(normalColumn.getUniqueKeyName())).append("</unique_key_name>\n");
        xml.append("\t<logical_name>").append(escape(logicalName)).append("</logical_name>\n");
        xml.append("\t<physical_name>").append(escape(physicalName)).append("</physical_name>\n");

        String type = "";
        if (sqlType != null) {
            type = sqlType.getId();
        }
        xml.append("\t<type>").append(type).append("</type>\n");
        xml.append("\t<constraint>").append(escape(normalColumn.getConstraint())).append("</constraint>\n");
        xml.append("\t<default_value>").append(escape(normalColumn.getDefaultValue())).append("</default_value>\n");
        xml.append("\t<auto_increment>").append(normalColumn.isAutoIncrement()).append("</auto_increment>\n");
        xml.append("\t<foreign_key>").append(normalColumn.isForeignKey()).append("</foreign_key>\n");
        xml.append("\t<not_null>").append(normalColumn.isNotNull()).append("</not_null>\n");
        xml.append("\t<primary_key>").append(normalColumn.isPrimaryKey()).append("</primary_key>\n");
        xml.append("\t<unique_key>").append(normalColumn.isUniqueKey()).append("</unique_key>\n");
        xml.append("\t<character_set>").append(escape(normalColumn.getCharacterSet())).append("</character_set>\n");
        xml.append("\t<collation>").append(escape(normalColumn.getCollation())).append("</collation>\n");
        xml.append(tab(sequenceBuilder.buildSequence(normalColumn.getAutoIncrementSetting())));
        xml.append("</normal_column>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                       Column Groups
    //                                                                       =============
    public String buildColumnGroups(GroupSet columnGroups, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_groups>\n");
        for (final ColumnGroup columnGroup : columnGroups) {
            xml.append(tab(tab(doBuildColumnGroup(columnGroup, context))));
        }
        xml.append("</column_groups>\n");
        return xml.toString();
    }

    private String doBuildColumnGroup(ColumnGroup columnGroup, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<column_group>\n");
        xml.append("\t<id>").append(context.columnGroupMap.get(columnGroup)).append("</id>\n");
        xml.append("\t<group_name>").append(escape(columnGroup.getGroupName())).append("</group_name>\n");
        xml.append("\t<columns>\n");
        for (final NormalColumn normalColumn : columnGroup.getColumns()) {
            xml.append(tab(tab(doBuildNormalColumn(normalColumn, context))));
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

    private String escape(String s) {
        return assistLogic.escape(s);
    }
}
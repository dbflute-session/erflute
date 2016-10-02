package org.dbflute.erflute.editor.persistent.xml.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.dbflute.erflute.editor.model.settings.DBSetting;
import org.dbflute.erflute.editor.model.settings.PageSetting;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ErmXmlWriter {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final DateFormat DATE_FORMAT = PersistentXml.DATE_FORMAT;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;

    // recycled
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenNodeElementBuilder nodeElementBuilder;
    protected final WrittenTablePropertiesBuilder tablePropertiesBuilder;

    // simple
    protected final WrittenSettingBuilder settingBuilder;
    protected final WrittenDictionaryBuilder dictionaryBuilder;
    protected final WrittenTablespaceBuilder tablespaceBuilder;
    protected final WrittenIndexBuilder indexBuilder;
    protected final WrittenComplexUniqueKeyBuilder complexUniqueKeyBuilder;
    protected final WrittenNoteBuilder noteBuilder;
    protected final WrittenImageBuilder imageBuilder;
    protected final WrittenSequenceBuilder sequenceBuilder;
    protected final WrittenTriggerBuilder triggerBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ErmXmlWriter(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
        this.assistLogic = new WrittenAssistLogic(persistentXml);
        this.nodeElementBuilder = new WrittenNodeElementBuilder(persistentXml, assistLogic);
        this.tablePropertiesBuilder = new WrittenTablePropertiesBuilder(persistentXml, assistLogic);
        this.settingBuilder = new WrittenSettingBuilder(persistentXml, assistLogic, nodeElementBuilder, tablePropertiesBuilder);
        this.dictionaryBuilder = new WrittenDictionaryBuilder(persistentXml, assistLogic);
        this.tablespaceBuilder = new WrittenTablespaceBuilder(persistentXml, assistLogic);
        this.indexBuilder = new WrittenIndexBuilder(persistentXml, assistLogic);
        this.complexUniqueKeyBuilder = new WrittenComplexUniqueKeyBuilder(persistentXml, assistLogic);
        this.noteBuilder = new WrittenNoteBuilder(persistentXml, assistLogic, nodeElementBuilder);
        this.imageBuilder = new WrittenImageBuilder(persistentXml, assistLogic, nodeElementBuilder);
        this.sequenceBuilder = new WrittenSequenceBuilder(persistentXml, assistLogic);
        this.triggerBuilder = new WrittenTriggerBuilder(persistentXml, assistLogic);
    }

    // ===================================================================================
    //                                                                              Write
    //                                                                             =======
    public InputStream write(ERDiagram diagram) throws IOException {
        InputStream inputStream = null;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String xml = buildDiagram(diagram);
        out.write(xml.getBytes("UTF-8"));
        inputStream = new ByteArrayInputStream(out.toByteArray());
        return inputStream;
    }

    // ===================================================================================
    //                                                                             Diagram
    //                                                                             =======
    private String buildDiagram(ERDiagram diagram) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<diagram>\n");
        if (diagram.getDbSetting() != null) {
            xml.append("\t<dbsetting>\n").append(tab(tab(buildDBSetting(diagram.getDbSetting())))).append("\t</dbsetting>\n");
        }
        if (diagram.getPageSetting() != null) {
            xml.append("\t<page_setting>\n").append(tab(tab(buildPageSetting(diagram.getPageSetting())))).append("\t</page_setting>\n");
        }
        xml.append("\t<category_index>").append(diagram.getCurrentCategoryIndex()).append("</category_index>\n");
        if (diagram.getCurrentErmodel() != null) {
            xml.append("\t<current_ermodel>").append(diagram.getCurrentErmodel().getName()).append("</current_ermodel>\n");
        }
        xml.append("\t<zoom>").append(diagram.getZoom()).append("</zoom>\n");
        xml.append("\t<x>").append(diagram.getX()).append("</x>\n");
        xml.append("\t<y>").append(diagram.getY()).append("</y>\n");
        appendColor(xml, "default_color", diagram.getDefaultColor());
        xml.append(tab(buildColor(diagram.getColor())));
        xml.append("\t<font_name>").append(escape(diagram.getFontName())).append("</font_name>\n");
        xml.append("\t<font_size>").append(diagram.getFontSize()).append("</font_size>\n");
        final PersistentContext context = persistentXml.getCurrentContext(diagram);
        xml.append(tab(buildDiagramContents(diagram.getDiagramContents(), context)));
        xml.append("</diagram>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                          DB Setting
    //                                                                          ==========
    private String buildDBSetting(DBSetting dbSetting) {
        return settingBuilder.buildDBSetting(dbSetting);
    }

    // ===================================================================================
    //                                                                        Page Setting
    //                                                                        ============
    private String buildPageSetting(PageSetting pageSetting) {
        return settingBuilder.buildPageSetting(pageSetting);
    }

    // ===================================================================================
    //                                                                    Diagram Contents
    //                                                                    ================
    private String buildDiagramContents(DiagramContents diagramContents, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append(buildSettings(diagramContents.getSettings(), context));
        xml.append(buildDictionary(diagramContents.getDictionary(), context));
        xml.append(buildTablespace(diagramContents.getTablespaceSet(), context));
        xml.append(buildContents(diagramContents.getContents(), context));
        xml.append(buildERModel(diagramContents.getModelSet(), context));
        xml.append(buildColumnGroups(diagramContents.getGroups(), context));
        xml.append(buildSequenceSet(diagramContents.getSequenceSet()));
        xml.append(buildTrigger(diagramContents.getTriggerSet()));
        return xml.toString();
    }

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    private String buildSettings(Settings settings, PersistentContext context) {
        return settingBuilder.buildSettings(settings, context);
    }

    // ===================================================================================
    //                                                                          Dictionary
    //                                                                          ==========
    private String buildDictionary(Dictionary dictionary, PersistentContext context) {
        return dictionaryBuilder.buildDictionary(dictionary, context);
    }

    // ===================================================================================
    //                                                                          Tablespace
    //                                                                          ==========
    private String buildTablespace(TablespaceSet tablespaceSet, PersistentContext context) {
        return tablespaceBuilder.buildTablespace(tablespaceSet, context);
    }

    // ===================================================================================
    //                                                                            Contents
    //                                                                            ========
    private String buildContents(NodeSet contents, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<contents>\n");
        for (final NodeElement content : contents) {
            String subxml = null;
            if (content instanceof ERTable) {
                subxml = this.buildTable((ERTable) content, context);
            } else if (content instanceof ERModel) {
                // do nothing
                //				subxml = this.createXMLERModel((ERModel) content, context);
            } else if (content instanceof Note) {
                //				subxml = this.createXML((Note) content, context);
            } else if (content instanceof ERView) {
                subxml = this.buildView((ERView) content, context);
            } else if (content instanceof InsertedImage) {
                subxml = this.buildImage((InsertedImage) content, context);
            } else if (content instanceof VGroup) {
                // do nothing
                //				subxml = this.createXML((VGroup) content, context);
            } else {
                throw new RuntimeException("not support " + content);
            }
            if (subxml != null)
                xml.append(tab(subxml));
        }
        xml.append("</contents>\n");
        return xml.toString();
    }

    private String buildERModel(ERModelSet modelSet, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<ermodels>\n");
        for (final ERModel erModel : modelSet) {
            xml.append("\t<ermodel>\n");
            xml.append("\t\t<id>").append(context.ermodelMap.get(erModel)).append("</id>\n");
            xml.append("\t\t<name>").append(erModel.getName()).append("</name>\n");
            appendColor(xml, "color", erModel.getColor());
            xml.append("\t\t<vtables>\n");
            for (final ERVirtualTable table : erModel.getTables()) {
                xml.append("\t\t\t<vtable>\n");
                xml.append("\t\t\t\t<id>").append(context.nodeElementMap.get(table.getRawTable())).append("</id>\n");
                xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
                xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
                appendFont(xml, table);
                xml.append("\t\t\t</vtable>\n");
            }
            xml.append("\t\t</vtables>\n");
            xml.append("\t\t<groups>\n");
            for (final VGroup group : erModel.getGroups()) {
                xml.append(buildVGroup(group, context));
            }
            xml.append("\t\t</groups>\n");
            xml.append("\t\t<notes>\n");
            for (final Note note : erModel.getNotes()) {
                xml.append(buildNote(note, context));
            }
            xml.append("\t\t</notes>\n");
            xml.append("\t</ermodel>\n");
        }
        xml.append("</ermodels>\n");
        return xml.toString();
    }

    private void appendFont(StringBuilder xml, NodeElement nodeElement) {
        xml.append("\t<font_name>").append(escape(nodeElement.getFontName())).append("</font_name>\n");
        xml.append("\t<font_size>").append(nodeElement.getFontSize()).append("</font_size>\n");
    }

    // ===================================================================================
    //                                                                              VGroup
    //                                                                              ======
    private String buildVGroup(VGroup group, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<group>\n");
        xml.append(tab(this.buildNodeElement(group, context)));
        xml.append("\t<name>").append(escape(group.getName())).append("</name>\n");
        //      xml.append("\t<selected>").append(isSelected).append("</selected>\n");
        for (final NodeElement nodeElement : group.getContents()) {
            xml.append("\t<node_element>")
                    .append(context.nodeElementMap.get(((ERVirtualTable) nodeElement).getRawTable()))
                    .append("</node_element>\n");
        }
        xml.append("</group>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    private String buildTable(ERTable table, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<table>\n");
        xml.append(tab(buildNodeElement(table, context)));
        xml.append("\t<physical_name>").append(escape(table.getPhysicalName())).append("</physical_name>\n");
        xml.append("\t<logical_name>").append(escape(table.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<description>").append(escape(table.getDescription())).append("</description>\n");
        xml.append("\t<constraint>").append(escape(table.getConstraint())).append("</constraint>\n");
        xml.append("\t<primary_key_name>").append(escape(table.getPrimaryKeyName())).append("</primary_key_name>\n");
        xml.append("\t<option>").append(escape(table.getOption())).append("</option>\n");
        final List<ERColumn> columns = table.getColumns();
        xml.append(tab(buildColumns(columns, context)));
        final List<ERIndex> indexes = table.getIndexes();
        xml.append(tab(buildIndexes(indexes, context)));
        final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
        xml.append(tab(buildComplexUniqueKeyList(complexUniqueKeyList, context)));
        final TableProperties tableProperties = (TableProperties) table.getTableViewProperties();
        xml.append(tab(tablePropertiesBuilder.buildTableProperties(tableProperties, context)));
        xml.append("</table>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                               View
    //                                                                              ======
    private String buildView(ERView view, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<view>\n");
        xml.append(tab(this.buildNodeElement(view, context)));
        xml.append("\t<physical_name>").append(escape(view.getPhysicalName())).append("</physical_name>\n");
        xml.append("\t<logical_name>").append(escape(view.getLogicalName())).append("</logical_name>\n");
        xml.append("\t<description>").append(escape(view.getDescription())).append("</description>\n");
        xml.append("\t<sql>").append(escape(view.getSql())).append("</sql>\n");
        final List<ERColumn> columns = view.getColumns();
        xml.append(tab(buildColumns(columns, context)));
        final ViewProperties viewProperties = (ViewProperties) view.getTableViewProperties();
        xml.append(tab(buildViewProperties(viewProperties, context)));
        xml.append("</view>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                     View Properties
    //                                                                     ===============
    private String buildViewProperties(ViewProperties viewProperties, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<view_properties>\n");
        final Integer tablespaceId = context.tablespaceMap.get(viewProperties.getTableSpace());
        if (tablespaceId != null) {
            xml.append("\t<tablespace_id>").append(tablespaceId).append("</tablespace_id>\n");
        }
        xml.append("<schema>").append(escape(viewProperties.getSchema())).append("</schema>\n");
        xml.append("</view_properties>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    private String buildColumns(List<ERColumn> columns, PersistentContext context) {
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
        Integer wordId = null;
        if (context != null) {
            wordId = context.wordMap.get(normalColumn.getWord());
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
        xml.append(tab(buildSequence(normalColumn.getAutoIncrementSetting())));
        xml.append("</normal_column>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                             Indexes
    //                                                                             =======
    private String buildIndexes(List<ERIndex> indexes, PersistentContext context) {
        return indexBuilder.buildIndexes(indexes, context);
    }

    // ===================================================================================
    //                                                             Complex Unique Key List
    //                                                             =======================
    private String buildComplexUniqueKeyList(List<ComplexUniqueKey> complexUniqueKeyList, PersistentContext context) {
        return complexUniqueKeyBuilder.buildComplexUniqueKeyList(complexUniqueKeyList, context);
    }

    // ===================================================================================
    //                                                                              Note
    //                                                                             =======
    private String buildNote(Note note, PersistentContext context) {
        return noteBuilder.buildNote(note, context);
    }

    // ===================================================================================
    //                                                                               Image
    //                                                                               =====
    private String buildImage(InsertedImage insertedImage, PersistentContext context) {
        return imageBuilder.buildImage(insertedImage, context);
    }

    // ===================================================================================
    //                                                                       Column Groups
    //                                                                       =============
    private String buildColumnGroups(GroupSet columnGroups, PersistentContext context) {
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
    //                                                                            Sequence
    //                                                                            ========
    private String buildSequenceSet(SequenceSet sequenceSet) {
        return sequenceBuilder.buildSequenceSet(sequenceSet);
    }

    private String buildSequence(Sequence sequence) {
        return sequenceBuilder.buildSequence(sequence);
    }

    // ===================================================================================
    //                                                                             Trigger
    //                                                                             =======
    private String buildTrigger(TriggerSet triggerSet) {
        return triggerBuilder.buildTrigger(triggerSet);
    }

    // ===================================================================================
    //                                                                        Node Element
    //                                                                        ============
    private String buildNodeElement(NodeElement nodeElement, PersistentContext context) {
        return nodeElementBuilder.buildNodeElement(nodeElement, context);
    }

    // ===================================================================================
    //                                                                               Color
    //                                                                               =====
    private String buildColor(int[] colors) {
        return assistLogic.buildColor(colors);
    }

    private void appendColor(StringBuilder xml, String tagName, int[] defaultColor) {
        assistLogic.appendColor(xml, tagName, defaultColor);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }

    public String escape(String s) {
        return assistLogic.escape(s);
    }
}
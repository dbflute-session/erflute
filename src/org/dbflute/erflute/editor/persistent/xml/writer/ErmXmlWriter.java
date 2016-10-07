package org.dbflute.erflute.editor.persistent.xml.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
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
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenNodeElementBuilder nodeElementBuilder;
    protected final WrittenTablePropertiesBuilder tablePropertiesBuilder;
    protected final WrittenSettingBuilder settingBuilder;
    protected final WrittenDictionaryBuilder dictionaryBuilder;
    protected final WrittenTablespaceBuilder tablespaceBuilder;
    protected final WrittenIndexBuilder indexBuilder;
    protected final WrittenUniqueKeyBuilder uniqueKeyBuilder;
    protected final WrittenNoteBuilder noteBuilder;
    protected final WrittenImageBuilder imageBuilder;
    protected final WrittenSequenceBuilder sequenceBuilder;
    protected final WrittenTriggerBuilder triggerBuilder;
    protected final WrittenColumnBuilder columnBuilder;
    protected final WrittenViewBuilder viewBuilder;
    protected final WrittenTableBuilder tableBuilder;
    protected final WrittenERModelBuilder ermodelBuilder;

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
        this.uniqueKeyBuilder = new WrittenUniqueKeyBuilder(persistentXml, assistLogic);
        this.noteBuilder = new WrittenNoteBuilder(persistentXml, assistLogic, nodeElementBuilder);
        this.imageBuilder = new WrittenImageBuilder(persistentXml, assistLogic, nodeElementBuilder);
        this.sequenceBuilder = new WrittenSequenceBuilder(persistentXml, assistLogic);
        this.triggerBuilder = new WrittenTriggerBuilder(persistentXml, assistLogic);
        this.columnBuilder = new WrittenColumnBuilder(persistentXml, assistLogic, sequenceBuilder);
        this.tableBuilder = new WrittenTableBuilder(persistentXml //
                , assistLogic, nodeElementBuilder, columnBuilder //
                , indexBuilder, uniqueKeyBuilder, tablePropertiesBuilder);
        this.viewBuilder = new WrittenViewBuilder(persistentXml, assistLogic, nodeElementBuilder, columnBuilder);
        this.ermodelBuilder = new WrittenERModelBuilder(persistentXml, assistLogic, nodeElementBuilder, noteBuilder);
    }

    // ===================================================================================
    //                                                                              Write
    //                                                                             =======
    public InputStream write(ERDiagram diagram) throws IOException {
        final String xml = buildDiagram(diagram);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(xml.getBytes("UTF-8"));
        return new ByteArrayInputStream(out.toByteArray());
    }

    // ===================================================================================
    //                                                                               Root
    //                                                                              ======
    private String buildDiagram(ERDiagram diagram) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<erflute>true</erflute>"); // mark just in case
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
        for (final NodeElement content : contents.getPersistentSet()) {
            final String subxml;
            if (content instanceof ERTable) {
                subxml = buildTable((ERTable) content, context);
            } else if (content instanceof ERView) {
                subxml = buildView((ERView) content, context);
            } else if (content instanceof InsertedImage) {
                subxml = buildImage((InsertedImage) content, context);
            } else {
                throw new RuntimeException("not support " + content);
            }
            if (subxml != null) { // no way, but just in case? by jflute
                xml.append(tab(subxml));
            }
        }
        xml.append("</contents>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                             ERModel
    //                                                                             =======
    private String buildERModel(ERModelSet modelSet, PersistentContext context) {
        return ermodelBuilder.buildERModel(modelSet, context);
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    private String buildTable(ERTable table, PersistentContext context) {
        return tableBuilder.buildTable(table, context);
    }

    // ===================================================================================
    //                                                                               View
    //                                                                              ======
    private String buildView(ERView view, PersistentContext context) {
        return viewBuilder.buildView(view, context);
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
        return columnBuilder.buildColumnGroups(columnGroups, context);
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    private String buildSequenceSet(SequenceSet sequenceSet) {
        return sequenceBuilder.buildSequenceSet(sequenceSet);
    }

    // ===================================================================================
    //                                                                             Trigger
    //                                                                             =======
    private String buildTrigger(TriggerSet triggerSet) {
        return triggerBuilder.buildTrigger(triggerSet);
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
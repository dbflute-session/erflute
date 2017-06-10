package org.dbflute.erflute.editor.persistent.xml.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.model.settings.PageSettings;
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
    protected final WrittenDiagramWalkerBuilder walkerBuilder;
    protected final WrittenTablePropertiesBuilder tablePropertiesBuilder;
    protected final WrittenSettingsBuilder settingsBuilder;
    protected final WrittenDictionaryBuilder dictionaryBuilder;
    protected final WrittenTablespaceBuilder tablespaceBuilder;
    protected final WrittenIndexBuilder indexBuilder;
    protected final WrittenUniqueKeyBuilder uniqueKeyBuilder;
    protected final WrittenWalkerNoteBuilder walkerNoteBuilder;
    protected final WrittenWalkerGroupBuilder walkerGroupBuilder;
    protected final WrittenInsertedImageBuilder imageBuilder;
    protected final WrittenSequenceBuilder sequenceBuilder;
    protected final WrittenTriggerBuilder triggerBuilder;
    protected final WrittenColumnBuilder columnBuilder;
    protected final WrittenViewBuilder viewBuilder;
    protected final WrittenTableBuilder tableBuilder;
    protected final WrittenVirtualDiagramBuilder virtualDiagramBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ErmXmlWriter(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
        this.assistLogic = new WrittenAssistLogic(persistentXml);
        this.walkerBuilder = new WrittenDiagramWalkerBuilder(persistentXml, assistLogic);
        this.tablePropertiesBuilder = new WrittenTablePropertiesBuilder(persistentXml, assistLogic);
        this.settingsBuilder = new WrittenSettingsBuilder(persistentXml, assistLogic, walkerBuilder, tablePropertiesBuilder);
        this.dictionaryBuilder = new WrittenDictionaryBuilder(persistentXml, assistLogic);
        this.tablespaceBuilder = new WrittenTablespaceBuilder(persistentXml, assistLogic);
        this.indexBuilder = new WrittenIndexBuilder(persistentXml, assistLogic);
        this.uniqueKeyBuilder = new WrittenUniqueKeyBuilder(persistentXml, assistLogic);
        this.walkerNoteBuilder = new WrittenWalkerNoteBuilder(persistentXml, assistLogic, walkerBuilder);
        this.walkerGroupBuilder = new WrittenWalkerGroupBuilder(persistentXml, assistLogic, walkerBuilder);
        this.imageBuilder = new WrittenInsertedImageBuilder(persistentXml, assistLogic, walkerBuilder);
        this.sequenceBuilder = new WrittenSequenceBuilder(persistentXml, assistLogic);
        this.triggerBuilder = new WrittenTriggerBuilder(persistentXml, assistLogic);
        this.columnBuilder = new WrittenColumnBuilder(persistentXml, assistLogic, sequenceBuilder);
        this.tableBuilder = new WrittenTableBuilder(persistentXml //
                , assistLogic, walkerBuilder, columnBuilder //
                , indexBuilder, uniqueKeyBuilder, tablePropertiesBuilder);
        this.viewBuilder = new WrittenViewBuilder(persistentXml, assistLogic, walkerBuilder, columnBuilder);
        this.virtualDiagramBuilder =
                new WrittenVirtualDiagramBuilder(persistentXml, assistLogic, walkerBuilder, walkerNoteBuilder, walkerGroupBuilder);
    }

    // ===================================================================================
    //                                                                              Write
    //                                                                             =======
    public InputStream write(ERDiagram diagram) {
        final String xml = buildDiagram(diagram);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(xml.getBytes("UTF-8"));
        } catch (final IOException e) {
            throw new IllegalStateException("failed to write xml.", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    // ===================================================================================
    //                                                                               Root
    //                                                                              ======
    private String buildDiagram(ERDiagram diagram) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<diagram>\n");
        xml.append("\t<presenter>ERFlute</presenter>\n"); // mark just in case
        if (diagram.getDbSettings() != null) {
            xml.append("\t<db_settings>\n").append(tab(tab(buildDBSettings(diagram.getDbSettings())))).append("\t</db_settings>\n");
        }
        if (diagram.getPageSetting() != null) {
            xml.append("\t<page_settings>\n").append(tab(tab(buildPageSetting(diagram.getPageSetting())))).append("\t</page_settings>\n");
        }
        // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
        // *basically not keep private viewing information e.g. location
        // _/_/_/_/_/_/_/_/_/_/
        // #for_erflute not keep category_index to immobilize XML (frequently changed by everybody)
        //xml.append("\t<category_index>").append(diagram.getCurrentCategoryIndex()).append("</category_index>\n");
        // #for_erflute not keep current_ermodel to immobilize XML (frequently changed by everybody)
        //if (diagram.getCurrentErmodel() != null) {
        //    xml.append("\t<current_ermodel>").append(diagram.getCurrentErmodel().getName()).append("</current_ermodel>\n");
        //}
        // #for_erflute not keep zoom to immobilize XML (frequently changed by everybody)
        //xml.append("\t<zoom>").append(diagram.getZoom()).append("</zoom>\n");
        // #for_erflute not keep location to immobilize XML (frequently changed by everybody)
        // and reader does not read it since ERMaster...? by jflute
        //xml.append("\t<x>").append(diagram.getX()).append("</x>\n");
        //xml.append("\t<y>").append(diagram.getY()).append("</y>\n");
        // #for_erflute not keep default_color to immobilize XML (frequently changed by everybody)
        //appendColor(xml, "default_color", diagram.getDefaultColor());
        xml.append(tab(buildColor(diagram.getColor())));
        xml.append("\t<font_name>").append(escape(diagram.getFontName())).append("</font_name>\n");
        xml.append("\t<font_size>").append(diagram.getFontSize()).append("</font_size>\n");
        final PersistentContext context = persistentXml.getCurrentContext(diagram);
        xml.append(tab(buildDiagramContents(diagram.getDiagramContents(), context)));
        xml.append("</diagram>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                         DB Settings
    //                                                                         ===========
    private String buildDBSettings(DBSettings dbSetting) {
        return settingsBuilder.buildDBSetting(dbSetting);
    }

    // ===================================================================================
    //                                                                        Page Setting
    //                                                                        ============
    private String buildPageSetting(PageSettings pageSetting) {
        return settingsBuilder.buildPageSettings(pageSetting);
    }

    // ===================================================================================
    //                                                                    Diagram Contents
    //                                                                    ================
    private String buildDiagramContents(DiagramContents diagramContents, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append(buildSettings(diagramContents.getSettings(), context));
        xml.append(buildDictionary(diagramContents.getDictionary(), context));
        xml.append(buildTablespace(diagramContents.getTablespaceSet(), context));
        xml.append(buildDiagramWalkers(diagramContents.getDiagramWalkers(), context));
        xml.append(buildVirtualDiagram(diagramContents.getVirtualDiagramSet(), context));
        xml.append(buildColumnGroups(diagramContents.getColumnGroupSet(), context));
        xml.append(buildSequenceSet(diagramContents.getSequenceSet()));
        xml.append(buildTrigger(diagramContents.getTriggerSet()));
        return xml.toString();
    }

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    private String buildSettings(DiagramSettings settings, PersistentContext context) {
        return settingsBuilder.buildDiagramSettings(settings, context);
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
    private String buildDiagramWalkers(DiagramWalkerSet contents, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<diagram_walkers>\n");
        for (final DiagramWalker content : contents.getPersistentSet()) {
            final String subxml;
            if (content instanceof ERTable) {
                subxml = buildTable((ERTable) content, context);
            } else if (content instanceof ERView) {
                subxml = buildView((ERView) content, context);
            } else if (content instanceof WalkerNote) {
                subxml = walkerNoteBuilder.buildNote((WalkerNote) content, context);
            } else if (content instanceof WalkerGroup) {
                subxml = walkerGroupBuilder.buildGroup((WalkerGroup) content, context);
            } else if (content instanceof InsertedImage) {
                subxml = imageBuilder.buildInsertedImage((InsertedImage) content, context);
            } else {
                throw new IllegalStateException("*Unsupported contents: " + content);
            }
            if (subxml != null) { // no way, but just in case? by jflute
                xml.append(tab(subxml));
            }
        }
        xml.append("</diagram_walkers>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                             ERModel
    //                                                                             =======
    private String buildVirtualDiagram(ERVirtualDiagramSet modelSet, PersistentContext context) {
        return virtualDiagramBuilder.buildVirtualDiagram(modelSet, context);
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
    //                                                                       Column Groups
    //                                                                       =============
    private String buildColumnGroups(ColumnGroupSet columnGroups, PersistentContext context) {
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
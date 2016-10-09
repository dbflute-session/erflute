package org.dbflute.erflute.editor.persistent.xml.writer;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenERModelBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenNodeElementBuilder nodeElementBuilder;
    protected final WrittenNoteBuilder noteBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenERModelBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenNodeElementBuilder nodeElementBuilder,
            WrittenNoteBuilder noteBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementBuilder = nodeElementBuilder;
        this.noteBuilder = noteBuilder;
    }

    // ===================================================================================
    //                                                                             ERModel
    //                                                                             =======
    public String buildERModel(ERModelSet modelSet, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<ermodels>\n");
        for (final ERModel ermodel : modelSet) {
            xml.append("\t<ermodel>\n");
            xml.append("\t\t<id>").append(context.ermodelMap.get(ermodel)).append("</id>\n");
            xml.append("\t\t<name>").append(ermodel.getName()).append("</name>\n");
            xml.append(tab(tab(assistLogic.buildColor(ermodel.getColor()))));
            xml.append("\t\t<vtables>\n");
            for (final ERVirtualTable table : ermodel.getTables()) {
                xml.append("\t\t\t<vtable>\n");
                xml.append("\t\t\t\t<id>").append(context.nodeElementMap.get(table.getRawTable())).append("</id>\n");
                xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
                xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
                xml.append("\t\t\t\t<font_name>").append(escape(ermodel.getFontName())).append("</font_name>\n");
                xml.append("\t\t\t\t<font_size>").append(ermodel.getFontSize()).append("</font_size>\n");
                xml.append("\t\t\t</vtable>\n");
            }
            xml.append("\t\t</vtables>\n");
            xml.append("\t\t<groups>\n");
            for (final VGroup group : ermodel.getGroups()) {
                xml.append(tab(tab(tab(buildVGroup(group, context)))));
            }
            xml.append("\t\t</groups>\n");
            xml.append("\t\t<notes>\n");
            for (final Note note : ermodel.getNotes()) {
                xml.append(noteBuilder.buildNote(note, context));
            }
            xml.append("\t\t</notes>\n");
            xml.append("\t</ermodel>\n");
        }
        xml.append("</ermodels>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                              VGroup
    //                                                                              ======
    private String buildVGroup(VGroup group, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<group>\n");
        xml.append(tab(nodeElementBuilder.buildNodeElement(group, context)));
        xml.append("\t<name>").append(escape(group.getName())).append("</name>\n");
        for (final NodeElement nodeElement : group.getContents()) {
            final String nodeId = context.nodeElementMap.get(((ERVirtualTable) nodeElement).getRawTable());
            xml.append("\t<node_element>").append(nodeId).append("</node_element>\n");
        }
        xml.append("</group>\n");
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
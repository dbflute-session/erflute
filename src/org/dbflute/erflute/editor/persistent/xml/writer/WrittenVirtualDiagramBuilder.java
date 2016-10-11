package org.dbflute.erflute.editor.persistent.xml.writer;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenVirtualDiagramBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenDiagramWalkerBuilder nodeElementBuilder;
    protected final WrittenWalkerNoteBuilder noteBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenVirtualDiagramBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic,
            WrittenDiagramWalkerBuilder nodeElementBuilder, WrittenWalkerNoteBuilder noteBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementBuilder = nodeElementBuilder;
        this.noteBuilder = noteBuilder;
    }

    // ===================================================================================
    //                                                                      VirtualDiagram
    //                                                                      ==============
    public String buildERModel(ERVirtualDiagramSet modelSet, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<vdiagrams>\n");
        for (final ERVirtualDiagram vdiagram : modelSet) {
            xml.append("\t<vdiagram>\n");
            xml.append("\t\t<id>").append(context.virtualDiagramMap.get(vdiagram)).append("</id>\n");
            xml.append("\t\t<name>").append(vdiagram.getName()).append("</name>\n");
            xml.append(tab(tab(assistLogic.buildColor(vdiagram.getColor()))));
            xml.append("\t\t<vtables>\n");
            for (final ERVirtualTable table : vdiagram.getTables()) {
                xml.append("\t\t\t<vtable>\n");
                xml.append("\t\t\t\t<id>").append(context.diagramWalkerMap.get(table.getRawTable())).append("</id>\n");
                xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
                xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
                xml.append("\t\t\t\t<font_name>").append(escape(vdiagram.getFontName())).append("</font_name>\n");
                xml.append("\t\t\t\t<font_size>").append(vdiagram.getFontSize()).append("</font_size>\n");
                xml.append("\t\t\t</vtable>\n");
            }
            xml.append("\t\t</vtables>\n");
            xml.append("\t\t<notes>\n");
            for (final WalkerNote note : vdiagram.getNotes()) {
                xml.append(tab(tab(tab(noteBuilder.buildNote(note, context)))));
            }
            xml.append("\t\t</notes>\n");
            xml.append("\t\t<groups>\n");
            for (final WalkerGroup group : vdiagram.getGroups()) {
                xml.append(tab(tab(tab(buildVGroup(group, context)))));
            }
            xml.append("\t\t</groups>\n");
            xml.append("\t</vdiagram>\n");
        }
        xml.append("</vdiagrams>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                              VGroup
    //                                                                              ======
    private String buildVGroup(WalkerGroup group, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<group>\n");
        xml.append(tab(nodeElementBuilder.buildNodeElement(group, context)));
        xml.append("\t<name>").append(escape(group.getName())).append("</name>\n");
        for (final DiagramWalker nodeElement : group.getDiagramWalkerList()) {
            final String nodeId = context.diagramWalkerMap.get(((ERVirtualTable) nodeElement).getRawTable());
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
package org.dbflute.erflute.editor.persistent.xml.writer;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;
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
    protected final WrittenDiagramWalkerBuilder walkerBuilder;
    protected final WrittenWalkerNoteBuilder walkerNoteBuilder;
    protected final WrittenWalkerGroupBuilder walkerGroupBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenVirtualDiagramBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic,
            WrittenDiagramWalkerBuilder walkerBuilder, WrittenWalkerNoteBuilder walkerNoteBuilder,
            WrittenWalkerGroupBuilder walkerGroupBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.walkerBuilder = walkerBuilder;
        this.walkerNoteBuilder = walkerNoteBuilder;
        this.walkerGroupBuilder = walkerGroupBuilder;
    }

    // ===================================================================================
    //                                                                      VirtualDiagram
    //                                                                      ==============
    public String buildVirtualDiagram(ERVirtualDiagramSet modelSet, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<vdiagrams>\n");
        for (final ERVirtualDiagram vdiagram : modelSet) {
            xml.append("\t<vdiagram>\n");
            if (vdiagram.isUsePersistentId()) {
                final Integer vdiagramId = context.virtualDiagramMap.get(vdiagram);
                xml.append("\t\t<vdiagram_id>").append(vdiagramId).append("</vdiagram_id>\n"); // #for_erflute
            }
            xml.append("\t\t<vdiagram_name>").append(vdiagram.getName()).append("</vdiagram_name>\n"); // #for_erflute
            xml.append(tab(tab(assistLogic.buildColor(vdiagram.getColor()))));
            xml.append("\t\t<vtables>\n");
            for (final ERVirtualTable table : vdiagram.getVirtualTables()) {
                xml.append("\t\t\t<vtable>\n");
                final String tableId = context.walkerMap.get(table.getRawTable());
                xml.append("\t\t\t\t<table_id>").append(tableId).append("</table_id>\n"); // #for_erflute
                xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
                xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
                xml.append("\t\t\t\t<font_name>").append(escape(vdiagram.getFontName())).append("</font_name>\n");
                xml.append("\t\t\t\t<font_size>").append(vdiagram.getFontSize()).append("</font_size>\n");
                xml.append("\t\t\t</vtable>\n");
            }
            xml.append("\t\t</vtables>\n");
            xml.append("\t\t<walker_notes>\n");
            for (final WalkerNote note : vdiagram.getWalkerNotes()) {
                xml.append(tab(tab(tab(walkerNoteBuilder.buildNote(note, context)))));
            }
            xml.append("\t\t</walker_notes>\n");
            xml.append("\t\t<walker_groups>\n");
            for (final WalkerGroup group : vdiagram.getWalkerGroups()) {
                xml.append(tab(tab(tab(buildWalkerGroup(group, context)))));
            }
            xml.append("\t\t</walker_groups>\n");
            xml.append("\t</vdiagram>\n");
        }
        xml.append("</vdiagrams>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Walker Group
    //                                                                        ============
    private String buildWalkerGroup(WalkerGroup group, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<walker_group>\n");
        xml.append(tab(walkerBuilder.buildWalker(group, context)));
        xml.append("\t<walker_group_name>").append(escape(group.getName())).append("</walker_group_name>\n");
        for (final DiagramWalker walker : group.getDiagramWalkerList()) {
            final String nodeId = context.walkerMap.get(((ERVirtualTable) walker).getRawTable());
            xml.append("\t<diagram_walker>").append(nodeId).append("</diagram_walker>\n");
        }
        xml.append("</walker_group>\n");
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
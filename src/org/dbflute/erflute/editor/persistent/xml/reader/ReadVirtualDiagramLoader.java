package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadVirtualDiagramLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadTableLoader tableLoader;
    protected final ReadWalkerNoteLoader noteLoader;
    protected final ReadWalkerGroupLoader groupLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadVirtualDiagramLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadTableLoader tableLoader,
            ReadWalkerNoteLoader noteLoader, ReadWalkerGroupLoader groupLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.tableLoader = tableLoader;
        this.noteLoader = noteLoader;
        this.groupLoader = groupLoader;
    }

    // ===================================================================================
    //                                                                      VirtualDiagram
    //                                                                      ==============
    public List<ERVirtualDiagram> loadVirtualDiagram(Element parent, LoadContext context, ERDiagram diagram) {
        final List<ERVirtualDiagram> results = new ArrayList<ERVirtualDiagram>();
        Element element = getElement(parent, "ermodels"); // migration from ERMaster
        if (element == null) {
            element = getElement(parent, "vdiagrams"); // #for_erflute
        }
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName("ermodel"); // migration from ERMaster
            if (nodeList.getLength() == 0) {
                nodeList = element.getElementsByTagName("vdiagram"); // #for_erflute
            }
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element modelElement = (Element) nodeList.item(i);
                final ERVirtualDiagram vdiagram = new ERVirtualDiagram(diagram);
                String vdiagramName = getStringValue(modelElement, "name"); // migration from ERMaster
                if (Srl.is_Null_or_Empty(vdiagramName)) {
                    vdiagramName = getStringValue(modelElement, "vdiagram_name"); // #for_erflute
                }
                vdiagram.setName(vdiagramName);
                assistLogic.loadColor(vdiagram, modelElement);
                final List<ERVirtualTable> tables = new ArrayList<ERVirtualTable>();
                final Element vtables = getElement(modelElement, "vtables");
                if (vtables != null) {
                    final NodeList tableEls = vtables.getElementsByTagName("vtable");
                    for (int k = 0; k < tableEls.getLength(); k++) {
                        final Element tableElement = (Element) tableEls.item(k);
                        tables.add(tableLoader.loadVirtualTable(vdiagram, tableElement, context));
                    }
                }
                vdiagram.setVirtualTables(tables);
                loadWalkerNotes(context, modelElement, vdiagram, diagram);
                loadWalkerGroups(context, modelElement, vdiagram);
                final String id = vdiagram.buildVirtualDiagramId(); // #for_erflute
                context.virtualDiagramMap.put(id, vdiagram);
                results.add(vdiagram);
            }
        }
        return results;
    }

    private void loadWalkerNotes(LoadContext context, Element modelElement, ERVirtualDiagram vdiagram, ERDiagram diagram) {
        final List<WalkerNote> notes = new ArrayList<WalkerNote>();
        Element notesElement = getElement(modelElement, "notes");
        if (notesElement == null) {
            notesElement = getElement(modelElement, "walker_notes"); // #for_erflute
        }
        if (notesElement != null) {
            NodeList noteNodeList = notesElement.getElementsByTagName("note");
            if (noteNodeList.getLength() == 0) {
                noteNodeList = notesElement.getElementsByTagName("walker_note"); // #for_erflute
            }
            for (int i = 0; i < noteNodeList.getLength(); i++) {
                final Element noteElement = (Element) noteNodeList.item(i);
                final WalkerNote note = noteLoader.loadNote(noteElement, context, vdiagram);
                notes.add(note);
                final String id = getStringValue(noteElement, "id");
                if (Srl.is_NotNull_and_NotTrimmedEmpty(id)) { // for compatible with ERMaster
                    context.walkerMap.put(id, note);
                }
                // unneeded because note is independent on model by jflute
                //diagram.getDiagramContents().getContents().addNodeElement(note);
            }
        }
        vdiagram.setWalkerNotes(notes);
    }

    private void loadWalkerGroups(LoadContext context, Element modelElement, ERVirtualDiagram vdiagram) {
        final List<WalkerGroup> groups = new ArrayList<WalkerGroup>();
        Element groupsElement = getElement(modelElement, "groups"); // migration from ERMaster
        if (groupsElement == null) {
            groupsElement = getElement(modelElement, "walker_groups"); // #for_erflute
        }
        if (groupsElement != null) {
            NodeList groupNoteList = groupsElement.getElementsByTagName("group");
            if (groupNoteList.getLength() == 0) {
                groupNoteList = groupsElement.getElementsByTagName("walker_group"); // #for_erflute
            }
            for (int k = 0; k < groupNoteList.getLength(); k++) {
                final Element groupElement = (Element) groupNoteList.item(k);
                final WalkerGroup group = groupLoader.loadWalkerGroup(vdiagram, groupElement, context);
                groups.add(group);
                final String id = getStringValue(groupElement, "id");
                if (Srl.is_NotNull_and_NotTrimmedEmpty(id)) { // for compatible with ERMaster
                    context.walkerMap.put(id, group);
                }
            }
        }
        vdiagram.setWalkerGroups(groups);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
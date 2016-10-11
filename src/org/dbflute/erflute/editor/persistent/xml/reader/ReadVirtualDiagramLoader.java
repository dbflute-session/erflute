package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
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
    protected final ReadNoteLoader noteLoader;
    protected final ReadGroupLoader groupLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadVirtualDiagramLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadTableLoader tableLoader,
            ReadNoteLoader noteLoader, ReadGroupLoader groupLoader) {
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
                vdiagram.setName(getStringValue(modelElement, "name"));
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
                vdiagram.setTables(tables);
                loadElementNotes(context, modelElement, vdiagram, diagram);
                loadElementVGroups(context, modelElement, vdiagram);
                final String id = vdiagram.buildVirtualDiagramId(); // #for_erflute
                context.virtualDiagramMap.put(id, vdiagram);
                results.add(vdiagram);
            }
        }
        return results;
    }

    private void loadElementNotes(LoadContext context, Element modelElement, ERVirtualDiagram model, ERDiagram diagram) {
        final List<Note> notes = new ArrayList<Note>();
        final Element elNotes = getElement(modelElement, "notes");
        if (elNotes != null) {
            final NodeList noteNodeList = elNotes.getElementsByTagName("note");
            for (int i = 0; i < noteNodeList.getLength(); i++) {
                final Element noteElement = (Element) noteNodeList.item(i);
                final Note note = noteLoader.loadNote(noteElement, context, model);
                notes.add(note);
                final String id = getStringValue(noteElement, "id");
                if (Srl.is_NotNull_and_NotTrimmedEmpty(id)) { // for compatible with ERMaster
                    context.nodeElementMap.put(id, note);
                }
                // unneeded because note is independent on model by jflute
                //diagram.getDiagramContents().getContents().addNodeElement(note);
            }
        }
        model.setNotes(notes);
    }

    private void loadElementVGroups(LoadContext context, Element modelElement, ERVirtualDiagram model) {
        final List<VGroup> groups = new ArrayList<VGroup>();
        final Element elGroups = getElement(modelElement, "groups");
        if (elGroups != null) {
            final NodeList groupEls = elGroups.getElementsByTagName("group");
            for (int k = 0; k < groupEls.getLength(); k++) {
                final Element groupElement = (Element) groupEls.item(k);
                final VGroup group = groupLoader.loadGroup(model, groupElement, context);
                groups.add(group);
                final String id = getStringValue(groupElement, "id");
                if (Srl.is_NotNull_and_NotTrimmedEmpty(id)) { // for compatible with ERMaster
                    context.nodeElementMap.put(id, group);
                }
            }
        }
        model.setGroups(groups);
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
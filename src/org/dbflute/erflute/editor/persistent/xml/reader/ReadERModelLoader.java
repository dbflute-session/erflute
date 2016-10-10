package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadERModelLoader {

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
    public ReadERModelLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadTableLoader tableLoader,
            ReadNoteLoader noteLoader, ReadGroupLoader groupLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.tableLoader = tableLoader;
        this.noteLoader = noteLoader;
        this.groupLoader = groupLoader;
    }

    // #analyzed unused, virtual models in ermodels by jflute
    //// ===================================================================================
    ////                                                                             ERModel
    ////                                                                             =======
    //public ERModel loadErmodel(Element parent, LoadContext context, ERDiagram diagram) {
    //    final ERModel model = new ERModel(diagram);
    //    model.setName(getStringValue(parent, "name"));
    //    final List<ERVirtualTable> tables = new ArrayList<ERVirtualTable>();
    //    final Element vtables = getElement(parent, "vtables");
    //    if (vtables != null) {
    //        final NodeList tableEls = vtables.getElementsByTagName("vtable");
    //        for (int k = 0; k < tableEls.getLength(); k++) {
    //            final Element tableElement = (Element) tableEls.item(k);
    //            tables.add(tableLoader.loadVirtualTable(model, tableElement, context));
    //        }
    //    }
    //    model.setTables(tables);
    //    final String id = getStringValue(parent, "id");
    //    context.ermodelMap.put(id, model);
    //    return model;
    //}

    // ===================================================================================
    //                                                                            ERModels
    //                                                                            ========
    public List<ERModel> loadErmodels(Element parent, LoadContext context, ERDiagram diagram) {
        final List<ERModel> results = new ArrayList<ERModel>();
        final Element element = getElement(parent, "ermodels");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("ermodel");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element modelElement = (Element) nodeList.item(i);
                final ERModel model = new ERModel(diagram);
                model.setName(getStringValue(modelElement, "name"));
                assistLogic.loadColor(model, modelElement);
                final List<ERVirtualTable> tables = new ArrayList<ERVirtualTable>();
                final Element vtables = getElement(modelElement, "vtables");
                if (vtables != null) {
                    final NodeList tableEls = vtables.getElementsByTagName("vtable");
                    for (int k = 0; k < tableEls.getLength(); k++) {
                        final Element tableElement = (Element) tableEls.item(k);
                        tables.add(tableLoader.loadVirtualTable(model, tableElement, context));
                    }
                }
                model.setTables(tables);
                loadElementNotes(context, modelElement, model, diagram);
                loadElementVGroups(context, modelElement, model);
                final String id = getStringValue(modelElement, "id");
                context.ermodelMap.put(id, model);
                results.add(model);
            }
        }
        return results;
    }

    private void loadElementNotes(LoadContext context, Element modelElement, ERModel model, ERDiagram diagram) {
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

    private void loadElementVGroups(LoadContext context, Element modelElement, ERModel model) {
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
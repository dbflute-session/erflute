package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadNoteLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadNodeElementLoader nodeElementLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadNoteLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadNodeElementLoader nodeElementLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementLoader = nodeElementLoader;
    }

    // ===================================================================================
    //                                                                               Note
    //                                                                              ======
    public Note loadNote(ERModel model, Element element, LoadContext context) {
        final Note note = new Note();
        note.setModel(model);
        note.setText(getStringValue(element, "text"));
        nodeElementLoader.loadNodeElement(note, element, context);
        return note;
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }
}
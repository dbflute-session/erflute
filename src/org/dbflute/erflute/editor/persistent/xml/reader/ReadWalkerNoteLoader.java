package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadWalkerNoteLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDiagramWalkerLoader nodeElementLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadWalkerNoteLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadDiagramWalkerLoader nodeElementLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementLoader = nodeElementLoader;
    }

    // ===================================================================================
    //                                                                               Note
    //                                                                              ======
    public WalkerNote loadNote(Element element, LoadContext context) { // for main model
        return doLoadNote(element, context, null);
    }

    public WalkerNote loadNote(Element element, LoadContext context, ERVirtualDiagram model) { // for virtual
        return doLoadNote(element, context, model);
    }

    private WalkerNote doLoadNote(Element element, LoadContext context, ERVirtualDiagram model) {
        final WalkerNote note = new WalkerNote();
        note.setText(getStringValue(element, "text"));
        if (model != null) {
            note.setVirtualDiagram(model);
        }
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
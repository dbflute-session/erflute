package org.dbflute.erflute.editor.persistent.xml.writer;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenNoteBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenDiagramWalkerBuilder nodeElementBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenNoteBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenDiagramWalkerBuilder nodeElementBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.nodeElementBuilder = nodeElementBuilder;
    }

    // ===================================================================================
    //                                                                              Note
    //                                                                             =======
    public String buildNote(Note note, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<note>\n");
        xml.append(tab(nodeElementBuilder.buildNodeElement(note, context)));
        xml.append("\t<text>").append(escape(note.getText())).append("</text>\n");
        xml.append("</note>\n");
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
package org.dbflute.erflute.editor.persistent.xml.writer;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenWalkerNoteBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;
    protected final WrittenDiagramWalkerBuilder walkerBuilder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenWalkerNoteBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic, WrittenDiagramWalkerBuilder walkerBuilder) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.walkerBuilder = walkerBuilder;
    }

    // ===================================================================================
    //                                                                         Walker Note
    //                                                                         ===========
    public String buildNote(WalkerNote note, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<walker_note>\n");
        xml.append(tab(walkerBuilder.buildWalker(note, context)));
        xml.append("\t<note_text>").append(escape(note.getNoteText())).append("</note_text>\n");
        xml.append("</walker_note>\n");
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
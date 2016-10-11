package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.note;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;

public class WalkerNoteEditCommand extends AbstractCommand {

    private final String oldText;
    private final String text;
    private final WalkerNote note;

    public WalkerNoteEditCommand(WalkerNote note, String text) {
        this.note = note;
        this.oldText = this.note.getText();
        this.text = text;
    }

    @Override
    protected void doExecute() {
        this.note.setText(text);
    }

    @Override
    protected void doUndo() {
        this.note.setText(oldText);
    }
}

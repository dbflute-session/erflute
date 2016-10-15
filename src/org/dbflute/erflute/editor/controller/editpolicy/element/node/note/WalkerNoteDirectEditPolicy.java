package org.dbflute.erflute.editor.controller.editpolicy.element.node.note;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.note.WalkerNoteEditCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

public class WalkerNoteDirectEditPolicy extends DirectEditPolicy {

    @Override
    protected Command getDirectEditCommand(DirectEditRequest request) {
        final String text = (String) request.getCellEditor().getValue();
        final WalkerNoteEditCommand command = new WalkerNoteEditCommand((WalkerNote) getHost().getModel(), text);
        return command;
    }

    @Override
    protected void showCurrentEditValue(DirectEditRequest request) {
    }
}

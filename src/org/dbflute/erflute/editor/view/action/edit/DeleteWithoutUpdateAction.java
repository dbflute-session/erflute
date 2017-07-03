package org.dbflute.erflute.editor.view.action.edit;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.WithoutUpdateCommandWrapper;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.IWorkbenchPart;

public class DeleteWithoutUpdateAction extends DeleteAction {

    private final MainDiagramEditor part;

    public DeleteWithoutUpdateAction(MainDiagramEditor part) {
        super((IWorkbenchPart) part);
        this.part = part;
        setText(DisplayMessages.getMessage("action.title.delete"));
        setToolTipText(DisplayMessages.getMessage("action.title.delete"));

        setActionDefinitionId("org.eclipse.ui.edit.delete");
    }

    @Override
    public Command createDeleteCommand(@SuppressWarnings("rawtypes") List objects) {
        final Command command = super.createDeleteCommand(objects);

        if (command == null) {
            return null;
        }

        if (command instanceof CompoundCommand) {
            final CompoundCommand compoundCommand = (CompoundCommand) command;
            if (compoundCommand.getCommands().isEmpty()) {
                return null;
            }
        }

        final EditPart editPart = part.getGraphicalViewer().getContents();
        final ERDiagram diagram = ERModelUtil.getDiagram(editPart);

        return new WithoutUpdateCommandWrapper(command, diagram);
    }

    @Override
    protected boolean calculateEnabled() {
        final Command cmd = createDeleteCommand(getSelectedObjects());
        if (cmd == null) {
            return false;
        }

        return true;
    }
}

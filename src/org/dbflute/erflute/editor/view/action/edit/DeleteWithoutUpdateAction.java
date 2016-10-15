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

    private MainDiagramEditor part;

    public DeleteWithoutUpdateAction(MainDiagramEditor part) {
        super((IWorkbenchPart) part);
        this.part = part;
        this.setText(DisplayMessages.getMessage("action.title.delete"));
        this.setToolTipText(DisplayMessages.getMessage("action.title.delete"));

        this.setActionDefinitionId("org.eclipse.ui.edit.delete");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command createDeleteCommand(List objects) {
        Command command = super.createDeleteCommand(objects);

        if (command == null) {
            return null;
        }

        if (command instanceof CompoundCommand) {
            CompoundCommand compoundCommand = (CompoundCommand) command;
            if (compoundCommand.getCommands().isEmpty()) {
                return null;
            }
        }

        EditPart editPart = part.getGraphicalViewer().getContents();
        ERDiagram diagram = ERModelUtil.getDiagram(editPart);

        return new WithoutUpdateCommandWrapper(command, diagram);
    }

    @Override
    protected boolean calculateEnabled() {
        Command cmd = createDeleteCommand(getSelectedObjects());
        if (cmd == null) {
            return false;
        }

        return true;
    }

}

package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.eclipse.gef.commands.Command;

public class WithoutUpdateCommandWrapper extends Command {

    private final Command command;
    private final ERDiagram diagram;

    public WithoutUpdateCommandWrapper(Command command, ERDiagram diagram) {
        this.command = command;
        this.diagram = diagram;
    }

    @Override
    public void execute() {
        ERDiagramEditPart.setUpdateable(false);

        command.execute();

        ERDiagramEditPart.setUpdateable(true);

        diagram.changeAll();
    }

    @Override
    public void undo() {
        ERDiagramEditPart.setUpdateable(false);

        command.undo();

        ERDiagramEditPart.setUpdateable(true);

        diagram.changeAll();
    }

    @Override
    public boolean canExecute() {
        return command.canExecute();
    }

    @Override
    public boolean canUndo() {
        return command.canUndo();
    }
}

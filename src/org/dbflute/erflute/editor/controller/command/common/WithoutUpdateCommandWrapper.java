package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.eclipse.gef.commands.Command;

public class WithoutUpdateCommandWrapper extends Command {

    private Command command;

    private ERDiagram diagram;

    public WithoutUpdateCommandWrapper(Command command, ERDiagram diagram) {
        this.command = command;
        this.diagram = diagram;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        ERDiagramEditPart.setUpdateable(false);

        command.execute();

        ERDiagramEditPart.setUpdateable(true);

        this.diagram.changeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        ERDiagramEditPart.setUpdateable(false);

        command.undo();

        ERDiagramEditPart.setUpdateable(true);

        this.diagram.changeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        return command.canExecute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUndo() {
        return command.canUndo();
    }

}

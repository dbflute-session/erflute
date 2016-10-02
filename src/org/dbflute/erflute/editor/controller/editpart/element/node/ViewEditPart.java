package org.dbflute.erflute.editor.controller.editpart.element.node;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.dialog.element.view.ViewDialog;
import org.dbflute.erflute.editor.view.figure.view.ViewFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class ViewEditPart extends TableViewEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        ERDiagram diagram = this.getDiagram();
        Settings settings = diagram.getDiagramContents().getSettings();

        ViewFigure figure = new ViewFigure(settings);

        this.changeFont(figure);

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequestOpen() {
        ERView view = (ERView) this.getModel();
        ERDiagram diagram = this.getDiagram();

        ERView copyView = view.copyData();

        ViewDialog dialog =
                new ViewDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this.getViewer(), copyView, diagram
                        .getDiagramContents().getGroups());

        if (dialog.open() == IDialogConstants.OK_ID) {
            CompoundCommand command = createChangeViewPropertyCommand(diagram, view, copyView);

            this.executeCommand(command.unwrap());
        }
    }

    public static CompoundCommand createChangeViewPropertyCommand(ERDiagram diagram, ERView view, ERView copyView) {
        CompoundCommand command = new CompoundCommand();

        ChangeTableViewPropertyCommand changeViewPropertyCommand = new ChangeTableViewPropertyCommand(view, copyView);
        command.add(changeViewPropertyCommand);

        return command;
    }

}

package org.dbflute.erflute.editor.controller.editpart.element.node;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.view.ViewDialog;
import org.dbflute.erflute.editor.view.figure.view.ViewFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class ViewEditPart extends TableViewEditPart {

    @Override
    protected IFigure createFigure() {
        final ERDiagram diagram = getDiagram();
        final DiagramSettings settings = diagram.getDiagramContents().getSettings();
        final ViewFigure figure = new ViewFigure(settings);

        changeFont(figure);

        return figure;
    }

    @Override
    public void performRequestOpen() {
        final ERView view = (ERView) getModel();
        final ERDiagram diagram = getDiagram();
        final ERView copyView = view.copyData();
        final ViewDialog dialog = new ViewDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                getViewer(), copyView, diagram.getDiagramContents().getColumnGroupSet());

        if (dialog.open() == IDialogConstants.OK_ID) {
            final CompoundCommand command = createChangeViewPropertyCommand(diagram, view, copyView);
            executeCommand(command.unwrap());
        }
    }

    public static CompoundCommand createChangeViewPropertyCommand(ERDiagram diagram, ERView view, ERView copyView) {
        final CompoundCommand command = new CompoundCommand();

        final ChangeTableViewPropertyCommand changeViewPropertyCommand = new ChangeTableViewPropertyCommand(view, copyView);
        command.add(changeViewPropertyCommand);

        return command;
    }
}

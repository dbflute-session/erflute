package org.dbflute.erflute.editor.view.action.outline;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.ermodel.ChangeVirtualDiagramNameCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.view.dialog.vdiagram.InputVirtualDiagramNameValidator;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

/**
 * @author who?
 * @author kajiku
 */
public class ChangeNameAction extends AbstractOutlineBaseAction {

    public static final String ID = ChangeNameAction.class.getName();

    public ChangeNameAction(TreeViewer treeViewer) {
        super(ID, "Rename", treeViewer);
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final List<?> selectedEditParts = getTreeViewer().getSelectedEditParts();
        final EditPart editPart = (EditPart) selectedEditParts.get(0);
        final Object model = editPart.getModel();
        if (model instanceof ERVirtualDiagram) {
            final ERVirtualDiagram vdiagram = (ERVirtualDiagram) model;
            final InputVirtualDiagramNameValidator validator = new InputVirtualDiagramNameValidator(diagram, vdiagram.getName());
            final InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Rename",
                    "Input new name", vdiagram.getName(), validator);
            if (dialog.open() == IDialogConstants.OK_ID) {
                final ChangeVirtualDiagramNameCommand command = new ChangeVirtualDiagramNameCommand(vdiagram, dialog.getValue());
                execute(command);
            }
        }
    }
}

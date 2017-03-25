package org.dbflute.erflute.editor.view.action.outline;

import java.util.List;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {

        final ERDiagram diagram = this.getDiagram();

        final List selectedEditParts = this.getTreeViewer().getSelectedEditParts();
        final EditPart editPart = (EditPart) selectedEditParts.get(0);
        final Object model = editPart.getModel();
        if (model instanceof ERVirtualDiagram) {
            final ERVirtualDiagram ermodel = (ERVirtualDiagram) model;
            final InputVirtualDiagramNameValidator validator = new InputVirtualDiagramNameValidator();
            final InputDialog dialog =
                    new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Rename", "Input new name",
                            ermodel.getName(), validator);
            if (dialog.open() == IDialogConstants.OK_ID) {
                ermodel.setName(dialog.getValue());
                diagram.getDiagramContents().getVirtualDiagramSet().changeModel(ermodel);
                ermodel.getDiagram().getEditor().setDirty(true);
                //				ermodel.changeAll();
                //				AddERModelCommand command = new AddERModelCommand(diagram, dialog.getValue());
                //				this.execute(command);
            }
        }

    }

}

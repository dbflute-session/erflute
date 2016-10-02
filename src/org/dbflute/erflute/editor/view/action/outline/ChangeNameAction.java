package org.dbflute.erflute.editor.view.action.outline;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class ChangeNameAction extends AbstractOutlineBaseAction {

    public static final String ID = ChangeNameAction.class.getName();

    public ChangeNameAction(TreeViewer treeViewer) {
        super(ID, DisplayMessages.getMessage("action.title.change.name"), treeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {

        ERDiagram diagram = this.getDiagram();

        List selectedEditParts = this.getTreeViewer().getSelectedEditParts();
        EditPart editPart = (EditPart) selectedEditParts.get(0);
        Object model = editPart.getModel();
        if (model instanceof ERModel) {
            ERModel ermodel = (ERModel) model;

            InputDialog dialog =
                    new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "���O�ύX",
                            "�_�C�A�O����������͂��ĉ������B", ermodel.getName(), null);
            if (dialog.open() == IDialogConstants.OK_ID) {
                ermodel.setName(dialog.getValue());
                diagram.getDiagramContents().getModelSet().changeModel(ermodel);
                ermodel.getDiagram().getEditor().setDirty(true);
                //				ermodel.changeAll();
                //				AddERModelCommand command = new AddERModelCommand(diagram, dialog.getValue());
                //				this.execute(command);
            }
        }

    }

}

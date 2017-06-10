package org.dbflute.erflute.editor.view.action.edit;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

public class SelectAllContentsAction extends SelectAllAction {

    private IWorkbenchPart part;

    public SelectAllContentsAction(IWorkbenchPart part) {
        super(part);
        this.part = part;
        this.setText(DisplayMessages.getMessage("action.title.select.all"));

        this.setActionDefinitionId("org.eclipse.ui.edit.selectAll");
    }

    @Override
    public void run() {
        GraphicalViewer viewer = (GraphicalViewer) part.getAdapter(GraphicalViewer.class);

        if (viewer != null) {
            List<DiagramWalkerEditPart> children = new ArrayList<DiagramWalkerEditPart>();

            for (Object child : viewer.getContents().getChildren()) {
                if (child instanceof DiagramWalkerEditPart) {
                    DiagramWalkerEditPart editPart = (DiagramWalkerEditPart) child;
                    if (editPart.getFigure().isVisible()) {
                        children.add(editPart);
                    }
                }
            }

            viewer.setSelection(new StructuredSelection(children));
        }
    }
}

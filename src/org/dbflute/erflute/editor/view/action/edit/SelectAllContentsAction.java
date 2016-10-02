package org.dbflute.erflute.editor.view.action.edit;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.editpart.element.node.NodeElementEditPart;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        GraphicalViewer viewer = (GraphicalViewer) part.getAdapter(GraphicalViewer.class);

        if (viewer != null) {
            List<NodeElementEditPart> children = new ArrayList<NodeElementEditPart>();

            for (Object child : viewer.getContents().getChildren()) {
                if (child instanceof NodeElementEditPart) {
                    NodeElementEditPart editPart = (NodeElementEditPart) child;
                    if (editPart.getFigure().isVisible()) {
                        children.add(editPart);
                    }
                }
            }

            viewer.setSelection(new StructuredSelection(children));
        }
    }
}

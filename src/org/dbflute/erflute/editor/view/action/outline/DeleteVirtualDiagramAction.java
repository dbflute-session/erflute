package org.dbflute.erflute.editor.view.action.outline;

import java.util.List;

import org.dbflute.erflute.editor.controller.command.ermodel.DeleteVirtualDiagramCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.widgets.Event;

/**
 * @author kajiku
 */
public class DeleteVirtualDiagramAction extends AbstractOutlineBaseAction {

    public static final String ID = DeleteVirtualDiagramAction.class.getName();

    public DeleteVirtualDiagramAction(TreeViewer treeViewer) {
        super(ID, "Delete Virtual Diagram", treeViewer);
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = getDiagram();
        final List<?> selectedEditParts = getTreeViewer().getSelectedEditParts();
        final EditPart editPart = (EditPart) selectedEditParts.get(0);
        final Object model = editPart.getModel();
        if (model instanceof ERVirtualDiagram) {
            final DeleteVirtualDiagramCommand command =
                    new DeleteVirtualDiagramCommand(diagram, ((ERVirtualDiagram) model).getName());
            execute(command);
        }
    }
}

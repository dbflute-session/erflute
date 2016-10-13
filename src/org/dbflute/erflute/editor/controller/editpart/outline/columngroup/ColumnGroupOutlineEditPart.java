package org.dbflute.erflute.editor.controller.editpart.outline.columngroup;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.group.ChangeColumnGroupCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.group.GroupComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.dbflute.erflute.editor.view.dialog.columngroup.ColumnGroupDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ColumnGroupOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    protected void refreshOutlineVisuals() {
        final ColumnGroup columnGroup = (ColumnGroup) this.getModel();
        this.setWidgetText(this.getDiagram().filter(columnGroup.getName()));
        this.setWidgetImage(Activator.getImage(ImageKey.GROUP));
    }

    @Override
    public void performRequest(Request request) {
        final ColumnGroup columnGroup = (ColumnGroup) getModel();
        final ERDiagram diagram = getDiagram();
        final ColumnGroupSet groupSet = diagram.getDiagramContents().getColumnGroupSet();
        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            final ColumnGroupDialog dialog = new ColumnGroupDialog(shell, groupSet, diagram, groupSet.indexOf(columnGroup));
            if (dialog.open() == IDialogConstants.OK_ID) {
                final List<CopyGroup> newColumnGroups = dialog.getCopyColumnGroups();
                final Command command = new ChangeColumnGroupCommand(diagram, groupSet, newColumnGroups);
                execute(command);
            }
        }
        super.performRequest(request);
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new GroupComponentEditPolicy());
    }
}

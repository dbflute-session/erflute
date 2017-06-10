package org.dbflute.erflute.editor.controller.editpart.outline.tablespace;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.db.EclipseDBManagerFactory;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.tablespace.EditTablespaceCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.not_element.tablespace.TablespaceComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;

public class TablespaceOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    protected void refreshOutlineVisuals() {
        Tablespace tablespace = (Tablespace) this.getModel();

        this.setWidgetText(this.getDiagram().filter(tablespace.getName()));
        this.setWidgetImage(Activator.getImage(ImageKey.TABLESPACE));
    }

    @Override
    public void performRequest(Request request) {
        Tablespace tablespace = (Tablespace) this.getModel();
        ERDiagram diagram = this.getDiagram();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            TablespaceDialog dialog = EclipseDBManagerFactory.getEclipseDBManager(diagram).createTablespaceDialog();

            if (dialog == null) {
                Activator.showMessageDialog("dialog.message.tablespace.not.supported");
            } else {
                dialog.init(tablespace, diagram);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    EditTablespaceCommand command = new EditTablespaceCommand(diagram, tablespace, dialog.getResult());
                    this.execute(command);
                }
            }
        }

        super.performRequest(request);
    }

    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new TablespaceComponentEditPolicy());
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    public boolean isDeleteable() {
        return true;
    }
}

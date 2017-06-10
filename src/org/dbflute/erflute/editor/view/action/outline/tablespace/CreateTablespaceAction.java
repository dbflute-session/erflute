package org.dbflute.erflute.editor.view.action.outline.tablespace;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.EclipseDBManagerFactory;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.tablespace.CreateTablespaceCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.outline.AbstractOutlineBaseAction;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;

public class CreateTablespaceAction extends AbstractOutlineBaseAction {

    public static final String ID = CreateTablespaceAction.class.getName();

    public CreateTablespaceAction(TreeViewer treeViewer) {
        super(ID, DisplayMessages.getMessage("action.title.create.tablespace"), treeViewer);
    }

    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        TablespaceDialog dialog = EclipseDBManagerFactory.getEclipseDBManager(diagram).createTablespaceDialog();
        if (dialog == null) {
            Activator.showMessageDialog("dialog.message.tablespace.not.supported");

        } else {
            dialog.init(null, diagram);

            if (dialog.open() == IDialogConstants.OK_ID) {
                CreateTablespaceCommand command = new CreateTablespaceCommand(diagram, dialog.getResult());
                this.execute(command);
            }
        }
    }
}

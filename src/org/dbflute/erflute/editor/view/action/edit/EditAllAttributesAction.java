package org.dbflute.erflute.editor.view.action.edit;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.controller.command.edit.EditAllAttributesCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.edit.EditAllAttributesDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class EditAllAttributesAction extends AbstractBaseAction {

    public static final String ID = EditAllAttributesAction.class.getName();

    public EditAllAttributesAction(RealModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.edit.all.attributes"), editor);
        this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EDIT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        EditAllAttributesDialog dialog =
                new EditAllAttributesDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        if (dialog.open() == IDialogConstants.OK_ID) {
            DiagramContents newDiagramContents = dialog.getDiagramContents();
            EditAllAttributesCommand command = new EditAllAttributesCommand(diagram, newDiagramContents);
            this.execute(command);
        }
    }
}

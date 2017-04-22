package org.dbflute.erflute.editor.view.action.outline.trigger;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger.CreateTriggerCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.outline.AbstractOutlineBaseAction;
import org.dbflute.erflute.editor.view.dialog.outline.trigger.TriggerDialog;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class CreateTriggerAction extends AbstractOutlineBaseAction {

    public static final String ID = CreateTriggerAction.class.getName();

    public CreateTriggerAction(TreeViewer treeViewer) {
        super(ID, DisplayMessages.getMessage("action.title.create.trigger"), treeViewer);
    }

    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        TriggerDialog dialog = new TriggerDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);

        if (dialog.open() == IDialogConstants.OK_ID) {
            CreateTriggerCommand command = new CreateTriggerCommand(diagram, dialog.getResult());
            this.execute(command);
        }
    }
}

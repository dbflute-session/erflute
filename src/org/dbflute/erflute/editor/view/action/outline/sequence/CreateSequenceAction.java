package org.dbflute.erflute.editor.view.action.outline.sequence;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence.CreateSequenceCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.outline.AbstractOutlineBaseAction;
import org.dbflute.erflute.editor.view.dialog.outline.sequence.SequenceDialog;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class CreateSequenceAction extends AbstractOutlineBaseAction {

    public static final String ID = CreateSequenceAction.class.getName();

    public CreateSequenceAction(TreeViewer treeViewer) {
        super(ID, DisplayMessages.getMessage("action.title.create.sequence"), treeViewer);
    }

    @Override
    public void execute(Event event) {
        final ERDiagram diagram = getDiagram();
        final SequenceDialog dialog = new SequenceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, diagram);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final CreateSequenceCommand command = new CreateSequenceCommand(diagram, dialog.getResult());
            execute(command);
        }
    }
}

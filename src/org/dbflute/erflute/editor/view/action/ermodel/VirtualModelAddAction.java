package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.ermodel.AddERModelCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class VirtualModelAddAction extends AbstractBaseAction {

    public static final String ID = VirtualModelAddAction.class.getName();

    public VirtualModelAddAction(MainDiagramEditor editor) {
        super(ID, "new VirtualModel()", editor);
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = this.getDiagram();
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final String dialogTitle = "new VirtualModel()";
        final String dialogMessage = "input name for new Virtual Model";
        final InputDialog dialog = new InputDialog(shell, dialogTitle, dialogMessage, "", null);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final AddERModelCommand command = new AddERModelCommand(diagram, dialog.getValue());
            execute(command);
        }
    }
}

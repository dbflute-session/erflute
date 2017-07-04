package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.ermodel.AddVirtualDiagramCommand;
import org.dbflute.erflute.editor.controller.command.ermodel.OpenERModelCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.vdiagram.InputVirtualDiagramNameValidator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 * @author kajiku
 */
public class VirtualDiagramAddAction extends AbstractBaseAction {

    public static final String ID = VirtualDiagramAddAction.class.getName();

    public VirtualDiagramAddAction(MainDiagramEditor editor) {
        super(ID, "New Virtual Diagram", editor);
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = this.getDiagram();
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final String dialogTitle = "New Virtual Diagram";
        final String dialogMessage = "Input name for new Virtual Diagram";
        final InputVirtualDiagramNameValidator validator = new InputVirtualDiagramNameValidator(diagram, null);
        final InputDialog dialog = new InputDialog(shell, dialogTitle, dialogMessage, "", validator);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final AddVirtualDiagramCommand addCommand = new AddVirtualDiagramCommand(diagram, dialog.getValue());
            execute(addCommand);
            final OpenERModelCommand openCommand = new OpenERModelCommand(diagram, diagram.getCurrentVirtualDiagram());
            execute(openCommand);
        }
    }
}

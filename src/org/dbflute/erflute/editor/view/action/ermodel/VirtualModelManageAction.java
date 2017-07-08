package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeSettingsCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.category.CategoryManageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class VirtualModelManageAction extends AbstractBaseAction {

    public static final String ID = VirtualModelManageAction.class.getName();

    public VirtualModelManageAction(MainDiagramEditor editor) {
        super(ID, "Manage Virtual Models", editor);
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = getDiagram();
        final DiagramSettings settings = (DiagramSettings) diagram.getDiagramContents().getSettings().clone();
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final CategoryManageDialog dialog = new CategoryManageDialog(shell, settings, diagram);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final ChangeSettingsCommand command = new ChangeSettingsCommand(diagram, settings);
            execute(command);
        }
    }
}

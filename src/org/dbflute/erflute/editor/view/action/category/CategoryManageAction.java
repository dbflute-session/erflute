package org.dbflute.erflute.editor.view.action.category;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainModelEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeSettingsCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.category.CategoryManageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class CategoryManageAction extends AbstractBaseAction {

    public static final String ID = CategoryManageAction.class.getName();

    public CategoryManageAction(MainModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.category.manage"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        Settings settings = (Settings) diagram.getDiagramContents().getSettings().clone();

        CategoryManageDialog dialog =
                new CategoryManageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), settings, diagram);

        if (dialog.open() == IDialogConstants.OK_ID) {
            ChangeSettingsCommand command = new ChangeSettingsCommand(diagram, settings);
            this.execute(command);
        }
    }

}

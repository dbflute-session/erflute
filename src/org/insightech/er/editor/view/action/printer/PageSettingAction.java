package org.insightech.er.editor.view.action.printer;

import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.MainModelEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.printer.PageSettingDialog;

public class PageSettingAction extends AbstractBaseAction {

    public static final String ID = PageSettingAction.class.getName();

    public PageSettingAction(MainModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.page.setting"), editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) {
        ERDiagram diagram = this.getDiagram();

        PageSettingDialog dialog = new PageSettingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        if (dialog.open() == IDialogConstants.OK_ID) {

        }
    }

}

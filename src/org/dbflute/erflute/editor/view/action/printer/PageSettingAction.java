package org.dbflute.erflute.editor.view.action.printer;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.printer.PageSettingDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class PageSettingAction extends AbstractBaseAction {

    public static final String ID = PageSettingAction.class.getName();

    public PageSettingAction(RealModelEditor editor) {
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

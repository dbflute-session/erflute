package org.dbflute.erflute.editor.view.dialog.dbexport;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ExportWarningDialog extends ExportErrorDialog {

    public ExportWarningDialog(Shell parentShell, List<ValidateResult> errorList) {
        super(parentShell, errorList);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        this.createButton(parent, IDialogConstants.OK_ID, DisplayMessages.getMessage("label.button.continue"), true);
        this.createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID || buttonId == IDialogConstants.CANCEL_ID) {
            setReturnCode(buttonId);
            close();

        } else if (buttonId == IDialogConstants.OK_ID) {
            setReturnCode(buttonId);
            close();
        }

        super.buttonPressed(buttonId);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.export.ddl";
    }
}

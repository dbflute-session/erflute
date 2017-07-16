package org.dbflute.erflute.editor.view.dialog.dbexport;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractErrorDialog extends Dialog {

    protected Text textArea;

    public AbstractErrorDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(DisplayMessages.getMessage(getTitle()));

        final Composite composite = (Composite) super.createDialogArea(parent);

        this.textArea = CompositeFactory.createTextArea(null, composite, getMessage(), 400, 200, 1, false, false);

        composite.setLayout(new GridLayout());

        textArea.setText(Format.null2blank(getData()));

        return composite;
    }

    protected abstract String getData();

    protected String getMessage() {
        return "dialog.message.export.ddl.error";
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();

        super.buttonPressed(buttonId);
    }

    protected abstract String getTitle();
}

package org.dbflute.erflute.editor.view.dialog.dbexport;

import org.eclipse.swt.widgets.Shell;

public class ErrorDialog extends AbstractErrorDialog {

    private final String message;

    public ErrorDialog(Shell parentShell, String message) {
        super(parentShell);

        this.message = message;
    }

    @Override
    protected String getData() {
        return message;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.error";
    }
}

package org.dbflute.erflute.editor.view.dialog.dbexport;

import java.util.List;

import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.eclipse.swt.widgets.Shell;

public class ExportErrorDialog extends AbstractErrorDialog {

    private final List<ValidateResult> errorList;

    public ExportErrorDialog(Shell parentShell, List<ValidateResult> errorList) {
        super(parentShell);

        this.errorList = errorList;
    }

    @Override
    protected String getMessage() {
        return "dialog.message.export.ddl.error.no.continue";
    }

    @Override
    protected String getData() {
        final StringBuilder text = new StringBuilder();

        for (final ValidateResult errorMessage : errorList) {
            text.append(errorMessage.getMessage());
            text.append("\r\n");
        }

        return text.toString();
    }

    @Override
    protected String getTitle() {
        return "dialog.title.export.db";
    }
}

package org.dbflute.erflute.core.dialog;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class AbstractDialog extends Dialog {

    private CLabel errorMessageText = null;
    private final int numColumns;
    private boolean enabledOkButton = true;
    protected boolean initialized = false;

    protected AbstractDialog(Shell parentShell) {
        this(parentShell, 1);
    }

    protected AbstractDialog(Shell parentShell, int numColumns) {
        super(parentShell);
        this.numColumns = numColumns;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        this.getShell().setText(DisplayMessages.getMessage(this.getTitle()));
        final Composite composite = (Composite) super.createDialogArea(parent);
        try {
            final GridLayout layout = new GridLayout();
            layout.numColumns = this.numColumns;
            this.initLayout(layout);
            composite.setLayout(layout);
            composite.setLayoutData(this.createLayoutData());
            this.createErrorComposite(composite);
            this.initialize(composite);
            this.setData();
            this.initialized = true;
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        return composite;
    }

    @Override
    protected Control createContents(Composite parent) {
        final Control control = super.createContents(parent);
        this.addListener();
        this.validate();
        return control;
    }

    protected void initLayout(GridLayout layout) {
    }

    protected int getNumColumns() {
        return this.numColumns;
    }

    protected int getErrorLine() {
        return 1;
    }

    protected Object createLayoutData() {
        return new GridData(GridData.FILL_BOTH);
    }

    protected void createErrorComposite(Composite parent) {
        this.errorMessageText = new CLabel(parent, SWT.NONE);
        this.errorMessageText.setText("");
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = 30 * this.getErrorLine();
        gridData.horizontalSpan = this.numColumns;
        this.errorMessageText.setLayoutData(gridData);
    }

    protected Integer getIntegerValue(Text text) {
        final String value = text.getText();
        if (Check.isEmpty(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    protected void setMessage(String errorMessage) {
        if (this.errorMessageText != null) {
            if (errorMessage == null) {
                this.errorMessageText.setImage(null);
                this.errorMessageText.setText("");
            } else {
                final Image errorIcon = Activator.getImage(ImageKey.ERROR);
                this.errorMessageText.setImage(errorIcon);
                this.errorMessageText.setText(errorMessage);
            }
        }
    }

    abstract protected void initialize(Composite composite);

    abstract protected void setData();

    protected void addListener() {
    }

    protected static boolean isBlank(Text text) {
        if (text.getText().trim().length() == 0) {
            return true;
        }
        return false;
    }

    protected static boolean isBlank(Combo combo) {
        if (combo.getText().trim().length() == 0) {
            return true;
        }
        return false;
    }

    protected void enabledButton(boolean enabled) {
        this.enabledOkButton = enabled;
        final Button button1 = this.getButton(IDialogConstants.OK_ID);
        if (button1 != null) {
            button1.setEnabled(enabled);
        }
        final Button button2 = this.getButton(IDialogConstants.CANCEL_ID);
        if (button2 != null) {
            button2.setEnabled(enabled);
        }
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID || buttonId == IDialogConstants.CANCEL_ID || buttonId == IDialogConstants.BACK_ID) {
            this.setReturnCode(buttonId);
            this.close();
        } else if (buttonId == IDialogConstants.OK_ID) {
            try {
                if (!validate()) {
                    return;
                }
                performOK();
                setReturnCode(buttonId);
                close();
            } catch (final InputException e) {
                this.setMessage(DisplayMessages.getMessage(e.getMessage()));
                return;
            } catch (final Exception e) {
                Activator.showExceptionDialog(e);
            }
        }

        super.buttonPressed(buttonId);
    }

    public final boolean validate() {
        if (!this.initialized) {
            return true;
        }
        final Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(false);
        }
        final String errorMessage = doValidate();
        if (errorMessage != null) {
            this.setMessage(DisplayMessages.getMessage(errorMessage));
            return false;
        }
        if (okButton != null && this.enabledOkButton) {
            okButton.setEnabled(true);
        }
        this.setMessage(null);
        return true;
    }

    abstract protected String doValidate();

    abstract protected void performOK() throws InputException;

    abstract protected String getTitle();

    protected Button createCheckbox(Composite composite, String title) {
        return CompositeFactory.createCheckbox(this, composite, title, this.getNumColumns());
    }
}

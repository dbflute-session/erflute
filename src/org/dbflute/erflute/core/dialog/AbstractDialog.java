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

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private CLabel errorMessageText;
    private final int numColumns;
    private boolean enabledOkButton = true;
    protected boolean initialized;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected AbstractDialog(Shell parentShell) {
        this(parentShell, 1);
    }

    protected AbstractDialog(Shell parentShell, int numColumns) {
        super(parentShell);
        this.numColumns = numColumns;
    }

    // ===================================================================================
    //                                                                         Dialog Area
    //                                                                         ===========
    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(DisplayMessages.getMessage(getTitle()));
        final Composite composite = (Composite) super.createDialogArea(parent);
        try {
            final GridLayout layout = new GridLayout();
            layout.numColumns = this.numColumns;
            initLayout(layout);
            composite.setLayout(layout);
            composite.setLayoutData(createLayoutData());
            createErrorComposite(composite);
            initComponent(composite);
            setupData();
            initialized = true;
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        return composite;
    }

    protected abstract String getTitle();

    protected void initLayout(GridLayout layout) {
    }

    protected Object createLayoutData() {
        return new GridData(GridData.FILL_BOTH);
    }

    abstract protected void initComponent(Composite composite);

    abstract protected void setupData();

    // ===================================================================================
    //                                                                            Contents
    //                                                                            ========
    @Override
    protected Control createContents(Composite parent) {
        final Control control = super.createContents(parent);
        addListener();
        validate();
        return control;
    }

    protected void addListener() {
    }

    // ===================================================================================
    //                                                                              Button
    //                                                                              ======
    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID || buttonId == IDialogConstants.CANCEL_ID || buttonId == IDialogConstants.BACK_ID) {
            setReturnCode(buttonId);
            close();
        } else if (buttonId == IDialogConstants.OK_ID) {
            try {
                if (!validate()) {
                    return;
                }
                performOK();
                setReturnCode(buttonId);
                close();
            } catch (final InputException e) {
                setMessage(DisplayMessages.getMessage(e.getMessage()));
                return;
            } catch (final Exception e) {
                Activator.showExceptionDialog(e);
            }
        }

        super.buttonPressed(buttonId);
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    public final boolean validate() {
        if (!initialized) {
            return true;
        }
        final Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(false);
        }
        final String errorMessage = doValidate();
        if (errorMessage != null) {
            setMessage(DisplayMessages.getMessage(errorMessage));
            return false;
        }
        if (okButton != null && enabledOkButton) {
            okButton.setEnabled(true);
        }
        setMessage(null);
        return true;
    }

    protected abstract String doValidate();

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    protected abstract void performOK() throws InputException;

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected Button createCheckbox(Composite composite, String title) {
        return CompositeFactory.createCheckbox(this, composite, title, getNumColumns());
    }

    protected int getNumColumns() {
        return numColumns;
    }

    protected int getErrorLine() {
        return 1;
    }

    protected void createErrorComposite(Composite parent) {
        errorMessageText = new CLabel(parent, SWT.NONE);
        errorMessageText.setText("");
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = 30 * getErrorLine();
        gridData.horizontalSpan = numColumns;
        errorMessageText.setLayoutData(gridData);
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
        if (errorMessageText != null) {
            if (errorMessage == null) {
                errorMessageText.setImage(null);
                errorMessageText.setText("");
            } else {
                final Image errorIcon = Activator.getImage(ImageKey.ERROR);
                errorMessageText.setImage(errorIcon);
                errorMessageText.setText(errorMessage);
            }
        }
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
        final Button button1 = getButton(IDialogConstants.OK_ID);
        if (button1 != null) {
            button1.setEnabled(enabled);
        }
        final Button button2 = getButton(IDialogConstants.CANCEL_ID);
        if (button2 != null) {
            button2.setEnabled(enabled);
        }
    }
}

package org.dbflute.erflute.editor.view.dialog.option.tab;

import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.InnerDirectoryText;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.dialog.option.OptionSettingDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class OptionTabWrapper extends ValidatableTabWrapper {

    private Button autoImeChangeCheck;

    private Button validatePhysicalNameCheck;

    private Button useBezierCurveCheck;

    private Button suspendValidatorCheck;

    private Settings settings;

    private OptionSettingDialog dialog;

    private InnerDirectoryText outputFileText;

    public OptionTabWrapper(OptionSettingDialog dialog, TabFolder parent, int style, Settings settings) {
        super(dialog, parent, style, "label.option");

        this.settings = settings;
        this.dialog = dialog;

        this.init();
    }

    @Override
    public void initComposite() {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        this.setLayout(layout);

        this.autoImeChangeCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.auto.ime.change");
        this.validatePhysicalNameCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.validate.physical.name");
        this.useBezierCurveCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.use.bezier.curve");
        this.suspendValidatorCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.suspend.validator");

        Composite innerComp = new Composite(this, SWT.NONE);
        GridLayout innerLayout = new GridLayout();
        innerLayout.numColumns = 3;
        innerComp.setLayout(innerLayout);
        CompositeFactory.createLabel(innerComp, "�}�X�^�f�[�^��f�B���N�g��");
        this.outputFileText = new InnerDirectoryText(innerComp, SWT.BORDER);
        GridData gridData = new GridData();
        gridData.widthHint = 200;
        this.outputFileText.setLayoutData(gridData);
        //		outputFileText.setText("");

    }

    @Override
    public void setData() {
        this.autoImeChangeCheck.setSelection(this.settings.isAutoImeChange());
        this.validatePhysicalNameCheck.setSelection(this.settings.isValidatePhysicalName());
        this.useBezierCurveCheck.setSelection(this.settings.isUseBezierCurve());
        this.suspendValidatorCheck.setSelection(this.settings.isSuspendValidator());

        if (settings.getMasterDataBasePath() != null) {
            outputFileText.setText(settings.getMasterDataBasePath());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        this.settings.setAutoImeChange(this.autoImeChangeCheck.getSelection());
        this.settings.setValidatePhysicalName(this.validatePhysicalNameCheck.getSelection());
        this.settings.setUseBezierCurve(this.useBezierCurveCheck.getSelection());
        this.settings.setSuspendValidator(this.suspendValidatorCheck.getSelection());
        settings.setMasterDataBasePath(outputFileText.getFilePath());
    }

    @Override
    public void setInitFocus() {
        this.autoImeChangeCheck.setFocus();
    }

    @Override
    public void perfomeOK() {
    }
}

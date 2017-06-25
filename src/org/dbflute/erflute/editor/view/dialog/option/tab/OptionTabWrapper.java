package org.dbflute.erflute.editor.view.dialog.option.tab;

import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.InnerDirectoryText;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.option.OptionSettingDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class OptionTabWrapper extends ValidatableTabWrapper {

    private Button autoImeChangeCheck;
    private Button validatePhysicalNameCheck;
    private Button useBezierCurveCheck;
    private Button suspendValidatorCheck;
    private Button useViewObjectCheck;

    private final DiagramSettings settings;
    private final OptionSettingDialog dialog;
    private InnerDirectoryText outputFileText;

    public OptionTabWrapper(OptionSettingDialog dialog, TabFolder parent, int style, DiagramSettings settings) {
        super(dialog, parent, style, "label.option");

        this.settings = settings;
        this.dialog = dialog;

        this.init();
    }

    @Override
    public void initComposite() {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        this.setLayout(layout);

        this.autoImeChangeCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.auto.ime.change");
        this.validatePhysicalNameCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.validate.physical.name");
        this.useBezierCurveCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.use.bezier.curve");
        this.suspendValidatorCheck = CompositeFactory.createCheckbox(this.dialog, this, "label.suspend.validator");
        this.useViewObjectCheck = CompositeFactory.createCheckbox(this.dialog, this, "Use view object");

        final Composite innerComp = new Composite(this, SWT.NONE);
        final GridLayout innerLayout = new GridLayout();
        innerLayout.numColumns = 3;
        innerComp.setLayout(innerLayout);
        /*
         * #willanalyze what is this? by jflute
         *
         * modified by ymd
         * 下記ラベルのテキストは、文字化けしていて解読できなかった。
         * ERMaster-bを起動して確認したところ「マスタデータ基準ディレクトリ」となっていた。
         * これをそのまま翻訳して修正した。これがどんな機能かは調べていない。
         */
        CompositeFactory.createLabel(innerComp, "Master data reference directory");
        this.outputFileText = new InnerDirectoryText(innerComp, SWT.BORDER);
        final GridData gridData = new GridData();
        gridData.widthHint = 200;
        this.outputFileText.setLayoutData(gridData);
        //		outputFileText.setText("");
    }

    @Override
    public void setupData() {
        this.autoImeChangeCheck.setSelection(this.settings.isAutoImeChange());
        this.validatePhysicalNameCheck.setSelection(this.settings.isValidatePhysicalName());
        this.useBezierCurveCheck.setSelection(this.settings.isUseBezierCurve());
        this.suspendValidatorCheck.setSelection(this.settings.isSuspendValidator());
        this.useViewObjectCheck.setSelection(this.settings.isUseViewObject());
        if (settings.getMasterDataBasePath() != null) {
            outputFileText.setText(settings.getMasterDataBasePath());
        }
    }

    @Override
    public void validatePage() throws InputException {
        settings.setAutoImeChange(autoImeChangeCheck.getSelection());
        settings.setValidatePhysicalName(validatePhysicalNameCheck.getSelection());
        settings.setUseBezierCurve(useBezierCurveCheck.getSelection());
        settings.setSuspendValidator(suspendValidatorCheck.getSelection());
        settings.setUseViewObject(useViewObjectCheck.getSelection());
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

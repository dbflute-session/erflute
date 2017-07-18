package org.dbflute.erflute.editor.view.dialog.image;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.SpinnerWithScale;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class InsertedImageDialog extends AbstractDialog {

    private SpinnerWithScale hueSpinner;
    private SpinnerWithScale saturationSpinner;
    private SpinnerWithScale brightnessSpinner;
    private SpinnerWithScale alphaSpinner;
    private Button fixAspectRatioCheckbox;
    private final InsertedImage insertedImage;
    private InsertedImage newInsertedImage;

    public InsertedImageDialog(Shell parentShell, InsertedImage insertedImage) {
        super(parentShell, 4);

        this.insertedImage = insertedImage;
    }

    @Override
    protected void initComponent(Composite composite) {
        this.hueSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.hue", "", 0, 360);
        // this.hueScale.setPageIncrement(10);
        this.saturationSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.saturation", -100, 100);
        this.brightnessSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.brightness", -100, 100);
        this.alphaSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.alpha", 0, 255);
        this.fixAspectRatioCheckbox = CompositeFactory.createCheckbox(this, composite, "label.image.fix.aspect.ratio", 3);
    }

    @Override
    protected String doValidate() {
        insertedImage.setHue(hueSpinner.getSelection());
        insertedImage.setSaturation(saturationSpinner.getSelection());
        insertedImage.setBrightness(brightnessSpinner.getSelection());
        insertedImage.setAlpha(alphaSpinner.getSelection());

        insertedImage.setFixAspectRatio(fixAspectRatioCheckbox.getSelection());

        insertedImage.setDirty();

        return null;
    }

    @Override
    protected void performOK() {
        this.newInsertedImage = new InsertedImage();
        newInsertedImage.setHue(hueSpinner.getSelection());
        newInsertedImage.setSaturation(saturationSpinner.getSelection());
        newInsertedImage.setBrightness(brightnessSpinner.getSelection());
        newInsertedImage.setAlpha(alphaSpinner.getSelection());
        newInsertedImage.setFixAspectRatio(fixAspectRatioCheckbox.getSelection());
    }

    @Override
    protected String getTitle() {
        return "dialog.title.image.information";
    }

    @Override
    protected void setupData() {
        hueSpinner.setSelection(insertedImage.getHue());
        saturationSpinner.setSelection(insertedImage.getSaturation());
        brightnessSpinner.setSelection(insertedImage.getBrightness());
        alphaSpinner.setSelection(insertedImage.getAlpha());
        fixAspectRatioCheckbox.setSelection(insertedImage.isFixAspectRatio());
    }

    public InsertedImage getNewInsertedImage() {
        return newInsertedImage;
    }
}

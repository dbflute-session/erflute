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

    private InsertedImage insertedImage;

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
        this.insertedImage.setHue(this.hueSpinner.getSelection());
        this.insertedImage.setSaturation(this.saturationSpinner.getSelection());
        this.insertedImage.setBrightness(this.brightnessSpinner.getSelection());
        this.insertedImage.setAlpha(this.alphaSpinner.getSelection());

        this.insertedImage.setFixAspectRatio(this.fixAspectRatioCheckbox.getSelection());

        this.insertedImage.setDirty();

        return null;
    }

    @Override
    protected void performOK() {
        this.newInsertedImage = new InsertedImage();
        this.newInsertedImage.setHue(this.hueSpinner.getSelection());
        this.newInsertedImage.setSaturation(this.saturationSpinner.getSelection());
        this.newInsertedImage.setBrightness(this.brightnessSpinner.getSelection());
        this.newInsertedImage.setAlpha(this.alphaSpinner.getSelection());
        this.newInsertedImage.setFixAspectRatio(this.fixAspectRatioCheckbox.getSelection());
    }

    @Override
    protected String getTitle() {
        return "dialog.title.image.information";
    }

    @Override
    protected void setupData() {
        this.hueSpinner.setSelection(this.insertedImage.getHue());
        this.saturationSpinner.setSelection(this.insertedImage.getSaturation());
        this.brightnessSpinner.setSelection(this.insertedImage.getBrightness());
        this.alphaSpinner.setSelection(this.insertedImage.getAlpha());
        this.fixAspectRatioCheckbox.setSelection(this.insertedImage.isFixAspectRatio());
    }

    public InsertedImage getNewInsertedImage() {
        return newInsertedImage;
    }
}

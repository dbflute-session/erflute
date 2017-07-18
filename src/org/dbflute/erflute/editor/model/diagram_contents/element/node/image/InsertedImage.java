package org.dbflute.erflute.editor.model.diagram_contents.element.node.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.util.io.IOUtils;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;

public class InsertedImage extends DiagramWalker {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_IMAGE = "image";

    private String base64EncodedData;
    private int hue;
    private int saturation;
    private int brightness;
    private int alpha;
    private boolean fixAspectRatio;

    public InsertedImage() {
        this.alpha = 255;
    }

    @Override
    public String getObjectType() {
        return "image";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return true;
    }

    @Override
    public int getPersistentOrder() {
        return 16;
    }

    @Override
    public boolean isUsePersistentId() {
        return true;
    }

    @Override
    public boolean isIndenpendentOnModel() {
        return true;
    }

    public void setImageFilePath(String imageFilePath) {
        InputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(imageFilePath));
            final byte[] data = IOUtils.toByteArray(in);
            final String encodedData = Base64.getEncoder().encodeToString(data);
            setBase64EncodedData(encodedData);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final Exception e) {
                    Activator.showExceptionDialog(e);
                }
            }
        }
    }

    public void setDirty() {
        firePropertyChange(PROPERTY_CHANGE_IMAGE, null, null);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getBase64EncodedData() {
        return base64EncodedData;
    }

    public void setBase64EncodedData(String base64EncodedData) {
        this.base64EncodedData = base64EncodedData;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public boolean isFixAspectRatio() {
        return fixAspectRatio;
    }

    public void setFixAspectRatio(boolean fixAspectRatio) {
        this.fixAspectRatio = fixAspectRatio;
    }
}

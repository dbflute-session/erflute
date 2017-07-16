package org.dbflute.erflute.editor.model;

import org.eclipse.swt.graphics.Color;

public abstract class ViewableModel extends AbstractModel {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_COLOR = "color";
    public static final String PROPERTY_CHANGE_FONT = "font";
    public static final int DEFAULT_FONT_SIZE = 9;

    private String fontName;
    private int fontSize;
    private int[] color;

    public ViewableModel() {
        this.fontName = null;
        this.fontSize = DEFAULT_FONT_SIZE;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        firePropertyChange(PROPERTY_CHANGE_FONT, null, null);
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
        firePropertyChange(PROPERTY_CHANGE_FONT, null, null);
    }

    public void setColor(int red, int green, int blue) {
        this.color = new int[3];
        this.color[0] = red;
        this.color[1] = green;
        this.color[2] = blue;

        firePropertyChange(PROPERTY_CHANGE_COLOR, null, null);
    }

    public void setColor(Color color) {
        this.color = new int[3];
        this.color[0] = color.getRed();
        this.color[1] = color.getGreen();
        this.color[2] = color.getBlue();

        firePropertyChange(PROPERTY_CHANGE_COLOR, null, null);
    }

    public int[] getColor() {
        return color;
    }

    @Override
    public ViewableModel clone() {
        final ViewableModel clone = (ViewableModel) super.clone();
        if (color != null) {
            clone.color = new int[] { color[0], color[1], color[2] };
        }

        return clone;
    }
}

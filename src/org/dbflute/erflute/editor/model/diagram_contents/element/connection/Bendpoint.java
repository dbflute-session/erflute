package org.dbflute.erflute.editor.model.diagram_contents.element.connection;

import java.io.Serializable;

public class Bendpoint implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private boolean relative;

    public Bendpoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;

        validate();
    }

    public void validate() {
        if (relative) {
            if (x < 20) {
                x = 20;
            } else if (x > 180) {
                x = 180;
            }

            if (y < 20) {
                y = 20;
            } else if (y > 180) {
                y = 180;
            }
        }
    }

    @Override
    public Object clone() {
        Bendpoint clone = null;
        try {
            clone = (Bendpoint) super.clone();
        } catch (final CloneNotSupportedException e) {}

        return clone;
    }
}

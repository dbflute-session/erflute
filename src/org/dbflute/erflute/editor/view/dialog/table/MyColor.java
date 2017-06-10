package org.dbflute.erflute.editor.view.dialog.table;

import java.util.Arrays;

public class MyColor {

    private final int[] colors;

    public MyColor(int[] color) {
        this.colors = color;
    }

    @Override
    public int hashCode() {
        return colors[0] + colors[1] + colors[2];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyColor) {
            final MyColor other = (MyColor) obj;
            return Arrays.equals(other.colors, this.colors);
        } else {
            return false;
        }
    }

    public int[] getColors() {
        return colors;
    }
}

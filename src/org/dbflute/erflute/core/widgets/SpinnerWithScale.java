package org.dbflute.erflute.core.widgets;

import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

public class SpinnerWithScale {

    private final Spinner spinner;
    private final Scale scale;
    private final int diff;

    public SpinnerWithScale(Spinner spinner, Scale scale, int diff) {
        this.spinner = spinner;
        this.scale = scale;
        this.diff = diff;
    }

    public void setSelection(int value) {
        this.spinner.setSelection(value);
        scale.setSelection(spinner.getSelection() - diff);
    }

    public int getSelection() {
        return spinner.getSelection();
    }
}

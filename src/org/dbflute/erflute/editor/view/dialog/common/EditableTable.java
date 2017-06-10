package org.dbflute.erflute.editor.view.dialog.common;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public interface EditableTable {

    void setData(Point xy, Control control);

    Control getControl(Point xy);

    void onDoubleClicked(Point xy);

    boolean validate();
}

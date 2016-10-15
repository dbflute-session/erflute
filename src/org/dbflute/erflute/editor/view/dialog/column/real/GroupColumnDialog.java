package org.dbflute.erflute.editor.view.dialog.column.real;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.eclipse.swt.widgets.Shell;

public class GroupColumnDialog extends AbstractRealColumnDialog {

    public GroupColumnDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, diagram);
    }

    protected int getStyle(int style) {
        return style;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.group.column";
    }

}

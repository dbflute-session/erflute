package org.dbflute.erflute.editor.view.editmanager;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class WalkerNoteCellEditor extends TextCellEditor {

    public WalkerNoteCellEditor(Composite parent) {
        super(parent, SWT.MULTI | SWT.WRAP);
    }
}

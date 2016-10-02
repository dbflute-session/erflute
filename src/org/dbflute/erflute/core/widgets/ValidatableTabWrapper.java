package org.dbflute.erflute.core.widgets;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class ValidatableTabWrapper extends Composite {

    protected TabItem tabItem;

    protected AbstractDialog dialog;

    public ValidatableTabWrapper(AbstractDialog dialog, TabFolder parent, int style, String title) {
        super(parent, style);

        this.dialog = dialog;

        this.tabItem = new TabItem(parent, style);
        this.tabItem.setText(DisplayMessages.getMessage(title));

        this.tabItem.setControl(this);
    }

    abstract public void validatePage() throws InputException;

    protected final void init() {
        this.initComposite();
        this.addListener();
        this.setData();
    }

    public void reset() {
    }

    abstract protected void initComposite();

    protected void addListener() {
    }

    protected void setData() {
    }

    abstract public void perfomeOK();

    abstract public void setInitFocus();
}

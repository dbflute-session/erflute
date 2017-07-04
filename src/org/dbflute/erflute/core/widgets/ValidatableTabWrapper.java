package org.dbflute.erflute.core.widgets;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class ValidatableTabWrapper extends Composite {

    protected final AbstractDialog dialog;
    protected final TabItem tabItem;

    public ValidatableTabWrapper(AbstractDialog dialog, TabFolder parent, int style, String title) {
        super(parent, style);
        this.dialog = dialog;
        this.tabItem = new TabItem(parent, style);
        tabItem.setText(DisplayMessages.getMessage(title));
        tabItem.setControl(this);
    }

    protected final void init() {
        initComposite();
        addListener();
        setupData();
    }

    protected abstract void initComposite();

    protected void addListener() {
    }

    protected void setupData() {
    }

    public abstract void validatePage() throws InputException;

    abstract public void perfomeOK();

    public abstract void setInitFocus();

    public void reset() {
    }
}

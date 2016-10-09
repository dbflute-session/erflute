package org.dbflute.erflute.editor.view.dialog.view.tab;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    private ERView view;

    private AdvancedComposite composite;

    public AdvancedTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERView view) {
        super(dialog, parent, style, "label.advanced.settings");

        this.view = view;

        this.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        this.composite.validate();
    }

    @Override
    public void initComposite() {
        this.setLayout(new GridLayout());
        this.composite = new AdvancedComposite(this);
        this.composite.initialize((ViewProperties) this.view.getTableViewProperties(), this.view.getDiagram());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitFocus() {
        this.composite.setInitFocus();
    }

    @Override
    public void perfomeOK() {
    }
}

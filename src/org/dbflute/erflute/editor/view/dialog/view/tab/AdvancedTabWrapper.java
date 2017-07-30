package org.dbflute.erflute.editor.view.dialog.view.tab;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    private final ERView view;
    private AdvancedComposite composite;

    public AdvancedTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERView view) {
        super(dialog, parent, style, "label.advanced.settings");

        this.view = view;

        init();
    }

    @Override
    public void validatePage() throws InputException {
        composite.validate();
    }

    @Override
    public void initComposite() {
        setLayout(new GridLayout());
        this.composite = new AdvancedComposite(this);
        composite.initialize((ViewProperties) view.getTableViewProperties(), view.getDiagram());
    }

    @Override
    public void setInitFocus() {
        composite.setInitFocus();
    }

    @Override
    public void perfomeOK() {
    }
}

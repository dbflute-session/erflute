package org.dbflute.erflute.editor.view.dialog.table.tab;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.db.EclipseDBManagerFactory;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    private final ERTable table;
    private AdvancedComposite composite;

    public AdvancedTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERTable table) {
        super(dialog, parent, style, "label.advanced.settings");

        this.table = table;

        init();
    }

    @Override
    public void validatePage() throws InputException {
        composite.validate();
    }

    @Override
    public void initComposite() {
        setLayout(new GridLayout());
        this.composite = EclipseDBManagerFactory.getEclipseDBManager(table.getDiagram()).createAdvancedComposite(this);
        composite.initialize(dialog, (TableProperties) table.getTableViewProperties(), table.getDiagram(), table);
    }

    @Override
    public void setInitFocus() {
        composite.setInitFocus();
    }

    @Override
    public void perfomeOK() {
    }
}

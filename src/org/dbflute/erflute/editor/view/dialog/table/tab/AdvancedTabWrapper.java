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

    private ERTable table;

    private AdvancedComposite composite;

    public AdvancedTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERTable table) {
        super(dialog, parent, style, "label.advanced.settings");

        this.table = table;

        this.init();
    }

    @Override
    public void validatePage() throws InputException {
        this.composite.validate();
    }

    @Override
    public void initComposite() {
        this.setLayout(new GridLayout());
        this.composite = EclipseDBManagerFactory.getEclipseDBManager(this.table.getDiagram()).createAdvancedComposite(this);
        this.composite.initialize(this.dialog, (TableProperties) this.table.getTableViewProperties(), this.table.getDiagram(), this.table);
    }

    @Override
    public void setInitFocus() {
        this.composite.setInitFocus();
    }

    @Override
    public void perfomeOK() {
    }
}

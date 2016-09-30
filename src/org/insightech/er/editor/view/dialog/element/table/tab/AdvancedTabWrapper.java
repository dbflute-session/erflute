package org.insightech.er.editor.view.dialog.element.table.tab;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.db.EclipseDBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    private ERTable table;

    private AdvancedComposite composite;

    public AdvancedTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERTable table) {
        super(dialog, parent, style, "label.advanced.settings");

        this.table = table;

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
        this.composite = EclipseDBManagerFactory.getEclipseDBManager(this.table.getDiagram()).createAdvancedComposite(this);
        this.composite.initialize(this.dialog, (TableProperties) this.table.getTableViewProperties(), this.table.getDiagram(), this.table);
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

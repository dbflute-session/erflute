package org.dbflute.erflute.editor.view.dialog.option.tab;

import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.db.EclipseDBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.dbflute.erflute.editor.view.dialog.option.OptionSettingDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    private Settings settings;

    private ERDiagram diagram;

    private AdvancedComposite composite;

    public AdvancedTabWrapper(OptionSettingDialog dialog, TabFolder parent, int style, Settings settings, ERDiagram diagram) {
        super(dialog, parent, style, "label.advanced.settings");

        this.diagram = diagram;
        this.settings = settings;

        this.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        this.composite.validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initComposite() {
        this.setLayout(new GridLayout());

        if (this.composite != null) {
            this.composite.dispose();
        }

        this.composite = EclipseDBManagerFactory.getEclipseDBManager(this.settings.getDatabase()).createAdvancedComposite(this);
        this.composite.initialize(this.dialog, (TableProperties) this.settings.getTableViewProperties(), this.diagram, null);

        this.pack();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitFocus() {
        this.composite.setInitFocus();
    }

    @Override
    public void reset() {
        this.init();
    }

    @Override
    public void perfomeOK() {
    }
}

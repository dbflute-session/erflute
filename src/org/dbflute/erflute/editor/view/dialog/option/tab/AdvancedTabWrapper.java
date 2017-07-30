package org.dbflute.erflute.editor.view.dialog.option.tab;

import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.db.EclipseDBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.option.OptionSettingDialog;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    private final DiagramSettings settings;
    private final ERDiagram diagram;
    private AdvancedComposite composite;

    public AdvancedTabWrapper(OptionSettingDialog dialog, TabFolder parent, int style, DiagramSettings settings, ERDiagram diagram) {
        super(dialog, parent, style, "label.advanced.settings");

        this.diagram = diagram;
        this.settings = settings;

        init();
    }

    @Override
    public void validatePage() throws InputException {
        composite.validate();
    }

    @Override
    public void initComposite() {
        setLayout(new GridLayout());

        if (composite != null) {
            composite.dispose();
        }

        this.composite = EclipseDBManagerFactory.getEclipseDBManager(settings.getDatabase()).createAdvancedComposite(this);
        composite.initialize(dialog, (TableProperties) settings.getTableViewProperties(), diagram, null);

        pack();
    }

    @Override
    public void setInitFocus() {
        composite.setInitFocus();
    }

    @Override
    public void reset() {
        init();
    }

    @Override
    public void perfomeOK() {
    }
}

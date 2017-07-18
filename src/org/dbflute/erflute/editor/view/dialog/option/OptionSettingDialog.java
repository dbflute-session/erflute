package org.dbflute.erflute.editor.view.dialog.option;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ListenerAppender;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.dialog.option.tab.AdvancedTabWrapper;
import org.dbflute.erflute.editor.view.dialog.option.tab.DBSelectTabWrapper;
import org.dbflute.erflute.editor.view.dialog.option.tab.EnvironmentTabWrapper;
import org.dbflute.erflute.editor.view.dialog.option.tab.OptionTabWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

public class OptionSettingDialog extends AbstractDialog {

    private TabFolder tabFolder;
    private final List<ValidatableTabWrapper> tabWrapperList;
    private final DiagramSettings settings;
    private final ERDiagram diagram;

    public OptionSettingDialog(Shell parentShell, DiagramSettings settings, ERDiagram diagram) {
        super(parentShell);

        this.diagram = diagram;
        this.settings = settings;
        this.tabWrapperList = new ArrayList<>();
    }

    @Override
    protected void initComponent(Composite composite) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;

        this.tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setLayoutData(gridData);

        tabWrapperList.add(new DBSelectTabWrapper(this, tabFolder, SWT.NONE, settings));
        tabWrapperList.add(new EnvironmentTabWrapper(this, tabFolder, SWT.NONE, settings));
        tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder, SWT.NONE, settings, diagram));
        tabWrapperList.add(new OptionTabWrapper(this, tabFolder, SWT.NONE, settings));

        ListenerAppender.addTabListener(tabFolder, tabWrapperList);

        tabWrapperList.get(0).setInitFocus();
    }

    @Override
    protected String doValidate() {
        try {
            for (final ValidatableTabWrapper tabWrapper : tabWrapperList) {
                tabWrapper.validatePage();
            }
        } catch (final InputException e) {
            return e.getMessage();
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.option";
    }

    @Override
    protected void performOK() throws InputException {
    }

    @Override
    protected void setupData() {
    }

    public void initTab() {
        for (final ValidatableTabWrapper tabWrapper : tabWrapperList) {
            tabWrapper.reset();
        }
    }
}

package org.dbflute.erflute.editor.view.dialog.option;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ListenerAppender;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;
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

    private List<ValidatableTabWrapper> tabWrapperList;

    private Settings settings;

    private ERDiagram diagram;

    public OptionSettingDialog(Shell parentShell, Settings settings, ERDiagram diagram) {
        super(parentShell);

        this.diagram = diagram;
        this.settings = settings;
        this.tabWrapperList = new ArrayList<ValidatableTabWrapper>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initComponent(Composite composite) {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;

        this.tabFolder = new TabFolder(composite, SWT.NONE);
        this.tabFolder.setLayoutData(gridData);

        this.tabWrapperList.add(new DBSelectTabWrapper(this, tabFolder, SWT.NONE, this.settings));
        this.tabWrapperList.add(new EnvironmentTabWrapper(this, tabFolder, SWT.NONE, this.settings));
        this.tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder, SWT.NONE, this.settings, this.diagram));
        this.tabWrapperList.add(new OptionTabWrapper(this, tabFolder, SWT.NONE, this.settings));

        ListenerAppender.addTabListener(tabFolder, tabWrapperList);

        this.tabWrapperList.get(0).setInitFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doValidate() {
        try {
            for (ValidatableTabWrapper tabWrapper : this.tabWrapperList) {
                tabWrapper.validatePage();
            }

        } catch (InputException e) {
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
        for (ValidatableTabWrapper tabWrapper : this.tabWrapperList) {
            tabWrapper.reset();
        }
    }
}

package org.dbflute.erflute.editor.view.dialog.common;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.settings.DBSetting;
import org.dbflute.erflute.preference.PreferenceInitializer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class DBSettingListDialog extends AbstractDialog {

    private Table settingTable;

    private List<DBSetting> dbSettingList;

    private DBSetting result;

    private String database;

    public DBSettingListDialog(Shell parentShell, String database) {
        super(parentShell);

        this.database = database;
        this.dbSettingList = new ArrayList<DBSetting>();
    }

    @Override
    protected void initialize(Composite composite) {
        GridData gridData = new GridData();
        gridData.heightHint = 150;

        this.settingTable = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
        this.settingTable.setHeaderVisible(true);
        this.settingTable.setLayoutData(gridData);
        this.settingTable.setLinesVisible(false);

        TableColumn dbsystemColumn = new TableColumn(this.settingTable, SWT.LEFT);
        dbsystemColumn.setWidth(100);
        dbsystemColumn.setText(DisplayMessages.getMessage("label.database"));

        TableColumn serverColumn = new TableColumn(this.settingTable, SWT.LEFT);
        serverColumn.setWidth(100);
        serverColumn.setText(DisplayMessages.getMessage("label.server.name"));

        TableColumn portColumn = new TableColumn(this.settingTable, SWT.RIGHT);
        portColumn.setWidth(80);
        portColumn.setText(DisplayMessages.getMessage("label.port"));

        TableColumn databaseColumn = new TableColumn(this.settingTable, SWT.LEFT);
        databaseColumn.setWidth(100);
        databaseColumn.setText(DisplayMessages.getMessage("label.database.name"));

        TableColumn userNameColumn = new TableColumn(this.settingTable, SWT.LEFT);
        userNameColumn.setWidth(100);
        userNameColumn.setText(DisplayMessages.getMessage("label.user.name"));

        TableColumn urlTableColumn = new TableColumn(this.settingTable, SWT.LEFT);
        urlTableColumn.setWidth(130);
        urlTableColumn.setText(DisplayMessages.getMessage("label.url"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        super.addListener();

        this.settingTable.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });

        this.settingTable.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                validate();

                int index = settingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                selectTable(index);
            }
        });

    }

    @Override
    protected void perfomeOK() throws InputException {
        int index = settingTable.getSelectionIndex();
        this.result = this.dbSettingList.get(index);
    }

    public DBSetting getResult() {
        return this.result;
    }

    public int getResultIndex() {
        return this.dbSettingList.indexOf(this.result);
    }

    @Override
    protected void setData() {
        this.dbSettingList = PreferenceInitializer.getDBSettingList(this.database);

        for (DBSetting dbSetting : this.dbSettingList) {
            TableItem item = new TableItem(this.settingTable, SWT.NONE);
            item.setText(0, dbSetting.getDbsystem());
            item.setText(1, dbSetting.getServer());
            if (dbSetting.getPort() != 0) {
                item.setText(2, String.valueOf(dbSetting.getPort()));
            }
            item.setText(3, dbSetting.getDatabase());
            item.setText(4, dbSetting.getUser());
            item.setText(5, Format.null2blank(dbSetting.getUrl()));
        }

        this.setButtonEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, DisplayMessages.getMessage("label.load.setting"), true);
        createButton(parent, IDialogConstants.STOP_ID, DisplayMessages.getMessage("label.delete"), false);
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);

        setButtonEnabled(false);
    }

    private void setButtonEnabled(boolean enabled) {
        Button okButton = this.getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(enabled);
        }

        Button deleteButton = this.getButton(IDialogConstants.STOP_ID);
        if (deleteButton != null) {
            deleteButton.setEnabled(enabled);
        }
    }

    private void selectTable(int index) {
        this.settingTable.select(index);

        if (index >= 0) {
            this.setButtonEnabled(true);
        } else {
            this.setButtonEnabled(false);
        }
    }

    @Override
    protected String getErrorMessage() {
        int index = settingTable.getSelectionIndex();
        if (index == -1) {
            return "dialog.message.load.db.setting";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "label.load.database.setting";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.STOP_ID) {
            int index = this.settingTable.getSelectionIndex();

            if (index != -1) {
                this.settingTable.remove(index);
                this.dbSettingList.remove(index);

                PreferenceInitializer.saveSetting(this.dbSettingList);

                if (index >= this.settingTable.getItemCount()) {
                    index = this.settingTable.getItemCount() - 1;
                }

                this.selectTable(index);
            }
        }

        super.buttonPressed(buttonId);
    }

}

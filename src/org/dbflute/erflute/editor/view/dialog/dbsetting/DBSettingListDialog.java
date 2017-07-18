package org.dbflute.erflute.editor.view.dialog.dbsetting;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.settings.DBSettings;
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

    private List<DBSettings> dbSettingList;

    private DBSettings result;

    private final String database;

    public DBSettingListDialog(Shell parentShell, String database) {
        super(parentShell);

        this.database = database;
        this.dbSettingList = new ArrayList<>();
    }

    @Override
    protected void initComponent(Composite composite) {
        final GridData gridData = new GridData();
        gridData.heightHint = 150;

        this.settingTable = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
        settingTable.setHeaderVisible(true);
        settingTable.setLayoutData(gridData);
        settingTable.setLinesVisible(false);

        final TableColumn dbsystemColumn = new TableColumn(settingTable, SWT.LEFT);
        dbsystemColumn.setWidth(100);
        dbsystemColumn.setText(DisplayMessages.getMessage("label.database"));

        final TableColumn serverColumn = new TableColumn(settingTable, SWT.LEFT);
        serverColumn.setWidth(100);
        serverColumn.setText(DisplayMessages.getMessage("label.server.name"));

        final TableColumn portColumn = new TableColumn(settingTable, SWT.RIGHT);
        portColumn.setWidth(80);
        portColumn.setText(DisplayMessages.getMessage("label.port"));

        final TableColumn databaseColumn = new TableColumn(settingTable, SWT.LEFT);
        databaseColumn.setWidth(100);
        databaseColumn.setText(DisplayMessages.getMessage("label.database.name"));

        final TableColumn userNameColumn = new TableColumn(settingTable, SWT.LEFT);
        userNameColumn.setWidth(100);
        userNameColumn.setText(DisplayMessages.getMessage("label.user.name"));

        final TableColumn urlTableColumn = new TableColumn(settingTable, SWT.LEFT);
        urlTableColumn.setWidth(130);
        urlTableColumn.setText(DisplayMessages.getMessage("label.url"));
    }

    @Override
    protected void addListener() {
        super.addListener();

        settingTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });

        settingTable.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                validate();

                final int index = settingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                selectTable(index);
            }
        });

    }

    @Override
    protected void performOK() throws InputException {
        final int index = settingTable.getSelectionIndex();
        this.result = dbSettingList.get(index);
    }

    public DBSettings getResult() {
        return result;
    }

    public int getResultIndex() {
        return dbSettingList.indexOf(result);
    }

    @Override
    protected void setupData() {
        this.dbSettingList = PreferenceInitializer.getDBSettingList(database);

        for (final DBSettings dbSetting : dbSettingList) {
            final TableItem item = new TableItem(settingTable, SWT.NONE);
            item.setText(0, dbSetting.getDbsystem());
            item.setText(1, dbSetting.getServer());
            if (dbSetting.getPort() != 0) {
                item.setText(2, String.valueOf(dbSetting.getPort()));
            }
            item.setText(3, dbSetting.getDatabase());
            item.setText(4, dbSetting.getUser());
            item.setText(5, Format.null2blank(dbSetting.getUrl()));
        }

        setButtonEnabled(false);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, DisplayMessages.getMessage("label.load.setting"), true);
        createButton(parent, IDialogConstants.STOP_ID, DisplayMessages.getMessage("label.delete"), false);
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);

        setButtonEnabled(false);
    }

    private void setButtonEnabled(boolean enabled) {
        final Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(enabled);
        }

        final Button deleteButton = getButton(IDialogConstants.STOP_ID);
        if (deleteButton != null) {
            deleteButton.setEnabled(enabled);
        }
    }

    private void selectTable(int index) {
        settingTable.select(index);
        if (index >= 0) {
            setButtonEnabled(true);
        } else {
            setButtonEnabled(false);
        }
    }

    @Override
    protected String doValidate() {
        final int index = settingTable.getSelectionIndex();
        if (index == -1) {
            return "dialog.message.load.db.setting";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "label.load.database.setting";
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.STOP_ID) {
            int index = settingTable.getSelectionIndex();

            if (index != -1) {
                settingTable.remove(index);
                dbSettingList.remove(index);

                PreferenceInitializer.saveSetting(dbSettingList);

                if (index >= settingTable.getItemCount()) {
                    index = settingTable.getItemCount() - 1;
                }

                selectTable(index);
            }
        }

        super.buttonPressed(buttonId);
    }
}

package org.dbflute.erflute.preference.jdbc;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.settings.JDBCDriverSetting;
import org.dbflute.erflute.preference.MultiFileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class JDBCPathDialog extends AbstractDialog {

    private MultiFileFieldEditor fileFieldEditor;
    private Combo databaseCombo;
    private Text driverClassNameText;
    private String database;
    private String driverClassName;
    private String path;
    private final List<JDBCDriverSetting> otherDriverSettingList;
    private boolean editable;

    public JDBCPathDialog(Shell parentShell, String database, String driverClassName, String path,
            List<JDBCDriverSetting> otherDriverSettingList, boolean editable) {
        super(parentShell, 3);

        this.database = database;
        this.driverClassName = driverClassName;
        this.path = path;

        this.otherDriverSettingList = otherDriverSettingList;
        this.editable = editable;
    }

    @Override
    protected Object createLayoutData() {
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 600;
        gridData.heightHint = 180;
        gridData.horizontalIndent = 10;
        gridData.horizontalSpan = 10;
        return gridData;
    }

    @Override
    protected void initComponent(Composite composite) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.heightHint = 50;

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
        label.setText(DisplayMessages.getMessage("label.jdbc.driver.message"));

        if (database != null) {
            final DBManager dbManager = DBManagerFactory.getDBManager(database);
            if (dbManager.getDriverClassName().equals(driverClassName) && !dbManager.getDriverClassName().equals("")) {
                editable = false;
            }
        }

        if (editable) {
            this.databaseCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.database", 2, -1);
            databaseCombo.setVisibleItemCount(10);
        } else {
            CompositeFactory.createLabel(composite, "label.database");
            CompositeFactory.createLabel(composite, database, 2);
        }

        this.driverClassNameText = CompositeFactory.createText(this, composite, "label.driver.class.name", 2, -1, SWT.BORDER, false);
        driverClassNameText.setEditable(editable);
        this.fileFieldEditor = new MultiFileFieldEditor("", DisplayMessages.getMessage("label.path"), composite);
        fileFieldEditor.setMultiple(true);
        fileFieldEditor.setFocus();
    }

    @Override
    protected String getTitle() {
        return "label.path";
    }

    @Override
    protected String doValidate() {
        String selectedDatabase = database;
        if (databaseCombo != null) {
            selectedDatabase = databaseCombo.getText();
            if (Check.isEmpty(selectedDatabase)) {
                return "error.database.name.is.empty";
            }
        }

        final String text = driverClassNameText.getText();
        if (Check.isEmpty(text)) {
            return "error.driver.class.name.is.empty";
        } else {
            final JDBCDriverSetting driverSetting = new JDBCDriverSetting(selectedDatabase, text, null);
            if (otherDriverSettingList.contains(driverSetting)) {
                return "error.driver.class.is.already.exist";
            }
        }
        return null;
    }

    @Override
    protected void performOK() throws InputException {
        this.path = fileFieldEditor.getStringValue();
        this.driverClassName = driverClassNameText.getText();
        if (databaseCombo != null) {
            this.database = databaseCombo.getText();
        }
    }

    @Override
    protected void setupData() {
        fileFieldEditor.setStringValue(path);
        driverClassNameText.setText(Format.null2blank(driverClassName));
        if (databaseCombo != null) {
            for (final String db : DBManagerFactory.getAllDBList()) {
                databaseCombo.add(db);
            }
            databaseCombo.setText(Format.null2blank(database));
        }
    }

    public String getPath() {
        return path;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getDatabase() {
        return database;
    }
}

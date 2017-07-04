package org.dbflute.erflute.editor.view.dialog.column.real;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.db.impl.mysql.MySQLDBManager;
import org.dbflute.erflute.db.impl.postgres.PostgresDBManager;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.view.dialog.table.sub.AutoIncrementSettingDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ColumnDialog extends AbstractRealColumnDialog {

    private final ERTable table;
    private Sequence autoIncrementSetting;
    protected Button primaryKeyCheck;
    protected Text uniqueKeyNameText;
    protected Combo characterSetCombo;
    protected Combo collationCombo;
    protected Button autoIncrementCheck;
    protected Button autoIncrementSettingButton;

    public ColumnDialog(Shell parentShell, ERTable table) {
        super(parentShell, table.getDiagram());
        this.table = table;
    }

    @Override
    protected void initializeDetailTab(Composite composite) {
        // first uniqueKeyNameText, second constraintText (in super's)
        this.uniqueKeyNameText = CompositeFactory.createText(this, composite, "label.unique.key.name", false);
        super.initializeDetailTab(composite);
        final DBManager manager = DBManagerFactory.getDBManager(diagram);
        if (MySQLDBManager.ID.equals(diagram.getDatabase())) {
            this.characterSetCombo = CompositeFactory.createCombo(this, composite, "label.character.set", 1);
            this.collationCombo = CompositeFactory.createCombo(this, composite, "label.collation", 1);
        }
        if (manager.isSupported(DBManager.SUPPORT_AUTO_INCREMENT_SETTING)) {
            CompositeFactory.filler(composite, 2);
            this.autoIncrementSettingButton = new Button(composite, SWT.NONE);
            autoIncrementSettingButton.setText(DisplayMessages.getMessage("label.auto.increment.setting"));
            autoIncrementSettingButton.setEnabled(false);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            autoIncrementSettingButton.setLayoutData(gridData);
        }
    }

    @Override
    protected int getCheckBoxCompositeNumColumns() {
        final DBManager manager = DBManagerFactory.getDBManager(diagram);
        if (manager.isSupported(DBManager.SUPPORT_AUTO_INCREMENT)) {
            return 4;
        }
        return 3;
    }

    @Override
    protected void initializeCheckBoxComposite(Composite composite) {
        primaryKeyCheck = CompositeFactory.createCheckbox(this, composite, "label.primary.key");
        super.initializeCheckBoxComposite(composite);
        final DBManager manager = DBManagerFactory.getDBManager(diagram);
        if (manager.isSupported(DBManager.SUPPORT_AUTO_INCREMENT)) {
            autoIncrementCheck = CompositeFactory.createCheckbox(this, composite, "label.auto.increment");
        }
        if (isRefered) {
            uniqueKeyCheck.setEnabled(false);
        }
        enableAutoIncrement(false);
        adjustCheckBoxDefault();
    }

    private void adjustCheckBoxDefault() {
        notNullCheck.setSelection(true); // as default (not-null column is better)
    }

    protected int getStyle(int style) {
        if (foreignKey) {
            style |= SWT.READ_ONLY;
        }
        return style;
    }

    @Override
    protected void initializeComposite(Composite composite) {
        super.initializeComposite(composite);
        if (foreignKey) {
            // #for_erflute not use word linkage
            //this.wordCombo.setEnabled(false);
            typeCombo.setEnabled(false);
            defaultText.setEnabled(false);
            lengthText.setEnabled(false);
            decimalText.setEnabled(false);
        }
    }

    @Override
    protected void setWordData() {
        super.setWordData();
        primaryKeyCheck.setSelection(targetColumn.isPrimaryKey());
        if (autoIncrementCheck != null) {
            autoIncrementCheck.setSelection(targetColumn.isAutoIncrement());
        }
        if (primaryKeyCheck.getSelection()) {
            notNullCheck.setSelection(true);
            notNullCheck.setEnabled(false);
        } else {
            notNullCheck.setEnabled(true);
        }
        final NormalColumn autoIncrementColumn = table.getAutoIncrementColumn();
        if (primaryKeyCheck.getSelection()) {
            if (autoIncrementColumn == null || autoIncrementColumn == targetColumn) {
                enableAutoIncrement(true);
            } else {
                enableAutoIncrement(false);
            }
        } else {
            enableAutoIncrement(false);
        }
        defaultText.setText(Format.null2blank(targetColumn.getDefaultValue()));
        setEnabledBySqlType();
        uniqueKeyNameText.setText(Format.null2blank(targetColumn.getUniqueKeyName()));
        if (characterSetCombo != null) {
            characterSetCombo.add("");
            for (final String characterSet : MySQLDBManager.getCharacterSetList()) {
                characterSetCombo.add(characterSet);
            }
            characterSetCombo.setText(Format.null2blank(targetColumn.getCharacterSet()));
            collationCombo.add("");
            for (final String collation : MySQLDBManager.getCollationList(targetColumn.getCharacterSet())) {
                collationCombo.add(collation);
            }
            collationCombo.setText(Format.null2blank(targetColumn.getCollation()));
        }
    }

    @Override
    protected String getTitle() {
        return "dialog.title.column";
    }

    private void enableAutoIncrement(boolean enabled) {
        if (autoIncrementCheck != null) {
            if (!enabled) {
                autoIncrementCheck.setSelection(false);
            }
            autoIncrementCheck.setEnabled(enabled);
            if (autoIncrementSettingButton != null) {
                autoIncrementSettingButton.setEnabled(enabled && autoIncrementCheck.getSelection());
            }
        }
    }

    @Override
    protected void setEnabledBySqlType() {
        super.setEnabledBySqlType();
        final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());
        if (selectedType != null) {
            if (PostgresDBManager.ID.equals(diagram.getDatabase())) {
                if (SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(selectedType.getId())
                        || SqlType.SQL_TYPE_ID_SERIAL.equals(selectedType.getId())) {
                    autoIncrementSettingButton.setEnabled(true);
                } else {
                    autoIncrementSettingButton.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected void addListener() {
        super.addListener();
        if (autoIncrementSettingButton != null) {
            autoIncrementSettingButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    final AutoIncrementSettingDialog dialog =
                            new AutoIncrementSettingDialog(shell, autoIncrementSetting, diagram.getDatabase());
                    if (dialog.open() == IDialogConstants.OK_ID) {
                        autoIncrementSetting = dialog.getResult();
                    }
                }
            });
        }
        final NormalColumn autoIncrementColumn = table.getAutoIncrementColumn();
        primaryKeyCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (primaryKeyCheck.getSelection()) {
                    adjustCheckBoxDefault();
                    notNullCheck.setEnabled(false);
                    if (autoIncrementColumn == null || autoIncrementColumn == targetColumn) {
                        enableAutoIncrement(true);
                    } else {
                        enableAutoIncrement(false);
                    }
                } else {
                    notNullCheck.setEnabled(true);
                    enableAutoIncrement(false);
                }
            }
        });
        uniqueKeyCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                uniqueKeyNameText.setEnabled(uniqueKeyCheck.getSelection());
            }
        });
        if (autoIncrementSettingButton != null && autoIncrementCheck != null) {
            autoIncrementCheck.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    autoIncrementSettingButton.setEnabled(autoIncrementCheck.getSelection());
                }
            });
        }
        if (characterSetCombo != null) {
            characterSetCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    final String selectedCollation = collationCombo.getText();
                    collationCombo.removeAll();
                    collationCombo.add("");
                    for (final String collation : MySQLDBManager.getCollationList(characterSetCombo.getText())) {
                        collationCombo.add(collation);
                    }
                    final int index = collationCombo.indexOf(selectedCollation);
                    collationCombo.select(index);
                }
            });
        }
    }

    @Override
    public void setTargetColumn(CopyColumn targetColumn, boolean foreignKey, boolean isRefered) {
        super.setTargetColumn(targetColumn, foreignKey, isRefered);
        if (targetColumn != null) {
            autoIncrementSetting = targetColumn.getAutoIncrementSetting();
        } else {
            autoIncrementSetting = new Sequence();
        }
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        final String superResult = super.doValidate();
        if (superResult != null) {
            return superResult;
        }
        if (autoIncrementCheck != null && autoIncrementCheck.getSelection()) {
            final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());
            if (selectedType == null || !selectedType.isNumber()) {
                return "error.no.auto.increment.column";
            }
        }
        final String uniqueKeyName = uniqueKeyNameText.getText().trim();
        if (table.getDiagramSettings().isValidatePhysicalName() && !Check.isAlphabet(uniqueKeyName)) {
            return "error.unique.key.name.not.alphabet";
        }
        final String physicalName = physicalNameText.getText().trim();
        final List<NormalColumn> columns = table.getNormalColumns();
        for (final NormalColumn column : columns) {
            final String currentName = column.getPhysicalName();
            if (add) {
                if (currentName.toLowerCase().equals(physicalName.toLowerCase())) {
                    return "error.column.physical.name.already.exists";
                }
            } else { // edit
                if (targetColumn != null) { // basically true, just in case
                    final String previousName = targetColumn.getPhysicalName();
                    if (!currentName.equalsIgnoreCase(previousName)) { // other columns
                        if (currentName.equalsIgnoreCase(physicalName)) {
                            return "error.column.physical.name.already.exists";
                        }
                    }
                }
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() {
        super.performOK();
        returnColumn.setPrimaryKey(primaryKeyCheck.getSelection());
        if (autoIncrementCheck != null) {
            returnColumn.setAutoIncrement(autoIncrementCheck.getSelection());
        }
        returnColumn.setAutoIncrementSetting(autoIncrementSetting);
        returnColumn.setUniqueKeyName(uniqueKeyNameText.getText());
        if (characterSetCombo != null) {
            returnColumn.setCharacterSet(characterSetCombo.getText());
            returnColumn.setCollation(collationCombo.getText());
        }
    }
}

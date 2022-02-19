package org.dbflute.erflute.editor.view.dialog.word;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.impl.mysql.MySQLDBManager;
import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.db.impl.oracle12c.Oracle12cDBManager;
import org.dbflute.erflute.db.impl.postgres.PostgresDBManager;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class AbstractWordDialog extends AbstractDialog {

    protected Combo typeCombo;
    protected Text logicalNameText;
    protected Text physicalNameText;
    protected Text lengthText;
    protected Text decimalText;
    protected Button arrayCheck;
    protected Text arrayDimensionText;
    protected Button unsignedCheck;
    protected boolean add;
    protected Text descriptionText;
    protected Text argsText;
    protected Button charSemanticsRadio;
    protected Button byteSemanticsRadio;
    protected ERDiagram diagram;

    public AbstractWordDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell);
        this.diagram = diagram;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    @Override
    protected void initComponent(Composite composite) {
        final Composite rootComposite = createRootComposite(composite);
        initializeComposite(rootComposite);
        physicalNameText.setFocus();
        validate();
    }

    protected Composite createRootComposite(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = getCompositeNumColumns();
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(gridLayout);
        return composite;
    }

    protected int getCompositeNumColumns() {
        if (PostgresDBManager.ID.equals(diagram.getDatabase())) {
            return 10;
        } else if (MySQLDBManager.ID.equals(diagram.getDatabase())) {
            return 8;
        }
        return 6;
    }

    protected void initializeComposite(Composite composite) {
        final int numColumns = getCompositeNumColumns();
        this.physicalNameText = CompositeFactory.createText(this, composite, "label.physical.name", numColumns - 1, false);
        this.logicalNameText = CompositeFactory.createText(this, composite, "label.logical.name", numColumns - 1, true);
        this.typeCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.column.type");
        this.lengthText = CompositeFactory.createNumText(this, composite, "label.column.length", 30);
        lengthText.setEnabled(false);
        this.decimalText = CompositeFactory.createNumText(this, composite, "label.column.decimal", 30);
        decimalText.setEnabled(false);
        if (PostgresDBManager.ID.equals(diagram.getDatabase())) {
            CompositeFactory.filler(composite, 1, 10);
            this.arrayCheck = CompositeFactory.createCheckbox(this, composite, "label.column.array");
            arrayCheck.setEnabled(true);
            this.arrayDimensionText = CompositeFactory.createNumText(this, composite, "label.column.array.dimension", 15);
            arrayDimensionText.setEnabled(false);
            arrayCheck.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    arrayDimensionText.setEnabled(arrayCheck.getSelection());
                    super.widgetSelected(e);
                }
            });
        }
        if (MySQLDBManager.ID.equals(diagram.getDatabase())) {
            CompositeFactory.filler(composite, 1, 10);
            this.unsignedCheck = CompositeFactory.createCheckbox(this, composite, "label.column.unsigned");
            unsignedCheck.setEnabled(false);
            CompositeFactory.filler(composite, 1);
            this.argsText = CompositeFactory.createText(this, composite, "label.column.type.enum.set", getCompositeNumColumns() - 2, false);
            argsText.setEnabled(false);
        }
        if (OracleDBManager.ID.equals(diagram.getDatabase())) {
            CompositeFactory.filler(composite, 1);
            final Composite childComposite = CompositeFactory.createChildComposite(composite, 5, 2);
            this.charSemanticsRadio = CompositeFactory.createRadio(this, childComposite, "char");
            charSemanticsRadio.setEnabled(false);
            charSemanticsRadio.setSelection(true);
            this.byteSemanticsRadio = CompositeFactory.createRadio(this, childComposite, "byte", 1, true);
            byteSemanticsRadio.setEnabled(false);
        }
        if (Oracle12cDBManager.ID.equals(diagram.getDatabase())) {
            CompositeFactory.filler(composite, 1);
            final Composite childComposite = CompositeFactory.createChildComposite(composite, 5, 2);
            this.charSemanticsRadio = CompositeFactory.createRadio(this, childComposite, "char");
            charSemanticsRadio.setEnabled(false);
            charSemanticsRadio.setSelection(true);
            this.byteSemanticsRadio = CompositeFactory.createRadio(this, childComposite, "byte", 1, true);
            byteSemanticsRadio.setEnabled(false);
        }
        this.descriptionText = CompositeFactory.createTextArea(this, composite, "label.column.description", -1, 100, numColumns - 1, true);
    }

    // ===================================================================================
    //                                                                            Set Data
    //                                                                            ========
    @Override
    protected final void setupData() {
        initializeTypeCombo();
        if (!add) {
            setWordData();
        }
    }

    private void initializeTypeCombo() {
        typeCombo.add("");
        prepareFrequentlyUsedType();
        final String database = diagram.getDatabase();
        for (final String alias : SqlType.getAliasList(database)) {
            typeCombo.add(alias);
        }
    }

    protected void prepareFrequentlyUsedType() {
        // MySQL only for now (2014/10/30)
        // to modify excel file is very difficult so easy-way for quick fix
        if (isDatabaseMySQL()) {
            typeCombo.add("char(n)");
            typeCombo.add("varchar(n)");
            typeCombo.add("text");
            typeCombo.add("int");
            typeCombo.add("bigint");
            typeCombo.add("date");
            typeCombo.add("datetime");
            typeCombo.add("boolean");
            typeCombo.add("---");
        }
    }

    protected boolean isDatabaseMySQL() {
        return MySQLDBManager.ID.equals(diagram.getDatabase()); // for now
    }

    abstract protected void setWordData();

    protected void setData(String physicalName, String logicalName, SqlType sqlType, TypeData typeData, String description) {
        physicalNameText.setText(Format.toString(physicalName));
        logicalNameText.setText(Format.toString(logicalName));
        if (sqlType != null) {
            final String database = diagram.getDatabase();
            final String sqlTypeAlias = sqlType.getAlias(database);
            if (sqlTypeAlias != null) {
                typeCombo.setText(sqlTypeAlias);
            }
            if (!sqlType.isNeedLength(database)) {
                lengthText.setEnabled(false);
            }
            if (!sqlType.isNeedDecimal(database)) {
                decimalText.setEnabled(false);
            }
            if (unsignedCheck != null && !sqlType.isNumber()) {
                unsignedCheck.setEnabled(false);
            }
            if (argsText != null) {
                if (sqlType.doesNeedArgs()) {
                    argsText.setEnabled(true);
                } else {
                    argsText.setEnabled(false);
                }
            }
        } else {
            lengthText.setEnabled(false);
            decimalText.setEnabled(false);
            if (unsignedCheck != null) {
                unsignedCheck.setEnabled(false);
            }
            if (argsText != null) {
                argsText.setEnabled(false);
            }
        }
        lengthText.setText(Format.toString(typeData.getLength()));
        decimalText.setText(Format.toString(typeData.getDecimal()));
        if (arrayDimensionText != null) {
            arrayCheck.setSelection(typeData.isArray());
            arrayDimensionText.setText(Format.toString(typeData.getArrayDimension()));
            arrayDimensionText.setEnabled(arrayCheck.getSelection());
        }
        if (unsignedCheck != null) {
            unsignedCheck.setSelection(typeData.isUnsigned());
        }
        if (argsText != null) {
            argsText.setText(Format.null2blank(typeData.getArgs()));
        }
        if (charSemanticsRadio != null) {
            final boolean charSemantics = typeData.isCharSemantics();
            charSemanticsRadio.setSelection(charSemantics);
            byteSemanticsRadio.setSelection(!charSemantics);
        }
        descriptionText.setText(Format.toString(description));
    }

    // ===================================================================================
    //                                                                        Add Listener
    //                                                                        ============
    @Override
    protected void addListener() {
        super.addListener();
        typeCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                setEnabledBySqlType();
            }
        });
        // #for_erflute quit physical name and logical name linkage
        //this.physicalNameText.addFocusListener(new FocusAdapter() {
        //    @Override
        //    public void focusLost(FocusEvent e) {
        //        if (logicalNameText.getText().equals("")) {
        //            logicalNameText.setText(physicalNameText.getText());
        //        }
        //    }
        //});
        //this.physicalNameText.addModifyListener(new ModifyListener() {
        //    @Override
        //    public void modifyText(ModifyEvent e) {
        //        final String logicalName = logicalNameText.getText();
        //        final String physicalName = physicalNameText.getText();
        //        if (oldPhysicalName.equals(logicalName) || logicalName.equals("")) {
        //            logicalNameText.setText(physicalName);
        //            oldPhysicalName = physicalName;
        //        }
        //    }
        //});
    }

    protected void setEnabledBySqlType() {
        final String database = diagram.getDatabase();
        final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());
        if (selectedType != null) {
            if (!selectedType.isNeedLength(diagram.getDatabase())) {
                lengthText.setEnabled(false);
            } else {
                lengthText.setEnabled(true);
            }
            if (!selectedType.isNeedDecimal(database)) {
                decimalText.setEnabled(false);
            } else {
                decimalText.setEnabled(true);
            }
            if (unsignedCheck != null) {
                if (!selectedType.isNumber()) {
                    unsignedCheck.setEnabled(false);
                } else {
                    unsignedCheck.setEnabled(true);
                }
            }
            if (argsText != null) {
                if (selectedType.doesNeedArgs()) {
                    argsText.setEnabled(true);
                } else {
                    argsText.setEnabled(false);
                }
            }
            if (charSemanticsRadio != null) {
                if (selectedType.isNeedCharSemantics(database)) {
                    charSemanticsRadio.setEnabled(true);
                    byteSemanticsRadio.setEnabled(true);

                } else {
                    charSemanticsRadio.setEnabled(false);
                    byteSemanticsRadio.setEnabled(false);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        final String physicalName = physicalNameText.getText().trim();
        if (physicalName.isEmpty()) { // required
            return "error.column.physical.name.empty";
        }
        if (!Check.isAlphabet(physicalName)) {
            if (diagram.getDiagramContents().getSettings().isValidatePhysicalName()) {
                return "error.column.physical.name.not.alphabet";
            }
        }
        final String length = lengthText.getText();
        if (!length.equals("")) {
            try {
                final int len = Integer.parseInt(length);
                if (len < 0) {
                    return "error.column.length.zero";
                }
            } catch (final NumberFormatException e) {
                return "error.column.length.degit";
            }
        }
        final String decimal = decimalText.getText();
        if (!decimal.equals("")) {
            try {
                final int len = Integer.parseInt(decimal);
                if (len < 0) {
                    return "error.column.decimal.zero";
                }
            } catch (final NumberFormatException e) {
                return "error.column.decimal.degit";
            }
        }
        if (arrayDimensionText != null) {
            final String arrayDimension = arrayDimensionText.getText();
            if (!arrayDimension.equals("")) {
                try {
                    final int len = Integer.parseInt(arrayDimension);
                    if (len < 1) {
                        return "error.column.array.dimension.one";
                    }
                } catch (final NumberFormatException e) {
                    return "error.column.array.dimension.degit";
                }
            } else {
                if (arrayCheck.getSelection()) {
                    return "error.column.array.dimension.one";
                }
            }
        }
        final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());
        if (selectedType != null && argsText != null) {
            final String args = argsText.getText();
            if (selectedType.doesNeedArgs()) {
                if (args.equals("")) {
                    return "error.column.type.enum.set";
                }
            }
        }
        return null;
    }
}

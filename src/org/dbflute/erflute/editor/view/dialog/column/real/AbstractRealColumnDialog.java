package org.dbflute.erflute.editor.view.dialog.column.real;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.view.dialog.column.AbstractColumnDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractRealColumnDialog extends AbstractColumnDialog {

    protected Button notNullCheck;
    protected Button uniqueKeyCheck;
    protected Combo defaultText;
    protected Text constraintText;
    private TabFolder tabFolder;
    protected TabItem tabItem;

    public AbstractRealColumnDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, diagram);
    }

    @Override
    protected Composite createRootComposite(Composite parent) {
        this.tabFolder = new TabFolder(parent, SWT.NONE);
        this.tabItem = new TabItem(this.tabFolder, SWT.NONE);
        this.tabItem.setText(DisplayMessages.getMessage("label.basic"));
        final Composite composite = super.createRootComposite(this.tabFolder);
        this.tabItem.setControl(composite);
        this.tabItem = new TabItem(this.tabFolder, SWT.NONE);
        this.tabItem.setText(DisplayMessages.getMessage("label.detail"));
        final Composite detailComposite = this.createDetailTab(this.tabFolder);
        this.initializeDetailTab(detailComposite);
        this.tabItem.setControl(detailComposite);
        return composite;
    }

    @Override
    protected void initializeComposite(Composite composite) {
        final int numColumns = this.getCompositeNumColumns();
        final Composite checkBoxComposite = new Composite(composite, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.horizontalSpan = numColumns;
        gridData.heightHint = 40;
        checkBoxComposite.setLayoutData(gridData);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = this.getCheckBoxCompositeNumColumns();
        checkBoxComposite.setLayout(gridLayout);
        this.initializeCheckBoxComposite(checkBoxComposite);
        super.initializeComposite(composite);
        this.defaultText = CompositeFactory.createCombo(this, composite, "label.column.default.value", numColumns - 1);
    }

    protected int getCheckBoxCompositeNumColumns() {
        return 2;
    }

    private Composite createDetailTab(TabFolder tabFolder) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        final Composite composite = new Composite(tabFolder, SWT.NONE);
        composite.setLayout(gridLayout);
        return composite;
    }

    protected void initializeDetailTab(Composite composite) {
        this.constraintText = CompositeFactory.createText(this, composite, "label.column.constraint", false);
    }

    protected void initializeCheckBoxComposite(Composite composite) {
        this.notNullCheck = CompositeFactory.createCheckbox(this, composite, "label.not.null");
        this.uniqueKeyCheck = CompositeFactory.createCheckbox(this, composite, "label.unique.key");
    }

    @Override
    protected void setWordData() {
        this.notNullCheck.setSelection(this.targetColumn.isNotNull());
        this.uniqueKeyCheck.setSelection(this.targetColumn.isUniqueKey());
        if (this.targetColumn.getConstraint() != null) {
            this.constraintText.setText(this.targetColumn.getConstraint());
        }
        if (this.targetColumn.getDefaultValue() != null) {
            this.defaultText.setText(this.targetColumn.getDefaultValue());
        }
        super.setWordData();
    }

    @Override
    protected void perfomeOK() {
        super.perfomeOK();
        this.returnColumn =
                new NormalColumn(returnWord, notNullCheck.getSelection(), false, uniqueKeyCheck.getSelection(), false,
                        defaultText.getText(), constraintText.getText(), null, null, null);
    }

    @Override
    protected void setEnabledBySqlType() {
        super.setEnabledBySqlType();
        final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());
        if (selectedType != null) {
            final String defaultValue = this.defaultText.getText();
            this.defaultText.removeAll();
            if (selectedType.isTimestamp()) {
                this.defaultText.add(DisplayMessages.getMessage("label.current.date.time"));
                this.defaultText.setText(defaultValue);
            } else {
                if (!DisplayMessages.getMessage("label.current.date.time").equals(defaultValue)) {
                    this.defaultText.setText(defaultValue);
                }
            }
        }
    }
}

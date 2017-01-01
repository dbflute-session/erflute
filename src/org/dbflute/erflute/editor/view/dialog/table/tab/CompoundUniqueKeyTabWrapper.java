package org.dbflute.erflute.editor.view.dialog.table.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CopyCompoundUniqueKey;
import org.dbflute.erflute.editor.view.dialog.table.ERTableComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class CompoundUniqueKeyTabWrapper extends ValidatableTabWrapper {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ERTable table;
    private Text uniqueKeyNameText;
    private String previousUniqueKeyName;
    private Combo compoundUniqueKeyCombo;
    private Table columnTable;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private final List<TableEditor> tableEditorList;
    private final Map<TableEditor, NormalColumn> editorColumnMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public CompoundUniqueKeyTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERTable table) {
        super(dialog, parent, style, "Compound Unique Key");
        this.table = table;
        this.tableEditorList = new ArrayList<TableEditor>();
        this.editorColumnMap = new HashMap<TableEditor, NormalColumn>();
        init();
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    @Override
    public void initComposite() {
        final GridLayout gridLayout = new GridLayout();
        setLayout(gridLayout);
        compoundUniqueKeyCombo = CompositeFactory.createReadOnlyCombo(null, this, "Compound Unique Key");
        uniqueKeyNameText = CompositeFactory.createText(null, this, "label.unique.key.name", false);
        previousUniqueKeyName = uniqueKeyNameText.getText().trim();
        CompositeFactory.filler(this, 1);
        columnTable = CompositeFactory.createTable(this, 200, 1);
        CompositeFactory.createTableColumn(this.columnTable, "label.logical.name", ERTableComposite.NAME_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(this.columnTable, "label.unique.key", ERTableComposite.UNIQUE_KEY_WIDTH, SWT.NONE);
        final GridLayout buttonGridLayout = new GridLayout();
        buttonGridLayout.numColumns = 3;
        final Composite buttonComposite = new Composite(this, SWT.NONE);
        buttonComposite.setLayout(buttonGridLayout);
        addButton = CompositeFactory.createButton(buttonComposite, "label.button.add");
        updateButton = CompositeFactory.createButton(buttonComposite, "label.button.update");
        deleteButton = CompositeFactory.createButton(buttonComposite, "label.button.delete");
    }

    // ===================================================================================
    //                                                                            Listener
    //                                                                            ========
    @Override
    protected void addListener() {
        super.addListener();
        this.compoundUniqueKeyCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkSelectedKey();
            }
        });
        this.addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final String uniqueKeyName = uniqueKeyNameText.getText().trim();
                if (!validateUniqueKeyName(uniqueKeyName)) {
                    return;
                }
                final List<NormalColumn> columnList = new ArrayList<NormalColumn>();
                for (final TableEditor tableEditor : tableEditorList) {
                    final Button checkBox = (Button) tableEditor.getEditor();
                    if (checkBox.getSelection()) {
                        columnList.add(editorColumnMap.get(tableEditor));
                    }
                }
                if (columnList.isEmpty()) {
                    Activator.showErrorDialog("error.not.checked.complex.unique.key.columns");
                    return;
                }
                if (findComplexUniqueKey(columnList) != null) {
                    Activator.showErrorDialog("error.already.exist.complex.unique.key");
                    return;
                }
                final CompoundUniqueKey complexUniqueKey = new CopyCompoundUniqueKey(new CompoundUniqueKey(uniqueKeyName), null);
                complexUniqueKey.setColumnList(columnList);
                table.getCompoundUniqueKeyList().add(complexUniqueKey);
                addComboData(complexUniqueKey);
                compoundUniqueKeyCombo.select(compoundUniqueKeyCombo.getItemCount() - 1);
                setUpdateDeleteButtonStatus(true);
            }
        });
        this.updateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = compoundUniqueKeyCombo.getSelectionIndex();
                if (index == -1) {
                    return;
                }
                final String uniqueKeyName = uniqueKeyNameText.getText().trim();
                if (!validateUniqueKeyName(uniqueKeyName)) {
                    return;
                }
                final CompoundUniqueKey complexUniqueKey = table.getCompoundUniqueKeyList().get(index);
                final List<NormalColumn> columnList = new ArrayList<NormalColumn>();
                for (final TableEditor tableEditor : tableEditorList) {
                    final Button checkBox = (Button) tableEditor.getEditor();
                    if (checkBox.getSelection()) {
                        columnList.add(editorColumnMap.get(tableEditor));
                    }
                }
                if (columnList.isEmpty()) {
                    Activator.showErrorDialog("error.not.checked.complex.unique.key.columns");
                    return;
                }
                final CompoundUniqueKey sameKey = findComplexUniqueKey(columnList);
                if (sameKey != null && sameKey != complexUniqueKey) {
                    Activator.showErrorDialog("error.already.exist.complex.unique.key");
                    return;
                }
                complexUniqueKey.setUniqueKeyName(uniqueKeyName);
                complexUniqueKey.setColumnList(columnList);
                compoundUniqueKeyCombo.remove(index);
                compoundUniqueKeyCombo.add(complexUniqueKey.getLabel(), index);
                compoundUniqueKeyCombo.select(index);
            }
        });
        this.deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = compoundUniqueKeyCombo.getSelectionIndex();
                if (index == -1) {
                    return;
                }
                compoundUniqueKeyCombo.remove(index);
                table.getCompoundUniqueKeyList().remove(index);
                if (index < table.getCompoundUniqueKeyList().size()) {
                    compoundUniqueKeyCombo.select(index);
                } else {
                    compoundUniqueKeyCombo.select(index - 1);
                }
                checkSelectedKey();
            }
        });
    }

    private void checkSelectedKey() {
        final int index = compoundUniqueKeyCombo.getSelectionIndex();
        CompoundUniqueKey complexUniqueKey = null;
        String name = null;
        if (index != -1) {
            complexUniqueKey = table.getCompoundUniqueKeyList().get(index);
            name = complexUniqueKey.getUniqueKeyName();
            setUpdateDeleteButtonStatus(true);
        } else {
            setUpdateDeleteButtonStatus(false);
        }
        uniqueKeyNameText.setText(Format.null2blank(name));
        for (final TableEditor tableEditor : tableEditorList) {
            final Button checkbox = (Button) tableEditor.getEditor();
            final NormalColumn column = editorColumnMap.get(tableEditor);
            if (complexUniqueKey != null && complexUniqueKey.getColumnList().contains(column)) {
                checkbox.setSelection(true);
            } else {
                checkbox.setSelection(false);
            }
        }
    }

    private boolean validateUniqueKeyName(String uniqueKeyName) { // for add/update button
        if (uniqueKeyName.isEmpty()) {
            Activator.showErrorDialog("The constraint name for unique key is required.");
            return false;
        }
        if (uniqueKeyName.equalsIgnoreCase(buildDefaultUniqueKeyNameTemplate())) {
            Activator.showErrorDialog("The constraint name for unique key is required: Change 'XXX' part: " + uniqueKeyName);
            return false;
        }
        if (!Check.isAlphabet(uniqueKeyName)) {
            Activator.showErrorDialog("error.unique.key.name.not.alphabet");
            return false;
        }
        final List<ERTable> tableList = table.getDiagram().getDiagramContents().getDiagramWalkers().getTableSet().getList();
        for (final ERTable table : tableList) {
            final List<CompoundUniqueKey> complexUniqueKeyList = table.getCompoundUniqueKeyList();
            for (final CompoundUniqueKey complexUniqueKey : complexUniqueKeyList) {
                final String currentUniqueKeyName = complexUniqueKey.getUniqueKeyName();
                if (currentUniqueKeyName != null) {
                    if (!currentUniqueKeyName.equalsIgnoreCase(previousUniqueKeyName)) {
                        if (currentUniqueKeyName.equalsIgnoreCase(uniqueKeyName)) {
                            Activator.showErrorDialog("error.unique.key.name.already.exists");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public CompoundUniqueKey findComplexUniqueKey(List<NormalColumn> columnList) {
        for (final CompoundUniqueKey complexUniqueKey : table.getCompoundUniqueKeyList()) {
            if (columnList.size() == complexUniqueKey.getColumnList().size()) {
                boolean exists = true;
                for (final NormalColumn column : columnList) {
                    if (!complexUniqueKey.getColumnList().contains(column)) {
                        exists = false;
                        break;
                    }
                }
                if (exists) {
                    return complexUniqueKey;
                }
            }
        }
        return null;
    }

    @Override
    protected void setupData() {
        super.setupData();
        if (compoundUniqueKeyCombo.getSelectionIndex() == -1) { // means new unique key
            uniqueKeyNameText.setText(buildDefaultUniqueKeyNameTemplate());
        }
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    public void validatePage() throws InputException {
        final String uniqueKeyName = uniqueKeyNameText.getText().trim();
        // #thinking want to validate but always checked by jflute
        //if (uniqueKeyName.isEmpty()) {
        //    throw new InputException("error.unique.key.name.empty");
        //}
        if (Srl.is_NotNull_and_NotTrimmedEmpty(uniqueKeyName)) {
            if (!Check.isAlphabet(uniqueKeyName)) {
                throw new InputException("error.unique.key.name.not.alphabet");
            }
            final List<ERTable> tableList = table.getDiagram().getDiagramContents().getDiagramWalkers().getTableSet().getList();
            for (final ERTable table : tableList) {
                final List<CompoundUniqueKey> complexUniqueKeyList = table.getCompoundUniqueKeyList();
                for (final CompoundUniqueKey complexUniqueKey : complexUniqueKeyList) {
                    final String currentUniqueKeyName = complexUniqueKey.getUniqueKeyName();
                    if (currentUniqueKeyName != null) {
                        if (!currentUniqueKeyName.equalsIgnoreCase(previousUniqueKeyName)) {
                            if (currentUniqueKeyName.equalsIgnoreCase(uniqueKeyName)) {
                                throw new InputException("error.unique.key.name.already.exists");
                            }
                        }
                    }
                }
            }
        }
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    public void perfomeOK() {
    }

    // ===================================================================================
    //                                                                             Dispose
    //                                                                             =======
    @Override
    public void dispose() {
        disposeTableEditor();
        super.dispose();
    }

    // ===================================================================================
    //                                                                               Focus
    //                                                                               =====
    @Override
    public void setInitFocus() {
        this.compoundUniqueKeyCombo.setFocus();
    }

    // ===================================================================================
    //                                                                      Various Public
    //                                                                      ==============
    public void restruct() {
        this.columnTable.removeAll();
        this.disposeTableEditor();
        for (final NormalColumn normalColumn : this.table.getNormalColumns()) {
            final TableItem tableItem = new TableItem(this.columnTable, SWT.NONE);
            tableItem.setText(0, Format.null2blank(normalColumn.getName()));
            final TableEditor tableEditor = CompositeFactory.createCheckBoxTableEditor(tableItem, false, 1);
            this.tableEditorList.add(tableEditor);
            this.editorColumnMap.put(tableEditor, normalColumn);
        }
        setComboData();
        setUpdateDeleteButtonStatus(false);
    }

    private void setComboData() {
        this.compoundUniqueKeyCombo.removeAll();
        for (final Iterator<CompoundUniqueKey> iter = this.table.getCompoundUniqueKeyList().iterator(); iter.hasNext();) {
            final CompoundUniqueKey complexUniqueKey = iter.next();
            if (complexUniqueKey.isRemoved(this.table.getNormalColumns())) {
                iter.remove();
            } else {
                this.addComboData(complexUniqueKey);
            }
        }
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String buildDefaultUniqueKeyNameTemplate() {
        return "UQ_" + table.getPhysicalName() + "_XXX";
    }

    private void setUpdateDeleteButtonStatus(boolean enabled) {
        if (enabled) {
            if (table.getCompoundUniqueKeyList().get(compoundUniqueKeyCombo.getSelectionIndex()).isReferred(table)) {
                enabled = false;
            }
        }
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    private void addComboData(CompoundUniqueKey complexUniqueKey) {
        this.compoundUniqueKeyCombo.add(complexUniqueKey.getLabel());
    }

    private void disposeTableEditor() {
        for (final TableEditor tableEditor : this.tableEditorList) {
            tableEditor.getEditor().dispose();
            tableEditor.dispose();
        }
        this.tableEditorList.clear();
        this.editorColumnMap.clear();
    }
}

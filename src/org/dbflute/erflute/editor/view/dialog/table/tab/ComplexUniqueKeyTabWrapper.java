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
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CopyComplexUniqueKey;
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
public class ComplexUniqueKeyTabWrapper extends ValidatableTabWrapper {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ERTable table;
    private Text uniqueKeyNameText;
    private String previousUniqueKeyName;
    private Combo complexUniqueKeyCombo;
    private Table columnTable;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private final List<TableEditor> tableEditorList;
    private final Map<TableEditor, NormalColumn> editorColumnMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ComplexUniqueKeyTabWrapper(AbstractDialog dialog, TabFolder parent, int style, ERTable table) {
        super(dialog, parent, style, "label.complex.unique.key");
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
        complexUniqueKeyCombo = CompositeFactory.createReadOnlyCombo(null, this, "label.complex.unique.key");
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
        this.complexUniqueKeyCombo.addSelectionListener(new SelectionAdapter() {
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
                final ComplexUniqueKey complexUniqueKey = new CopyComplexUniqueKey(new ComplexUniqueKey(uniqueKeyName), null);
                complexUniqueKey.setColumnList(columnList);
                table.getComplexUniqueKeyList().add(complexUniqueKey);
                addComboData(complexUniqueKey);
                complexUniqueKeyCombo.select(complexUniqueKeyCombo.getItemCount() - 1);
                setButtonStatus(true);
            }
        });
        this.updateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = complexUniqueKeyCombo.getSelectionIndex();
                if (index == -1) {
                    return;
                }
                final String uniqueKeyName = uniqueKeyNameText.getText().trim();
                if (!validateUniqueKeyName(uniqueKeyName)) {
                    return;
                }
                final ComplexUniqueKey complexUniqueKey = table.getComplexUniqueKeyList().get(index);
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
                final ComplexUniqueKey sameKey = findComplexUniqueKey(columnList);
                if (sameKey != null && sameKey != complexUniqueKey) {
                    Activator.showErrorDialog("error.already.exist.complex.unique.key");
                    return;
                }
                complexUniqueKey.setUniqueKeyName(uniqueKeyName);
                complexUniqueKey.setColumnList(columnList);
                complexUniqueKeyCombo.remove(index);
                complexUniqueKeyCombo.add(complexUniqueKey.getLabel(), index);
                complexUniqueKeyCombo.select(index);
            }
        });

        this.deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = complexUniqueKeyCombo.getSelectionIndex();
                if (index == -1) {
                    return;
                }
                complexUniqueKeyCombo.remove(index);
                table.getComplexUniqueKeyList().remove(index);
                if (index < table.getComplexUniqueKeyList().size()) {
                    complexUniqueKeyCombo.select(index);
                } else {
                    complexUniqueKeyCombo.select(index - 1);
                }
                checkSelectedKey();
            }
        });
    }

    private void checkSelectedKey() {
        final int index = complexUniqueKeyCombo.getSelectionIndex();
        ComplexUniqueKey complexUniqueKey = null;
        String name = null;
        if (index != -1) {
            complexUniqueKey = table.getComplexUniqueKeyList().get(index);
            name = complexUniqueKey.getUniqueKeyName();
            setButtonStatus(true);
        } else {
            setButtonStatus(false);
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

    private boolean validateUniqueKeyName(String uniqueKeyName) {
        if (uniqueKeyName.isEmpty()) {
            Activator.showErrorDialog("error.unique.key.name.empty");
            return false;
        }
        if (!Check.isAlphabet(uniqueKeyName)) {
            Activator.showErrorDialog("error.unique.key.name.not.alphabet");
            return false;
        }
        final List<ERTable> tableList = table.getDiagram().getDiagramContents().getDiagramWalkers().getTableSet().getList();
        for (final ERTable table : tableList) {
            final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
            for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
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

    public ComplexUniqueKey findComplexUniqueKey(List<NormalColumn> columnList) {
        for (final ComplexUniqueKey complexUniqueKey : table.getComplexUniqueKeyList()) {
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
                final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
                for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
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
        this.complexUniqueKeyCombo.setFocus();
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
        setButtonStatus(false);
    }

    private void setComboData() {
        this.complexUniqueKeyCombo.removeAll();
        for (final Iterator<ComplexUniqueKey> iter = this.table.getComplexUniqueKeyList().iterator(); iter.hasNext();) {
            final ComplexUniqueKey complexUniqueKey = iter.next();
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
    private void setButtonStatus(boolean enabled) {
        if (enabled) {
            if (table.getComplexUniqueKeyList().get(complexUniqueKeyCombo.getSelectionIndex()).isReferred(table)) {
                enabled = false;
            }
        }
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    private void addComboData(ComplexUniqueKey complexUniqueKey) {
        this.complexUniqueKeyCombo.add(complexUniqueKey.getLabel());
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

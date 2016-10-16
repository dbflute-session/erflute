package org.dbflute.erflute.editor.view.dialog.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.view.dialog.relationship.RelationshipDialog.ReferredColumnState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class RelationshipByExistingColumnsDialog extends AbstractDialog {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final int COLUMN_WIDTH = 200;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ERTable source; // parent table e.g. MEMBER_STATUS
    private List<NormalColumn> referredColumnList;
    private final List<NormalColumn> foreignKeyColumnList;
    private final List<NormalColumn> candidateForeignKeyColumns;
    private final Map<NormalColumn, List<NormalColumn>> rootReferredColumnsMap;
    private final Map<Relationship, Set<NormalColumn>> foreignKeyColumnsMap;
    private final List<TableEditor> tableEditorList;
    private final Map<TableEditor, List<NormalColumn>> editorReferredColumnsMap;

    private Combo referredColumnSelector;
    private ReferredColumnState referredColumnState;
    private Table foreignKeyColumnMapper; // avoid abstract word 'table' where

    private boolean referenceForPK;
    private ComplexUniqueKey referencedComplexUniqueKey;
    private NormalColumn referencedColumn;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public RelationshipByExistingColumnsDialog(Shell parentShell, ERTable source, List<NormalColumn> candidateForeignKeyColumns,
            Map<NormalColumn, List<NormalColumn>> rootReferredColumnsMap, Map<Relationship, Set<NormalColumn>> foreignKeyColumnsMap) {
        super(parentShell, 2);
        this.source = source;
        this.referredColumnList = new ArrayList<NormalColumn>();
        this.foreignKeyColumnList = new ArrayList<NormalColumn>();
        this.candidateForeignKeyColumns = candidateForeignKeyColumns;
        this.rootReferredColumnsMap = rootReferredColumnsMap;
        this.foreignKeyColumnsMap = foreignKeyColumnsMap;
        this.tableEditorList = new ArrayList<TableEditor>();
        this.editorReferredColumnsMap = new HashMap<TableEditor, List<NormalColumn>>();
    }

    // ===================================================================================
    //                                                                         Dialog Area
    //                                                                         ===========
    @Override
    protected String getTitle() {
        return "dialog.title.relation";
    }

    // -----------------------------------------------------
    //                                                Layout
    //                                                ------
    @Override
    protected void initLayout(GridLayout layout) {
        super.initLayout(layout);
        layout.verticalSpacing = 20;
    }

    // -----------------------------------------------------
    //                                             Component
    //                                             ---------
    @Override
    protected void initComponent(Composite composite) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
        label.setText("Select PK or UQ column and select FK column");
        createReferredColumnSelector(composite);
        createForeignKeyColumnMapper(composite);
    }

    // -----------------------------------------------------
    //                              Referred Column Selector
    //                              ------------------------
    private void createReferredColumnSelector(Composite composite) {
        final Label label = new Label(composite, SWT.NONE);
        label.setText("Referred Column");
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        referredColumnSelector = new Combo(composite, SWT.READ_ONLY);
        referredColumnSelector.setLayoutData(gridData);
        referredColumnSelector.setVisibleItemCount(20);
    }

    // -----------------------------------------------------
    //                              ForeignKey Column Mapper
    //                              =-----------------------
    private void createForeignKeyColumnMapper(Composite composite) {
        final GridData tableGridData = new GridData();
        tableGridData.horizontalSpan = 2;
        tableGridData.heightHint = 100;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace = true;
        foreignKeyColumnMapper = new Table(composite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
        foreignKeyColumnMapper.setLayoutData(tableGridData);
        foreignKeyColumnMapper.setHeaderVisible(true);
        foreignKeyColumnMapper.setLinesVisible(true);
        final TableColumn referredColumn = new TableColumn(foreignKeyColumnMapper, SWT.NONE);
        referredColumn.setWidth(COLUMN_WIDTH);
        referredColumn.setText("Referred Column");
        final TableColumn foreignKeyColumn = new TableColumn(foreignKeyColumnMapper, SWT.NONE);
        foreignKeyColumn.setWidth(COLUMN_WIDTH);
        foreignKeyColumn.setText("ForeignKey Column");
    }

    // -----------------------------------------------------
    //                                           Set up Data
    //                                           -----------
    @Override
    protected void setupData() {
        referredColumnState = RelationshipDialog.setupReferencedColumnComboData(referredColumnSelector, source);
        referredColumnSelector.select(0);
        prepareForeignKeyColumnMapperRows();
    }

    // ===================================================================================
    //                                                                            Listener
    //                                                                            ========
    @Override
    protected void addListener() {
        super.addListener();
        referredColumnSelector.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO jflute making automatically creating column (2016/10/16)
                //final ERTable sourceTable = (ERTable) source.getModel(); // e.g. MEMBER_STATUS
                //final Relationship temp = sourceTable.createRelation();
                //relationship.setReferenceForPK(temp.isReferenceForPK());
                //relationship.setReferencedComplexUniqueKey(temp.getReferencedComplexUniqueKey());
                //relationship.setReferencedColumn(temp.getReferencedColumn());
                // prepare new columns for comparisonTable here
                foreignKeyColumnMapper.removeAll();
                disposeTableEditor();
                prepareForeignKeyColumnMapperRows();
                validate();
            }
        });
        foreignKeyColumnMapper.addListener(SWT.MeasureItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                event.height = referredColumnSelector.getSize().y;
            }
        });
    }

    private void prepareForeignKeyColumnMapperRows() {
        try {
            final int referredColumnIndex = referredColumnSelector.getSelectionIndex();
            if (referredColumnIndex < referredColumnState.complexUniqueKeyStartIndex) {
                referredColumnList = source.getPrimaryKeys();
            } else if (referredColumnIndex < referredColumnState.columnStartIndex) {
                final ComplexUniqueKey complexUniqueKey =
                        source.getComplexUniqueKeyList().get(referredColumnIndex - referredColumnState.complexUniqueKeyStartIndex);
                referredColumnList = complexUniqueKey.getColumnList();
            } else {
                final NormalColumn referencedColumn =
                        referredColumnState.candidateColumns.get(referredColumnIndex - referredColumnState.columnStartIndex);
                referredColumnList = new ArrayList<NormalColumn>();
                referredColumnList.add(referencedColumn);
            }
            for (final NormalColumn referredColumn : referredColumnList) {
                addColumnToMapperItem(referredColumn);
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    private void addColumnToMapperItem(NormalColumn referredColumn) {
        final TableItem tableItem = new TableItem(foreignKeyColumnMapper, SWT.NONE);
        tableItem.setText(0, Format.null2blank(referredColumn.getLogicalName()));
        final List<NormalColumn> foreignKeyColumnList = rootReferredColumnsMap.get(referredColumn.getRootReferredColumn());
        final TableEditor tableEditor = new TableEditor(foreignKeyColumnMapper);
        tableEditor.grabHorizontal = true;
        final Combo foreignKeyColumnSelector = createForeignKeyColumnSelector(foreignKeyColumnList);
        tableEditor.setEditor(foreignKeyColumnSelector, tableItem, 1);
        tableEditorList.add(tableEditor);
        editorReferredColumnsMap.put(tableEditor, foreignKeyColumnList);
    }

    protected Combo createForeignKeyColumnSelector(List<NormalColumn> foreignKeyColumnList) {
        final Combo foreignKeyColumnSelector = CompositeFactory.createReadOnlyCombo(this, foreignKeyColumnMapper, /*title*/null);
        foreignKeyColumnSelector.add("");
        if (foreignKeyColumnList != null) {
            for (final NormalColumn foreignKeyColumn : foreignKeyColumnList) {
                foreignKeyColumnSelector.add(Format.toString(foreignKeyColumn.getName()));
            }
        }
        for (final NormalColumn foreignKeyColumn : candidateForeignKeyColumns) {
            foreignKeyColumnSelector.add(Format.toString(foreignKeyColumn.getName()));
        }
        if (foreignKeyColumnSelector.getItemCount() > 0) {
            foreignKeyColumnSelector.select(0);
        }
        return foreignKeyColumnSelector;
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        final Set<NormalColumn> selectedColumns = new HashSet<NormalColumn>();
        for (final TableEditor tableEditor : tableEditorList) {
            final Combo foreignKeyCombo = (Combo) tableEditor.getEditor();
            final int index = foreignKeyCombo.getSelectionIndex();
            if (index == 0) {
                return "error.foreign.key.not.selected";
            }
            final NormalColumn selectedColumn = findSelectedColumn(tableEditor);
            if (selectedColumns.contains(selectedColumn)) {
                return "error.foreign.key.must.be.different";
            }
            selectedColumns.add(selectedColumn);
        }
        if (existsForeignKeyColumnSet(selectedColumns)) {
            return "error.foreign.key.already.exist";
        }
        return null;
    }

    private boolean existsForeignKeyColumnSet(Set<NormalColumn> columnSet) {
        boolean exist = false;
        for (final Set<NormalColumn> foreignKeyColumnSet : foreignKeyColumnsMap.values()) {
            if (foreignKeyColumnSet.size() == columnSet.size()) {
                exist = true;
                for (final NormalColumn normalColumn : columnSet) {
                    if (!foreignKeyColumnSet.contains(normalColumn)) {
                        exist = false;
                        continue;
                    }
                }
                break;
            }
        }
        return exist;
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() {
        final int index = referredColumnSelector.getSelectionIndex();
        if (index < referredColumnState.complexUniqueKeyStartIndex) {
            referenceForPK = true;
        } else if (index < referredColumnState.columnStartIndex) {
            final ComplexUniqueKey complexUniqueKey =
                    source.getComplexUniqueKeyList().get(index - referredColumnState.complexUniqueKeyStartIndex);
            referencedComplexUniqueKey = complexUniqueKey;
        } else {
            referencedColumn = referredColumnState.candidateColumns.get(index - referredColumnState.columnStartIndex);
        }
        for (final TableEditor tableEditor : tableEditorList) {
            final NormalColumn foreignKeyColumn = findSelectedColumn(tableEditor);
            foreignKeyColumnList.add(foreignKeyColumn);
        }
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private NormalColumn findSelectedColumn(TableEditor tableEditor) {
        final Combo foreignKeyCombo = (Combo) tableEditor.getEditor();
        final int foreignKeyComboIndex = foreignKeyCombo.getSelectionIndex();
        int startIndex = 1;
        NormalColumn foreignKeyColumn = null;
        final List<NormalColumn> foreignKeyList = editorReferredColumnsMap.get(tableEditor);
        if (foreignKeyList != null) {
            if (foreignKeyComboIndex <= foreignKeyList.size()) {
                foreignKeyColumn = foreignKeyList.get(foreignKeyComboIndex - startIndex);
            } else {
                startIndex += foreignKeyList.size();
            }
        }
        if (foreignKeyColumn == null) {
            foreignKeyColumn = this.candidateForeignKeyColumns.get(foreignKeyComboIndex - startIndex);
        }
        return foreignKeyColumn;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean close() {
        disposeTableEditor();
        return super.close();
    }

    private void disposeTableEditor() {
        for (final TableEditor tableEditor : this.tableEditorList) {
            tableEditor.getEditor().dispose();
            tableEditor.dispose();
        }
        this.tableEditorList.clear();
        this.editorReferredColumnsMap.clear();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<NormalColumn> getReferencedColumnList() {
        return referredColumnList;
    }

    public List<NormalColumn> getForeignKeyColumnList() {
        return foreignKeyColumnList;
    }

    public boolean isReferenceForPK() {
        return referenceForPK;
    }

    public ComplexUniqueKey getReferencedComplexUniqueKey() {
        return referencedComplexUniqueKey;
    }

    public NormalColumn getReferencedColumn() {
        return this.referencedColumn;
    }
}

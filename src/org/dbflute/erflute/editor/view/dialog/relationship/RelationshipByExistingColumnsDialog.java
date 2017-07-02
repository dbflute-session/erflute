package org.dbflute.erflute.editor.view.dialog.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.fkname.DefaultForeignKeyNameProvider;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.dbflute.erflute.editor.view.dialog.relationship.RelationshipDialog.ReferredColumnState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

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
    private final ERTable source; // foreign table e.g. MEMBER_STATUS
    private final TableView target; // local table e.g. MEMBER
    private final List<NormalColumn> candidateForeignKeyColumns;
    private final Map<NormalColumn, List<NormalColumn>> existingRootReferredToFkColumnsMap;
    private final Map<Relationship, Set<NormalColumn>> existingRelationshipToFkColumnsMap;

    // -----------------------------------------------------
    //                                             Component
    //                                             ---------
    private Text foreignKeyNameText; // #for_erflute
    private Combo referredColumnSelector;
    private ReferredColumnState referredColumnState;
    private Table foreignKeyColumnMapper; // avoid abstract word 'table' here
    private final List<TableEditor> mapperEditorList;
    private final Map<TableEditor, List<NormalColumn>> mapperEditorToReferredColumnsMap;
    private Button newColumnCheckBox; // #for_erflute

    // -----------------------------------------------------
    //                                         Dialog Result
    //                                         -------------
    private boolean resultReferenceForPK; // to create relationship
    private CompoundUniqueKey resultReferredComplexUniqueKey; // to create relationship
    private NormalColumn resultReferredSimpleUniqueColumn; // to create relationship
    private List<NormalColumn> selectedReferredColumnList; // added when select referred columns, may be plural when primary
    private final List<NormalColumn> selectedForeignKeyColumnList; // added when perform, may be plural when compound FK
    private Relationship newCreatedRelationship; // created when perform

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public RelationshipByExistingColumnsDialog(Shell parentShell, ERTable source, TableView target,
            List<NormalColumn> candidateForeignKeyColumns, Map<NormalColumn, List<NormalColumn>> existingRootReferredToFkColumnsMap,
            Map<Relationship, Set<NormalColumn>> existingRelationshipToFkColumnsMap) {
        super(parentShell, 2);
        this.source = source;
        this.target = target;
        this.selectedReferredColumnList = new ArrayList<>();
        this.candidateForeignKeyColumns = candidateForeignKeyColumns;
        this.existingRootReferredToFkColumnsMap = existingRootReferredToFkColumnsMap;
        this.existingRelationshipToFkColumnsMap = existingRelationshipToFkColumnsMap;

        this.mapperEditorList = new ArrayList<>();
        this.mapperEditorToReferredColumnsMap = new HashMap<>();

        this.selectedForeignKeyColumnList = new ArrayList<>();
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
        createForeignKeyNameText(composite);
        createGridLayout(composite);
        createReferredColumnSelector(composite);
        createForeignKeyColumnMapper(composite);
        newColumnCheckBox = CompositeFactory.createCheckbox(this, composite, "Create new column(s)");
    }

    // -----------------------------------------------------
    //                                      Foreign Key Name
    //                                      ----------------
    private void createForeignKeyNameText(Composite composite) {
        foreignKeyNameText = CompositeFactory.createText(this, composite, "Foreign Key Name", false);
        foreignKeyNameText.setText(provideDefaultForeignKeyName(source, target));
    }

    private String provideDefaultForeignKeyName(ERTable sourceTable, TableView targetTable) {
        // #hope use selected foreign columns when duplicate
        return new DefaultForeignKeyNameProvider().provide(sourceTable, targetTable);
    }

    // -----------------------------------------------------
    //                                           Grid Layout
    //                                           -----------
    private void createGridLayout(Composite composite) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
        label.setText("Select PK or UQ column and select FK column");
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
        referredColumnState = RelationshipDialog.setupReferredColumnComboData(referredColumnSelector, source);
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
                resetForeignKeyColumnMapper();
                validate();
            }
        });
        foreignKeyColumnMapper.addListener(SWT.MeasureItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                event.height = referredColumnSelector.getSize().y;
            }
        });
        newColumnCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                resetForeignKeyColumnMapper();
                validate(); // to allow OK when checked
            }
        });
    }

    private void resetForeignKeyColumnMapper() {
        foreignKeyColumnMapper.removeAll();
        disposeMapperEditor();
        prepareForeignKeyColumnMapperRows();
    }

    private void prepareForeignKeyColumnMapperRows() {
        try {
            final int referredColumnIndex = referredColumnSelector.getSelectionIndex();
            if (referredColumnIndex < referredColumnState.complexUniqueKeyStartIndex) {
                selectedReferredColumnList = source.getPrimaryKeys();
            } else if (referredColumnIndex < referredColumnState.columnStartIndex) {
                final CompoundUniqueKey complexUniqueKey =
                        source.getCompoundUniqueKeyList().get(referredColumnIndex - referredColumnState.complexUniqueKeyStartIndex);
                selectedReferredColumnList = complexUniqueKey.getColumnList();
            } else {
                final NormalColumn referencedColumn =
                        referredColumnState.candidateColumns.get(referredColumnIndex - referredColumnState.columnStartIndex);
                selectedReferredColumnList = new ArrayList<>();
                selectedReferredColumnList.add(referencedColumn);
            }
            for (final NormalColumn referredColumn : selectedReferredColumnList) {
                addColumnToMapperItem(referredColumn);
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    private void addColumnToMapperItem(NormalColumn referredColumn) {
        final TableItem tableItem = new TableItem(foreignKeyColumnMapper, SWT.NONE);
        tableItem.setText(0, Format.null2blank(referredColumn.getLogicalName()));
        final List<NormalColumn> foreignKeyColumnList = existingRootReferredToFkColumnsMap.get(referredColumn.getFirstRootReferredColumn());
        final TableEditor mapperEditor = new TableEditor(foreignKeyColumnMapper);
        mapperEditor.grabHorizontal = true;
        final Combo foreignKeyColumnSelector = createForeignKeyColumnSelector(foreignKeyColumnList);
        mapperEditor.setEditor(foreignKeyColumnSelector, tableItem, 1);
        mapperEditorList.add(mapperEditor);
        mapperEditorToReferredColumnsMap.put(mapperEditor, foreignKeyColumnList);
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
        final String foreignKeyName = foreignKeyNameText.getText().trim();
        if (Srl.is_Null_or_TrimmedEmpty(foreignKeyName)) {
            return "Input foreign key name e.g. FK_XXX";
        }
        if (source.getDiagramSettings().isValidatePhysicalName() && !Check.isAlphabet(foreignKeyName)) {
            return "error.foreign.key.name.not.alphabet";
        }
        final ERDiagram diagram = target.getDiagram();
        final List<TableView> tableViewList = diagram.getDiagramContents().getDiagramWalkers().getTableViewList();
        for (final TableView tableView : tableViewList) {
            final List<Relationship> relationshipList = tableView.getIncomingRelationshipList();
            for (final Relationship currentRel : relationshipList) {
                final String currentForeignKeyName = currentRel.getForeignKeyName();
                if (currentForeignKeyName != null) {
                    if (currentForeignKeyName.equalsIgnoreCase(foreignKeyName)) {
                        return "error.foreign.key.name.already.exists";
                    }
                }
            }
        }
        if (isCreateNewColumn()) {
            for (final NormalColumn referredColumn : selectedReferredColumnList) {
                final NormalColumn existingColumn = target.findColumnByPhysicalName(referredColumn.getPhysicalName());
                if (existingColumn != null) {
                    return "The same name column already exists, cannot create new column";
                }
            }
            return null;
        } else {
            // #hope check type difference by jflute
            final Set<NormalColumn> selectedColumns = new HashSet<>();
            for (final TableEditor tableEditor : mapperEditorList) {
                final Combo foreignKeySelector = (Combo) tableEditor.getEditor();
                final int selectionIndex = foreignKeySelector.getSelectionIndex();
                if (selectionIndex == 0) {
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
    }

    private boolean existsForeignKeyColumnSet(Set<NormalColumn> columnSet) {
        boolean exists = false;
        for (final Set<NormalColumn> foreignKeyColumnSet : existingRelationshipToFkColumnsMap.values()) {
            if (foreignKeyColumnSet.size() == columnSet.size()) {
                exists = true;
                for (final NormalColumn normalColumn : columnSet) {
                    if (!foreignKeyColumnSet.contains(normalColumn)) {
                        exists = false;
                        continue;
                    }
                }
                break;
            }
        }
        return exists;
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() {
        setupReferredResult(); // for new-created relationship
        setupRelationship(); // using referred result
        showPerformOK();
    }

    private void setupReferredResult() {
        final int referredSelectionIndex = referredColumnSelector.getSelectionIndex();
        final int complexUniqueKeyStartIndex = referredColumnState.complexUniqueKeyStartIndex;
        if (referredSelectionIndex < complexUniqueKeyStartIndex) { // means selecting PK
            resultReferenceForPK = true;
        } else {
            final int simpleUniqueyKeyStartIndex = referredColumnState.columnStartIndex;
            if (referredSelectionIndex < simpleUniqueyKeyStartIndex) { // means selecting complex unique key
                final List<CompoundUniqueKey> complexUniqueKeyList = source.getCompoundUniqueKeyList();
                resultReferredComplexUniqueKey = complexUniqueKeyList.get(referredSelectionIndex - complexUniqueKeyStartIndex);
            } else { // means selecting simple unique key
                resultReferredSimpleUniqueColumn =
                        referredColumnState.candidateColumns.get(referredSelectionIndex - simpleUniqueyKeyStartIndex);
            }
        }
    }

    private void setupRelationship() {
        newCreatedRelationship = createRelationship();
        newCreatedRelationship.setForeignKeyName(foreignKeyNameText.getText().trim());
        if (isCreateNewColumn()) {
            // TODO ymd コマンド外の操作でカラムを追加しているため、undoしてもカラムが削除されない。コマンド化する。
            for (final NormalColumn referredColumn : selectedReferredColumnList) {
                final NormalColumn newColumn = createForeignKeyColumn(referredColumn, newCreatedRelationship, resultReferenceForPK);
                adjustNewForeignKeyColumn(newColumn);
                selectedForeignKeyColumnList.add(newColumn);
                target.addColumn(newColumn);
            }
        } else {
            for (final TableEditor mapperEditor : mapperEditorList) {
                selectedForeignKeyColumnList.add(findSelectedColumn(mapperEditor));
            }
        }
    }

    private Relationship createRelationship() {
        return new Relationship(resultReferenceForPK, resultReferredComplexUniqueKey, resultReferredSimpleUniqueColumn);
    }

    private NormalColumn createForeignKeyColumn(NormalColumn referredColumn, Relationship relationship, boolean referenceForPK) {
        return new NormalColumn(referredColumn, referredColumn, relationship, referenceForPK);
    }

    private void adjustNewForeignKeyColumn(NormalColumn newColumn) {
        newColumn.setPrimaryKey(false);
    }

    private void showPerformOK() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Performed the relationship dialog:");
        final String ln = "\n";
        sb.append(ln).append("[Relationship]");
        sb.append(ln).append(" tables: from ").append(target.getPhysicalName()).append(" to ").append(source.getPhysicalName());
        sb.append(ln).append(" candidateForeignKeyColumns: ").append(candidateForeignKeyColumns);
        sb.append(ln).append(" existingRootReferredToFkColumnsMap: ").append(existingRootReferredToFkColumnsMap);
        sb.append(ln).append(" existingRelationshipToFkColumnsMap: ").append(existingRelationshipToFkColumnsMap);
        sb.append(ln).append(" selectedReferredColumnList: ").append(selectedReferredColumnList);
        sb.append(ln).append(" selectedForeignKeyColumnList: ").append(selectedForeignKeyColumnList);
        sb.append(ln).append(" resultReferenceForPK: ").append(resultReferenceForPK);
        sb.append(ln).append(" resultReferredComplexUniqueKey: ").append(resultReferredComplexUniqueKey);
        sb.append(ln).append(" resultReferredSimpleUniqueColumn: ").append(resultReferredSimpleUniqueColumn);
        Activator.debug(this, "performOK()", sb.toString());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private boolean isCreateNewColumn() {
        return newColumnCheckBox.getSelection();
    }

    private NormalColumn findSelectedColumn(TableEditor tableEditor) { // not null
        final Combo foreignKeySelector = (Combo) tableEditor.getEditor();
        final int selectionIndex = foreignKeySelector.getSelectionIndex();
        int startIndex = 1;
        NormalColumn foreignKeyColumn = null;
        final List<NormalColumn> foreignKeyList = mapperEditorToReferredColumnsMap.get(tableEditor);
        if (foreignKeyList != null) {
            if (selectionIndex <= foreignKeyList.size()) {
                foreignKeyColumn = foreignKeyList.get(selectionIndex - startIndex);
            } else {
                startIndex += foreignKeyList.size();
            }
        }
        if (foreignKeyColumn == null) {
            foreignKeyColumn = candidateForeignKeyColumns.get(selectionIndex - startIndex);
        }
        return foreignKeyColumn;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean close() {
        disposeMapperEditor();
        return super.close();
    }

    private void disposeMapperEditor() {
        for (final TableEditor tableEditor : mapperEditorList) {
            tableEditor.getEditor().dispose();
            tableEditor.dispose();
        }
        mapperEditorList.clear();
        mapperEditorToReferredColumnsMap.clear();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                         Dialog Result
    //                                         -------------
    public List<NormalColumn> getSelectedReferencedColumnList() {
        return selectedReferredColumnList;
    }

    public List<NormalColumn> getSelectedForeignKeyColumnList() {
        return selectedForeignKeyColumnList;
    }

    public Relationship getNewCreatedRelationship() {
        return newCreatedRelationship;
    }
}

package org.dbflute.erflute.editor.view.dialog.table.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class IndexDialog extends AbstractDialog {

    private Text indexNameText;
    private String previousIndexName;
    private Button addButton;
    private Button removeButton;
    private Button upButton;
    private Button downButton;
    private org.eclipse.swt.widgets.List allColumnList;
    private Table indexColumnList;
    private final List<NormalColumn> selectedColumns;
    private final List<NormalColumn> allColumns;
    private final ERTable table;
    private Combo typeCombo;
    private Text tableText;
    private Text descriptionText;
    private Button uniqueCheckBox;
    private Button fullTextCheckBox;
    private boolean add;
    private final ERIndex targetIndex;
    private ERIndex resultIndex;
    private final Map<ERColumn, Button> descCheckBoxMap = new HashMap<ERColumn, Button>();
    private final Map<ERColumn, TableEditor> columnCheckMap = new HashMap<ERColumn, TableEditor>();

    public IndexDialog(Shell parentShell, ERIndex targetIndex, ERTable table) {
        super(parentShell);
        this.targetIndex = targetIndex;
        this.table = table;
        this.allColumns = table.getExpandedColumns();
        this.selectedColumns = new ArrayList<NormalColumn>();
    }

    @Override
    protected void initialize(Composite composite) {
        this.createComposite(composite);
        this.createComposite1(composite);
        this.initializeAllList();
        this.setListener();
        this.indexNameText.setFocus();
        this.previousIndexName = indexNameText.getText().trim();
    }

    /**
     * This method initializes composite
     */
    private void createComposite(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(gridData);
        composite.setLayout(gridLayout);

        this.createCheckComposite(composite);

        this.tableText = CompositeFactory.createText(this, composite, "label.table.name", 1, -1, SWT.READ_ONLY | SWT.BORDER, false);
        this.indexNameText = CompositeFactory.createText(this, composite, "label.index.name", false);
        this.typeCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.index.type");

        this.initTypeCombo();
        this.descriptionText = CompositeFactory.createTextArea(this, composite, "label.description", -1, 100, 1, true);
    }

    private void initTypeCombo() {
        final java.util.List<String> indexTypeList = DBManagerFactory.getDBManager(this.table.getDiagram()).getIndexTypeList(this.table);
        this.typeCombo.add("");
        for (final String indexType : indexTypeList) {
            this.typeCombo.add(indexType);
        }
    }

    /**
     * This method initializes composite
     */
    private void createCheckComposite(Composite composite) {
        final GridData gridData2 = new GridData();
        gridData2.horizontalSpan = 2;
        gridData2.heightHint = 30;
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;

        final Composite checkComposite = new Composite(composite, SWT.NONE);
        checkComposite.setLayoutData(gridData2);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        checkComposite.setLayout(gridLayout);

        this.uniqueCheckBox = new Button(checkComposite, SWT.CHECK);
        this.uniqueCheckBox.setText(DisplayMessages.getMessage("label.index.unique"));

        final DBManager dbManager = DBManagerFactory.getDBManager(this.table.getDiagram());

        if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
            this.fullTextCheckBox = new Button(checkComposite, SWT.CHECK);
            this.fullTextCheckBox.setText(DisplayMessages.getMessage("label.index.fulltext"));
        }
    }

    /**
     * This method initializes composite1
     * 
     */
    private void createComposite1(Composite parent) {
        final GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 3;
        gridLayout2.verticalSpacing = 20;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Composite composite = new Composite(parent, SWT.NONE);
        createGroup(composite);
        composite.setLayout(gridLayout2);
        composite.setLayoutData(gridData);

        this.addButton = CompositeFactory.createAddButton(composite);
        this.createGroup1(composite);
        this.removeButton = CompositeFactory.createRemoveButton(composite);
    }

    /**
     * This method initializes group
     * 
     */
    private void createGroup(Composite composite) {
        final GridLayout gridLayout4 = new GridLayout();
        gridLayout4.verticalSpacing = 5;
        gridLayout4.marginHeight = 10;
        final GridData gridData6 = new GridData();
        gridData6.widthHint = 150;
        gridData6.heightHint = 150;
        final GridData gridData3 = new GridData();
        gridData3.verticalSpan = 2;
        gridData3.horizontalAlignment = GridData.BEGINNING;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData3);
        group.setLayout(gridLayout4);
        group.setText(DisplayMessages.getMessage("label.all.column.list"));
        allColumnList = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
        allColumnList.setLayoutData(gridData6);
    }

    /**
     * This method initializes group1
     * 
     */
    private void createGroup1(Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginHeight = 10;

        final GridData upButtonGridData = new GridData();
        upButtonGridData.grabExcessHorizontalSpace = false;
        upButtonGridData.verticalAlignment = GridData.END;
        upButtonGridData.grabExcessVerticalSpace = true;
        upButtonGridData.widthHint = DesignResources.BUTTON_WIDTH;

        final GridData downButtonGridData = new GridData();
        downButtonGridData.grabExcessVerticalSpace = true;
        downButtonGridData.verticalAlignment = GridData.BEGINNING;
        downButtonGridData.widthHint = DesignResources.BUTTON_WIDTH;

        final GridData gridData4 = new GridData();
        gridData4.verticalSpan = 2;

        final Group group = new Group(composite, SWT.NONE);
        group.setText(DisplayMessages.getMessage("label.index.column.list"));
        group.setLayout(gridLayout);
        group.setLayoutData(gridData4);

        this.initializeIndexColumnList(group);

        // indexColumnList = new List(group, SWT.BORDER | SWT.V_SCROLL);
        // indexColumnList.setLayoutData(gridData5);

        this.upButton = new Button(group, SWT.NONE);
        this.upButton.setText(DisplayMessages.getMessage("label.up.arrow"));
        this.upButton.setLayoutData(upButtonGridData);

        this.downButton = new Button(group, SWT.NONE);
        this.downButton.setText(DisplayMessages.getMessage("label.down.arrow"));
        this.downButton.setLayoutData(downButtonGridData);
    }

    private void initializeAllList() {
        for (final NormalColumn column : this.allColumns) {
            this.allColumnList.add(column.getPhysicalName());
        }
    }

    private void initializeIndexColumnList(Composite parent) {
        final GridData gridData = new GridData();
        gridData.heightHint = 150;
        gridData.verticalSpan = 2;

        indexColumnList = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        indexColumnList.setHeaderVisible(true);
        indexColumnList.setLayoutData(gridData);
        indexColumnList.setLinesVisible(false);

        final TableColumn tableColumn = new TableColumn(indexColumnList, SWT.CENTER);
        tableColumn.setWidth(150);
        tableColumn.setText(DisplayMessages.getMessage("label.column.name"));

        if (DBManagerFactory.getDBManager(this.table.getDiagram()).isSupported(DBManager.SUPPORT_DESC_INDEX)) {
            final TableColumn tableColumn1 = new TableColumn(indexColumnList, SWT.CENTER);
            tableColumn1.setWidth(50);
            tableColumn1.setText(DisplayMessages.getMessage("label.order.desc"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        if (this.targetIndex != null && !add) {
            this.tableText.setText(Format.null2blank(this.targetIndex.getTable().getPhysicalName()));

            this.indexNameText.setText(this.targetIndex.getName());

            this.descriptionText.setText(Format.null2blank(this.targetIndex.getDescription()));

            if (!Check.isEmpty(this.targetIndex.getType())) {
                boolean selected = false;

                for (int i = 0; i < this.typeCombo.getItemCount(); i++) {
                    if (this.typeCombo.getItem(i).equals(this.targetIndex.getType())) {
                        this.typeCombo.select(i);
                        selected = true;
                        break;
                    }
                }

                if (!selected) {
                    typeCombo.setText(this.targetIndex.getType());
                }
            }

            final java.util.List<Boolean> descs = this.targetIndex.getDescs();
            int i = 0;

            for (final NormalColumn column : this.targetIndex.getColumns()) {
                Boolean desc = Boolean.FALSE;

                if (descs.size() > i && descs.get(i) != null) {
                    desc = descs.get(i);
                }

                this.addIndexColumn(column, desc);
                i++;
            }

            this.uniqueCheckBox.setSelection(!this.targetIndex.isNonUnique());

            final DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());
            if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
                this.fullTextCheckBox.setSelection(this.targetIndex.isFullText());
            }
        }
    }

    private void addIndexColumn(NormalColumn column, Boolean desc) {
        final TableItem tableItem = new TableItem(this.indexColumnList, SWT.NONE);

        tableItem.setText(0, column.getPhysicalName());

        this.setTableEditor(column, tableItem, desc);

        this.selectedColumns.add(column);

    }

    private void setTableEditor(final NormalColumn normalColumn, TableItem tableItem, Boolean desc) {
        final Button descCheckButton = new Button(this.indexColumnList, SWT.CHECK);
        descCheckButton.pack();

        if (DBManagerFactory.getDBManager(this.table.getDiagram()).isSupported(DBManager.SUPPORT_DESC_INDEX)) {

            final TableEditor editor = new TableEditor(this.indexColumnList);

            editor.minimumWidth = descCheckButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(descCheckButton, tableItem, 1);

            this.columnCheckMap.put(normalColumn, editor);
        }

        this.descCheckBoxMap.put(normalColumn, descCheckButton);
        descCheckButton.setSelection(desc.booleanValue());
    }

    private void setListener() {
        this.upButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = indexColumnList.getSelectionIndex();

                if (index == -1 || index == 0) {
                    return;
                }

                changeColumn(index - 1, index);
                indexColumnList.setSelection(index - 1);
            }

        });

        this.downButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = indexColumnList.getSelectionIndex();

                if (index == -1 || index == indexColumnList.getItemCount() - 1) {
                    return;
                }

                changeColumn(index, index + 1);
                indexColumnList.setSelection(index + 1);
            }

        });

        this.addButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = allColumnList.getSelectionIndex();

                if (index == -1) {
                    return;
                }

                final NormalColumn column = allColumns.get(index);
                if (selectedColumns.contains(column)) {
                    return;
                }

                addIndexColumn(column, Boolean.FALSE);

                validate();
            }

        });

        this.removeButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = indexColumnList.getSelectionIndex();

                if (index == -1) {
                    return;
                }

                indexColumnList.remove(index);
                NormalColumn column = selectedColumns.remove(index);
                descCheckBoxMap.remove(column);

                disposeCheckBox(column);

                for (int i = index; i < indexColumnList.getItemCount(); i++) {
                    column = selectedColumns.get(i);

                    final Button descCheckBox = descCheckBoxMap.get(column);
                    final boolean desc = descCheckBox.getSelection();
                    disposeCheckBox(column);

                    final TableItem tableItem = indexColumnList.getItem(i);
                    setTableEditor(column, tableItem, desc);
                }

                validate();
            }

        });

        this.indexNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });
    }

    public void changeColumn(int index1, int index2) {
        final NormalColumn column1 = selectedColumns.remove(index1);
        NormalColumn column2 = null;

        if (index1 < index2) {
            column2 = selectedColumns.remove(index2 - 1);
            selectedColumns.add(index1, column2);
            selectedColumns.add(index2, column1);

        } else if (index1 > index2) {
            column2 = selectedColumns.remove(index2);
            selectedColumns.add(index1 - 1, column2);
            selectedColumns.add(index2, column1);
        }

        final boolean desc1 = this.descCheckBoxMap.get(column1).getSelection();
        final boolean desc2 = this.descCheckBoxMap.get(column2).getSelection();

        final TableItem[] tableItems = indexColumnList.getItems();

        this.column2TableItem(column1, desc1, tableItems[index2]);
        this.column2TableItem(column2, desc2, tableItems[index1]);

    }

    private void column2TableItem(NormalColumn column, boolean desc, TableItem tableItem) {
        this.disposeCheckBox(column);
        tableItem.setText(0, column.getPhysicalName());
        this.setTableEditor(column, tableItem, new Boolean(desc));
    }

    private void disposeCheckBox(ERColumn column) {
        final TableEditor oldEditor = this.columnCheckMap.get(column);
        if (oldEditor != null) {
            if (oldEditor.getEditor() != null) {
                oldEditor.getEditor().dispose();
            }
            oldEditor.dispose();
        }
        this.columnCheckMap.remove(column);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.index";
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public ERIndex getResultIndex() {
        return this.resultIndex;
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        final String indexName = indexNameText.getText().trim();
        if (indexName.isEmpty()) {
            return "error.index.name.empty";
        }
        if (!Check.isAlphabet(indexName)) {
            return "error.index.name.not.alphabet";
        }
        final List<ERTable> tableList = table.getDiagram().getDiagramContents().getDiagramWalkers().getTableSet().getList();
        for (final ERTable table : tableList) {
            final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
            for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
                final String currentUniqueKeyName = complexUniqueKey.getUniqueKeyName();
                if (currentUniqueKeyName != null) {
                    if (!currentUniqueKeyName.equalsIgnoreCase(previousIndexName)) {
                        if (currentUniqueKeyName.equalsIgnoreCase(indexName)) {
                            return "error.index.name.already.exists";
                        }
                    }
                }
            }
        }
        if (indexColumnList.getItemCount() == 0) {
            return "error.index.column.empty";
        }
        final DBManager dbManager = DBManagerFactory.getDBManager(this.table.getDiagram());
        if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
            if (fullTextCheckBox.getSelection()) {
                for (final NormalColumn indexColumn : selectedColumns) {
                    if (!indexColumn.isFullTextIndexable()) {
                        return "error.index.fulltext.impossible";
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
        final String text = indexNameText.getText();
        this.resultIndex = new ERIndex(table, text, !this.uniqueCheckBox.getSelection(), this.typeCombo.getText(), null);
        this.resultIndex.setDescription(this.descriptionText.getText().trim());
        for (final NormalColumn selectedColumn : selectedColumns) {
            final Boolean desc = Boolean.valueOf(this.descCheckBoxMap.get(selectedColumn).getSelection());
            this.resultIndex.addColumn(selectedColumn, desc);
        }
        final DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());
        if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
            this.resultIndex.setFullText(this.fullTextCheckBox.getSelection());
        }
    }
}

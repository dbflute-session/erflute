package org.dbflute.erflute.editor.view.dialog.search;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.ReplaceCommand;
import org.dbflute.erflute.editor.controller.editpart.outline.ERDiagramOutlineEditPartFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.search.ReplaceManager;
import org.dbflute.erflute.editor.model.search.SearchManager;
import org.dbflute.erflute.editor.model.search.SearchResult;
import org.dbflute.erflute.editor.model.search.SearchResultRow;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.views.contentoutline.ContentOutline;

public class SearchDialog extends Dialog {

    public static final int REPLACE_ID = 100;
    public static final int SEARCH_ALL_ID = 101;
    public static final int SEARCH_NEXT_ID = 102;

    private Button allCheckBox;

    // 単語
    private Button wordCheckBox;
    private Button physicalWordNameCheckBox;
    private Button logicalWordNameCheckBox;
    private Button wordTypeCheckBox;
    private Button wordLengthCheckBox;
    private Button wordDecimalCheckBox;
    private Button wordDescriptionCheckBox;

    // テーブル
    private Button tableCheckBox;
    private Button physicalTableNameCheckBox;
    private Button logicalTableNameCheckBox;
    private Button physicalColumnNameCheckBox;
    private Button logicalColumnNameCheckBox;
    private Button columnTypeCheckBox;
    private Button columnLengthCheckBox;
    private Button columnDecimalCheckBox;
    private Button columnDefaultValueCheckBox;
    private Button columnDescriptionCheckBox;
    private Button columnGroupNameCheckBox;

    // グループ
    private Button groupCheckBox;
    private Button groupNameCheckBox;
    private Button physicalGroupColumnNameCheckBox;
    private Button logicalGroupColumnNameCheckBox;
    private Button groupColumnTypeCheckBox;
    private Button groupColumnLengthCheckBox;
    private Button groupColumnDecimalCheckBox;
    private Button groupColumnDefaultValueCheckBox;
    private Button groupColumnDescriptionCheckBox;

    // その他
    private Button modelPropertiesCheckBox;
    private Button indexCheckBox;
    private Button relationCheckBox;
    private Button noteCheckBox;

    // 検索・置換語
    private Combo keywordCombo;
    private Combo replaceCombo;

    // 検索結果
    private Table resultTable;
    private final GraphicalViewer viewer;
    private final ERDiagram diagram;
    private final SearchManager searchManager;
    private SearchResult searchResult;
    private boolean all;
    private TabFolder tabFolder;

    public SearchDialog(Shell parentShell, GraphicalViewer viewer, MainDiagramEditor erDiagramEditor, ERDiagram diagram) {
        super(parentShell);

        setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
        setBlockOnOpen(false);

        this.viewer = viewer;
        this.diagram = diagram;

        this.searchManager = new SearchManager(diagram);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(DisplayMessages.getMessage("dialog.title.search"));

        Composite composite = null;
        composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout());

        initialize(composite);

        return composite;
    }

    private void initialize(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.verticalSpacing = 15;

        createKeywordCombo(parent);

        createReplaceCombo(parent);

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;

        this.tabFolder = new TabFolder(parent, SWT.NONE);
        tabFolder.setLayoutData(gridData);

        createRegionGroup(tabFolder);
        createResultGroup(tabFolder);

        parent.setLayout(gridLayout);

        selectAllCheckBox(true);
    }

    private void createRegionGroup(TabFolder tabFolder) {
        final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(DisplayMessages.getMessage("label.search.range"));

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        final Composite group = new Composite(tabFolder, SWT.NONE);
        group.setLayout(gridLayout);

        allCheckBox = new Button(group, SWT.CHECK);
        allCheckBox.setText(DisplayMessages.getMessage("label.search.range.all"));

        final GridData allCheckBoxGridData = new GridData();
        allCheckBoxGridData.horizontalAlignment = GridData.FILL;
        allCheckBoxGridData.horizontalSpan = 3;
        allCheckBoxGridData.grabExcessHorizontalSpace = true;

        allCheckBox.setLayoutData(allCheckBoxGridData);
        allCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectAllCheckBox(allCheckBox.getSelection());
            }
        });

        wordCheckBox = new Button(group, SWT.CHECK);
        wordCheckBox.setText(DisplayMessages.getMessage("label.search.range.word"));
        wordCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectWordCheckBox(wordCheckBox.getSelection());
            }
        });

        createWordCheckboxGroup(group);

        tableCheckBox = new Button(group, SWT.CHECK);
        tableCheckBox.setText(DisplayMessages.getMessage("label.search.range.table"));
        tableCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectTableCheckBox(tableCheckBox.getSelection());
            }
        });

        createTableCheckboxGroup(group);

        groupCheckBox = new Button(group, SWT.CHECK);
        groupCheckBox.setText(DisplayMessages.getMessage("label.search.range.group"));
        groupCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectGroupCheckBox(groupCheckBox.getSelection());
            }
        });

        createGroupCheckboxGroup(group);

        modelPropertiesCheckBox = new Button(group, SWT.CHECK);
        modelPropertiesCheckBox.setText(DisplayMessages.getMessage("label.search.range.model.property"));
        indexCheckBox = new Button(group, SWT.CHECK);
        indexCheckBox.setText(DisplayMessages.getMessage("label.search.range.index"));
        relationCheckBox = new Button(group, SWT.CHECK);
        relationCheckBox.setText(DisplayMessages.getMessage("label.search.range.relation"));
        noteCheckBox = new Button(group, SWT.CHECK);
        noteCheckBox.setText(DisplayMessages.getMessage("label.search.range.note"));

        tabItem.setControl(group);
    }

    private void createWordCheckboxGroup(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;

        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        physicalWordNameCheckBox = new Button(group, SWT.CHECK);
        physicalWordNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.28"));

        logicalWordNameCheckBox = new Button(group, SWT.CHECK);
        logicalWordNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.29"));

        wordTypeCheckBox = new Button(group, SWT.CHECK);
        wordTypeCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.30"));

        wordLengthCheckBox = new Button(group, SWT.CHECK);
        wordLengthCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.31"));

        wordDecimalCheckBox = new Button(group, SWT.CHECK);
        wordDecimalCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.32"));

        wordDescriptionCheckBox = new Button(group, SWT.CHECK);
        wordDescriptionCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.33"));
    }

    private void createTableCheckboxGroup(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;

        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        physicalTableNameCheckBox = new Button(group, SWT.CHECK);
        physicalTableNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.11"));

        logicalTableNameCheckBox = new Button(group, SWT.CHECK);
        logicalTableNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.12"));

        columnGroupNameCheckBox = new Button(group, SWT.CHECK);
        columnGroupNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.20"));

        new Label(group, SWT.NONE);

        physicalColumnNameCheckBox = new Button(group, SWT.CHECK);
        physicalColumnNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.13"));

        logicalColumnNameCheckBox = new Button(group, SWT.CHECK);
        logicalColumnNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.14"));

        columnTypeCheckBox = new Button(group, SWT.CHECK);
        columnTypeCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.15"));

        columnLengthCheckBox = new Button(group, SWT.CHECK);
        columnLengthCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.16"));

        columnDecimalCheckBox = new Button(group, SWT.CHECK);
        columnDecimalCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.17"));

        columnDefaultValueCheckBox = new Button(group, SWT.CHECK);
        columnDefaultValueCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.18"));

        columnDescriptionCheckBox = new Button(group, SWT.CHECK);
        columnDescriptionCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.19"));
    }

    private void createGroupCheckboxGroup(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;

        final Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);

        groupNameCheckBox = new Button(group, SWT.CHECK);
        groupNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.20"));

        new Label(group, SWT.NONE);
        new Label(group, SWT.NONE);
        new Label(group, SWT.NONE);

        physicalGroupColumnNameCheckBox = new Button(group, SWT.CHECK);
        physicalGroupColumnNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.13"));

        logicalGroupColumnNameCheckBox = new Button(group, SWT.CHECK);
        logicalGroupColumnNameCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.14"));

        groupColumnTypeCheckBox = new Button(group, SWT.CHECK);
        groupColumnTypeCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.15"));

        groupColumnLengthCheckBox = new Button(group, SWT.CHECK);
        groupColumnLengthCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.16"));

        groupColumnDecimalCheckBox = new Button(group, SWT.CHECK);
        groupColumnDecimalCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.17"));

        groupColumnDefaultValueCheckBox = new Button(group, SWT.CHECK);
        groupColumnDefaultValueCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.18"));

        groupColumnDescriptionCheckBox = new Button(group, SWT.CHECK);
        groupColumnDescriptionCheckBox.setText(DisplayMessages.getMessage("search.result.row.name.19"));
    }

    private void selectAllCheckBox(boolean checked) {
        allCheckBox.setSelection(checked);

        selectWordCheckBox(checked);
        wordCheckBox.setEnabled(!checked);

        selectTableCheckBox(checked);
        tableCheckBox.setEnabled(!checked);

        modelPropertiesCheckBox.setSelection(checked);
        modelPropertiesCheckBox.setEnabled(!checked);
        indexCheckBox.setSelection(checked);
        indexCheckBox.setEnabled(!checked);
        relationCheckBox.setSelection(checked);
        relationCheckBox.setEnabled(!checked);
        noteCheckBox.setSelection(checked);
        noteCheckBox.setEnabled(!checked);

        selectGroupCheckBox(checked);
        groupCheckBox.setEnabled(!checked);
    }

    private void selectWordCheckBox(boolean checked) {
        wordCheckBox.setSelection(checked);
        physicalWordNameCheckBox.setSelection(checked);
        logicalWordNameCheckBox.setSelection(checked);
        wordTypeCheckBox.setSelection(checked);
        wordLengthCheckBox.setSelection(checked);
        wordDecimalCheckBox.setSelection(checked);
        wordDescriptionCheckBox.setSelection(checked);

        physicalWordNameCheckBox.setEnabled(!checked);
        logicalWordNameCheckBox.setEnabled(!checked);
        wordTypeCheckBox.setEnabled(!checked);
        wordLengthCheckBox.setEnabled(!checked);
        wordDecimalCheckBox.setEnabled(!checked);
        wordDescriptionCheckBox.setEnabled(!checked);
    }

    private void selectTableCheckBox(boolean checked) {
        tableCheckBox.setSelection(checked);
        physicalTableNameCheckBox.setSelection(checked);
        logicalTableNameCheckBox.setSelection(checked);
        physicalColumnNameCheckBox.setSelection(checked);
        logicalColumnNameCheckBox.setSelection(checked);
        columnTypeCheckBox.setSelection(checked);
        columnLengthCheckBox.setSelection(checked);
        columnDecimalCheckBox.setSelection(checked);
        columnDefaultValueCheckBox.setSelection(checked);
        columnDescriptionCheckBox.setSelection(checked);
        columnGroupNameCheckBox.setSelection(checked);

        physicalTableNameCheckBox.setEnabled(!checked);
        logicalTableNameCheckBox.setEnabled(!checked);
        physicalColumnNameCheckBox.setEnabled(!checked);
        logicalColumnNameCheckBox.setEnabled(!checked);
        columnTypeCheckBox.setEnabled(!checked);
        columnLengthCheckBox.setEnabled(!checked);
        columnDecimalCheckBox.setEnabled(!checked);
        columnDefaultValueCheckBox.setEnabled(!checked);
        columnDescriptionCheckBox.setEnabled(!checked);
        columnGroupNameCheckBox.setEnabled(!checked);
    }

    private void selectGroupCheckBox(boolean checked) {
        groupCheckBox.setSelection(checked);
        groupNameCheckBox.setSelection(checked);
        physicalGroupColumnNameCheckBox.setSelection(checked);
        logicalGroupColumnNameCheckBox.setSelection(checked);
        groupColumnTypeCheckBox.setSelection(checked);
        groupColumnLengthCheckBox.setSelection(checked);
        groupColumnDecimalCheckBox.setSelection(checked);
        groupColumnDefaultValueCheckBox.setSelection(checked);
        groupColumnDescriptionCheckBox.setSelection(checked);

        groupNameCheckBox.setEnabled(!checked);
        physicalGroupColumnNameCheckBox.setEnabled(!checked);
        logicalGroupColumnNameCheckBox.setEnabled(!checked);
        groupColumnTypeCheckBox.setEnabled(!checked);
        groupColumnLengthCheckBox.setEnabled(!checked);
        groupColumnDecimalCheckBox.setEnabled(!checked);
        groupColumnDefaultValueCheckBox.setEnabled(!checked);
        groupColumnDescriptionCheckBox.setEnabled(!checked);
    }

    private void createKeywordCombo(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(DisplayMessages.getMessage("label.search.keyword"));

        final GridData fillerGridData = new GridData();
        fillerGridData.widthHint = 10;

        label = new Label(parent, SWT.NONE);
        label.setText("");
        label.setLayoutData(fillerGridData);

        final GridData gridData = new GridData();
        gridData.widthHint = 200;

        keywordCombo = new Combo(parent, SWT.NONE);
        keywordCombo.setLayoutData(gridData);
        keywordCombo.setVisibleItemCount(20);

        initKeywordCombo();
    }

    private void createReplaceCombo(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(DisplayMessages.getMessage("label.search.replace.word"));

        final GridData fillerGridData = new GridData();
        fillerGridData.widthHint = 10;

        label = new Label(parent, SWT.NONE);
        label.setText("");
        label.setLayoutData(fillerGridData);

        final GridData gridData = new GridData();
        gridData.widthHint = 200;

        replaceCombo = new Combo(parent, SWT.NONE);
        replaceCombo.setLayoutData(gridData);
        replaceCombo.setVisibleItemCount(20);

        initReplaceWordCombo();
    }

    private void initKeywordCombo() {
        keywordCombo.removeAll();

        for (final String str : SearchManager.getKeywordList()) {
            keywordCombo.add(str);
        }
    }

    private void initReplaceWordCombo() {
        replaceCombo.removeAll();

        for (final String str : ReplaceManager.getReplaceWordList()) {
            replaceCombo.add(str);
        }
    }

    private SearchResultRow searchResultRow;

    private void createResultGroup(TabFolder tabFolder) {
        final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(DisplayMessages.getMessage("label.search.result"));

        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;

        final Composite resultGroup = new Composite(tabFolder, SWT.NONE);
        resultGroup.setLayout(layout);

        final GridData gridData = new GridData();
        gridData.widthHint = -1;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = SWT.FILL;
        gridData.horizontalSpan = 4;

        resultTable = new Table(resultGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        resultTable.setHeaderVisible(true);
        resultTable.setLayoutData(gridData);
        resultTable.setLinesVisible(true);
        resultTable.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = resultTable.getSelectionIndex();

                searchResultRow = searchResult.getRows().get(index);
                final Object object = searchResultRow.getTargetNode();

                if (object != null) {
                    focus(object);
                }
            }
        });

        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (searchResultRow != null) {
                    close();

                    final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                    ERTable table = null;
                    if (searchResultRow.getTargetNode() instanceof ERTable) {
                        table = (ERTable) searchResultRow.getTargetNode();
                    }
                    if (table != null) {
                        try {
                            final IViewPart view = page.showView("org.eclipse.ui.views.ContentOutline");
                            final ContentOutline outlineView = (ContentOutline) view;
                            outlineView.getSelection();

                            final EditPart editpart = ERDiagramOutlineEditPartFactory.tableParts.get(table.getLogicalName());
                            final ISelection selection = new StructuredSelection(editpart);
                            outlineView.setSelection(selection);

                            final IContributedContentsView v =
                                    (IContributedContentsView) outlineView.getAdapter(IContributedContentsView.class);
                            v.getContributingPart();
                        } catch (final PartInitException e1) {
                            e1.printStackTrace();
                        }

                        //					for (ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

                        //						ERTable copyTable = table.copyData();
                        //
                        //						TableDialog dialog = new TableDialog(PlatformUI.getWorkbench()
                        //								.getActiveWorkbenchWindow().getShell(), viewer,
                        //								copyTable, diagram.getDiagramContents().getGroups());
                        //
                        //						if (dialog.open() == IDialogConstants.OK_ID) {
                        //							CompoundCommand command = ERTableEditPart.createChangeTablePropertyCommand(diagram,
                        //									table, copyTable);
                        //
                        //							viewer.getEditDomain().getCommandStack().execute(command.unwrap());
                        //						}
                    }
                }
            }
        });

        final TableColumn tableColumn0 = new TableColumn(resultTable, SWT.LEFT);
        tableColumn0.setWidth(250);
        tableColumn0.setText(DisplayMessages.getMessage("label.search.result.table.path"));
        tableColumn0.addSelectionListener(new SearchResultSortListener(SearchResult.SORT_TYPE_PATH));

        final TableColumn tableColumn1 = new TableColumn(resultTable, SWT.LEFT);
        tableColumn1.setWidth(100);
        tableColumn1.setText(DisplayMessages.getMessage("label.search.result.table.type"));
        tableColumn1.addSelectionListener(new SearchResultSortListener(SearchResult.SORT_TYPE_TYPE));

        final TableColumn tableColumn2 = new TableColumn(resultTable, SWT.LEFT);
        tableColumn2.setWidth(100);
        tableColumn2.setText(DisplayMessages.getMessage("label.search.result.table.name"));
        tableColumn2.addSelectionListener(new SearchResultSortListener(SearchResult.SORT_TYPE_NAME));

        final TableColumn tableColumn3 = new TableColumn(resultTable, SWT.LEFT);
        tableColumn3.setWidth(200);
        tableColumn3.setText(DisplayMessages.getMessage("label.search.result.table.value"));
        tableColumn3.addSelectionListener(new SearchResultSortListener(SearchResult.SORT_TYPE_VALUE));

        tabItem.setControl(resultGroup);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, REPLACE_ID, DisplayMessages.getMessage("label.search.replace.button"), false);
        createButton(parent, SEARCH_ALL_ID, DisplayMessages.getMessage("label.search.all.button"), false);
        createButton(parent, SEARCH_NEXT_ID, DisplayMessages.getMessage("label.search.next.button"), true);
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID) {
            setReturnCode(buttonId);
            close();
        } else if (buttonId == SEARCH_NEXT_ID || buttonId == SEARCH_ALL_ID) {
            tabFolder.setSelection(1);

            this.all = false;
            if (buttonId == SEARCH_ALL_ID) {
                this.all = true;
            }

            final String keyword = keywordCombo.getText();
            this.searchResult =
                    searchManager.search(keyword, all, physicalWordNameCheckBox.getSelection(),
                            logicalWordNameCheckBox.getSelection(), wordTypeCheckBox.getSelection(),
                            wordLengthCheckBox.getSelection(), wordDecimalCheckBox.getSelection(),
                            wordDescriptionCheckBox.getSelection(), physicalTableNameCheckBox.getSelection(),
                            logicalTableNameCheckBox.getSelection(), physicalColumnNameCheckBox.getSelection(),
                            logicalColumnNameCheckBox.getSelection(), columnTypeCheckBox.getSelection(),
                            columnLengthCheckBox.getSelection(), columnDecimalCheckBox.getSelection(),
                            columnDefaultValueCheckBox.getSelection(), columnDescriptionCheckBox.getSelection(),
                            columnGroupNameCheckBox.getSelection(), indexCheckBox.getSelection(),
                            noteCheckBox.getSelection(), modelPropertiesCheckBox.getSelection(),
                            relationCheckBox.getSelection(), groupNameCheckBox.getSelection(),
                            physicalGroupColumnNameCheckBox.getSelection(), logicalGroupColumnNameCheckBox.getSelection(),
                            groupColumnTypeCheckBox.getSelection(), groupColumnLengthCheckBox.getSelection(),
                            groupColumnDecimalCheckBox.getSelection(), groupColumnDefaultValueCheckBox.getSelection(),
                            groupColumnDescriptionCheckBox.getSelection());

            showSearchResult();

            initKeywordCombo();
            keywordCombo.setText(keyword);

            return;
        } else if (buttonId == REPLACE_ID) {
            tabFolder.setSelection(1);

            final List<SearchResultRow> replaceRows = getReplaceRows();
            if (replaceRows.isEmpty()) {
                return;
            }

            final CompoundCommand command = new CompoundCommand();

            final String keyword = keywordCombo.getText();
            final String replaceWord = replaceCombo.getText();

            for (final SearchResultRow row : replaceRows) {
                final ReplaceCommand replaceCommand =
                        new ReplaceCommand(diagram, row.getType(), row.getTarget(), keyword, replaceWord);
                command.add(replaceCommand);
            }

            viewer.getEditDomain().getCommandStack().execute(command.unwrap());

            this.searchResult = searchManager.research();

            showSearchResult();

            initKeywordCombo();
            keywordCombo.setText(keyword);
            initReplaceWordCombo();
            replaceCombo.setText(replaceWord);

            return;
        }

        super.buttonPressed(buttonId);
    }

    private void showSearchResult() {
        if (searchResult != null) {
            setResultRowData(searchResult.getRows());

            final Object object = searchResult.getResultObject();
            if (object != null) {
                focus(object);
            }
        } else {
            resultTable.removeAll();
        }
    }

    private void focus(Object object) {
        final EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(object);

        if (editPart != null) {
            viewer.select(editPart);
            viewer.reveal(editPart);
        }
    }

    private void setResultRowData(List<SearchResultRow> rows) {
        resultTable.removeAll();

        for (final SearchResultRow row : rows) {
            final String type = DisplayMessages.getMessage("search.result.row.type." + row.getType());
            final String name = DisplayMessages.getMessage("search.result.row.name." + row.getType());

            final TableItem tableItem = new TableItem(resultTable, SWT.NONE);

            String path = row.getPath();
            if (path == null) {
                path = type;
            }

            if (row.getPath() != null) {
                tableItem.setText(0, path);
            }

            tableItem.setText(1, type);
            tableItem.setText(2, name);
            tableItem.setText(3, row.getText());
        }
    }

    private List<SearchResultRow> getReplaceRows() {
        final List<SearchResultRow> replaceRows = new ArrayList<>();

        if (searchResult == null) {
            return replaceRows;
        }

        final List<SearchResultRow> rows = searchResult.getRows();
        if (rows == null) {
            return replaceRows;
        }

        final int[] indexes = resultTable.getSelectionIndices();
        if (indexes != null) {
            for (int i = 0; i < indexes.length; i++) {
                replaceRows.add(rows.get(indexes[i]));
            }
        }

        return replaceRows;
    }

    private class SearchResultSortListener extends SelectionAdapter {

        private final int sortType;

        private SearchResultSortListener(int sortType) {
            this.sortType = sortType;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (searchResult == null) {
                return;
            }

            searchResult.sort(sortType);

            showSearchResult();
        }
    }
}

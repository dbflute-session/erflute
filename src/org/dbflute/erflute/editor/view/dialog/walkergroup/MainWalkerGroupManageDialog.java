package org.dbflute.erflute.editor.view.dialog.walkergroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class MainWalkerGroupManageDialog extends AbstractDialog {

    private final ERDiagram diagram;
    private Table categoryTable;
    private Table nodeTable;
    private Button addCategoryButton;
    private Button updateCategoryButton;
    private Button deleteCategoryButton;
    private Text categoryNameText;
    private Map<WalkerGroup, TableEditor> categoryCheckMap;
    private Map<DiagramWalker, TableEditor> nodeCheckMap;
    private WalkerGroup walkerGroup;
    private Button upButton;
    private Button downButton;

    public MainWalkerGroupManageDialog(Shell parentShell, ERDiagram diagram) {
        super(parentShell, 2);
        this.diagram = diagram;
    }

    @Override
    protected void initComponent(Composite composite) {
        createCategoryGroup(composite);
        createNodeGroup(composite);
    }

    private void createCategoryGroup(Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;

        final Group group = new Group(composite, SWT.NONE);
        group.setText(DisplayMessages.getMessage("label.category.message"));
        group.setLayout(gridLayout);

        CompositeFactory.filler(group, 4);

        final GridData tableGridData = new GridData();
        tableGridData.heightHint = 200;
        tableGridData.horizontalSpan = 3;
        tableGridData.verticalSpan = 2;

        this.categoryTable = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
        categoryTable.setHeaderVisible(true);
        categoryTable.setLayoutData(tableGridData);
        categoryTable.setLinesVisible(true);

        final GridData upButtonGridData = new GridData();
        upButtonGridData.grabExcessHorizontalSpace = false;
        upButtonGridData.verticalAlignment = GridData.END;
        upButtonGridData.grabExcessVerticalSpace = true;
        upButtonGridData.widthHint = DesignResources.BUTTON_WIDTH;

        final GridData downButtonGridData = new GridData();
        downButtonGridData.grabExcessVerticalSpace = true;
        downButtonGridData.verticalAlignment = GridData.BEGINNING;
        downButtonGridData.widthHint = DesignResources.BUTTON_WIDTH;

        this.upButton = new Button(group, SWT.NONE);
        upButton.setText(DisplayMessages.getMessage("label.up.arrow"));
        upButton.setLayoutData(upButtonGridData);

        this.downButton = new Button(group, SWT.NONE);
        downButton.setText(DisplayMessages.getMessage("label.down.arrow"));
        downButton.setLayoutData(downButtonGridData);

        final GridData textGridData = new GridData();
        textGridData.widthHint = 150;

        this.categoryNameText = new Text(group, SWT.BORDER);
        categoryNameText.setLayoutData(textGridData);

        final GridData buttonGridData = new GridData();
        buttonGridData.widthHint = DesignResources.BUTTON_WIDTH;

        this.addCategoryButton = new Button(group, SWT.NONE);
        addCategoryButton.setLayoutData(buttonGridData);
        addCategoryButton.setText(DisplayMessages.getMessage("label.button.add"));

        this.updateCategoryButton = new Button(group, SWT.NONE);
        updateCategoryButton.setLayoutData(buttonGridData);
        updateCategoryButton.setText(DisplayMessages.getMessage("label.button.update"));

        this.deleteCategoryButton = new Button(group, SWT.NONE);
        deleteCategoryButton.setLayoutData(buttonGridData);
        deleteCategoryButton.setText(DisplayMessages.getMessage("label.button.delete"));

        final TableColumn tableColumn = new TableColumn(categoryTable, SWT.NONE);
        tableColumn.setWidth(30);
        tableColumn.setResizable(false);
        final TableColumn tableColumn1 = new TableColumn(categoryTable, SWT.NONE);
        tableColumn1.setWidth(230);
        tableColumn1.setResizable(false);
        tableColumn1.setText(DisplayMessages.getMessage("label.category.name"));
    }

    private void createNodeGroup(Composite composite) {
        final Group group = new Group(composite, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setText(DisplayMessages.getMessage("label.category.object.message"));

        final GridData gridData1 = new GridData();
        gridData1.heightHint = 15;

        Label label = new Label(group, SWT.NONE);
        label.setText("");
        label.setLayoutData(gridData1);

        final GridData tableGridData = new GridData();
        tableGridData.heightHint = 200;

        this.nodeTable = new Table(group, SWT.BORDER | SWT.HIDE_SELECTION);
        nodeTable.setHeaderVisible(true);
        nodeTable.setLayoutData(tableGridData);
        nodeTable.setLinesVisible(true);

        final GridData gridData2 = new GridData();
        gridData2.heightHint = 22;

        label = new Label(group, SWT.NONE);
        label.setText("");
        label.setLayoutData(gridData2);

        final TableColumn tableColumn2 = new TableColumn(nodeTable, SWT.NONE);
        tableColumn2.setWidth(30);
        tableColumn2.setResizable(false);
        tableColumn2.setText("");
        final TableColumn tableColumn3 = new TableColumn(nodeTable, SWT.NONE);
        tableColumn3.setWidth(80);
        tableColumn3.setResizable(false);
        tableColumn3.setText(DisplayMessages.getMessage("label.object.type"));
        final TableColumn tableColumn4 = new TableColumn(nodeTable, SWT.NONE);
        tableColumn4.setWidth(200);
        tableColumn4.setResizable(false);
        tableColumn4.setText(DisplayMessages.getMessage("label.object.name"));
    }

    private void initCategoryTable() {
        categoryTable.removeAll();

        if (categoryCheckMap != null) {
            for (final TableEditor editor : categoryCheckMap.values()) {
                editor.getEditor().dispose();
                editor.dispose();
            }

            categoryCheckMap.clear();
        } else {
            categoryCheckMap = new HashMap<>();
        }

        for (final WalkerGroup group : getWalkerGroups()) {
            final TableItem tableItem = new TableItem(categoryTable, SWT.NONE);
            final Button selectCheckButton = new Button(categoryTable, SWT.CHECK);
            selectCheckButton.pack();
            final TableEditor editor = new TableEditor(categoryTable);
            editor.minimumWidth = selectCheckButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(selectCheckButton, tableItem, 0);
            tableItem.setText(1, group.getName());
            categoryCheckMap.put(group, editor);
            if (walkerGroup == group) {
                categoryTable.setSelection(tableItem);
            }
        }
        if (walkerGroup != null) {
            initNodeList(walkerGroup);
        } else {
            deleteNodeList();
        }
    }

    private void initNodeTable() {
        nodeTable.removeAll();
        nodeCheckMap = new HashMap<>();
        for (final DiagramWalker walker : getTableWalkers()) {
            final TableItem tableItem = new TableItem(nodeTable, SWT.NONE);
            final Button selectCheckButton = new Button(nodeTable, SWT.CHECK);
            selectCheckButton.pack();
            final TableEditor editor = new TableEditor(nodeTable);
            editor.minimumWidth = selectCheckButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(selectCheckButton, tableItem, 0);
            tableItem.setText(1, DisplayMessages.getMessage("label.object.type." + walker.getObjectType()));
            tableItem.setText(2, walker.getName());
            nodeCheckMap.put(walker, editor);
        }
    }

    private void initNodeList(WalkerGroup category) {
        categoryNameText.setText(category.getName());
        for (final DiagramWalker walker : nodeCheckMap.keySet()) {
            final Button selectCheckButton = (Button) nodeCheckMap.get(walker).getEditor();
            if (category.contains(walker)) {
                selectCheckButton.setSelection(true);
            } else {
                selectCheckButton.setSelection(false);
            }
        }
    }

    private void deleteNodeList() {
        categoryNameText.setText("");
        nodeTable.removeAll();
        if (nodeCheckMap != null) {
            for (final TableEditor editor : nodeCheckMap.values()) {
                editor.getEditor().dispose();
                editor.dispose();
            }
            nodeCheckMap.clear();
        }
    }

    @Override
    protected void addListener() {
        super.addListener();
        categoryTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = categoryTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }
                validatePage();
                if (walkerGroup == null) {
                    initNodeTable();
                }
                walkerGroup = getWalkerGroups().get(index);
                initNodeList(walkerGroup);
            }
        });
        addCategoryButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final String name = categoryNameText.getText().trim();
                if (name.equals("")) {
                    return;
                }
                validatePage();
                if (walkerGroup == null) {
                    initNodeTable();
                }
                walkerGroup = new WalkerGroup();
                final int[] color = getDefaultColor();
                walkerGroup.setName(name);
                walkerGroup.setColor(color[0], color[1], color[2]);
                initCategoryTable();
            }
        });
        updateCategoryButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final String name = categoryNameText.getText().trim();
                if (name.equals("")) {
                    return;
                }
                validatePage();
                walkerGroup.setName(name);
                initCategoryTable();
            }
        });

        // what is this? by jflute
        //		this.deleteCategoryButton.addSelectionListener(new SelectionAdapter() {
        //			@Override
        //			public void widgetSelected(SelectionEvent event) {
        //				try {
        //					int index = categoryTable.getSelectionIndex();
        //
        //					validatePage();
        //
        //					categorySettings.removeCategory(index);
        //
        //					if (categoryTable.getItemCount() > index + 1) {
        //
        //					} else if (categoryTable.getItemCount() != 0) {
        //						index = categoryTable.getItemCount() - 2;
        //
        //					} else {
        //						index = -1;
        //					}
        //
        //					if (index != -1) {
        //						targetCategory = categorySettings.getAllCategories()
        //								.get(index);
        //					} else {
        //						targetCategory = null;
        //					}
        //
        //					initCategoryTable();
        //
        //				} catch (Exception e) {
        //					Activator.log(e);
        //				}
        //			}
        //		});
        upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = categoryTable.getSelectionIndex();
                if (index == -1 || index == 0) {
                    return;
                }
                validatePage();
                changeColumn(index - 1, index);
                initCategoryTable();
            }
        });
        downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = categoryTable.getSelectionIndex();
                if (index == -1 || index == categoryTable.getItemCount() - 1) {
                    return;
                }
                validatePage();
                changeColumn(index, index + 1);
                initCategoryTable();
            }
        });
    }

    public void changeColumn(int index1, int index2) {
        final List<WalkerGroup> allCategories = getWalkerGroups();
        final WalkerGroup category1 = allCategories.remove(index1);
        WalkerGroup category2 = null;
        if (index1 < index2) {
            category2 = allCategories.remove(index2 - 1);
            allCategories.add(index1, category2);
            allCategories.add(index2, category1);
        } else if (index1 > index2) {
            category2 = allCategories.remove(index2);
            allCategories.add(index1 - 1, category2);
            allCategories.add(index2, category1);
        }
    }

    @Override
    protected String getTitle() {
        return "label.category";
    }

    @Override
    protected void performOK() throws InputException {
        validatePage();
    }

    @Override
    protected void setupData() {
        initCategoryTable();
    }

    @Override
    protected String doValidate() {
        return null;
    }

    public void validatePage() {
        if (walkerGroup != null) {
            final List<DiagramWalker> selectedNodeElementList = new ArrayList<>();
            for (final DiagramWalker table : nodeCheckMap.keySet()) {
                final Button selectCheckButton = (Button) nodeCheckMap.get(table).getEditor();
                if (selectCheckButton.getSelection()) {
                    selectedNodeElementList.add(table);
                }
            }
            walkerGroup.setWalkers(selectedNodeElementList);
        }
        final List<WalkerGroup> selectedCategories = new ArrayList<>();
        for (final WalkerGroup category : getWalkerGroups()) {
            final Button button = (Button) categoryCheckMap.get(category).getEditor();

            if (button.getSelection()) {
                selectedCategories.add(category);
            }
        }
    }

    // ===================================================================================
    //                                                                   Diagram Resources
    //                                                                   =================
    protected List<WalkerGroup> getWalkerGroups() {
        return diagram.getDiagramContents().getDiagramWalkers().getWalkerGroupSet().getList();
    }

    protected List<? extends DiagramWalker> getTableWalkers() {
        return diagram.getDiagramContents().getDiagramWalkers().getTableSet().getList();
    }

    protected int[] getDefaultColor() {
        return diagram.getDefaultColor();
    }
}

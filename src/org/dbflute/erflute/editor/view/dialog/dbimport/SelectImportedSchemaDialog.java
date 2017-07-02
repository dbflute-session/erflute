package org.dbflute.erflute.editor.view.dialog.dbimport;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.StringObjectModel;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

public class SelectImportedSchemaDialog extends AbstractDialog {

    private ContainerCheckedTreeViewer viewer;
    private final List<String> schemaList;
    private final List<String> selectedSchemaList;
    private final List<String> resultSelectedSchemas;
    private final String importDB;

    public SelectImportedSchemaDialog(Shell parentShell, ERDiagram diagram, String importDB, List<String> schemaList,
            List<String> selectedSchemaList) {
        super(parentShell);

        this.schemaList = schemaList;
        this.selectedSchemaList = selectedSchemaList;
        this.resultSelectedSchemas = new ArrayList<>();
        this.importDB = importDB;
    }

    @Override
    protected void initComponent(Composite composite) {
        createObjectListComposite(composite);

        setListener();
    }

    private void createObjectListComposite(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.verticalSpacing = 20;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(gridLayout);
        composite.setLayoutData(gridData);

        createAllSchemaGroup(composite);
    }

    private void createAllSchemaGroup(Composite composite) {
        final GridData gridData = new GridData();
        gridData.heightHint = 300;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        this.viewer = new ContainerCheckedTreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final Tree tree = viewer.getTree();
        tree.setLayoutData(gridData);

        viewer.setContentProvider(new TreeNodeContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
    }

    private void setListener() {
        viewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                validate();
            }
        });
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.BACK_ID, IDialogConstants.BACK_LABEL, false);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.NEXT_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void performOK() throws InputException {
        final Object[] selectedNodes = viewer.getCheckedElements();

        resultSelectedSchemas.clear();

        for (int i = 0; i < selectedNodes.length; i++) {
            final Object value = ((TreeNode) selectedNodes[i]).getValue();
            if (value instanceof String) {
                resultSelectedSchemas.add((String) value);
            }
        }
    }

    @Override
    protected String doValidate() {
        if (viewer.getCheckedElements().length == 0) {
            return "error.import.schema.empty";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.select.import.schema";
    }

    @Override
    protected void setupData() {
        final List<TreeNode> treeNodeList = createTreeNodeList();

        final TreeNode[] treeNodes = treeNodeList.toArray(new TreeNode[treeNodeList.size()]);
        viewer.setInput(treeNodes);

        final TreeNode[] schemaNodes = treeNodes[0].getChildren();
        final List<TreeNode> checkedList = new ArrayList<>();
        if (selectedSchemaList.isEmpty()) {
            for (final TreeNode schemaNode : schemaNodes) {
                if (!DBManagerFactory
                        .getDBManager(importDB)
                        .getSystemSchemaList()
                        .contains(String.valueOf(schemaNode.getValue()).toLowerCase())) {
                    checkedList.add(schemaNode);
                }
            }
        } else {
            for (final TreeNode schemaNode : schemaNodes) {
                if (selectedSchemaList.contains(schemaNode.getValue())) {
                    checkedList.add(schemaNode);
                }
            }
        }

        viewer.setCheckedElements(checkedList.toArray(new TreeNode[checkedList.size()]));

        viewer.expandAll();
    }

    protected List<TreeNode> createTreeNodeList() {
        final List<TreeNode> treeNodeList = new ArrayList<>();
        final TreeNode topNode = new TreeNode(new StringObjectModel(DisplayMessages.getMessage("label.schema")));
        treeNodeList.add(topNode);

        final List<TreeNode> schemaNodeList = new ArrayList<>();
        for (final String schemaName : schemaList) {
            final TreeNode schemaNode = new TreeNode(schemaName);
            schemaNode.setParent(topNode);
            schemaNodeList.add(schemaNode);
        }

        topNode.setChildren(schemaNodeList.toArray(new TreeNode[schemaNodeList.size()]));

        return treeNodeList;
    }

    public List<String> getSelectedSchemas() {
        return resultSelectedSchemas;
    }
}

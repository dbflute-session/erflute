package org.dbflute.erflute.editor.view.dialog.dbimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.StringObjectModel;
import org.dbflute.erflute.editor.model.dbimport.DBObject;
import org.dbflute.erflute.editor.model.dbimport.DBObjectSet;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

public abstract class AbstractSelectImportedObjectDialog extends AbstractDialog {

    private ContainerCheckedTreeViewer viewer;
    protected Button useCommentAsLogicalNameButton;
    private Button mergeWordButton;
    private Button mergeGroupButton;
    protected DBObjectSet dbObjectSet;
    protected boolean resultUseCommentAsLogicalName;
    private boolean resultMergeWord;
    private boolean resultMergeGroup;
    private List<DBObject> resultSelectedDbObjects;

    public AbstractSelectImportedObjectDialog(Shell parentShell, ERDiagram diagram, DBObjectSet dbObjectSet) {
        super(parentShell);

        this.dbObjectSet = dbObjectSet;
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

        createAllObjectGroup(composite);

        final GridData groupGridData = new GridData();
        groupGridData.horizontalAlignment = GridData.FILL;
        groupGridData.grabExcessHorizontalSpace = true;
        groupGridData.horizontalSpan = 3;

        final GridLayout groupLayout = new GridLayout();
        groupLayout.marginWidth = 15;
        groupLayout.marginHeight = 15;

        final Group group = new Group(composite, SWT.NONE);
        group.setText(DisplayMessages.getMessage("label.option"));
        group.setLayoutData(groupGridData);
        group.setLayout(groupLayout);

        initializeOptionGroup(group);
    }

    protected void initializeOptionGroup(Group group) {
        this.mergeWordButton = CompositeFactory.createCheckbox(this, group, "label.merge.word");
        mergeWordButton.setSelection(true);

        this.mergeGroupButton = CompositeFactory.createCheckbox(this, group, "label.merge.group");
        mergeGroupButton.setSelection(true);

    }

    private void createAllObjectGroup(Composite composite) {
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
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void performOK() throws InputException {
        final Object[] selectedNodes = viewer.getCheckedElements();

        this.resultSelectedDbObjects = new ArrayList<>();

        for (int i = 0; i < selectedNodes.length; i++) {
            final Object value = ((TreeNode) selectedNodes[i]).getValue();

            if (value instanceof DBObject) {
                resultSelectedDbObjects.add((DBObject) value);
            }
        }

        this.resultMergeWord = mergeWordButton.getSelection();
        this.resultMergeGroup = mergeGroupButton.getSelection();
    }

    @Override
    protected String doValidate() {
        if (viewer.getCheckedElements().length == 0) {
            return "error.import.object.empty";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.select.import.object";
    }

    @Override
    protected void setupData() {
        final List<TreeNode> treeNodeList = createTreeNodeList();

        final TreeNode[] treeNodes = treeNodeList.toArray(new TreeNode[treeNodeList.size()]);
        viewer.setInput(treeNodes);
        viewer.setCheckedElements(treeNodes);
        viewer.expandAll();
    }

    protected List<TreeNode> createTreeNodeList() {
        final List<TreeNode> treeNodeList = new ArrayList<>();

        TreeNode topNode = new TreeNode(new StringObjectModel(DisplayMessages.getMessage("label.schema")));
        treeNodeList.add(topNode);

        final List<TreeNode> schemaNodeList = new ArrayList<>();

        for (final Map.Entry<String, List<DBObject>> entry : dbObjectSet.getSchemaDbObjectListMap().entrySet()) {
            String schemaName = entry.getKey();
            if ("".equals(schemaName)) {
                schemaName = DisplayMessages.getMessage("label.none");
            }
            final TreeNode schemaNode = new TreeNode(new StringObjectModel(schemaName));
            schemaNode.setParent(topNode);
            schemaNodeList.add(schemaNode);

            final List<DBObject> dbObjectList = entry.getValue();

            final TreeNode[] objectTypeNodes = new TreeNode[DBObject.ALL_TYPES.length];

            for (int i = 0; i < DBObject.ALL_TYPES.length; i++) {
                objectTypeNodes[i] =
                        new TreeNode(new StringObjectModel(DisplayMessages.getMessage("label.object.type." + DBObject.ALL_TYPES[i])));

                final List<TreeNode> objectNodeList = new ArrayList<>();

                for (final DBObject dbObject : dbObjectList) {
                    if (DBObject.ALL_TYPES[i].equals(dbObject.getType())) {
                        final TreeNode objectNode = new TreeNode(dbObject);
                        objectNode.setParent(objectTypeNodes[i]);

                        objectNodeList.add(objectNode);
                    }
                }

                objectTypeNodes[i].setChildren(objectNodeList.toArray(new TreeNode[objectNodeList.size()]));
            }

            schemaNode.setChildren(objectTypeNodes);
        }

        topNode.setChildren(schemaNodeList.toArray(new TreeNode[schemaNodeList.size()]));

        topNode = createTopNode(DBObject.TYPE_TABLESPACE, dbObjectSet.getTablespaceList());
        treeNodeList.add(topNode);

        return treeNodeList;
    }

    protected TreeNode createTopNode(String objectType, List<DBObject> dbObjectList) {
        final TreeNode treeNode = new TreeNode(new StringObjectModel(DisplayMessages.getMessage("label.object.type." + objectType)));
        final List<TreeNode> objectNodeList = new ArrayList<>();

        for (final DBObject dbObject : dbObjectList) {
            final TreeNode objectNode = new TreeNode(dbObject);
            objectNode.setParent(treeNode);

            objectNodeList.add(objectNode);
        }

        treeNode.setChildren(objectNodeList.toArray(new TreeNode[objectNodeList.size()]));

        return treeNode;
    }

    public boolean isUseCommentAsLogicalName() {
        return resultUseCommentAsLogicalName;
    }

    public boolean isMergeWord() {
        return resultMergeWord;
    }

    public boolean isMergeGroup() {
        return resultMergeGroup;
    }

    public List<DBObject> getSelectedDbObjects() {
        return resultSelectedDbObjects;
    }
}

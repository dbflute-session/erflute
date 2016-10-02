package org.dbflute.erflute.editor.view.dialog.dbimport;

import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbimport.DBObject;
import org.dbflute.erflute.editor.model.dbimport.DBObjectSet;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Shell;

public class SelectImportedObjectFromFileDialog extends AbstractSelectImportedObjectDialog {

    public SelectImportedObjectFromFileDialog(Shell parentShell, ERDiagram diagram, DBObjectSet allObjectSet) {
        super(parentShell, diagram, allObjectSet);
    }

    @Override
    protected List<TreeNode> createTreeNodeList() {
        List<TreeNode> treeNodeList = super.createTreeNodeList();

        TreeNode topNode = createTopNode(DBObject.TYPE_NOTE, this.dbObjectSet.getNoteList());
        treeNodeList.add(topNode);
        topNode = createTopNode(DBObject.TYPE_GROUP, this.dbObjectSet.getGroupList());
        treeNodeList.add(topNode);

        return treeNodeList;
    }
}

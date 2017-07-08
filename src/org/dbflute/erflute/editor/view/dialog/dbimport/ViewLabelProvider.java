package org.dbflute.erflute.editor.view.dialog.dbimport;

import org.dbflute.erflute.editor.model.StringObjectModel;
import org.dbflute.erflute.editor.model.dbimport.DBObject;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;

public class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public String getText(Object element) {
        final TreeNode treeNode = (TreeNode) element;

        final Object value = treeNode.getValue();
        if (value instanceof DBObject) {
            final DBObject dbObject = (DBObject) value;
            return dbObject.getName();
        } else if (value instanceof StringObjectModel) {
            final StringObjectModel object = (StringObjectModel) value;
            return object.getName();
        }

        return value.toString();
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        return "xxx";
    }
}

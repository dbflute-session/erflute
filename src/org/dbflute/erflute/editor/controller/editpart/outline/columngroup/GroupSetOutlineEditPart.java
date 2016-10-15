package org.dbflute.erflute.editor.controller.editpart.outline.columngroup;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.eclipse.gef.EditPart;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class GroupSetOutlineEditPart extends AbstractOutlineEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ColumnGroupSet.PROPERTY_CHANGE_GROUP_SET)) {
            refresh();
        }
    }

    @Override
    protected List<ColumnGroup> getModelChildren() {
        final ColumnGroupSet columnGroupSet = (ColumnGroupSet) this.getModel();
        final List<ColumnGroup> columnGroupList = columnGroupSet.getGroupList();
        Collections.sort(columnGroupList);
        return columnGroupList;
    }

    @Override
    protected void refreshOutlineVisuals() {
        this.setWidgetText("Column Group" + " (" + getModelChildren().size() + ")");
        this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();
        for (final Object child : this.getChildren()) {
            final EditPart part = (EditPart) child;
            part.refresh();
        }
    }
}

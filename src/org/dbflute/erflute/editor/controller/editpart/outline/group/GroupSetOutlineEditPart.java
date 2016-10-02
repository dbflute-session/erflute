package org.dbflute.erflute.editor.controller.editpart.outline.group;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
import org.eclipse.gef.EditPart;

public class GroupSetOutlineEditPart extends AbstractOutlineEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(GroupSet.PROPERTY_CHANGE_GROUP_SET)) {
            refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        GroupSet columnGroupSet = (GroupSet) this.getModel();

        List<ColumnGroup> columnGroupList = columnGroupSet.getGroupList();

        Collections.sort(columnGroupList);

        return columnGroupList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        this.setWidgetText(DisplayMessages.getMessage("label.column.group") + " (" + this.getModelChildren().size() + ")");
        this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshChildren() {
        super.refreshChildren();

        for (Object child : this.getChildren()) {
            EditPart part = (EditPart) child;
            part.refresh();
        }
    }

}

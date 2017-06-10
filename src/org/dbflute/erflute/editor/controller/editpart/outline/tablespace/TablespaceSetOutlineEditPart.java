package org.dbflute.erflute.editor.controller.editpart.outline.tablespace;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.eclipse.gef.EditPart;

public class TablespaceSetOutlineEditPart extends AbstractOutlineEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TablespaceSet.PROPERTY_CHANGE_TABLESPACE_SET)) {
            refresh();
        }
    }

    @Override
    protected List getModelChildren() {
        TablespaceSet tablespaceSet = (TablespaceSet) this.getModel();

        List<Tablespace> tablespaceList = tablespaceSet.getTablespaceList();
        Collections.sort(tablespaceList);

        return tablespaceList;
    }

    @Override
    protected void refreshOutlineVisuals() {
        this.setWidgetText(DisplayMessages.getMessage("label.tablespace") + " (" + this.getModelChildren().size() + ")");
        this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();

        for (Object child : this.getChildren()) {
            EditPart part = (EditPart) child;
            part.refresh();
        }
    }
}

package org.dbflute.erflute.editor.controller.editpart.outline.view;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ViewSet;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.eclipse.gef.EditPart;

public class ViewSetOutlineEditPart extends AbstractOutlineEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ViewSet.PROPERTY_CHANGE_VIEW_SET)) {
            refresh();
        }
    }

    @Override
    protected List<ERView> getModelChildren() {
        final ViewSet viewSet = (ViewSet) getModel();
        final List<ERView> list = viewSet.getList();
        if (getDiagram().getDiagramContents().getSettings().getViewOrderBy() == DiagramSettings.VIEW_MODE_LOGICAL) {
            Collections.sort(list, TableView.LOGICAL_NAME_COMPARATOR);
        } else {
            Collections.sort(list, TableView.PHYSICAL_NAME_COMPARATOR);
        }

        return list;
    }

    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(DisplayMessages.getMessage("label.view") + " (" + getModelChildren().size() + ")");
        setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();

        for (final Object child : getChildren()) {
            final EditPart part = (EditPart) child;
            part.refresh();
        }
    }
}

package org.dbflute.erflute.editor.controller.editpart.outline.index;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.eclipse.gef.EditPart;

public class IndexSetOutlineEditPart extends AbstractOutlineEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(IndexSet.PROPERTY_CHANGE_INDEXES)) {
            refresh();
        }
    }

    @Override
    protected List<ERIndex> getModelChildren() {
        final List<ERIndex> children = new ArrayList<>();

        final ERDiagram diagram = getDiagram();
        final Category category = getCurrentCategory();

        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            if (category == null || category.contains(table)) {
                children.addAll(table.getIndexes());
            }
        }

        Collections.sort(children);

        return children;
    }

    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(DisplayMessages.getMessage("label.index") + " (" + getModelChildren().size() + ")");
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

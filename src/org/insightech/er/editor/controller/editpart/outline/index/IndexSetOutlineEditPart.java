package org.insightech.er.editor.controller.editpart.outline.index;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.eclipse.gef.EditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;

public class IndexSetOutlineEditPart extends AbstractOutlineEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(IndexSet.PROPERTY_CHANGE_INDEXES)) {
            refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        List<ERIndex> children = new ArrayList<ERIndex>();

        ERDiagram diagram = this.getDiagram();
        Category category = this.getCurrentCategory();

        for (ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {
            if (category == null || category.contains(table)) {
                children.addAll(table.getIndexes());
            }
        }

        Collections.sort(children);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        this.setWidgetText(DisplayMessages.getMessage("label.index") + " (" + this.getModelChildren().size() + ")");
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

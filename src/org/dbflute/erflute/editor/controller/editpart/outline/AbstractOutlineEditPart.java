package org.dbflute.erflute.editor.controller.editpart.outline;

import java.beans.PropertyChangeListener;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

public abstract class AbstractOutlineEditPart extends AbstractTreeEditPart implements PropertyChangeListener, FilteringEditPart {

    private String filterText;

    @Override
    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();
        ((AbstractModel) getModel()).addPropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        ((AbstractModel) getModel()).removePropertyChangeListener(this);
        super.deactivate();
    }

    @Override
    public void refresh() {
        if (ERDiagramEditPart.isUpdateable()) {
            refreshChildren();
            refreshVisuals();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void refreshVisuals() {
        if (ERDiagramEditPart.isUpdateable()) {
            this.refreshOutlineVisuals();

            for (Object child : this.getChildren()) {
                AbstractOutlineEditPart part = (AbstractOutlineEditPart) child;
                part.refreshVisuals();
            }
        }
    }

    protected ERDiagram getDiagram() {
        return (ERDiagram) this.getRoot().getContents().getModel();
    }

    protected Category getCurrentCategory() {
        return this.getDiagram().getCurrentCategory();
    }

    abstract protected void refreshOutlineVisuals();

    protected void execute(Command command) {
        this.getViewer().getEditDomain().getCommandStack().execute(command);
    }

    /**
     * filterText���擾���܂��B
     * @return filterText
     */
    public String getFilterText() {
        return filterText;
    }
}

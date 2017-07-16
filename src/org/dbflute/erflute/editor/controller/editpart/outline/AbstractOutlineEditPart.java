package org.dbflute.erflute.editor.controller.editpart.outline;

import java.beans.PropertyChangeListener;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class AbstractOutlineEditPart extends AbstractTreeEditPart implements PropertyChangeListener, FilteringEditPart {

    private String filterText;

    @Override
    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    @Override
    public void activate() {
        super.activate();
        ((AbstractModel) getModel()).addPropertyChangeListener(this);
    }

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

    @Override
    final public void refreshVisuals() {
        if (ERDiagramEditPart.isUpdateable()) {
            refreshOutlineVisuals();

            for (final Object child : getChildren()) {
                final AbstractOutlineEditPart part = (AbstractOutlineEditPart) child;
                part.refreshVisuals();
            }
        }
    }

    protected ERDiagram getDiagram() {
        if (getModel() instanceof DiagramWalker) {
            return ((DiagramWalker) getModel()).getDiagram();
        }

        Activator.debug(this, "getDiagram", "Not DiagramWalker");
        return (ERDiagram) getRoot().getContents().getModel();
    }

    protected Category getCurrentCategory() {
        return getDiagram().getCurrentCategory();
    }

    abstract protected void refreshOutlineVisuals();

    protected void execute(Command command) {
        getViewer().getEditDomain().getCommandStack().execute(command);
    }

    public String getFilterText() {
        return filterText;
    }
}

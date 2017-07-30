package org.dbflute.erflute.editor.controller.editpart.element.node;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.view.figure.CategoryFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

public class CategoryEditPart extends DiagramWalkerEditPart implements IResizable {

    public CategoryEditPart() {
        super();
    }

    @Override
    protected IFigure createFigure() {
        final Category category = (Category) getModel();
        final CategoryFigure figure = new CategoryFigure(category.getName());

        return figure;
    }

    @Override
    protected Rectangle getRectangle() {
        final Rectangle rectangle = super.getRectangle();

        final Category category = (Category) getModel();
        final ERDiagramEditPart rootEditPart = (ERDiagramEditPart) getRoot().getContents();

        for (final Object child : rootEditPart.getChildren()) {
            if (child instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart editPart = (DiagramWalkerEditPart) child;

                if (category.contains((DiagramWalker) editPart.getModel())) {
                    final Rectangle bounds = editPart.getFigure().getBounds();

                    if (bounds.x + bounds.width > rectangle.x + rectangle.width) {
                        rectangle.width = bounds.x + bounds.width - rectangle.x;
                    }
                    if (bounds.y + bounds.height > rectangle.y + rectangle.height) {
                        rectangle.height = bounds.y + bounds.height - rectangle.y;
                    }

                    if (rectangle.width != category.getWidth() || rectangle.height != category.getHeight()) {
                        category.setLocation(new Location(category.getX(), category.getY(), rectangle.width, rectangle.height));
                    }
                }
            }
        }

        return rectangle;
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());

        super.createEditPolicies();
    }

    @Override
    protected void performRequestOpen() {
    }
}

package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.eclipse.draw2d.geometry.Rectangle;

public class MoveElementCommand extends AbstractCommand {

    protected int x;
    protected int oldX;
    protected int y;
    protected int oldY;
    protected int width;
    protected int oldWidth;
    protected int height;
    protected int oldHeight;

    private final DiagramWalker element;
    private final Map<Category, Rectangle> oldCategoryRectangleMap;
    private final Map<Category, Rectangle> newCategoryRectangleMap;
    private final List<Category> removedCategories;
    private final List<Category> addCategories;
    private final ERDiagram diagram;
    private final Rectangle bounds;

    public MoveElementCommand(ERDiagram diagram, Rectangle bounds, int x, int y, int width, int height, DiagramWalker element) {
        this.element = element;
        setNewRectangle(x, y, width, height);

        this.oldX = element.getX();
        this.oldY = element.getY();
        this.oldWidth = element.getWidth();
        this.oldHeight = element.getHeight();

        this.oldCategoryRectangleMap = new HashMap<>();
        this.newCategoryRectangleMap = new HashMap<>();

        this.removedCategories = new ArrayList<>();
        this.addCategories = new ArrayList<>();

        this.bounds = bounds;
        this.diagram = diagram;
    }

    protected void setNewRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private void initCategory(ERDiagram diagram, Rectangle bounds) {
        for (final Category category : diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories()) {
            if (category.contains(element)) {
                int categoryX = category.getX();
                int categoryY = category.getY();
                int categoryWidth = category.getWidth();
                int categoryHeight = category.getHeight();

                final Rectangle oldRectangle = new Rectangle(categoryX, categoryY, categoryWidth, categoryHeight);

                boolean isDirty = false;

                if (diagram.getCurrentCategory() == null) {
                    if (bounds.x + bounds.width < category.getX() || bounds.x > category.getX() + category.getWidth()
                            || bounds.y + bounds.height < category.getY() || bounds.y > category.getY() + category.getHeight()) {

                        removedCategories.add(category);

                        continue;
                    }
                }

                if (bounds.x < category.getX()) {
                    categoryX = bounds.x;
                    isDirty = true;
                }
                if (bounds.y < category.getY()) {
                    categoryY = bounds.y;
                    isDirty = true;
                }
                if (bounds.x + bounds.width > categoryX + categoryWidth) {
                    categoryWidth = bounds.x + bounds.width - categoryX;
                    isDirty = true;
                }
                if (bounds.y + bounds.height > categoryY + categoryHeight) {
                    categoryHeight = bounds.y + bounds.height - categoryY;
                    isDirty = true;
                }

                if (isDirty) {
                    newCategoryRectangleMap.put(category, new Rectangle(categoryX, categoryY, categoryWidth, categoryHeight));
                    oldCategoryRectangleMap.put(category, oldRectangle);
                }
            } else {
                if (diagram.getCurrentCategory() == null) {
                    if (bounds.x >= category.getX() && bounds.x + bounds.width <= category.getX() + category.getWidth()
                            && bounds.y >= category.getY() && bounds.y + bounds.height <= category.getY() + category.getHeight()) {
                        addCategories.add(category);
                    }
                }
            }
        }
    }

    @Override
    protected void doExecute() {
        if (bounds != null) {
            final Rectangle rectangle = new Rectangle(bounds);

            if (rectangle.x != x) {
                rectangle.x = x;
            }
            if (rectangle.y != y) {
                rectangle.y = y;
            }
            if (rectangle.width < width) {
                rectangle.width = width;
            }
            if (rectangle.height < height) {
                rectangle.height = height;
            }

            initCategory(diagram, rectangle);
        }

        for (final Category category : newCategoryRectangleMap.keySet()) {
            final Rectangle rectangle = newCategoryRectangleMap.get(category);
            category.setLocation(new Location(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
        }

        for (final Category category : removedCategories) {
            category.getContents().remove(element);
        }

        for (final Category category : addCategories) {
            category.getContents().add(element);
        }

        element.setLocation(new Location(x, y, width, height));
    }

    @Override
    protected void doUndo() {
        element.setLocation(new Location(oldX, oldY, oldWidth, oldHeight));

        for (final Category category : oldCategoryRectangleMap.keySet()) {
            final Rectangle rectangle = oldCategoryRectangleMap.get(category);
            category.setLocation(new Location(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
        }

        for (final Category category : removedCategories) {
            category.getContents().add(element);
        }

        for (final Category category : addCategories) {
            category.getContents().remove(element);
        }
    }
}

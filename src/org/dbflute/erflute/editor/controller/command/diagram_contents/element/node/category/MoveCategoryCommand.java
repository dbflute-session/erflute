package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.eclipse.swt.graphics.Rectangle;

public class MoveCategoryCommand extends MoveElementCommand {

    private final boolean move;
    private final List<DiagramWalker> walkerList;
    private Map<DiagramWalker, Rectangle> walkerOldLocationMap;
    private final Category category;
    private int diffX;
    private int diffY;
    private Map<WalkerConnection, List<Bendpoint>> bendpointListMap;

    public MoveCategoryCommand(ERDiagram diagram, int x, int y, int width, int height, Category category, List<Category> otherCategories,
            boolean move) {
        super(diagram, null, x, y, width, height, category);

        this.walkerList = new ArrayList<>(category.getContents());
        this.category = category;
        this.move = move;

        if (!move) {
            for (final DiagramWalker walker : walkerList) {
                final int nodeElementX = walker.getX();
                final int nodeElementY = walker.getY();
                int nodeElementWidth = walker.getWidth();
                int nodeElementHeight = walker.getHeight();

                if (x > nodeElementX) {
                    nodeElementWidth += x - nodeElementX;
                    x = nodeElementX;
                }
                if (y > nodeElementY) {
                    nodeElementHeight += y - nodeElementY;
                    y = nodeElementY;
                }

                if (nodeElementX - x + nodeElementWidth > width) {
                    width = nodeElementX - x + nodeElementWidth;
                }

                if (nodeElementY - y + nodeElementHeight > height) {
                    height = nodeElementY - y + nodeElementHeight;
                }
            }

            setNewRectangle(x, y, width, height);
        } else {
            this.walkerOldLocationMap = new HashMap<>();
            this.diffX = x - category.getX();
            this.diffY = y - category.getY();

            for (final Iterator<DiagramWalker> iter = walkerList.iterator(); iter.hasNext();) {
                final DiagramWalker walker = iter.next();
                for (final Category otherCategory : otherCategories) {
                    if (otherCategory.contains(walker)) {
                        iter.remove();
                        break;
                    }
                }
            }

            for (final DiagramWalker walker : walkerList) {
                walkerOldLocationMap.put(walker, new Rectangle(walker.getX(), walker.getY(), walker.getWidth(), walker.getHeight()));
            }
        }
    }

    @Override
    protected void doExecute() {
        if (move) {
            this.bendpointListMap = new HashMap<>();
            for (final DiagramWalker walker : walkerList) {
                walker.setLocation(new Location(walker.getX() + diffX, walker.getY() + diffY, walker.getWidth(), walker.getHeight()));
                moveBendpoints(walker);
            }
        }
        super.doExecute();
    }

    @Override
    protected void doUndo() {
        if (move) {
            for (final DiagramWalker nodeElement : walkerList) {
                final Rectangle rectangle = walkerOldLocationMap.get(nodeElement);
                nodeElement.setLocation(new Location(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
            }
            restoreBendpoints();
        }
        super.doUndo();
    }

    private void moveBendpoints(DiagramWalker source) {
        for (final WalkerConnection connectionElement : source.getOutgoings()) {
            final DiagramWalker target = connectionElement.getTargetWalker();

            if (category.contains(target)) {
                final List<Bendpoint> bendpointList = connectionElement.getBendpoints();

                final List<Bendpoint> oldBendpointList = new ArrayList<>();

                for (int index = 0; index < bendpointList.size(); index++) {
                    final Bendpoint oldBendPoint = bendpointList.get(index);

                    if (oldBendPoint.isRelative()) {
                        break;
                    }

                    final Bendpoint newBendpoint = new Bendpoint(oldBendPoint.getX() + diffX, oldBendPoint.getY() + diffY);
                    connectionElement.replaceBendpoint(index, newBendpoint);

                    oldBendpointList.add(oldBendPoint);
                }

                bendpointListMap.put(connectionElement, oldBendpointList);
            }
        }
    }

    private void restoreBendpoints() {
        for (final WalkerConnection connectionElement : bendpointListMap.keySet()) {
            final List<Bendpoint> oldBendpointList = bendpointListMap.get(connectionElement);

            for (int index = 0; index < oldBendpointList.size(); index++) {
                connectionElement.replaceBendpoint(index, oldBendpointList.get(index));
            }
        }
    }
}

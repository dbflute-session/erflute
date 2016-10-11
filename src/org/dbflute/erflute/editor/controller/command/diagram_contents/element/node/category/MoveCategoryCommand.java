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

        this.walkerList = new ArrayList<DiagramWalker>(category.getContents());
        this.category = category;
        this.move = move;

        if (!this.move) {
            for (final DiagramWalker walker : this.walkerList) {
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

            this.setNewRectangle(x, y, width, height);

        } else {
            this.walkerOldLocationMap = new HashMap<DiagramWalker, Rectangle>();
            this.diffX = x - category.getX();
            this.diffY = y - category.getY();

            for (final Iterator<DiagramWalker> iter = this.walkerList.iterator(); iter.hasNext();) {
                final DiagramWalker walker = iter.next();
                for (final Category otherCategory : otherCategories) {
                    if (otherCategory.contains(walker)) {
                        iter.remove();
                        break;
                    }
                }
            }

            for (final DiagramWalker nodeElement : this.walkerList) {
                this.walkerOldLocationMap.put(nodeElement, new Rectangle(nodeElement.getX(), nodeElement.getY(), nodeElement.getWidth(),
                        nodeElement.getHeight()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        if (this.move) {
            this.bendpointListMap = new HashMap<WalkerConnection, List<Bendpoint>>();

            for (final DiagramWalker nodeElement : this.walkerList) {
                nodeElement.setLocation(new Location(nodeElement.getX() + diffX, nodeElement.getY() + diffY, nodeElement.getWidth(),
                        nodeElement.getHeight()));
                this.moveBendpoints(nodeElement);

            }
        }

        super.doExecute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        if (this.move) {
            for (final DiagramWalker nodeElement : this.walkerList) {
                final Rectangle rectangle = this.walkerOldLocationMap.get(nodeElement);
                nodeElement.setLocation(new Location(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
            }

            this.restoreBendpoints();
        }

        super.doUndo();
    }

    private void moveBendpoints(DiagramWalker source) {
        for (final WalkerConnection connectionElement : source.getOutgoings()) {
            final DiagramWalker target = connectionElement.getWalkerTarget();

            if (this.category.contains(target)) {
                final List<Bendpoint> bendpointList = connectionElement.getBendpoints();

                final List<Bendpoint> oldBendpointList = new ArrayList<Bendpoint>();

                for (int index = 0; index < bendpointList.size(); index++) {
                    final Bendpoint oldBendPoint = bendpointList.get(index);

                    if (oldBendPoint.isRelative()) {
                        break;
                    }

                    final Bendpoint newBendpoint = new Bendpoint(oldBendPoint.getX() + this.diffX, oldBendPoint.getY() + this.diffY);
                    connectionElement.replaceBendpoint(index, newBendpoint);

                    oldBendpointList.add(oldBendPoint);
                }

                this.bendpointListMap.put(connectionElement, oldBendpointList);
            }
        }
    }

    private void restoreBendpoints() {
        for (final WalkerConnection connectionElement : this.bendpointListMap.keySet()) {
            final List<Bendpoint> oldBendpointList = this.bendpointListMap.get(connectionElement);

            for (int index = 0; index < oldBendpointList.size(); index++) {
                connectionElement.replaceBendpoint(index, oldBendpointList.get(index));
            }
        }
    }

}

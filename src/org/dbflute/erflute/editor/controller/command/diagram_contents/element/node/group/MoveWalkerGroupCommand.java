package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.group;

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
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.eclipse.swt.graphics.Rectangle;

public class MoveWalkerGroupCommand extends MoveElementCommand {

    private final WalkerGroup walkerGroup;
    private final List<DiagramWalker> walkerList;
    private final boolean move;
    private Map<DiagramWalker, Rectangle> walkerOldLocationMap;
    private int diffX;
    private int diffY;
    private Map<WalkerConnection, List<Bendpoint>> bendpointListMap;

    public MoveWalkerGroupCommand(ERDiagram diagram, int x, int y, int width, int height, WalkerGroup walkerGroup,
            List<WalkerGroup> otherCategories, boolean move) {
        super(diagram, null, x, y, width, height, walkerGroup);
        this.walkerGroup = walkerGroup;
        this.walkerList = new ArrayList<>(walkerGroup.getDiagramWalkerList());
        this.move = move;

        if (!move) {
            for (final DiagramWalker walker : walkerList) {
                final int walkerX = walker.getX();
                final int walkerY = walker.getY();
                int walkerWidth = walker.getWidth();
                int walkerHeight = walker.getHeight();
                if (x > walkerX) {
                    walkerWidth += x - walkerX;
                    x = walkerX;
                }
                if (y > walkerY) {
                    walkerHeight += y - walkerY;
                    y = walkerY;
                }
                if (walkerX - x + walkerWidth > width) {
                    width = walkerX - x + walkerWidth;
                }
                if (walkerY - y + walkerHeight > height) {
                    height = walkerY - y + walkerHeight;
                }
            }
            setNewRectangle(x, y, width, height);
        } else {
            this.walkerOldLocationMap = new HashMap<>();
            this.diffX = x - walkerGroup.getX();
            this.diffY = y - walkerGroup.getY();
            for (final Iterator<DiagramWalker> iter = walkerList.iterator(); iter.hasNext();) {
                final DiagramWalker walker = iter.next();
                for (final WalkerGroup otherCategory : otherCategories) {
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
            bendpointListMap = new HashMap<>();
            for (final DiagramWalker walker : walkerList) {
                final Location location = new Location(walker.getX() + diffX, walker.getY() + diffY, walker.getWidth(), walker.getHeight());
                walker.setLocation(location);
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
            if (walkerGroup.contains(target)) {
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

package org.dbflute.erflute.editor.controller.editpolicy;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.common.NothingToDoCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint.MoveBendpointCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.CreateElementCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.MoveWalkerGroupCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.PlaceTableCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.category.MoveCategoryCommand;
import org.dbflute.erflute.editor.controller.editpart.element.AbstractModelEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.CategoryEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.DiagramWalkerEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.WalkerGroupEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerSelectionEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.view.drag_drop.ERDiagramTransferDragSourceListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DirectEditRequest;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramLayoutEditPolicy extends XYLayoutEditPolicy {

    @Override
    protected void showSizeOnDropFeedback(CreateRequest request) {
        final Point p = new Point(request.getLocation().getCopy());
        final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this.getHost().getRoot()).getZoomManager();
        final double zoom = zoomManager.getZoom();
        final IFigure feedback = getSizeOnDropFeedback(request);
        final Dimension size = request.getSize().getCopy();
        feedback.translateToRelative(size);
        feedback.setBounds(new Rectangle((int) (p.x * zoom), (int) (p.y * zoom), size.width, size.height).expand(getCreationFeedbackOffset(request)));
    }

    @Override
    protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
        if (!(child instanceof DiagramWalkerEditPart)) {
            return null;
        }
        try {
            final Rectangle rectangle = (Rectangle) constraint;
            @SuppressWarnings("unchecked")
            final List<Object> selectedEditParts = this.getHost().getViewer().getSelectedEditParts();
            final DiagramWalkerEditPart editPart = (DiagramWalkerEditPart) child;
            final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();
            final Rectangle currentRectangle = editPart.getFigure().getBounds();
            boolean move = false;
            if (rectangle.width == currentRectangle.width && rectangle.height == currentRectangle.height) {
                move = true;
            }
            boolean nothingToDo = false;
            if (move && !(editPart instanceof CategoryEditPart)) {
                for (final Object selectedEditPart : selectedEditParts) {
                    if (selectedEditPart instanceof CategoryEditPart) {
                        final CategoryEditPart categoryEditPart = (CategoryEditPart) selectedEditPart;
                        final Category category = (Category) categoryEditPart.getModel();

                        if (category.contains(nodeElement)) {
                            nothingToDo = true;
                        }
                    }
                }
            }
            if (move && !(editPart instanceof WalkerGroupEditPart)) {
                for (final Object selectedEditPart : selectedEditParts) {
                    if (selectedEditPart instanceof WalkerGroupEditPart) {
                        final WalkerGroupEditPart categoryEditPart = (WalkerGroupEditPart) selectedEditPart;
                        final WalkerGroup category = (WalkerGroup) categoryEditPart.getModel();
                        if (category.contains(nodeElement)) {
                            nothingToDo = true;
                        }
                    }
                }
            }
            final List<Command> bendpointMoveCommandList = new ArrayList<Command>();
            final int oldX = nodeElement.getX();
            final int oldY = nodeElement.getY();
            final int diffX = rectangle.x - oldX;
            final int diffY = rectangle.y - oldY;
            for (final Object obj : editPart.getSourceConnections()) {
                final AbstractConnectionEditPart connection = (AbstractConnectionEditPart) obj;
                if (selectedEditParts.contains(connection.getTarget())) {
                    final WalkerConnection connectionElement = (WalkerConnection) connection.getModel();
                    final List<Bendpoint> bendpointList = connectionElement.getBendpoints();
                    for (int index = 0; index < bendpointList.size(); index++) {
                        final Bendpoint bendPoint = bendpointList.get(index);
                        if (bendPoint.isRelative()) {
                            break;
                        }
                        final MoveBendpointCommand moveCommand =
                                new MoveBendpointCommand(connection, bendPoint.getX() + diffX, bendPoint.getY() + diffY, index);
                        bendpointMoveCommandList.add(moveCommand);
                    }
                }
            }
            final CompoundCommand compoundCommand = new CompoundCommand();
            if (!nothingToDo) {
                final Command changeConstraintCommand = this.createChangeConstraintCommand(editPart, rectangle);
                if (bendpointMoveCommandList.isEmpty()) {
                    return changeConstraintCommand;
                }
                compoundCommand.add(changeConstraintCommand);
            } else {
                compoundCommand.add(new NothingToDoCommand());
            }
            for (final Command command : bendpointMoveCommandList) {
                compoundCommand.add(command);
            }
            return compoundCommand;
        } catch (final Exception e) {
            Activator.error(e);
            return null;
        }
    }

    @Override
    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
        final Rectangle rectangle = (Rectangle) constraint;
        final DiagramWalkerEditPart editPart = (DiagramWalkerEditPart) child;
        final DiagramWalker nodeElement = (DiagramWalker) editPart.getModel();
        final Rectangle currentRectangle = editPart.getFigure().getBounds();
        boolean move = false;
        if (rectangle.width == currentRectangle.width && rectangle.height == currentRectangle.height) {
            move = true;
        }
        if (nodeElement instanceof Category) {
            final Category category = (Category) nodeElement;
            List<Category> otherCategories = null;
            if (move) {
                if (this.getOtherCategory((Category) nodeElement) != null) {
                    return null;
                }
                otherCategories = this.getOtherSelectedCategories(category);
            }
            final ERDiagram diagram = ERModelUtil.getDiagram(getHost());
            return new MoveCategoryCommand(diagram, rectangle.x, rectangle.y, rectangle.width, rectangle.height, category, otherCategories,
                    move);
        } else if (nodeElement instanceof WalkerGroup) {
            final WalkerGroup vgroup = (WalkerGroup) nodeElement;
            List<WalkerGroup> otherGroups = null;
            if (move) {
                //				if (this.getOtherCategory((VGroup) nodeElement) != null) {
                //					return null;
                //				}
                otherGroups = getOtherSelectedGroups(vgroup);
            }
            final ERDiagram diagram = ERModelUtil.getDiagram(getHost());
            return new MoveWalkerGroupCommand(diagram, rectangle.x, rectangle.y, rectangle.width, rectangle.height, vgroup, otherGroups, move);

        } else {
            final ERDiagram diagram = ERModelUtil.getDiagram(getHost());
            return new MoveElementCommand(diagram, currentRectangle, rectangle.x, rectangle.y, rectangle.width, rectangle.height,
                    nodeElement);
        }
    }

    private Category getOtherCategory(Category category) {
        final ERDiagram diagram = ERModelUtil.getDiagram(getHost());
        final List<Category> selectedCategories = diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories();
        for (final DiagramWalker nodeElement : category.getContents()) {
            for (final Category otherCategory : selectedCategories) {
                if (otherCategory != category && !isSelected(otherCategory)) {
                    if (otherCategory.contains(nodeElement)) {
                        return otherCategory;
                    }
                }
            }
        }
        return null;
    }

    private List<Category> getOtherSelectedCategories(Category category) {
        final List<Category> otherCategories = new ArrayList<Category>();
        @SuppressWarnings("unchecked")
        final List<Object> selectedEditParts = this.getHost().getViewer().getSelectedEditParts();
        for (final Object object : selectedEditParts) {
            if (object instanceof CategoryEditPart) {
                final CategoryEditPart categoryEditPart = (CategoryEditPart) object;
                final Category otherCategory = (Category) categoryEditPart.getModel();
                if (otherCategory == category) {
                    break;
                }
                otherCategories.add(otherCategory);
            }
        }
        return otherCategories;
    }

    //	private Category getOtherGroup(VGroup vgroup) {
    //		ERModel model = (ERModel) getHost().getModel();
    //
    //		List<VGroup> selectedCategories = diagram.getDiagramContents()
    //				.getSettings().getCategorySetting().getSelectedCategories();
    //
    //		for (NodeElement nodeElement : vgroup.getContents()) {
    //			for (VGroup otherCategory : selectedCategories) {
    //				if (otherCategory != vgroup && !isSelected(otherCategory)) {
    //					if (otherCategory.contains(nodeElement)) {
    //						return otherCategory;
    //					}
    //				}
    //			}
    //		}
    //
    //		return null;
    //	}

    private List<WalkerGroup> getOtherSelectedGroups(WalkerGroup group) {
        final List<WalkerGroup> otherCategories = new ArrayList<WalkerGroup>();
        @SuppressWarnings("unchecked")
        final List<Object> selectedEditParts = this.getHost().getViewer().getSelectedEditParts();
        for (final Object object : selectedEditParts) {
            if (object instanceof WalkerGroupEditPart) {
                final WalkerGroupEditPart categoryEditPart = (WalkerGroupEditPart) object;
                final WalkerGroup otherCategory = (WalkerGroup) categoryEditPart.getModel();
                if (otherCategory == group) {
                    break;
                }
                otherCategories.add(otherCategory);
            }
        }
        return otherCategories;
    }

    private boolean isSelected(Category category) {
        @SuppressWarnings("unchecked")
        final List<Object> selectedEditParts = this.getHost().getViewer().getSelectedEditParts();
        for (final Object object : selectedEditParts) {
            if (object instanceof DiagramWalkerEditPart) {
                final DiagramWalkerEditPart editPart = (DiagramWalkerEditPart) object;
                if (editPart.getModel() == category) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Command getCreateCommand(CreateRequest request) {
        // #willanalyze what is this? by jflute
        //		if (getHost() instanceof ERModelEditPart) {
        //			ERModelEditPart editPart = (ERModelEditPart) this.getHost();
        //
        //			Point point = request.getLocation();
        //			editPart.getFigure().translateToRelative(point);
        //
        //			NodeElement element = (NodeElement) request.getNewObject();
        //			ERDiagram diagram = (ERDiagram) editPart.getModel();
        //
        //			Dimension size = request.getSize();
        //			List<NodeElement> enclosedElementList = new ArrayList<NodeElement>();
        //
        //			if (size != null) {
        //				ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
        //						.getHost().getRoot()).getZoomManager();
        //				double zoom = zoomManager.getZoom();
        //				size = new Dimension((int) (size.width / zoom),
        //						(int) (size.height / zoom));
        //
        //				for (Object child : editPart.getChildren()) {
        //					if (child instanceof NodeElementEditPart) {
        //						NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) child;
        //						Rectangle bounds = nodeElementEditPart.getFigure()
        //								.getBounds();
        //
        //						if (bounds.x > point.x
        //								&& bounds.x + bounds.width < point.x + size.width
        //								&& bounds.y > point.y
        //								&& bounds.y + bounds.height < point.y + size.height) {
        //							enclosedElementList
        //									.add((NodeElement) nodeElementEditPart
        //											.getModel());
        //						}
        //					}
        //				}
        //			}
        //			return new CreateElementCommand(diagram, element, point.x, point.y,
        //					size, enclosedElementList);
        //		}
        final AbstractModelEditPart editPart = (AbstractModelEditPart) this.getHost();
        final Point point = request.getLocation();
        editPart.getFigure().translateToRelative(point);
        final DiagramWalker element = (DiagramWalker) request.getNewObject(); // e.g. table, note
        final ERDiagram diagram = ERModelUtil.getDiagram(editPart);
        Dimension size = request.getSize();
        final List<DiagramWalker> enclosedElementList = new ArrayList<DiagramWalker>();
        if (size != null) {
            final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this.getHost().getRoot()).getZoomManager();
            final double zoom = zoomManager.getZoom();
            size = new Dimension((int) (size.width / zoom), (int) (size.height / zoom));
            for (final Object child : editPart.getChildren()) {
                if (child instanceof DiagramWalkerEditPart) {
                    final DiagramWalkerEditPart nodeElementEditPart = (DiagramWalkerEditPart) child;
                    final Rectangle bounds = nodeElementEditPart.getFigure().getBounds();
                    if (bounds.x > point.x && bounds.x + bounds.width < point.x + size.width && bounds.y > point.y
                            && bounds.y + bounds.height < point.y + size.height) {
                        enclosedElementList.add((DiagramWalker) nodeElementEditPart.getModel());
                    }
                }
            }
        }
        return new CreateElementCommand(diagram, element, point.x, point.y, size, enclosedElementList);
    }

    @Override
    protected EditPolicy createChildEditPolicy(EditPart child) {
        return new DiagramWalkerSelectionEditPolicy();
    }

    @Override
    public Command getCommand(Request request) {
        if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE.equals(request.getType())) {
            final DirectEditRequest editRequest = (DirectEditRequest) request;
            final Object feature = editRequest.getDirectEditFeature();
            if (feature instanceof ERTable) {
                final ERTable ertable = (ERTable) feature;
                return new PlaceTableCommand(ertable);
            }
            if (feature instanceof List) {
                final List<?> list = (List<?>) feature;
                return new PlaceTableCommand(list);
            }
        }
        return super.getCommand(request);
    }
}

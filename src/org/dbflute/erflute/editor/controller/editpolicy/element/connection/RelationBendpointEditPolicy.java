package org.dbflute.erflute.editor.controller.editpolicy.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.bendpoint.MoveRelationBendpointCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.connection.RelationEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.handles.BendpointMoveHandle;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.swt.SWT;

public class RelationBendpointEditPolicy extends ERDiagramBendpointEditPolicy {

    @Override
    protected void showMoveBendpointFeedback(BendpointRequest bendpointrequest) {
        final Relationship relation = (Relationship) getHost().getModel();
        final RelationEditPart editPart = (RelationEditPart) getHost();

        if (relation.getSourceWalker() == relation.getTargetWalker()) {
            if (bendpointrequest.getIndex() != 1) {
                return;
            }
            final Point point = bendpointrequest.getLocation();
            getConnection().translateToRelative(point);
            final Bendpoint rate = getRate(point);
            rate.setRelative(true);

            final float rateX = (100f - (rate.getX() / 2)) / 100;
            final float rateY = (100f - (rate.getY() / 2)) / 100;

            final ERTableEditPart tableEditPart = (ERTableEditPart) editPart.getSource();
            final Rectangle bounds = tableEditPart.getFigure().getBounds();

            final Rectangle rect = new Rectangle();
            rect.x = (int) (bounds.x + (bounds.width * rateX));
            rect.y = (int) (bounds.y + (bounds.height * rateY));
            rect.width = (int) (bounds.width * rate.getX() / 100);
            rect.height = (int) (bounds.height * rate.getY() / 100);

            relation.setSourceLocationp(100, (int) (100 * rateY));

            relation.setTargetLocationp((int) (100 * rateX), 100);

            final LayerManager manager = (LayerManager) tableEditPart.getRoot();
            final IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);
            getFeedbackLayer().setBounds(layer.getBounds());

            final List children = getFeedbackLayer().getChildren();
            children.clear();
            getFeedbackLayer().repaint();

            final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
            final double zoom = zoomManager.getZoom();

            final Polyline feedbackFigure = new Polyline();
            feedbackFigure.addPoint(new Point((int) (rect.x * zoom), (int) (rect.y * zoom)));
            feedbackFigure.addPoint(new Point((int) (rect.x * zoom), (int) ((rect.y + rect.height) * zoom)));
            feedbackFigure.addPoint(new Point((int) ((rect.x + rect.width) * zoom), (int) ((rect.y + rect.height) * zoom)));
            feedbackFigure.addPoint(new Point((int) ((rect.x + rect.width) * zoom), (int) (rect.y * zoom)));
            feedbackFigure.addPoint(new Point((int) (rect.x * zoom), (int) (rect.y * zoom)));

            feedbackFigure.setLineStyle(SWT.LINE_DASH);

            feedbackFigure.translateToRelative(feedbackFigure.getLocation());

            addFeedback(feedbackFigure);

        } else {
            super.showMoveBendpointFeedback(bendpointrequest);
        }
    }

    @Override
    protected void showCreateBendpointFeedback(BendpointRequest bendpointrequest) {
        final Relationship relation = (Relationship) getHost().getModel();

        if (relation.getSourceWalker() == relation.getTargetWalker()) {
            return;
        }
        super.showCreateBendpointFeedback(bendpointrequest);
    }

    @Override
    protected void eraseConnectionFeedback(BendpointRequest request) {
        getFeedbackLayer().getChildren().clear();
        super.eraseConnectionFeedback(request);
    }

    @Override
    protected Command getMoveBendpointCommand(BendpointRequest bendpointrequest) {
        final Relationship relation = (Relationship) getHost().getModel();
        final RelationEditPart editPart = (RelationEditPart) getHost();

        if (relation.getSourceWalker() == relation.getTargetWalker()) {
            if (bendpointrequest.getIndex() != 1) {
                return null;

            } else {
                final Point point = bendpointrequest.getLocation();
                final Bendpoint rate = getRate(point);

                final MoveRelationBendpointCommand command =
                        new MoveRelationBendpointCommand(editPart, rate.getX(), rate.getY(), bendpointrequest.getIndex());

                return command;
            }
        }

        final Point point = bendpointrequest.getLocation();
        getConnection().translateToRelative(point);

        final MoveRelationBendpointCommand command =
                new MoveRelationBendpointCommand(editPart, point.x, point.y, bendpointrequest.getIndex());

        return command;
    }

    private Bendpoint getRate(Point point) {
        final RelationEditPart editPart = (RelationEditPart) getHost();

        final ERTableEditPart tableEditPart = (ERTableEditPart) editPart.getSource();
        final Rectangle rectangle = tableEditPart.getFigure().getBounds();

        final int xRate = (point.x - rectangle.x - rectangle.width) * 200 / rectangle.width;
        final int yRate = (point.y - rectangle.y - rectangle.height) * 200 / rectangle.height;

        return new Bendpoint(xRate, yRate);
    }

    @Override
    protected void showSelection() {
        super.showSelection();

        final RelationEditPart editPart = (RelationEditPart) getHost();
        editPart.refresh();
    }

    @Override
    protected void hideSelection() {
        super.hideSelection();

        final RelationEditPart editPart = (RelationEditPart) getHost();
        editPart.refresh();
    }

    @Override
    protected List createSelectionHandles() {
        final Relationship relation = (Relationship) getHost().getModel();
        if (relation.getSourceWalker() == relation.getTargetWalker()) {
            showSelectedLine();

            if (getHost().getRoot().getContents() instanceof ERDiagramEditPart) {
                // TODO ymd ここを通るケースを確認できず。消すかもしれない。
                final ERDiagramEditPart diagramEditPart = (ERDiagramEditPart) getHost().getRoot().getContents();
                diagramEditPart.refreshVisuals();
            }

            final List<BendpointMoveHandle> list = new ArrayList<>();
            list.add(new BendpointMoveHandle((ConnectionEditPart) getHost(), 1, 2));
            return list;
        }

        return super.createSelectionHandles();
    }
}

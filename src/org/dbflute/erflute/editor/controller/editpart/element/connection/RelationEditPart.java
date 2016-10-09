package org.dbflute.erflute.editor.controller.editpart.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.ChangeRelationshipPropertyCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.connection.RelationBendpointEditPolicy;
import org.dbflute.erflute.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.view.dialog.relationship.RelationshipDialog;
import org.dbflute.erflute.editor.view.figure.anchor.XYChopboxAnchor;
import org.dbflute.erflute.editor.view.figure.connection.ERDiagramConnection;
import org.dbflute.erflute.editor.view.figure.connection.decoration.DecorationFactory;
import org.dbflute.erflute.editor.view.figure.connection.decoration.DecorationFactory.Decoration;
import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;

public class RelationEditPart extends ERDiagramConnectionEditPart {

    private Label targetLabel;

    @Override
    protected IFigure createFigure() {
        final boolean bezier = this.getDiagram().getDiagramContents().getSettings().isUseBezierCurve();
        final PolylineConnection connection = new ERDiagramConnection(bezier);
        connection.setConnectionRouter(new BendpointConnectionRouter());

        final ConnectionEndpointLocator targetLocator = new ConnectionEndpointLocator(connection, true);
        this.targetLabel = new Label("");
        connection.add(targetLabel, targetLocator);

        return connection;
    }

    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        this.installEditPolicy(EditPolicy.CONNECTION_ROLE, new RelationEditPolicy());
        this.installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new RelationBendpointEditPolicy());
    }

    @Override
    protected void refreshBendpoints() {
        try {
            // ベンド・ポイントの位置情報の取得
            final Relationship relation = (Relationship) this.getModel();

            // 実際のベンド・ポイントのリスト
            final List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();

            for (final Bendpoint bendPoint : relation.getBendpoints()) {
                if (bendPoint.isRelative()) {

                    final ERTableEditPart tableEditPart = (ERTableEditPart) this.getSource();
                    if (tableEditPart != null) {
                        Rectangle bounds = tableEditPart.getFigure().getBounds();
                        int width = bounds.width;
                        int height = bounds.height;

                        if (width == 0) {
                            // tableEditPart.getFigure().getUpdateManager()
                            // .performUpdate();

                            bounds = tableEditPart.getFigure().getBounds();
                            width = bounds.width;
                            height = bounds.height;
                        }

                        RelativeBendpoint point = new RelativeBendpoint();

                        final int xp = relation.getTargetXp();
                        int x;

                        if (xp == -1) {
                            x = bounds.x + bounds.width;
                        } else {
                            x = bounds.x + (bounds.width * xp / 100);
                        }

                        point.setRelativeDimensions(new Dimension(width * bendPoint.getX() / 100 - bounds.x - bounds.width + x, 0),
                                new Dimension(width * bendPoint.getX() / 100 - bounds.x - bounds.width + x, 0));
                        point.setWeight(0);
                        point.setConnection(this.getConnectionFigure());

                        constraint.add(point);

                        point = new RelativeBendpoint();
                        point.setRelativeDimensions(new Dimension(width * bendPoint.getX() / 100 - bounds.x - bounds.width + x, height
                                * bendPoint.getY() / 100), new Dimension(width * bendPoint.getX() / 100 - bounds.x - bounds.width + x,
                                height * bendPoint.getY() / 100));
                        point.setWeight(0);
                        point.setConnection(this.getConnectionFigure());

                        constraint.add(point);

                        point = new RelativeBendpoint();
                        point.setRelativeDimensions(new Dimension(x - bounds.x - bounds.width, height * bendPoint.getY() / 100),
                                new Dimension(x - bounds.x - bounds.width, height * bendPoint.getY() / 100));
                        point.setWeight(0);
                        point.setConnection(this.getConnectionFigure());

                        constraint.add(point);
                    }

                } else {
                    constraint.add(new AbsoluteBendpoint(bendPoint.getX(), bendPoint.getY()));
                }

            }

            this.getConnectionFigure().setRoutingConstraint(constraint);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    @Override
    protected void refreshVisuals() {
        super.refreshVisuals();
        final ERDiagram diagram = this.getDiagram();
        if (diagram != null) {
            final Relationship relation = (Relationship) this.getModel();
            final PolylineConnection connection = (PolylineConnection) this.getConnectionFigure();
            final String notation = diagram.getDiagramContents().getSettings().getNotation();
            final Decoration decoration =
                    DecorationFactory.getDecoration(notation, relation.getParentCardinality(), relation.getChildCardinality());
            connection.setSourceDecoration(decoration.getSourceDecoration());
            connection.setTargetDecoration(decoration.getTargetDecoration());
            targetLabel.setText(Format.null2blank(decoration.getTargetLabel()));
        }
        this.calculateAnchorLocation();
        this.refreshBendpoints();
    }

    @Override
    public void performRequest(Request request) {
        final Relationship relation = (Relationship) this.getModel();
        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final Relationship copy = relation.copy();
            final RelationshipDialog dialog = new RelationshipDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);
            if (dialog.open() == IDialogConstants.OK_ID) {
                final ChangeRelationshipPropertyCommand command = new ChangeRelationshipPropertyCommand(relation, copy);
                this.getViewer().getEditDomain().getCommandStack().execute(command);
            }
        }
        super.performRequest(request);
    }

    private void calculateAnchorLocation() {
        final Relationship relation = (Relationship) this.getModel();
        final TableViewEditPart sourceEditPart = (TableViewEditPart) this.getSource();
        Point sourcePoint = null;
        Point targetPoint = null;
        if (sourceEditPart != null && relation.getSourceXp() != -1 && relation.getSourceYp() != -1) {
            final Rectangle bounds = sourceEditPart.getFigure().getBounds();
            sourcePoint =
                    new Point(bounds.x + (bounds.width * relation.getSourceXp() / 100), bounds.y
                            + (bounds.height * relation.getSourceYp() / 100));
        }
        final TableViewEditPart targetEditPart = (TableViewEditPart) this.getTarget();
        if (targetEditPart != null && relation.getTargetXp() != -1 && relation.getTargetYp() != -1) {
            final Rectangle bounds = targetEditPart.getFigure().getBounds();
            targetPoint =
                    new Point(bounds.x + (bounds.width * relation.getTargetXp() / 100), bounds.y
                            + (bounds.height * relation.getTargetYp() / 100));
        }
        final ConnectionAnchor sourceAnchor = this.getConnectionFigure().getSourceAnchor();
        if (sourceAnchor instanceof XYChopboxAnchor) {
            ((XYChopboxAnchor) sourceAnchor).setLocation(sourcePoint);
        }
        final ConnectionAnchor targetAnchor = this.getConnectionFigure().getTargetAnchor();
        if (targetAnchor instanceof XYChopboxAnchor) {
            ((XYChopboxAnchor) targetAnchor).setLocation(targetPoint);
        }
    }
}

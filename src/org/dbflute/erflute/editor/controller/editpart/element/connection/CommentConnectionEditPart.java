package org.dbflute.erflute.editor.controller.editpart.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.editpolicy.element.connection.CommentConnectionEditPolicy;
import org.dbflute.erflute.editor.controller.editpolicy.element.connection.ERDiagramBendpointEditPolicy;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.view.figure.connection.ERDiagramConnection;
import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.SWT;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class CommentConnectionEditPart extends ERDiagramConnectionEditPart {

    @Override
    protected IFigure createFigure() {
        final boolean bezier = getDiagram().getDiagramContents().getSettings().isUseBezierCurve();
        final PolylineConnection connection = new ERDiagramConnection(bezier);
        connection.setConnectionRouter(new BendpointConnectionRouter());
        connection.setLineStyle(SWT.LINE_DASH);
        return connection;
    }

    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        installEditPolicy(EditPolicy.CONNECTION_ROLE, new CommentConnectionEditPolicy());
        installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new ERDiagramBendpointEditPolicy());
    }

    @Override
    protected void refreshBendpoints() {
        // ベンド・ポイントの位置情報の取得
        final WalkerConnection connection = (WalkerConnection) getModel();

        // 実際のベンド・ポイントのリスト
        final List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<>();

        for (final Bendpoint bendPoint : connection.getBendpoints()) {
            constraint.add(new AbsoluteBendpoint(bendPoint.getX(), bendPoint.getY()));
        }
        getConnectionFigure().setRoutingConstraint(constraint);
    }
}

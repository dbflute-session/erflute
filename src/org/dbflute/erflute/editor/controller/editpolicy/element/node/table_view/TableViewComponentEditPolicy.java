package org.dbflute.erflute.editor.controller.editpolicy.element.node.table_view;

import java.util.Map;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.AddColumnGroupCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.AddWordCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.view.drag_drop.ERDiagramTransferDragSourceListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.DirectEditRequest;

public class TableViewComponentEditPolicy extends DiagramWalkerComponentEditPolicy {

    @Override
    public void showTargetFeedback(Request request) {
        super.showTargetFeedback(request);
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP.equals(request.getType())
                || ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
            final DirectEditRequest editRequest = (DirectEditRequest) request;
            final TableView tableView = (TableView) getHost().getModel();
            final ColumnGroup columnGroup = (ColumnGroup) ((Map<?, ?>) editRequest.getDirectEditFeature()).get("group");
            if (!tableView.getColumns().contains(columnGroup)) {
                return getHost();
            }
        } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD.equals(request.getType())) {
            return getHost();
        } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN.equals(request.getType())) {
            return getHost();
        }

        return super.getTargetEditPart(request);
    }

    @Override
    public Command getCommand(Request request) {
        try {
            if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP.equals(request.getType())) {
                final TableView tableView = (TableView) getHost().getModel();
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                final ColumnGroup columnGroup = (ColumnGroup) ((Map<?, ?>) editRequest.getDirectEditFeature()).get("group");
                if (!tableView.getColumns().contains(columnGroup)) {
                    return new AddColumnGroupCommand(tableView, columnGroup, getColumnIndex(editRequest));
                }
            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD.equals(request.getType())) {
                final TableView table = (TableView) getHost().getModel();
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                final Word word = (Word) editRequest.getDirectEditFeature();
                return new AddWordCommand(table, word, getColumnIndex(editRequest));
            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                return ColumnSelectionHandlesEditPolicy.createMoveColumnCommand(
                        editRequest, getHost().getViewer(), (TableView) getHost().getModel(), getColumnIndex(editRequest));
            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                return ColumnSelectionHandlesEditPolicy.createMoveColumnGroupCommand(
                        editRequest, (TableView) getHost().getModel(), getColumnIndex(editRequest));
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }

        return super.getCommand(request);
    }

    private int getColumnIndex(DirectEditRequest editRequest) {
        final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
        final double zoom = zoomManager.getZoom();

        final IFigure figure = ((TableViewEditPart) getHost()).getFigure();
        final int center = (int) (figure.getBounds().y + (figure.getBounds().height / 2) * zoom);

        int index = 0;
        if (editRequest.getLocation().y >= center) {
            final TableView newTableView = (TableView) getHost().getModel();
            index = newTableView.getColumns().size();
        }

        return index;
    }
}

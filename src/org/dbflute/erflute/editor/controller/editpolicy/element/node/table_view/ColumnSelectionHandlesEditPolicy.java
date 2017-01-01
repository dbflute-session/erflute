package org.dbflute.erflute.editor.controller.editpolicy.element.node.table_view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.CreateRelationshipByNewColumnCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.DeleteRelationshipCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.AddColumnGroupCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.AddWordCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.ChangeColumnOrderCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.view.drag_drop.ERDiagramTransferDragSourceListener;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

public class ColumnSelectionHandlesEditPolicy extends NonResizableEditPolicy {

    @Override
    protected List<Object> createSelectionHandles() {
        final List<Object> list = new ArrayList<Object>();
        getHost().getRoot().getContents().refresh();
        // NonResizableHandleKit.addHandles((GraphicalEditPart) getHost(), list,
        // new SelectEditPartTracker(getHost()), SharedCursors.ARROW);
        return list;
    }

    @Override
    public void showTargetFeedback(Request request) {
        if (request instanceof DirectEditRequest) {
            final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this.getHost().getRoot()).getZoomManager();
            final double zoom = zoomManager.getZoom();
            final Rectangle columnRectangle = this.getColumnRectangle();
            final int center = (int) ((columnRectangle.y + (columnRectangle.height / 2)) * zoom);
            final DirectEditRequest directEditRequest = (DirectEditRequest) request;
            int y = 0;
            if (directEditRequest.getLocation().y < center) {
                y = columnRectangle.y - 1;
            } else {
                y = columnRectangle.y + columnRectangle.height - 1;
            }
            final RectangleFigure feedbackFigure = new RectangleFigure();
            feedbackFigure.setForegroundColor(ColorConstants.lightGray);
            feedbackFigure.setBackgroundColor(ColorConstants.lightGray);
            feedbackFigure.setBounds(new Rectangle((int) (zoom * columnRectangle.x), (int) (zoom * y),
                    (int) (zoom * columnRectangle.width), (int) (zoom * 2)));
            final LayerManager manager = (LayerManager) this.getHost().getRoot();
            final IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);
            final IFigure feedbackLayer = this.getFeedbackLayer();
            final List<?> children = this.getFeedbackLayer().getChildren();
            children.clear();
            feedbackLayer.setBounds(layer.getBounds());
            feedbackLayer.add(feedbackFigure);
            feedbackLayer.repaint();
        }
        super.showTargetFeedback(request);
    }

    @Override
    public void eraseTargetFeedback(Request request) {
        if (request instanceof DirectEditRequest) {
            this.getFeedbackLayer().getChildren().clear();
        }
        super.eraseTargetFeedback(request);
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP.equals(request.getType())
                || ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
            final DirectEditRequest editRequest = (DirectEditRequest) request;
            final TableView tableView = (TableView) this.getHost().getParent().getModel();
            final ColumnGroup columnGroup =
                    (ColumnGroup) ((Map<?, ?>) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);
            final Object parent =
                    ((Map<?, ?>) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT);
            if (parent == tableView || !tableView.getColumns().contains(columnGroup)) {
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
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                final TableView tableView = (TableView) this.getHost().getParent().getModel();
                final ColumnGroup columnGroup =
                        (ColumnGroup) ((Map<?, ?>) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);
                if (!tableView.getColumns().contains(columnGroup)) {
                    return new AddColumnGroupCommand(tableView, columnGroup, this.getColumnIndex(editRequest));
                }
            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                final TableView table = (TableView) this.getHost().getParent().getModel();
                final Word word = (Word) editRequest.getDirectEditFeature();
                return new AddWordCommand(table, word, this.getColumnIndex(editRequest));
            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                final TableView newTableView = (TableView) this.getHost().getParent().getModel();
                return createMoveColumnCommand(editRequest, this.getHost().getViewer(), newTableView, this.getColumnIndex(editRequest));
            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;
                final TableView newTableView = (TableView) this.getHost().getParent().getModel();
                return createMoveColumnGroupCommand(editRequest, newTableView, this.getColumnIndex(editRequest));
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        return super.getCommand(request);
    }

    public static Command createMoveColumnCommand(DirectEditRequest editRequest, EditPartViewer viewer, TableView newTableView, int index) {
        final NormalColumn oldColumn = (NormalColumn) editRequest.getDirectEditFeature();
        final TableView oldTableView = (TableView) oldColumn.getColumnHolder();
        if (newTableView == oldTableView) {
            return new ChangeColumnOrderCommand(newTableView, oldColumn, index);
        }
        final CompoundCommand command = new CompoundCommand();
        final List<Relationship> relationList = oldColumn.getOutgoingRelationList();
        if (!relationList.isEmpty()) {
            Activator.showErrorDialog("error.reference.key.not.moveable");
            return null;
        } else if (oldColumn.isForeignKey()) {
            final Relationship oldRelationship = oldColumn.getRelationshipList().get(0);
            final TableView referredTableView = oldRelationship.getSourceTableView();
            if (ERTable.isRecursive(referredTableView, newTableView)) {
                Activator.showErrorDialog("error.recursive.relation");
                return null;
            }
            final DeleteRelationshipCommand deleteOldRelationCommand = new DeleteRelationshipCommand(oldRelationship, true);
            command.add(deleteOldRelationCommand);
            final Relationship newRelation =
                    new Relationship(oldRelationship.isReferenceForPK(), oldRelationship.getReferredCompoundUniqueKey(),
                            oldRelationship.getReferredSimpleUniqueColumn());
            final List<NormalColumn> oldForeignKeyColumnList = new ArrayList<NormalColumn>();
            if (referredTableView == newTableView) {
                Activator.showErrorDialog("error.foreign.key.not.moveable.to.reference.table");
                return null;
            }
            if (oldRelationship.isReferenceForPK()) {
                for (final NormalColumn referencedPrimaryKey : ((ERTable) referredTableView).getPrimaryKeys()) {
                    for (final NormalColumn oldTableColumn : oldTableView.getNormalColumns()) {
                        if (oldTableColumn.isForeignKey()) {
                            if (oldTableColumn.getReferredColumn(oldRelationship) == referencedPrimaryKey) {
                                oldForeignKeyColumnList.add(oldTableColumn);
                                break;
                            }
                        }
                    }
                }
            } else if (oldRelationship.getReferredCompoundUniqueKey() != null) {
                for (final NormalColumn referredColumn : oldRelationship.getReferredCompoundUniqueKey().getColumnList()) {
                    for (final NormalColumn oldTableColumn : oldTableView.getNormalColumns()) {
                        if (oldTableColumn.isForeignKey()) {
                            if (oldTableColumn.getReferredColumn(oldRelationship) == referredColumn) {
                                oldForeignKeyColumnList.add(oldTableColumn);
                                break;
                            }
                        }
                    }
                }
            } else {
                oldForeignKeyColumnList.add(oldColumn);
            }
            for (final NormalColumn oldForeignKey : oldForeignKeyColumnList) {
                final List<Relationship> oldRelationList = oldForeignKey.getOutgoingRelationList();
                if (!oldRelationList.isEmpty()) {
                    Activator.showErrorDialog("error.reference.key.not.moveable");
                    return null;
                }
            }
            final CreateRelationshipByNewColumnCommand createNewRelationCommand =
                    new CreateRelationshipByNewColumnCommand(newRelation, oldForeignKeyColumnList);
            final EditPart sourceEditPart = (EditPart) viewer.getEditPartRegistry().get(referredTableView);
            final EditPart targetEditPart = (EditPart) viewer.getEditPartRegistry().get(newTableView);
            createNewRelationCommand.setSource(sourceEditPart);
            createNewRelationCommand.setTarget(targetEditPart);
            command.add(createNewRelationCommand);
        } else {
            final TableView copyOldTableView = oldTableView.copyData();
            for (final NormalColumn column : copyOldTableView.getNormalColumns()) {
                final CopyColumn copyColumn = (CopyColumn) column;
                if (copyColumn.getOriginalColumn() == oldColumn) {
                    copyOldTableView.removeColumn(copyColumn);
                    break;
                }
            }
            final ChangeTableViewPropertyCommand sourceTableCommand = new ChangeTableViewPropertyCommand(oldTableView, copyOldTableView);
            command.add(sourceTableCommand);
            final TableView copyNewTableView = newTableView.copyData();
            final CopyColumn copyColumn = new CopyColumn(oldColumn);
            copyColumn.setWord(new CopyWord(oldColumn.getWord()));
            copyNewTableView.addColumn(index, copyColumn);
            final ChangeTableViewPropertyCommand targetTableCommand = new ChangeTableViewPropertyCommand(newTableView, copyNewTableView);
            command.add(targetTableCommand);
        }
        return command.unwrap();
    }

    public static Command createMoveColumnGroupCommand(DirectEditRequest editRequest, TableView newTableView, int index) {
        final ColumnGroup columnGroup =
                (ColumnGroup) ((Map<?, ?>) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);
        final TableView oldTableView =
                (TableView) ((Map<?, ?>) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT);
        if (newTableView == oldTableView) {
            return new ChangeColumnOrderCommand(newTableView, columnGroup, index);
        }
        final CompoundCommand command = new CompoundCommand();
        final TableView copyOldTableView = oldTableView.copyData();
        for (final ERColumn column : copyOldTableView.getColumns()) {
            if (column == columnGroup) {
                copyOldTableView.removeColumn(column);
                break;
            }
        }
        final ChangeTableViewPropertyCommand sourceTableCommand = new ChangeTableViewPropertyCommand(oldTableView, copyOldTableView);
        command.add(sourceTableCommand);
        if (!newTableView.getColumns().contains(columnGroup)) {
            command.add(new AddColumnGroupCommand(newTableView, columnGroup, index));
        }
        return command.unwrap();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private int getColumnIndex(DirectEditRequest editRequest) {
        final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this.getHost().getRoot()).getZoomManager();
        final double zoom = zoomManager.getZoom();
        final ColumnEditPart columnEditPart = (ColumnEditPart) this.getHost();
        ERColumn column = (ERColumn) columnEditPart.getModel();
        final TableView newTableView = (TableView) this.getHost().getParent().getModel();
        final List<ERColumn> columns = newTableView.getColumns();
        if (column.getColumnHolder() instanceof ColumnGroup) {
            column = (ColumnGroup) column.getColumnHolder();
        }
        int index = columns.indexOf(column);
        final Rectangle columnRectangle = this.getColumnRectangle();
        final int center = (int) ((columnRectangle.y + (columnRectangle.height / 2)) * zoom);
        if (editRequest.getLocation().y >= center) {
            index++;
        }
        return index;
    }

    private Rectangle getColumnRectangle() {
        final ColumnEditPart columnEditPart = (ColumnEditPart) this.getHost();
        final NormalColumn column = (NormalColumn) columnEditPart.getModel();
        final IFigure figure = columnEditPart.getFigure();
        final Rectangle rect = figure.getBounds();
        int startY = 0;
        int endY = 0;
        if (column.getColumnHolder() instanceof ColumnGroup) {
            final ColumnGroup columnGroup = (ColumnGroup) column.getColumnHolder();
            final NormalColumn firstColumn = columnGroup.getColumns().get(0);
            final NormalColumn finalColumn = columnGroup.getColumns().get(columnGroup.getColumns().size() - 1);
            for (final Object editPart : columnEditPart.getParent().getChildren()) {
                final NormalColumnEditPart normalColumnEditPart = (NormalColumnEditPart) editPart;
                if (normalColumnEditPart.getModel() == firstColumn) {
                    final Rectangle bounds = normalColumnEditPart.getFigure().getBounds();
                    startY = bounds.y;
                } else if (normalColumnEditPart.getModel() == finalColumn) {
                    final Rectangle bounds = normalColumnEditPart.getFigure().getBounds();
                    endY = bounds.y + bounds.height;
                }
            }
        } else {
            startY = rect.y;
            endY = rect.y + rect.height;
        }
        return new Rectangle(rect.x, startY, rect.width, endY - startY);
    }
}

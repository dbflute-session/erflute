package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.connection.RelationEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.GroupColumnEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.index.IndexEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.table_view.TableViewComponentEditPolicy;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.table_view.TableViewGraphicalNodeEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.figure.anchor.XYChopboxAnchor;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.dbflute.erflute.editor.view.figure.table.column.GroupColumnFigure;
import org.dbflute.erflute.editor.view.figure.table.column.NormalColumnFigure;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * @author modified by jflute (originated in ermaster)
 */
public abstract class TableViewEditPart extends DiagramWalkerEditPart implements IResizable {

    private Font titleFont;

    @Override
    protected List<Object> getModelChildren() {
        final List<Object> modelChildren = new ArrayList<Object>();
        final TableView tableView = (TableView) getModel();
        final ERDiagram diagram = getDiagram();
        if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
            modelChildren.addAll(tableView.getExpandedColumns());
        } else {
            modelChildren.addAll(tableView.getColumns());
        }
        if (tableView instanceof ERTable) {
            modelChildren.addAll(((ERTable) tableView).getIndexes());
        }
        Activator.trace(this, "getModelChildren()", "...Preparing model children: " + modelChildren.size());
        return modelChildren;
    }

    @Override
    protected List<WalkerConnection> getModelSourceConnections() {
        final List<WalkerConnection> filteredList = filterConnections(super.getModelSourceConnections());
        Activator.trace(this, "getModelSourceConnections()", "...Preparing model source connections: " + filteredList.size());
        return filteredList;
    }

    @Override
    protected List<WalkerConnection> getModelTargetConnections() {
        final List<WalkerConnection> filteredList = filterConnections(super.getModelTargetConnections());
        Activator.trace(this, "getModelTargetConnections()", "...Preparing model target connections: " + filteredList.size());
        return filteredList;
    }

    protected List<WalkerConnection> filterConnections(final List<WalkerConnection> connections) {
        final List<WalkerConnection> filteredList = new ArrayList<WalkerConnection>();
        for (final WalkerConnection connection : connections) { // #for_erflute
            if (isVirtualDiagram()) {
                if (connection.isVirtualDiagramOnly()) {
                    filteredList.add(connection);
                }
            } else {
                if (!connection.isVirtualDiagramOnly()) {
                    filteredList.add(connection);
                }
            }
        }
        return filteredList;
    }

    protected boolean isVirtualDiagram() {
        return false;
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(TableView.PROPERTY_CHANGE_PHYSICAL_NAME)) {
            refreshVisuals();
        } else if (event.getPropertyName().equals(TableView.PROPERTY_CHANGE_LOGICAL_NAME)) {
            refreshVisuals();
        } else if (event.getPropertyName().equals(TableView.PROPERTY_CHANGE_COLUMNS)) {
            this.refreshChildren();
            refreshVisuals();
        }
        super.doPropertyChange(event);
        this.refreshConnections();
    }

    @Override
    public void refresh() {
        super.refresh();
        this.refreshConnections();
    }

    @Override
    public void refreshVisuals() {
        try {
            final TableFigure tableFigure = (TableFigure) this.getFigure();
            final TableView tableView = (TableView) this.getModel();
            tableFigure.create(tableView.getColor());
            final ERDiagram diagram = this.getDiagram();
            tableFigure.setName(getTableViewName(tableView, diagram));
            for (final Object child : this.getChildren()) {
                if (child instanceof ColumnEditPart) {
                    final ColumnEditPart part = (ColumnEditPart) child;
                    part.refreshTableColumns();
                }
                if (child instanceof IndexEditPart) {
                    final IndexEditPart part = (IndexEditPart) child;
                    part.refreshTableColumns();
                }
                //              if (diagram.isShowMainColumn()) {
                //              } else {
                //                  part.refreshTableColumns(updated);
                //              }
            }
            super.refreshVisuals();
            if (ERDiagramEditPart.isUpdateable()) {
                this.getFigure().getUpdateManager().performValidation();
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    public static void showRemovedColumns(ERDiagram diagram, ERTable table, TableFigure tableFigure, Collection<ERColumn> removedColumns,
            boolean isRemoved) {
        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();
        for (final ERColumn removedColumn : removedColumns) {
            if (removedColumn instanceof ColumnGroup) {
                if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
                    final ColumnGroup columnGroup = (ColumnGroup) removedColumn;
                    for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                        if (notationLevel == Settings.NOTATION_LEVLE_KEY && !normalColumn.isPrimaryKey() && !normalColumn.isForeignKey()
                                && !normalColumn.isReferedStrictly()) {
                            continue;
                        }
                        final NormalColumnFigure columnFigure = new NormalColumnFigure();
                        tableFigure.getColumns().add(columnFigure);
                        NormalColumnEditPart.addColumnFigure(diagram, table, tableFigure, columnFigure, normalColumn, false, false, false,
                                false, isRemoved);
                    }
                } else {
                    if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
                        continue;
                    }
                    final GroupColumnFigure columnFigure = new GroupColumnFigure();
                    tableFigure.getColumns().add(columnFigure);
                    GroupColumnEditPart.addGroupColumnFigure(diagram, tableFigure, columnFigure, removedColumn, false, false, isRemoved);
                }
            } else {
                final NormalColumn normalColumn = (NormalColumn) removedColumn;
                if (notationLevel == Settings.NOTATION_LEVLE_KEY && !normalColumn.isPrimaryKey() && !normalColumn.isForeignKey()
                        && !normalColumn.isReferedStrictly()) {
                    continue;
                }
                final NormalColumnFigure columnFigure = new NormalColumnFigure();
                tableFigure.getColumns().add(columnFigure);
                NormalColumnEditPart.addColumnFigure(diagram, table, tableFigure, columnFigure, normalColumn, false, false, false, false,
                        isRemoved);
            }
        }
    }

    @Override
    public void changeSettings(Settings settings) {
        final TableFigure figure = (TableFigure) this.getFigure();
        figure.setSettings(settings);
        super.changeSettings(settings);
    }

    @Override
    protected void disposeFont() {
        if (this.titleFont != null) {
            this.titleFont.dispose();
        }
        super.disposeFont();
    }

    protected Font changeFont(TableFigure tableFigure) {
        final Font font = super.changeFont(tableFigure);

        final FontData fonData = font.getFontData()[0];

        this.titleFont = new Font(Display.getCurrent(), fonData.getName(), fonData.getHeight(), SWT.BOLD);

        tableFigure.setFont(font, this.titleFont);

        return font;
    }

    public static String getTableViewName(TableView tableView, ERDiagram diagram) {
        String name = null;

        final int viewMode = diagram.getDiagramContents().getSettings().getViewMode();

        if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
            name = diagram.filter(tableView.getPhysicalName());

        } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
            name = diagram.filter(tableView.getLogicalName());

        } else {
            name = diagram.filter(tableView.getLogicalName()) + " / " + diagram.filter(tableView.getPhysicalName());
        }

        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart editPart) {
        if (!(editPart instanceof RelationEditPart)) {
            return super.getSourceConnectionAnchor(editPart);
        }

        final Relationship relation = (Relationship) editPart.getModel();

        final Rectangle bounds = this.getFigure().getBounds();

        final XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

        if (relation.getSourceXp() != -1 && relation.getSourceYp() != -1) {
            anchor.setLocation(new Point(bounds.x + (bounds.width * relation.getSourceXp() / 100), bounds.y
                    + (bounds.height * relation.getSourceYp() / 100)));
        }

        return anchor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
        if (request instanceof ReconnectRequest) {
            final ReconnectRequest reconnectRequest = (ReconnectRequest) request;

            final ConnectionEditPart connectionEditPart = reconnectRequest.getConnectionEditPart();

            if (!(connectionEditPart instanceof RelationEditPart)) {
                return super.getSourceConnectionAnchor(request);
            }

            final Relationship relation = (Relationship) connectionEditPart.getModel();
            if (relation.getWalkerSource() == relation.getWalkerTarget()) {
                return new XYChopboxAnchor(this.getFigure());
            }

            final EditPart editPart = reconnectRequest.getTarget();

            if (editPart == null || !editPart.getModel().equals(relation.getWalkerSource())) {
                return new XYChopboxAnchor(this.getFigure());
            }

            final Point location = new Point(reconnectRequest.getLocation());
            this.getFigure().translateToRelative(location);
            final IFigure sourceFigure = ((TableViewEditPart) connectionEditPart.getSource()).getFigure();

            final XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

            final Rectangle bounds = sourceFigure.getBounds();

            final Rectangle centerRectangle =
                    new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);

            if (!centerRectangle.contains(location)) {
                final Point point = getIntersectionPoint(location, sourceFigure);
                anchor.setLocation(point);
            }

            return anchor;

        } else if (request instanceof CreateConnectionRequest) {
            final CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

            final Command command = connectionRequest.getStartCommand();

            if (command instanceof CreateCommentConnectionCommand) {
                return super.getTargetConnectionAnchor(request);
            }
        }

        return new XYChopboxAnchor(this.getFigure());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart editPart) {
        if (!(editPart instanceof RelationEditPart)) {
            return super.getTargetConnectionAnchor(editPart);
        }

        final Relationship relation = (Relationship) editPart.getModel();

        final XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

        final Rectangle bounds = this.getFigure().getBounds();

        if (relation.getTargetXp() != -1 && relation.getTargetYp() != -1) {
            anchor.setLocation(new Point(bounds.x + (bounds.width * relation.getTargetXp() / 100), bounds.y
                    + (bounds.height * relation.getTargetYp() / 100)));
        }

        return anchor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        if (request instanceof ReconnectRequest) {
            final ReconnectRequest reconnectRequest = (ReconnectRequest) request;

            final ConnectionEditPart connectionEditPart = reconnectRequest.getConnectionEditPart();

            if (!(connectionEditPart instanceof RelationEditPart)) {
                return super.getTargetConnectionAnchor(request);
            }

            final Relationship relation = (Relationship) connectionEditPart.getModel();
            if (relation.getWalkerSource() == relation.getWalkerTarget()) {
                return new XYChopboxAnchor(this.getFigure());
            }

            final EditPart editPart = reconnectRequest.getTarget();

            if (editPart == null || !editPart.getModel().equals(relation.getWalkerTarget())) {
                return new XYChopboxAnchor(this.getFigure());
            }

            final Point location = new Point(reconnectRequest.getLocation());
            this.getFigure().translateToRelative(location);
            final IFigure targetFigure = ((TableViewEditPart) connectionEditPart.getTarget()).getFigure();

            final XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

            final Rectangle bounds = targetFigure.getBounds();

            final Rectangle centerRectangle =
                    new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);

            if (!centerRectangle.contains(location)) {
                final Point point = getIntersectionPoint(location, targetFigure);
                anchor.setLocation(point);
            }

            return anchor;

        } else if (request instanceof CreateConnectionRequest) {
            final CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

            final Command command = connectionRequest.getStartCommand();

            if (command instanceof CreateCommentConnectionCommand) {
                return super.getTargetConnectionAnchor(request);
            }
        }

        return new XYChopboxAnchor(this.getFigure());
    }

    public static Point getIntersectionPoint(Point s, IFigure figure) {

        final Rectangle r = figure.getBounds();

        final int x1 = s.x - r.x;
        final int x2 = r.x + r.width - s.x;
        final int y1 = s.y - r.y;
        final int y2 = r.y + r.height - s.y;

        int x = 0;
        int dx = 0;
        if (x1 < x2) {
            x = r.x;
            dx = x1;

        } else {
            x = r.x + r.width;
            dx = x2;
        }

        int y = 0;
        int dy = 0;

        if (y1 < y2) {
            y = r.y;
            dy = y1;

        } else {
            y = r.y + r.height;
            dy = y2;
        }

        if (dx < dy) {
            y = s.y;
        } else {
            x = s.x;
        }

        return new Point(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFigure getContentPane() {
        final TableFigure figure = (TableFigure) super.getContentPane();

        return figure.getColumns();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new TableViewComponentEditPolicy());
        this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new TableViewGraphicalNodeEditPolicy());
    }
}

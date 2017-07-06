package org.dbflute.erflute.editor.controller.editpart.element.node.column;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.dbflute.erflute.editor.view.figure.table.column.NormalColumnFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class NormalColumnEditPart extends ColumnEditPart {

    private boolean selected;

    @Override
    protected IFigure createFigure() {
        final NormalColumnFigure figure = new NormalColumnFigure();
        return figure;
    }

    @Override
    public void refreshTableColumns() {
        final ERDiagram diagram = getDiagram();
        final NormalColumnFigure columnFigure = (NormalColumnFigure) getFigure();
        final NormalColumn normalColumn = (NormalColumn) getModel();
        if (diagram.isShowMainColumn()) {
            if (normalColumn.isAutoIncrement() || normalColumn.getWord() == null) {
                // 表示する
            } else {
                return; // 表示しない
            }
        }

        final TableViewEditPart parent = (TableViewEditPart) getParent();
        parent.getContentPane().add(figure);

        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

        if (notationLevel != DiagramSettings.NOTATION_LEVLE_TITLE) {
            final TableFigure tableFigure = (TableFigure) parent.getFigure();

            final List<NormalColumn> selectedReferencedColulmnList = getSelectedReferencedColulmnList();
            final List<NormalColumn> selectedForeignKeyColulmnList = getSelectedForeignKeyColulmnList();

            final boolean isSelectedReferenced = selectedReferencedColulmnList.contains(normalColumn);
            final boolean isSelectedForeignKey = selectedForeignKeyColulmnList.contains(normalColumn);

            final boolean isAdded = false;
            final boolean isUpdated = false;

            if ((notationLevel == DiagramSettings.NOTATION_LEVLE_KEY) && !normalColumn.isPrimaryKey()
                    && !normalColumn.isForeignKey() && !normalColumn.isReferedStrictly()) {
                columnFigure.clearLabel();
                return;
            }

            final ERTable table = (ERTable) parent.getModel(); // TODO

            addColumnFigure(diagram, table, tableFigure, columnFigure, normalColumn,
                    isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, false);

            if (selected) {
                columnFigure.setBackgroundColor(ColorConstants.titleBackground);
                columnFigure.setForegroundColor(ColorConstants.titleForeground);
            }
        } else {
            columnFigure.clearLabel();
            return;
        }
    }

    public static void addColumnFigure(ERDiagram diagram, ERTable table, TableFigure tableFigure,
            NormalColumnFigure columnFigure, NormalColumn normalColumn, boolean isSelectedReferenced,
            boolean isSelectedForeignKey, boolean isAdded, boolean isUpdated, boolean isRemoved) {
        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

        final String type = diagram.filter(Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), diagram.getDatabase()));

        boolean displayKey = true;
        if (notationLevel == DiagramSettings.NOTATION_LEVLE_COLUMN) {
            displayKey = false;
        }

        boolean displayDetail = false;
        if (notationLevel == DiagramSettings.NOTATION_LEVLE_KEY || notationLevel == DiagramSettings.NOTATION_LEVLE_EXCLUDE_TYPE
                || notationLevel == DiagramSettings.NOTATION_LEVLE_DETAIL) {
            displayDetail = true;
        }

        boolean displayType = false;
        if (notationLevel == DiagramSettings.NOTATION_LEVLE_DETAIL) {
            displayType = true;
        }

        //		List<ERVirtualTable> tables = diagram.getCurrentErmodel().getTables();
        //		for (ERVirtualTable vtable : tables) {
        //			tableFigure.getName();
        //			//vtable.getName()
        //
        //		}

        //diagram.getDiagramContents().get

        tableFigure.addColumn(table, normalColumn, columnFigure, diagram.getDiagramContents().getSettings().getViewMode(),
                diagram.filter(normalColumn.getPhysicalName()), diagram.filter(normalColumn.getLogicalName()), type,
                normalColumn.isPrimaryKey(), normalColumn.isForeignKey(), normalColumn.isNotNull(), normalColumn.isUniqueKey(), displayKey,
                displayDetail, displayType, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);
    }

    private List<NormalColumn> getSelectedReferencedColulmnList() {
        final List<NormalColumn> referencedColulmnList = new ArrayList<>();

        final TableViewEditPart parent = (TableViewEditPart) getParent();
        final TableView tableView = (TableView) parent.getModel();

        for (final Object object : parent.getSourceConnections()) {
            final ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;

            final int selected = connectionEditPart.getSelected();

            if (selected == EditPart.SELECTED || selected == EditPart.SELECTED_PRIMARY) {
                final WalkerConnection connectionElement = (WalkerConnection) connectionEditPart.getModel();

                if (connectionElement instanceof Relationship) {
                    final Relationship relation = (Relationship) connectionElement;

                    if (relation.isReferenceForPK()) {
                        referencedColulmnList.addAll(((ERTable) tableView).getPrimaryKeys());

                    } else if (relation.getReferredCompoundUniqueKey() != null) {
                        referencedColulmnList.addAll(relation.getReferredCompoundUniqueKey().getColumnList());

                    } else {
                        referencedColulmnList.add(relation.getReferredSimpleUniqueColumn());
                    }
                }
            }
        }
        return referencedColulmnList;
    }

    private List<NormalColumn> getSelectedForeignKeyColulmnList() {
        final List<NormalColumn> foreignKeyColulmnList = new ArrayList<>();
        final TableViewEditPart parent = (TableViewEditPart) getParent();
        for (final Object object : parent.getTargetConnections()) {
            final ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;
            final int selected = connectionEditPart.getSelected();
            if (selected == EditPart.SELECTED || selected == EditPart.SELECTED_PRIMARY) {
                final WalkerConnection connectionElement = (WalkerConnection) connectionEditPart.getModel();
                if (connectionElement instanceof Relationship) {
                    final Relationship relation = (Relationship) connectionElement;
                    foreignKeyColulmnList.addAll(relation.getForeignKeyColumns());
                }
            }
        }
        return foreignKeyColulmnList;
    }

    @Override
    public void setSelected(int value) {
        final NormalColumnFigure figure = (NormalColumnFigure) getFigure();
        if (value != 0 && getParent() != null && getParent().getParent() != null) {
            final List<?> selectedEditParts = getViewer().getSelectedEditParts();
            if (selectedEditParts != null && selectedEditParts.size() == 1) {
                final NormalColumn normalColumn = (NormalColumn) getModel();
                if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
                    for (final Object child : getParent().getChildren()) {
                        final AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;
                        final NormalColumn column = (NormalColumn) childEditPart.getModel();
                        if (column.getColumnHolder() == normalColumn.getColumnHolder()) {
                            setGroupColumnFigureColor((TableViewEditPart) getParent(),
                                    (ColumnGroup) normalColumn.getColumnHolder(), true);
                        }
                    }
                } else {
                    figure.setBackgroundColor(ColorConstants.titleBackground);
                    figure.setForegroundColor(ColorConstants.titleForeground);
                    selected = true;
                }
                super.setSelected(value);
            }
        } else {
            final NormalColumn normalColumn = (NormalColumn) getModel();
            if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
                for (final Object child : getParent().getChildren()) {
                    final AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;
                    final NormalColumn column = (NormalColumn) childEditPart.getModel();
                    if (column.getColumnHolder() == normalColumn.getColumnHolder()) {
                        setGroupColumnFigureColor((TableViewEditPart) getParent(),
                                (ColumnGroup) normalColumn.getColumnHolder(), false);
                    }
                }
            } else {
                figure.setBackgroundColor(null);
                figure.setForegroundColor(null);
                selected = false;
            }
            super.setSelected(value);
        }
    }

    private void setGroupColumnFigureColor(TableViewEditPart parentEditPart, ColumnGroup columnGroup, boolean selected) {
        for (final NormalColumn column : columnGroup.getColumns()) {
            for (final Object editPart : parentEditPart.getChildren()) {
                final NormalColumnEditPart childEditPart = (NormalColumnEditPart) editPart;
                if (childEditPart.getModel() == column) {
                    final NormalColumnFigure columnFigure = (NormalColumnFigure) childEditPart.getFigure();
                    if (selected) {
                        columnFigure.setBackgroundColor(ColorConstants.titleBackground);
                        columnFigure.setForegroundColor(ColorConstants.titleForeground);
                    } else {
                        columnFigure.setBackgroundColor(null);
                        columnFigure.setForegroundColor(null);
                    }

                    childEditPart.selected = selected;
                    break;
                }
            }
        }
    }
}

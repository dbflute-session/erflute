package org.dbflute.erflute.editor.controller.editpart.element.node.column;

import java.util.List;

import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.dbflute.erflute.editor.view.figure.table.column.GroupColumnFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;

public class GroupColumnEditPart extends ColumnEditPart {

    private boolean selected;

    @Override
    protected IFigure createFigure() {
        return new GroupColumnFigure();
    }

    @Override
    public void refreshTableColumns() {
        final ERDiagram diagram = this.getDiagram();
        final GroupColumnFigure columnFigure = (GroupColumnFigure) this.getFigure();
        final TableViewEditPart parent = (TableViewEditPart) this.getParent();
        parent.getContentPane().add(figure);
        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();
        final ERColumn column = (ERColumn) this.getModel();
        if (notationLevel != DiagramSettings.NOTATION_LEVLE_TITLE) {
            final TableFigure tableFigure = (TableFigure) parent.getFigure();
            final boolean isAdded = false;
            final boolean isUpdated = false;
            if ((notationLevel == DiagramSettings.NOTATION_LEVLE_KEY)) {
                columnFigure.clearLabel();
                return;
            }
            addGroupColumnFigure(diagram, tableFigure, columnFigure, column, isAdded, isUpdated, false);
            if (selected) {
                columnFigure.setBackgroundColor(ColorConstants.titleBackground);
                columnFigure.setForegroundColor(ColorConstants.titleForeground);
            }
        } else {
            columnFigure.clearLabel();
            return;
        }
    }

    public static void addGroupColumnFigure(ERDiagram diagram, TableFigure tableFigure, GroupColumnFigure columnFigure, ERColumn column,
            boolean isAdded, boolean isUpdated, boolean isRemoved) {

        final ColumnGroup groupColumn = (ColumnGroup) column;

        tableFigure.addColumnGroup(columnFigure, diagram.getDiagramContents().getSettings().getViewMode(),
                diagram.filter(groupColumn.getName()), isAdded, isUpdated, isRemoved);
    }

    @Override
    public void setSelected(int value) {
        final GroupColumnFigure figure = (GroupColumnFigure) this.getFigure();

        if (value != 0 && this.getParent() != null && this.getParent().getParent() != null) {
            final List selectedEditParts = this.getViewer().getSelectedEditParts();

            if (selectedEditParts != null && selectedEditParts.size() == 1) {
                figure.setBackgroundColor(ColorConstants.titleBackground);
                figure.setForegroundColor(ColorConstants.titleForeground);
                selected = true;

                super.setSelected(value);
            }
        } else {
            figure.setBackgroundColor(null);
            figure.setForegroundColor(null);
            selected = false;

            super.setSelected(value);
        }
    }
}

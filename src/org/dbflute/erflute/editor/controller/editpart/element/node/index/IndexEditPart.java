package org.dbflute.erflute.editor.controller.editpart.element.node.index;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.dbflute.erflute.editor.controller.editpart.element.AbstractModelEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.figure.table.IndexFigure;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.internal.ui.rulers.GuideEditPart;

//#for_now jflute cannot test so suppress warning only (2020/05/16)
@SuppressWarnings("restriction")
public class IndexEditPart extends AbstractModelEditPart {

    private boolean selected;

    @Override
    protected void createEditPolicies() {
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        final EditPart editPart = super.getTargetEditPart(request);

        if (!getDiagram().isDisableSelectColumn()) {
            return editPart;
        }

        if (editPart != null) {
            return editPart.getParent();
        }

        return null;
    }

    @Override
    protected IFigure createFigure() {
        final IndexFigure figure = new IndexFigure();
        return figure;
    }

    public void refreshTableColumns() {
        final ERDiagram diagram = getDiagram();
        final IndexFigure indexFigure = (IndexFigure) getFigure();
        final ERIndex index = (ERIndex) getModel();
        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

        final TableViewEditPart parent = (TableViewEditPart) getParent();
        final List<?> figures = parent.getContentPane().getChildren();
        boolean isFirst = false;
        if (!(figures.get(figures.size() - 1) instanceof IndexFigure)) {
            if (notationLevel != DiagramSettings.NOTATION_LEVLE_TITLE) {
                isFirst = true;
                parent.getContentPane().add(new GuideEditPart.GuideLineFigure());
                final Label indexHeader = new Label();
                indexHeader.setLabelAlignment(PositionConstants.LEFT);
                indexHeader.setText("<< index >>");
                indexHeader.setBorder(new MarginBorder(new Insets(4, 3, 0, 0)));

                parent.getContentPane().add(indexHeader);
            }
        }
        parent.getContentPane().add(figure);
        if (notationLevel != DiagramSettings.NOTATION_LEVLE_TITLE) {
            final TableFigure tableFigure = (TableFigure) parent.getFigure();

            addColumnFigure(diagram, tableFigure, indexFigure, index, isFirst, false);

            if (selected) {
                indexFigure.setBackgroundColor(ColorConstants.titleBackground);
                indexFigure.setForegroundColor(ColorConstants.titleForeground);
            }
        } else {
            indexFigure.clearLabel();
            return;
        }
    }

    public static void addColumnFigure(ERDiagram diagram, TableFigure tableFigure,
            IndexFigure indexFigure, ERIndex index, boolean isFirst, boolean isRemoved) {
        tableFigure.addIndex(indexFigure, diagram.getDiagramContents().getSettings().getViewMode(),
                diagram.filter(index.getName()), diagram.filter(index.getName()), isFirst);
    }
}

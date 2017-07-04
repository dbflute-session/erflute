package org.dbflute.erflute.editor.view.action.ermodel;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class PlaceTableAction extends AbstractBaseAction {

    public static final String ID = PlaceTableAction.class.getName();
    private final VirtualDiagramEditor oneEditor;

    public PlaceTableAction(VirtualDiagramEditor editor) {
        super(ID, "Locate Table", editor);
        this.oneEditor = editor;
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = getDiagram();

        final List<ERTable> input = new ArrayList<>();
        input.addAll(diagram.getDiagramContents().getDiagramWalkers().getTableSet().getList());

        final NodeSelectionDialog dialog =
                new NodeSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        final FigureCanvas canvas = (FigureCanvas) oneEditor.getGraphicalViewer().getControl();
        final Point point =
                new Point(canvas.getHorizontalBar().getSelection() + canvas.getClientArea().width / 2, canvas.getVerticalBar()
                        .getSelection() + canvas.getClientArea().height / 2);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final Object[] results = dialog.getResult();
            for (final Object result : results) {
                final ERTable curTable = (ERTable) result;
                final ERVirtualTable virtualTable = new ERVirtualTable(oneEditor.getVirtualDiagram(), curTable);
                virtualTable.setPoint(point.x, point.y);
                getDiagram().addWalkerPlainly(virtualTable);
            }
        }
    }
}

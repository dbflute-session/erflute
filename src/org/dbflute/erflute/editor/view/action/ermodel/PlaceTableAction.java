package org.dbflute.erflute.editor.view.action.ermodel;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.VirtualModelEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
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
    private VirtualModelEditor oneEditor;

    public PlaceTableAction(VirtualModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.ermodel.place.table"), editor);
        this.oneEditor = editor;
    }

    @Override
    public void execute(Event event) throws Exception {
        ERDiagram diagram = this.getDiagram();
        ERModel model = oneEditor.getModel();

        List<ERTable> input = new ArrayList<ERTable>();
        input.addAll(diagram.getDiagramContents().getContents().getTableSet().getList());

        NodeSelectionDialog dialog = new NodeSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        FigureCanvas canvas = (FigureCanvas) oneEditor.getGraphicalViewer().getControl();
        Point point =
                new Point(canvas.getHorizontalBar().getSelection() + canvas.getClientArea().width / 2, canvas.getVerticalBar()
                        .getSelection() + canvas.getClientArea().height / 2);

        if (dialog.open() == IDialogConstants.OK_ID) {
            Object[] results = dialog.getResult();
            for (Object result : results) {
                ERTable curTable = (ERTable) result;
                ERVirtualTable virtualTable = new ERVirtualTable(model, curTable);
                virtualTable.setPoint(point.x, point.y);
                model.addTable(virtualTable);
                //				oneEditor.setContents(model);
                //				oneEditor.refreshContents();
                //				model.changeAll();
            }

        }

    }

}

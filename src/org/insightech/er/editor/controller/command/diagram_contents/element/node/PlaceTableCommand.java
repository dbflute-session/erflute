package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.SubModelEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.view.dialog.dbexport.ErrorDialog;

public class PlaceTableCommand extends AbstractCommand {

    private ERTable orgTable;
    private List<ERTable> orgTables;

    private ERVirtualTable virtualTable;
    private List<ERVirtualTable> virtualTables;

    public PlaceTableCommand(ERTable orgTable) {
        this.orgTable = orgTable;
    }

    public PlaceTableCommand(List orgTables) {
        this.orgTables = orgTables;
    }

    @Override
    protected void doExecute() {

        if (orgTables != null) {
            // �����z�u
            SubModelEditor modelEditor = (SubModelEditor) orgTables.get(0).getDiagram().getEditor().getActiveEditor();

            Point cursorLocation = Display.getCurrent().getCursorLocation();
            Point point = modelEditor.getGraphicalViewer().getControl().toControl(cursorLocation);
            FigureCanvas canvas = (FigureCanvas) modelEditor.getGraphicalViewer().getControl();
            point.x += canvas.getHorizontalBar().getSelection();
            point.y += canvas.getVerticalBar().getSelection();

            virtualTables = new ArrayList<ERVirtualTable>();
            for (ERTable curTable : orgTables) {
                boolean cantPlace = false;
                for (ERVirtualTable vtable : modelEditor.getModel().getTables()) {
                    if (vtable.getRawTable().equals(curTable)) {
                        cantPlace = true;
                    }
                }
                if (cantPlace)
                    continue;

                ERModel model = curTable.getDiagram().getCurrentErmodel();

                virtualTable = new ERVirtualTable(model, curTable);
                virtualTable.setPoint(point.x, point.y);
                model.addTable(virtualTable);
                virtualTables.add(virtualTable);
                // ����E���ɂ��炵�Ĕz�u
                point.x += 32;
                point.y += 32;
            }
            //			modelEditor.setContents(orgTables.get(0).getDiagram().getCurrentErmodel());
            modelEditor.refresh();
        } else {
            ERTable curTable = orgTable;

            SubModelEditor modelEditor = (SubModelEditor) curTable.getDiagram().getEditor().getActiveEditor();

            // ���Ƀr���[��ɓ���e�[�u������������u���Ȃ�
            for (ERVirtualTable vtable : modelEditor.getModel().getTables()) {
                if (vtable.getRawTable().equals(curTable)) {
                    ErrorDialog dialog =
                            new ErrorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                    "���ɂ��̃e�[�u���̓r���[�ɔz�u����Ă��܂��B�r���[���ɓ���e�[�u���͈�����u���܂���B");
                    dialog.open();
                    return;
                }
            }

            Point cursorLocation = Display.getCurrent().getCursorLocation();
            Point point = modelEditor.getGraphicalViewer().getControl().toControl(cursorLocation);
            FigureCanvas canvas = (FigureCanvas) modelEditor.getGraphicalViewer().getControl();
            point.x += canvas.getHorizontalBar().getSelection();
            point.y += canvas.getVerticalBar().getSelection();

            ERModel model = curTable.getDiagram().getCurrentErmodel();

            virtualTable = new ERVirtualTable(model, curTable);
            virtualTable.setPoint(point.x, point.y);
            model.addTable(virtualTable);
            //			modelEditor.setContents(model);
            modelEditor.refresh();
        }
    }

    @Override
    protected void doUndo() {
        if (orgTables != null) {
            ERModel model = orgTables.get(0).getDiagram().getCurrentErmodel();

            for (ERVirtualTable vtable : virtualTables) {
                model.remove(vtable);
            }

            SubModelEditor modelEditor = (SubModelEditor) orgTables.get(0).getDiagram().getEditor().getActiveEditor();
            modelEditor.setContents(model);
        } else {
            ERModel model = orgTable.getDiagram().getCurrentErmodel();
            model.remove(virtualTable);

            SubModelEditor modelEditor = (SubModelEditor) orgTable.getDiagram().getEditor().getActiveEditor();
            modelEditor.setContents(model);
        }
    }

}

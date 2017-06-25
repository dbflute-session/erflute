package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.view.dialog.dbexport.ErrorDialog;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

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
            // 複数配置
            final VirtualDiagramEditor modelEditor = (VirtualDiagramEditor) orgTables.get(0).getDiagram().getEditor().getActiveEditor();

            final Point cursorLocation = Display.getCurrent().getCursorLocation();
            final Point point = modelEditor.getGraphicalViewer().getControl().toControl(cursorLocation);
            final FigureCanvas canvas = (FigureCanvas) modelEditor.getGraphicalViewer().getControl();
            point.x += canvas.getHorizontalBar().getSelection();
            point.y += canvas.getVerticalBar().getSelection();

            virtualTables = new ArrayList<>();
            for (final ERTable curTable : orgTables) {
                boolean cantPlace = false;
                for (final ERVirtualTable vtable : modelEditor.getVirtualDiagram().getVirtualTables()) {
                    if (vtable.getRawTable().equals(curTable)) {
                        cantPlace = true;
                    }
                }
                if (cantPlace)
                    continue;

                final ERVirtualDiagram model = curTable.getDiagram().getCurrentVirtualDiagram();

                virtualTable = new ERVirtualTable(model, curTable);
                virtualTable.setPoint(point.x, point.y);
                model.addTable(virtualTable);
                virtualTables.add(virtualTable);
                // 一つずつ右下にずらして配置
                point.x += 32;
                point.y += 32;
            }
            ERModelUtil.refreshDiagram(modelEditor.getDiagram());
        } else {
            final ERTable curTable = orgTable;

            final VirtualDiagramEditor modelEditor = (VirtualDiagramEditor) curTable.getDiagram().getEditor().getActiveEditor();

            // 既にビュー上に同一テーブルがあったら置けない
            for (final ERVirtualTable vtable : modelEditor.getVirtualDiagram().getVirtualTables()) {
                if (vtable.getRawTable().equals(curTable)) {
                    final ErrorDialog dialog = new ErrorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            "既にこのテーブルはビューに配置されています。ビュー内に同一テーブルは一つしか置けません。");
                    dialog.open();
                    return;
                }
            }

            final Point cursorLocation = Display.getCurrent().getCursorLocation();
            final Point point = modelEditor.getGraphicalViewer().getControl().toControl(cursorLocation);
            final FigureCanvas canvas = (FigureCanvas) modelEditor.getGraphicalViewer().getControl();
            point.x += canvas.getHorizontalBar().getSelection();
            point.y += canvas.getVerticalBar().getSelection();

            final ERVirtualDiagram model = curTable.getDiagram().getCurrentVirtualDiagram();

            virtualTable = new ERVirtualTable(model, curTable);
            virtualTable.setPoint(point.x, point.y);
            model.addTable(virtualTable);
            ERModelUtil.refreshDiagram(modelEditor.getDiagram());
        }
    }

    @Override
    protected void doUndo() {
        if (orgTables != null) {
            final ERVirtualDiagram model = orgTables.get(0).getDiagram().getCurrentVirtualDiagram();

            for (final ERVirtualTable vtable : virtualTables) {
                model.remove(vtable);
            }

            final VirtualDiagramEditor modelEditor = (VirtualDiagramEditor) orgTables.get(0).getDiagram().getEditor().getActiveEditor();
            modelEditor.setContents(model);
        } else {
            final ERVirtualDiagram model = orgTable.getDiagram().getCurrentVirtualDiagram();
            model.remove(virtualTable);

            final VirtualDiagramEditor modelEditor = (VirtualDiagramEditor) orgTable.getDiagram().getEditor().getActiveEditor();
            modelEditor.setContents(model);
        }
    }
}

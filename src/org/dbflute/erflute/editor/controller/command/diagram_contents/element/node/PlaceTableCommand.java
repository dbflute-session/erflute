package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
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

    public PlaceTableCommand(List<ERTable> orgTables) {
        this.orgTables = orgTables;
    }

    @Override
    protected void doExecute() {
        VirtualDiagramEditor editor;
        if (orgTables != null) {
            // 複数配置
            editor = (VirtualDiagramEditor) orgTables.get(0).getDiagram().getEditor().getActiveEditor();

            final Point cursorLocation = Display.getCurrent().getCursorLocation();
            final Point point = editor.getGraphicalViewer().getControl().toControl(cursorLocation);
            final FigureCanvas canvas = (FigureCanvas) editor.getGraphicalViewer().getControl();
            point.x += canvas.getHorizontalBar().getSelection();
            point.y += canvas.getVerticalBar().getSelection();

            virtualTables = new ArrayList<>();
            for (final ERTable curTable : orgTables) {
                boolean cantPlace = false;
                for (final ERVirtualTable vtable : editor.getVirtualDiagram().getVirtualTables()) {
                    if (vtable.getRawTable().equals(curTable)) {
                        cantPlace = true;
                    }
                }
                if (cantPlace) {
                    continue;
                }

                virtualTable = new ERVirtualTable(curTable.getDiagram().getCurrentVirtualDiagram(), curTable);
                virtualTable.setPoint(point.x, point.y);
                orgTables.get(0).getDiagram().addWalkerPlainly(virtualTable);

                // 一つずつ右下にずらして配置
                point.x += 32;
                point.y += 32;
            }
        } else {
            editor = (VirtualDiagramEditor) orgTable.getDiagram().getEditor().getActiveEditor();

            // 既にビュー上に同一テーブルがあったら置けない
            for (final ERVirtualTable vtable : editor.getVirtualDiagram().getVirtualTables()) {
                if (vtable.getRawTable().equals(orgTable)) {
                    final ErrorDialog dialog = new ErrorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            "既にこのテーブルはビューに配置されています。ビュー内に同一テーブルは一つしか置けません。");
                    dialog.open();
                    return;
                }
            }

            final Point cursorLocation = Display.getCurrent().getCursorLocation();
            final Point point = editor.getGraphicalViewer().getControl().toControl(cursorLocation);
            final FigureCanvas canvas = (FigureCanvas) editor.getGraphicalViewer().getControl();
            point.x += canvas.getHorizontalBar().getSelection();
            point.y += canvas.getVerticalBar().getSelection();

            virtualTable = new ERVirtualTable(orgTable.getDiagram().getCurrentVirtualDiagram(), orgTable);
            virtualTable.setPoint(point.x, point.y);
            orgTable.getDiagram().addWalkerPlainly(virtualTable);
        }

        ERModelUtil.refreshDiagram(editor.getDiagram());
    }

    @Override
    protected void doUndo() {
        if (orgTables != null) {
            final ERDiagram diagram = orgTables.get(0).getDiagram();
            for (final ERVirtualTable vtable : virtualTables) {
                diagram.removeWalker(vtable);
            }

            final VirtualDiagramEditor editor = (VirtualDiagramEditor) diagram.getEditor().getActiveEditor();
            editor.setContents(diagram.getCurrentVirtualDiagram());
        } else {
            final ERDiagram diagram = orgTable.getDiagram();
            diagram.removeWalker(virtualTable);

            final VirtualDiagramEditor editor = (VirtualDiagramEditor) diagram.getEditor().getActiveEditor();
            editor.setContents(diagram.getCurrentVirtualDiagram());
        }
    }
}

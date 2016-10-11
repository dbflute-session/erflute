package org.dbflute.erflute.editor.controller.command.common;

import java.util.ArrayList;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;

public class PasteCommand extends AbstractCommand {

    private ERDiagram diagram;

    private final GraphicalViewer viewer;
    private final DiagramWalkerSet walkers;
    private final ColumnGroupSet columnGroups;

    public PasteCommand(MainDiagramEditor editor, DiagramWalkerSet walkers, int x, int y) {
        this.viewer = editor.getGraphicalViewer();
        final Object model = viewer.getContents().getModel();
        if (model instanceof ERDiagram) {
            this.diagram = (ERDiagram) model;
        }
        if (model instanceof ERVirtualDiagram) {
            this.diagram = ((ERVirtualDiagram) model).getDiagram();
        }

        this.walkers = walkers;

        this.columnGroups = new ColumnGroupSet();

        // 貼り付け対象に対して処理を繰り返します
        for (final DiagramWalker walker : walkers) {
            walker.setLocation(new Location(walker.getX() + x, walker.getY() + y, walker.getWidth(), walker.getHeight()));

            // 貼り付け対象がテーブルの場合
            if (walker instanceof ERTable) {
                final ERTable table = (ERTable) walker;
                if (table instanceof ERVirtualTable) {
                    final ERTable rawTable = ((ERVirtualTable) table).getRawTable();
                    rawTable.setIncoming(new ArrayList<WalkerConnection>());
                    rawTable.setOutgoing(new ArrayList<WalkerConnection>());
                    for (final ERColumn column : rawTable.getColumns()) {
                        if (column instanceof NormalColumn) {
                            ((NormalColumn) column).clearRelations();
                        }
                    }
                }

                // 列に対して処理を繰り返します
                for (final ERColumn column : table.getColumns()) {

                    // 列がグループ列の場合
                    if (column instanceof ColumnGroup) {
                        final ColumnGroup group = (ColumnGroup) column;

                        // この図のグループ列でない場合
                        if (!diagram.getDiagramContents().getColumnGroupSet().contains(group)) {
                            // 対象のグループ列に追加します。
                            columnGroups.add(group);
                        }
                    }
                }
            }
        }
    }

    /**
     * 貼り付け処理を実行する
     */
    @Override
    protected void doExecute() {
        // 描画更新をとめます。
        ERDiagramEditPart.setUpdateable(false);

        final ColumnGroupSet columnGroupSet = this.diagram.getDiagramContents().getColumnGroupSet();

        // 図にノードを追加します。
        for (final DiagramWalker nodeElement : this.walkers) {
            if (nodeElement instanceof ERVirtualTable) {
                this.diagram.addContent(((ERVirtualTable) nodeElement).getRawTable());
            } else {
                this.diagram.addContent(nodeElement);
            }
        }

        // グループ列を追加します。
        for (final ColumnGroup columnGroup : this.columnGroups) {
            columnGroupSet.add(columnGroup);
        }

        // 描画更新を再開します。
        ERDiagramEditPart.setUpdateable(true);

        this.diagram.changeAll();

        // 貼り付けられたテーブルを選択状態にします。
        this.setFocus();
    }

    /**
     * 貼り付け処理を元に戻す
     */
    @Override
    protected void doUndo() {
        // 描画更新をとめます。
        ERDiagramEditPart.setUpdateable(false);

        final ColumnGroupSet columnGroupSet = this.diagram.getDiagramContents().getColumnGroupSet();

        // 図からノードを削除します。
        for (final DiagramWalker nodeElement : this.walkers) {
            this.diagram.removeContent(nodeElement);
        }

        // グループ列を削除します。
        for (final ColumnGroup columnGroup : this.columnGroups) {
            columnGroupSet.remove(columnGroup);
        }

        // 描画更新を再開します。
        ERDiagramEditPart.setUpdateable(true);

        this.diagram.changeAll();
    }

    /**
     * 貼り付けられたテーブルを選択状態にします。
     */
    private void setFocus() {
        // 貼り付けられたテーブルを選択状態にします。
        for (final DiagramWalker nodeElement : this.walkers) {
            final EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(nodeElement);

            if (editPart != null) {
                this.viewer.getSelectionManager().appendSelection(editPart);
            }
        }
    }
}

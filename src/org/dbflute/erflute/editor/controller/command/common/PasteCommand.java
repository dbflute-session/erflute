package org.dbflute.erflute.editor.controller.command.common;

import java.util.ArrayList;

import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GroupSet;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;

public class PasteCommand extends AbstractCommand {

    private ERDiagram diagram;

    private GraphicalViewer viewer;

    // 貼り付け対象の一覧
    private NodeSet nodeElements;

    // 貼り付け時に追加するグループ列の一覧
    private GroupSet columnGroups;

    /**
     * 貼り付けコマンドを作成します。
     *
     * @param editor
     * @param nodeElements
     * @param x
     * @param y
     */
    public PasteCommand(RealModelEditor editor, NodeSet nodeElements, int x, int y) {
        this.viewer = editor.getGraphicalViewer();
        Object model = viewer.getContents().getModel();
        if (model instanceof ERDiagram) {
            this.diagram = (ERDiagram) model;
        }
        if (model instanceof ERModel) {
            this.diagram = ((ERModel) model).getDiagram();
        }

        this.nodeElements = nodeElements;

        this.columnGroups = new GroupSet();

        // 貼り付け対象に対して処理を繰り返します
        for (NodeElement nodeElement : nodeElements) {
            nodeElement.setLocation(new Location(nodeElement.getX() + x, nodeElement.getY() + y, nodeElement.getWidth(), nodeElement
                    .getHeight()));

            // 貼り付け対象がテーブルの場合
            if (nodeElement instanceof ERTable) {

                ERTable table = (ERTable) nodeElement;

                if (table instanceof ERVirtualTable) {
                    ERTable rawTable = ((ERVirtualTable) table).getRawTable();
                    rawTable.setIncoming(new ArrayList<ConnectionElement>());
                    rawTable.setOutgoing(new ArrayList<ConnectionElement>());
                    for (ERColumn column : rawTable.getColumns()) {
                        if (column instanceof NormalColumn) {
                            ((NormalColumn) column).clearRelations();
                        }
                    }
                }

                // 列に対して処理を繰り返します
                for (ERColumn column : table.getColumns()) {

                    // 列がグループ列の場合
                    if (column instanceof ColumnGroup) {
                        ColumnGroup group = (ColumnGroup) column;

                        // この図のグループ列でない場合
                        if (!diagram.getDiagramContents().getGroups().contains(group)) {
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

        GroupSet columnGroupSet = this.diagram.getDiagramContents().getGroups();

        // 図にノードを追加します。
        for (NodeElement nodeElement : this.nodeElements) {
            if (nodeElement instanceof ERVirtualTable) {
                this.diagram.addContent(((ERVirtualTable) nodeElement).getRawTable());
            } else {
                this.diagram.addContent(nodeElement);
            }
        }

        // グループ列を追加します。
        for (ColumnGroup columnGroup : this.columnGroups) {
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

        GroupSet columnGroupSet = this.diagram.getDiagramContents().getGroups();

        // 図からノードを削除します。
        for (NodeElement nodeElement : this.nodeElements) {
            this.diagram.removeContent(nodeElement);
        }

        // グループ列を削除します。
        for (ColumnGroup columnGroup : this.columnGroups) {
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
        for (NodeElement nodeElement : this.nodeElements) {
            EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(nodeElement);

            if (editPart != null) {
                this.viewer.getSelectionManager().appendSelection(editPart);
            }
        }
    }
}

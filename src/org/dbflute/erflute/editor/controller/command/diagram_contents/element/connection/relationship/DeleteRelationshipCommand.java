package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.eclipse.swt.SWT;

public class DeleteRelationshipCommand extends DeleteConnectionCommand {

    private TableView oldTargetCopyTable;
    private final TableView oldTargetTable;
    private final Relationship relationship;
    private Boolean removeForeignKey;
    private Map<NormalColumn, NormalColumn> referencedColumnMap;

    public DeleteRelationshipCommand(Relationship relationship, Boolean removeForeignKey) {
        super(relationship);

        this.relationship = relationship;
        this.oldTargetTable = relationship.getTargetTableView();
        this.removeForeignKey = removeForeignKey;
        this.referencedColumnMap = new HashMap<>();
    }

    @Override
    protected void doExecute() {
        if (oldTargetCopyTable == null) {
            for (final NormalColumn foreignKey : relationship.getForeignKeyColumns()) {
                final NormalColumn referencedColumn = foreignKey.getReferredColumn(relationship);
                referencedColumnMap.put(foreignKey, referencedColumn);
            }
            this.oldTargetCopyTable = oldTargetTable.copyData();
        }

        final Dictionary dictionary = oldTargetTable.getDiagram().getDiagramContents().getDictionary();

        relationship.delete(removeForeignKey, dictionary);
        relationship.getTargetTableView().getDiagram().change();

        if (relationship.getSourceWalker() instanceof ERTable || relationship.getTargetWalker() instanceof ERTable) {
            // ビュー内でリレーションを消した場合、ここにはERVirtualTableでなくERTableで来る
            final ERVirtualDiagramSet modelSet = relationship.getSourceWalker().getDiagram().getDiagramContents().getVirtualDiagramSet();
            modelSet.deleteRelationship(relationship);
        }

        if (removeForeignKey) {
            ERModelUtil.refreshDiagram(relationship.getTargetTableView().getDiagram());
        }

        //		source.removeOutgoing(this/*relation*/);
        //		target.removeIncoming(this);
        //
        //		if (tableView instanceof ERVirtualTable) {
        //			ERVirtualTable vtable = (ERVirtualTable) tableView;
        //
        //			// メインビューを更新（枠の再生成）
        //			this.newCopyTableView.restructureData(vtable.getRawTable());
        //			// TableView.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
        //
        //			// サブビューも更新
        //			vtable.doChangeTable();
        //
        //			// テーブルの更新（線も含めた再生成）
        //			this.tableView.getDiagram().changeTable(newCopyTableView);
        //			// ERDiagram.firePropertyChange(PROPERTY_CHANGE_TABLE)
        //
        //		} else {
        //			// メインビューを更新
        //			this.newCopyTableView.restructureData(tableView);
        //			this.tableView.getDiagram().changeTable(newCopyTableView);
        //
        //			// サブビューも更新
        //			tableView.getDiagram().doChangeTable(newCopyTableView);
        //		}
    }

    @Override
    protected void doUndo() {
        super.doUndo();

        for (final NormalColumn foreignKey : referencedColumnMap.keySet()) {
            if (!removeForeignKey) {
                final Dictionary dictionary = oldTargetTable.getDiagram().getDiagramContents().getDictionary();
                dictionary.remove(foreignKey);
            }

            foreignKey.addReference(referencedColumnMap.get(foreignKey), relationship);
        }

        oldTargetCopyTable.restructureData(oldTargetTable);

        // TODO ymd 不完全。一瞬無関係なリレーションシップの描画がバグる。ひどいとバグったまま。
        oldTargetTable.refreshInVirtualDiagram(relationship.getSourceWalker());
    }

    @Override
    public boolean canExecute() {
        if (removeForeignKey == null) {
            if (relationship.isReferedStrictly()) {
                if (isReferencedByMultiRelations()) {
                    Activator.showErrorDialog("dialog.message.referenced.by.multi.foreign.key");
                    return false;
                }

                this.removeForeignKey = false;
                this.referencedColumnMap = new HashMap<>();
                for (final NormalColumn foreignKey : relationship.getForeignKeyColumns()) {
                    final NormalColumn referencedColumn = foreignKey.getReferredColumn(relationship);
                    referencedColumnMap.put(foreignKey, referencedColumn);
                }

                return true;
            }

            if (Activator.showConfirmDialog("dialog.message.confirm.remove.foreign.key", SWT.YES, SWT.NO)) {
                this.removeForeignKey = true;
            } else {
                this.removeForeignKey = false;
                this.referencedColumnMap = new HashMap<>();
                for (final NormalColumn foreignKey : relationship.getForeignKeyColumns()) {
                    final NormalColumn referencedColumn = foreignKey.getReferredColumn(relationship);
                    referencedColumnMap.put(foreignKey, referencedColumn);
                }
            }
        }

        return true;
    }

    private boolean isReferencedByMultiRelations() {
        for (final NormalColumn foreignKeyColumn : relationship.getForeignKeyColumns()) {
            for (final NormalColumn childForeignKeyColumn : foreignKeyColumn.getForeignKeyList()) {
                if (childForeignKeyColumn.getRelationshipList().size() >= 2) {
                    final Set<TableView> referencedTables = new HashSet<>();

                    for (final Relationship relation : childForeignKeyColumn.getRelationshipList()) {
                        referencedTables.add(relation.getSourceTableView());
                    }

                    if (referencedTables.size() >= 2) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

package org.dbflute.erflute.editor;

import org.eclipse.gef.EditPart;

/**
 * TODO ymd 技術的負債
 * MainDiagramEditor#reveal()で選択したテーブルのEditPartを格納する。
 * インスタンス(this)から、最後にQuick Outlineで検索したテーブルを取得する方法が分からなかったため、このような実装になった。
 */
public class SelectionEditPartWrapper {

    private EditPart selectionEditPart;

    public void changeSelection(EditPart editPart) {
        clearSelection();
        selectionEditPart = editPart;
        selectionEditPart.setSelected(EditPart.SELECTED_PRIMARY);
    }

    public void clearSelection() {
        if (selectionEditPart != null) {
            selectionEditPart.setSelected(EditPart.SELECTED_NONE);
            selectionEditPart = null;
        }
    }
}

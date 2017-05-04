package org.dbflute.erflute.editor.extension;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.eclipse.jface.action.IAction;

/**
 * 拡張ポイントから読み込むクラスのインターフェイス
 */
public interface IERDiagramActionFactory {

    /**
     * IAction を実装したクラスを返す
     * @param editor
     * @return IAction
     */
    IAction createIAction(MainDiagramEditor editor);
}

package org.dbflute.erflute.editor.extension;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.eclipse.jface.action.IAction;

/**
 * �g���|�C���g����ǂݍ��ރN���X�̃C���^�[�t�F�C�X
 */
public interface IERDiagramActionFactory {

    /**
     * IAction �����������N���X��Ԃ�
     * 
     * @param editor
     * @return IAction
     */
    public IAction createIAction(MainDiagramEditor editor);

}

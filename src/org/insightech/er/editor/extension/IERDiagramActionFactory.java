package org.insightech.er.editor.extension;

import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.MainModelEditor;

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
    public IAction createIAction(MainModelEditor editor);

}

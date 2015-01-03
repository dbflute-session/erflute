package org.insightech.er.extention;

import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.ERDiagramEditor;

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
	public IAction createIAction(ERDiagramEditor editor);

}

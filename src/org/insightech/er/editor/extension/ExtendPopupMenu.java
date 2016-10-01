package org.insightech.er.editor.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.MainModelEditor;

public class ExtendPopupMenu {

    /** �g���|�C���g��ID */
    private static final String EXTENSION_POINT_ID = "org.insightech.er.popupMenus";

    /** �g���|�C���g�� element �̖��O */
    private static final String EXTENSION_NAME = "popupMenu";

    /** ���j���[��ǉ�����ʒu���w�肷�� Attribute �̖��O */
    private static final String ATTRIBUTE_PATH = "path";

    /** �ǉ����郁�j���[�̃A�N�V�����t�@�N�g���[�N���X���w�肷�� Attribute �̖��O */
    private static final String ATTRIBUTE_CLASS = "class";

    private IAction action;

    private String path;

    public IAction getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

    public static ExtendPopupMenu createExtendPopupMenu(IConfigurationElement configurationElement, MainModelEditor editor)
            throws CoreException {
        ExtendPopupMenu menu = null;

        if (ExtendPopupMenu.EXTENSION_NAME.equals(configurationElement.getName())) {

        }
        String path = configurationElement.getAttribute(ATTRIBUTE_PATH);
        Object obj = configurationElement.createExecutableExtension(ATTRIBUTE_CLASS);

        if (obj instanceof IERDiagramActionFactory) {
            menu = new ExtendPopupMenu();
            IERDiagramActionFactory actionFactory = (IERDiagramActionFactory) obj;

            menu.action = actionFactory.createIAction(editor);
            menu.path = path;
        }

        return menu;
    }

    /**
     * plugin.xml����^�O��ǂݍ���.
     * 
     * @throws CoreException
     * 
     * @throws CoreException
     */
    public static List<ExtendPopupMenu> loadExtensions(MainModelEditor editor) throws CoreException {
        List<ExtendPopupMenu> extendPopupMenuList = new ArrayList<ExtendPopupMenu>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_POINT_ID);

        if (extensionPoint != null) {
            for (IExtension extension : extensionPoint.getExtensions()) {
                for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {

                    ExtendPopupMenu extendPopupMenu = ExtendPopupMenu.createExtendPopupMenu(configurationElement, editor);

                    if (extendPopupMenu != null) {
                        extendPopupMenuList.add(extendPopupMenu);
                    }
                }
            }
        }

        return extendPopupMenuList;
    }

}

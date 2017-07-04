package org.dbflute.erflute.editor.extension;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ExtendPopupMenu {

    private static final String EXTENSION_POINT_ID = "org.insightech.er.popupMenus";
    private static final String EXTENSION_NAME = "popupMenu";
    private static final String ATTRIBUTE_PATH = "path";
    private static final String ATTRIBUTE_CLASS = "class";

    private IAction action;
    private String path;

    public IAction getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

    public static ExtendPopupMenu createExtendPopupMenu(IConfigurationElement configurationElement, MainDiagramEditor editor)
            throws CoreException {
        ExtendPopupMenu menu = null;
        if (ExtendPopupMenu.EXTENSION_NAME.equals(configurationElement.getName())) {}
        final String path = configurationElement.getAttribute(ATTRIBUTE_PATH);
        final Object obj = configurationElement.createExecutableExtension(ATTRIBUTE_CLASS);

        if (obj instanceof IERDiagramActionFactory) {
            menu = new ExtendPopupMenu();
            final IERDiagramActionFactory actionFactory = (IERDiagramActionFactory) obj;
            menu.action = actionFactory.createIAction(editor);
            menu.path = path;
        }

        return menu;
    }

    public static List<ExtendPopupMenu> loadExtensions(MainDiagramEditor editor) throws CoreException {
        final List<ExtendPopupMenu> extendPopupMenuList = new ArrayList<>();

        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_POINT_ID);

        if (extensionPoint != null) {
            for (final IExtension extension : extensionPoint.getExtensions()) {
                for (final IConfigurationElement configurationElement : extension.getConfigurationElements()) {
                    final ExtendPopupMenu extendPopupMenu = ExtendPopupMenu.createExtendPopupMenu(configurationElement, editor);
                    if (extendPopupMenu != null) {
                        extendPopupMenuList.add(extendPopupMenu);
                    }
                }
            }
        }
        return extendPopupMenuList;
    }
}

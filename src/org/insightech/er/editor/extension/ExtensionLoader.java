package org.insightech.er.editor.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.insightech.er.editor.MainModelEditor;

public class ExtensionLoader {

    private List<ExtendPopupMenu> extendPopupMenuList = new ArrayList<ExtendPopupMenu>();;

    public ExtensionLoader(MainModelEditor editor) throws CoreException {
        this.extendPopupMenuList = ExtendPopupMenu.loadExtensions(editor);
    }

    public List<IAction> createExtendedActions() {
        List<IAction> actionList = new ArrayList<IAction>();

        for (ExtendPopupMenu extendPopupMenu : this.extendPopupMenuList) {
            actionList.add(extendPopupMenu.getAction());
        }

        return actionList;
    }

    public void addERDiagramPopupMenu(MenuManager menuMgr, ActionRegistry actionregistry) {
        for (ExtendPopupMenu extendPopupMenu : this.extendPopupMenuList) {
            menuMgr.findMenuUsingPath(extendPopupMenu.getPath()).add(extendPopupMenu.getAction());
        }
    }

}

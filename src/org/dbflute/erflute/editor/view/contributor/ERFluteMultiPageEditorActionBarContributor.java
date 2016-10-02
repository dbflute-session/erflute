package org.dbflute.erflute.editor.view.contributor;

import org.dbflute.erflute.editor.MainModelEditor;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERFluteMultiPageEditorActionBarContributor extends MultiPageEditorActionBarContributor {

    @Override
    public void setActivePage(IEditorPart activeEditor) {
        final MainModelEditor editor = (MainModelEditor) activeEditor;
        final ERDiagramActionBarContributor actionBarContributor = editor.getActionBarContributor();
        final IActionBars actionBars = this.getActionBars();
        actionBars.clearGlobalActionHandlers();
        actionBars.getToolBarManager().removeAll();
        actionBarContributor.init(actionBars, editor.getEditorSite().getPage());
        actionBarContributor.setActiveEditor(editor);
        final ZoomComboContributionItem item =
                (ZoomComboContributionItem) getActionBars().getToolBarManager().find(GEFActionConstants.ZOOM_TOOLBAR_WIDGET);
        if (item != null) {
            final ZoomManager zoomManager = (ZoomManager) editor.getAdapter(ZoomManager.class);
            item.setZoomManager(zoomManager);
        }
        getActionBars().updateActionBars();
    }
}

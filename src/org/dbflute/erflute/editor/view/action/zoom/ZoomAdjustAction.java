package org.dbflute.erflute.editor.view.action.zoom;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.Action;

public class ZoomAdjustAction extends Action implements ZoomListener, Disposable {

    public static final String ID = ZoomAdjustAction.class.getName();

    protected ZoomManager zoomManager;

    public ZoomAdjustAction(ZoomManager zoomManager) {
        super(DisplayMessages.getMessage("action.title.zoom.adjust"), Activator.getImageDescriptor(ImageKey.ZOOM_ADJUST));
        this.zoomManager = zoomManager;
        zoomManager.addZoomListener(this);

        setToolTipText(DisplayMessages.getMessage("action.title.zoom.adjust"));
        setId(ID);
    }

    public void dispose() {
        this.zoomManager.removeZoomListener(this);
    }

    @Override
    public void run() {
        this.zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
    }

    public void zoomChanged(double zoom) {
        setEnabled(true);
    }

}

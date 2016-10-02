package org.dbflute.erflute.editor.view.action.zoom;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.eclipse.ui.actions.RetargetAction;

public class ZoomAdjustRetargetAction extends RetargetAction {

    public ZoomAdjustRetargetAction() {
        super(null, null);
        setText(DisplayMessages.getMessage("action.title.zoom.adjust"));
        setId(ZoomAdjustAction.ID);
        setToolTipText(DisplayMessages.getMessage("action.title.zoom.adjust"));
        setImageDescriptor(Activator.getImageDescriptor(ImageKey.ZOOM_ADJUST));
    }
}

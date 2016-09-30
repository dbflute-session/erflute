package org.insightech.er.editor.view.action.zoom;

import org.eclipse.ui.actions.RetargetAction;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.DisplayMessages;

public class ZoomAdjustRetargetAction extends RetargetAction {

    public ZoomAdjustRetargetAction() {
        super(null, null);
        setText(DisplayMessages.getMessage("action.title.zoom.adjust"));
        setId(ZoomAdjustAction.ID);
        setToolTipText(DisplayMessages.getMessage("action.title.zoom.adjust"));
        setImageDescriptor(Activator.getImageDescriptor(ImageKey.ZOOM_ADJUST));
    }
}

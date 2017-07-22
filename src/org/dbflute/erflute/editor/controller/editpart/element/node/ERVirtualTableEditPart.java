package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERVirtualTableEditPart extends ERTableEditPart {

    @Override
    protected boolean isVirtualDiagram() {
        return true;
    }

    @Override
    public void refreshVisuals() {
        super.refreshVisuals();
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        super.doPropertyChange(event);
    }
}

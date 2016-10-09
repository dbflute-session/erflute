package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

public class ERVirtualTableEditPart extends ERTableEditPart {

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
        // what is this? by jflute
        //		if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_COLOR)) {
        //			this.refreshVisuals();
        //		}
        //		if (event.getPropertyName().equals(TableView.PROPERTY_CHANGE_COLUMNS)) {
        //			this.refreshChildren();
        ////			refreshVisuals();
        //		}
        super.doPropertyChange(event);

    }
}

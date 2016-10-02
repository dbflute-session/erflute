package org.dbflute.erflute.editor.controller.editpart.outline.trigger;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.eclipse.gef.EditPart;

public class TriggerSetOutlineEditPart extends AbstractOutlineEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TriggerSet.PROPERTY_CHANGE_TRIGGER_SET)) {
            refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        TriggerSet triggerSet = (TriggerSet) this.getModel();

        List<Trigger> triggerList = triggerSet.getTriggerList();

        Collections.sort(triggerList);

        return triggerList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        this.setWidgetText(DisplayMessages.getMessage("label.trigger") + " (" + this.getModelChildren().size() + ")");
        this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshChildren() {
        super.refreshChildren();

        for (Object child : this.getChildren()) {
            EditPart part = (EditPart) child;
            part.refresh();
        }
    }

}

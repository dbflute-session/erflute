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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TriggerSet.PROPERTY_CHANGE_TRIGGER_SET)) {
            refresh();
        }
    }

    @Override
    protected List<Trigger> getModelChildren() {
        final TriggerSet triggerSet = (TriggerSet) getModel();
        final List<Trigger> triggerList = triggerSet.getTriggerList();
        Collections.sort(triggerList);

        return triggerList;
    }

    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(DisplayMessages.getMessage("label.trigger") + " (" + getModelChildren().size() + ")");
        setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();

        for (final Object child : getChildren()) {
            final EditPart part = (EditPart) child;
            part.refresh();
        }
    }
}

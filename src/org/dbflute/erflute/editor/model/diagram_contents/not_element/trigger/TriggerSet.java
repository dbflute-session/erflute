package org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class TriggerSet extends AbstractModel implements ObjectListModel, Iterable<Trigger> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_TRIGGER_SET = "TriggerSet";

    private List<Trigger> triggerList;

    public TriggerSet() {
        this.triggerList = new ArrayList<>();
    }

    public void addTrigger(Trigger trigger) {
        triggerList.add(trigger);
        Collections.sort(triggerList);

        firePropertyChange(PROPERTY_CHANGE_TRIGGER_SET, null, null);
    }

    public int remove(Trigger trigger) {
        final int index = triggerList.indexOf(trigger);
        triggerList.remove(index);
        firePropertyChange(PROPERTY_CHANGE_TRIGGER_SET, null, null);

        return index;
    }

    public boolean contains(String name) {
        for (final Trigger trigger : triggerList) {
            if (name.equalsIgnoreCase(trigger.getName())) {
                return true;
            }
        }

        return false;
    }

    public Trigger get(String name) {
        for (final Trigger trigger : triggerList) {
            if (name.equalsIgnoreCase(trigger.getName())) {
                return trigger;
            }
        }

        return null;
    }

    public List<Trigger> getTriggerList() {
        return triggerList;
    }

    @Override
    public Iterator<Trigger> iterator() {
        return triggerList.iterator();
    }

    @Override
    public TriggerSet clone() {
        final TriggerSet triggerSet = (TriggerSet) super.clone();
        final List<Trigger> newTriggerList = new ArrayList<>();

        for (final Trigger trigger : triggerList) {
            final Trigger newTrigger = (Trigger) trigger.clone();
            newTriggerList.add(newTrigger);
        }

        triggerSet.triggerList = newTriggerList;

        return triggerSet;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.trigger_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}

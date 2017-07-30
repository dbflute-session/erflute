package org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class SequenceSet extends AbstractModel implements ObjectListModel, Iterable<Sequence> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_SEQUENCE_SET = "SequenceSet";

    private List<Sequence> sequenceList;

    public SequenceSet() {
        this.sequenceList = new ArrayList<>();
    }

    public void addSequence(Sequence sequence) {
        sequenceList.add(sequence);
        Collections.sort(sequenceList);

        firePropertyChange(PROPERTY_CHANGE_SEQUENCE_SET, null, null);
    }

    public int remove(Sequence sequence) {
        final int index = sequenceList.indexOf(sequence);
        sequenceList.remove(index);
        firePropertyChange(PROPERTY_CHANGE_SEQUENCE_SET, null, null);

        return index;
    }

    public boolean contains(String name) {
        for (final Sequence sequence : sequenceList) {
            if (name.equalsIgnoreCase(sequence.getName())) {
                return true;
            }
        }

        return false;
    }

    public Sequence get(String name) {
        for (final Sequence sequence : sequenceList) {
            if (name.equalsIgnoreCase(sequence.getName())) {
                return sequence;
            }
        }

        return null;
    }

    public List<Sequence> getSequenceList() {
        return sequenceList;
    }

    @Override
    public Iterator<Sequence> iterator() {
        return sequenceList.iterator();
    }

    @Override
    public SequenceSet clone() {
        final SequenceSet sequenceSet = (SequenceSet) super.clone();
        final List<Sequence> newSequenceList = new ArrayList<>();

        for (final Sequence sequence : sequenceList) {
            final Sequence newSequence = (Sequence) sequence.clone();
            newSequenceList.add(newSequence);
        }

        sequenceSet.sequenceList = newSequenceList;

        return sequenceSet;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.sequence_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}

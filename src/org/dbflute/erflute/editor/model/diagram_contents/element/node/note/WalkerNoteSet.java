package org.dbflute.erflute.editor.model.diagram_contents.element.node.note;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class WalkerNoteSet extends AbstractModel implements ObjectListModel, Iterable<WalkerNote> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_WALKER_NOTE_SET = "NoteSet";

    private List<WalkerNote> noteList;

    public WalkerNoteSet() {
        this.noteList = new ArrayList<>();
    }

    public void add(WalkerNote note) {
        noteList.add(note);
        firePropertyChange(PROPERTY_CHANGE_WALKER_NOTE_SET, null, null);
    }

    public int remove(WalkerNote note) {
        final int index = noteList.indexOf(note);
        if (0 < index) {
            noteList.remove(note);
        }

        firePropertyChange(PROPERTY_CHANGE_WALKER_NOTE_SET, null, null);

        return index;
    }

    public List<WalkerNote> getList() {
        return noteList;
    }

    @Override
    public Iterator<WalkerNote> iterator() {
        return noteList.iterator();
    }

    @Override
    public WalkerNoteSet clone() {
        final WalkerNoteSet noteSet = (WalkerNoteSet) super.clone();
        final List<WalkerNote> newNoteList = new ArrayList<>();
        for (final WalkerNote note : noteList) {
            final WalkerNote newNote = (WalkerNote) note.clone();
            newNoteList.add(newNote);
        }
        noteSet.noteList = newNoteList;
        return noteSet;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.note_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}

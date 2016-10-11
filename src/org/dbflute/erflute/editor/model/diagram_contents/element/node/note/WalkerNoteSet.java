package org.dbflute.erflute.editor.model.diagram_contents.element.node.note;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class WalkerNoteSet extends AbstractModel implements ObjectListModel, Iterable<WalkerNote> {

    private static final long serialVersionUID = -7000722010136664297L;
    public static final String PROPERTY_CHANGE_WALKER_NOTE_SET = "NoteSet";

    private List<WalkerNote> noteList;

    public WalkerNoteSet() {
        this.noteList = new ArrayList<WalkerNote>();
    }

    public void add(WalkerNote note) {
        this.noteList.add(note);
        this.firePropertyChange(PROPERTY_CHANGE_WALKER_NOTE_SET, null, null);
    }

    public int remove(WalkerNote note) {
        final int index = this.noteList.indexOf(note);
        this.noteList.remove(index);
        this.firePropertyChange(PROPERTY_CHANGE_WALKER_NOTE_SET, null, null);

        return index;
    }

    public List<WalkerNote> getList() {
        return this.noteList;
    }

    @Override
    public Iterator<WalkerNote> iterator() {
        return this.noteList.iterator();
    }

    @Override
    public WalkerNoteSet clone() {
        final WalkerNoteSet noteSet = (WalkerNoteSet) super.clone();
        final List<WalkerNote> newNoteList = new ArrayList<WalkerNote>();
        for (final WalkerNote note : this.noteList) {
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

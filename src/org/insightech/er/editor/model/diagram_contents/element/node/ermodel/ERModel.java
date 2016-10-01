package org.insightech.er.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relationship;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERModel extends NodeElement {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_VTABLES = "vtables";

    private int[] defaultColor;
    private String name;

    private List<ERVirtualTable> tables;
    private List<Note> notes;
    private List<VGroup> groups;

    public ERModel(ERDiagram diagram) {
        setDiagram(diagram);
        tables = new ArrayList<ERVirtualTable>();
        notes = new ArrayList<Note>();
        groups = new ArrayList<VGroup>();
    }

    @Override
    public String getObjectType() {
        return "ermodel";
    }

    public boolean containsTable(ERTable table) {
        for (final ERVirtualTable vtable : tables) {
            if (vtable.getRawTable().equals(table)) {
                return true;
            }
        }
        return false;
    }

    public void remove(ERVirtualTable element) {
        tables.remove(element);
        firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }

    public void changeAll() {
        firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }

    public void addTable(ERVirtualTable virtualTable) {
        tables.add(virtualTable);
    }

    public int[] getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int red, int green, int blue) {
        this.defaultColor = new int[3];
        this.defaultColor[0] = red;
        this.defaultColor[1] = green;
        this.defaultColor[2] = blue;
    }

    public ERVirtualTable findVirtualTable(TableView table) {
        for (final ERVirtualTable vtable : tables) {
            if (vtable.getRawTable().getPhysicalName().equals(table.getPhysicalName())) {
                return vtable;
            }
        }
        return null;
    }

    public void deleteRelation(Relationship relation) {
        for (final ERVirtualTable vtable : tables) {
            vtable.removeOutgoing(relation);
            vtable.removeIncoming(relation);
        }
    }

    public void createRelation(Relationship relation) {
        boolean dirty = false;
        for (final ERVirtualTable vtable : tables) {
            if (relation.getSourceTableView().equals(vtable.getRawTable())) {
                dirty = true;
            } else if (relation.getTargetTableView().equals(vtable.getRawTable())) {
                dirty = true;
            }
        }
        if (dirty) {
            changeAll();
        }
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return false;
    }

    public void addNewContent(NodeElement element) {
        getDiagram().addContent(element);
        if (element instanceof Note) {
            ((Note) element).setModel(this);
        }

        int[] color = defaultColor;
        if (color == null) {
            color = getDiagram().getDefaultColor();
        }
        element.setColor(color[0], color[1], color[2]);

        if (getFontName() != null) {
            element.setFontName(this.getFontName());
        } else {
            element.setFontName(getDiagram().getFontName());
        }

        if (getFontSize() != 0) {
            element.setFontSize(this.getFontSize());
        } else {
            element.setFontSize(getDiagram().getFontSize());
        }

        if (element instanceof Note) {
            final Note note = (Note) element;
            notes.add(note);
            this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
        }
    }

    public void addGroup(VGroup group) {
        groups.add(group);
        this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }

    public void remove(VGroup element) {
        groups.remove(element);
        this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }

    public void remove(Note element) {
        notes.remove(element);
        this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return null; // unsupported here
    }

    public List<ERVirtualTable> getTables() {
        return tables;
    }

    public void setTables(List<ERVirtualTable> tables) { // when e.g. loading XML
        this.tables = tables;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) { // when e.g. loading XML
        this.notes = notes;
    }

    public List<VGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<VGroup> groups) {
        this.groups = groups;
        this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }
}

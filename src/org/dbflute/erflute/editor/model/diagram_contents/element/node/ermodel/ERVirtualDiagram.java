package org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERVirtualDiagram extends DiagramWalker {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_VTABLES = "vtables";
    public static final String REMOVE_VWALKER = "remove_vwalker";

    private int[] defaultColor;
    private String name;

    private List<ERVirtualTable> tables;
    private List<WalkerNote> notes;
    private List<WalkerGroup> groups;
    private Point mousePoint = new Point();

    public ERVirtualDiagram(ERDiagram diagram) {
        setDiagram(diagram);
        tables = new ArrayList<>();
        notes = new ArrayList<>();
        groups = new ArrayList<>();
    }

    @Override
    public String getObjectType() {
        return "virtual_diagram";
    }

    public boolean contains(Object... models) {
        return Arrays.stream(models).allMatch(m -> contains(m));
    }

    public boolean contains(Object model) {
        final List<DiagramWalker> walkers = new ArrayList<>(tables);
        walkers.addAll(notes);
        walkers.addAll(groups);
        return walkers.stream().anyMatch(w -> w.toMaterialize().equals(convertToMaterializedIfCan(model)));
    }

    private static Object convertToMaterializedIfCan(Object model) {
        if (model instanceof DiagramWalker) {
            return ((DiagramWalker) model).toMaterialize();
        } else {
            return model;
        }
    }

    public boolean containsTable(ERTable table) {
        for (final ERVirtualTable vtable : tables) {
            if (vtable.getRawTable().equals(table)) {
                return true;
            }
        }
        return false;
    }

    public void changeAll() {
        firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }

    public int[] getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(Color color) {
        this.defaultColor = new int[3];
        this.defaultColor[0] = color.getRed();
        this.defaultColor[1] = color.getGreen();
        this.defaultColor[2] = color.getBlue();
    }

    public ERVirtualTable findVirtualTable(TableView table) {
        ERVirtualTable ret = null;
        for (final ERVirtualTable vtable : tables) {
            if (vtable.getRawTable().equals(table)) {
                ret = vtable;
            }
        }
        return ret;
    }

    public void deleteRelationship(Relationship relation) {
        for (final ERVirtualTable vtable : tables) {
            vtable.removeOutgoing(relation);
            vtable.removeIncoming(relation);
        }
    }

    public void createRelationship(Relationship relationship) {
        boolean dirty = false;
        for (final ERVirtualTable vtable : tables) {
            if (relationship.getSourceTableView().equals(vtable.getRawTable())) {
                dirty = true;
            } else if (relationship.getTargetTableView().equals(vtable.getRawTable())) {
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

    public void addWalkerPlainly(DiagramWalker walker) {
        if (walker == null) {
            return;
        }

        if (walker instanceof WalkerNote) {
            final WalkerNote note = (WalkerNote) walker;
            notes.add(note);
            note.setVirtualDiagram(this);
            firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
        } else if (walker instanceof WalkerGroup) {
            final WalkerGroup group = (WalkerGroup) walker;
            groups.add(group);
            group.setVirtualDiagram(this);
            firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
        } else if (walker instanceof ERTable) {
            ERVirtualTable virtualTable;
            if (walker instanceof ERVirtualTable) {
                virtualTable = (ERVirtualTable) walker;
            } else {
                virtualTable = new ERVirtualTable(this, (ERTable) walker);
                virtualTable.setLocation(new Location(walker.getX(), walker.getY(), walker.getWidth(), walker.getHeight()));
                walker.setLocation(new Location(0, 0, walker.getWidth(), walker.getHeight())); // メインダイアグラム上では左上に配置
            }
            tables.add(virtualTable);
            firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
        }
    }

    public void removeWalker(DiagramWalker walker) {
        if (walker == null) {
            return;
        }

        if (walker instanceof WalkerNote) {
            notes.remove(walker);
            firePropertyChange(REMOVE_VWALKER, null, null);
        } else if (walker instanceof WalkerGroup) {
            groups.remove(walker);
            firePropertyChange(REMOVE_VWALKER, null, null);
        } else if (walker instanceof ERVirtualTable) {
            tables.remove(walker);
            firePropertyChange(REMOVE_VWALKER, null, null);
        }
    }

    @Override
    public int getPersistentOrder() {
        return 6;
    }

    @Override
    public boolean isUsePersistentId() {
        return false;
    }

    @Override
    public boolean isIndenpendentOnModel() {
        return false;
    }

    public String buildVirtualDiagramId() {
        return name + "_" + hashCode();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + name + ", tables=" + (tables != null ? tables.size() : null) + "}";
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
        return ""; // unsupported here
    }

    public List<ERVirtualTable> getVirtualTables() {
        return tables;
    }

    public void setVirtualTables(List<ERVirtualTable> tables) { // when e.g. loading XML
        this.tables = tables;
    }

    public List<WalkerNote> getWalkerNotes() {
        return notes;
    }

    public void setWalkerNotes(List<WalkerNote> notes) { // when e.g. loading XML
        this.notes = notes;
    }

    public List<WalkerGroup> getWalkerGroups() {
        return groups;
    }

    public void setWalkerGroups(List<WalkerGroup> groups) {
        this.groups = groups;
        firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
    }

    public Set<ERVirtualTable> getVirtualTableSet() {
        final TreeSet<ERVirtualTable> set = new TreeSet<>(Comparator.comparing(o -> o.getRawTable()));
        set.addAll(getVirtualTables());
        return set;
    }

    public Point getMousePoint() {
        return mousePoint;
    }

    public void setMousePoint(Point mousePoint) {
        this.mousePoint = mousePoint;
    }
}

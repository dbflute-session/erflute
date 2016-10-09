package org.dbflute.erflute.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImageSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.NoteSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ViewSet;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class NodeSet extends AbstractModel implements Iterable<NodeElement> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = -120487815554383179L;
    public static final String PROPERTY_CHANGE_CONTENTS = "contents";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final TableSet tableSet;
    private final ViewSet viewSet;
    private final NoteSet noteSet;
    private final InsertedImageSet insertedImageSet;
    private final List<NodeElement> nodeElementList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public NodeSet() {
        this.tableSet = new TableSet();
        this.viewSet = new ViewSet();
        this.noteSet = new NoteSet();
        this.insertedImageSet = new InsertedImageSet();
        this.nodeElementList = new ArrayList<NodeElement>();
    }

    // ===================================================================================
    //                                                                            Add Node
    //                                                                            ========
    public void addNodeElement(NodeElement nodeElement) {
        if (nodeElement instanceof ERTable) {
            this.tableSet.add((ERTable) nodeElement);
        } else if (nodeElement instanceof ERView) {
            this.viewSet.add((ERView) nodeElement);
        } else if (nodeElement instanceof Note) {
            this.noteSet.add((Note) nodeElement);
        } else if (nodeElement instanceof InsertedImage) {
            this.insertedImageSet.add((InsertedImage) nodeElement);
        } else {
            System.out.println("not support " + nodeElement); // why sysout? by jflute
        }
        nodeElementList.add(nodeElement);
        firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public void remove(NodeElement nodeElement) {
        if (nodeElement instanceof ERTable) {
            this.tableSet.remove((ERTable) nodeElement);
        } else if (nodeElement instanceof ERView) {
            this.viewSet.remove((ERView) nodeElement);
        } else if (nodeElement instanceof Note) {
            this.noteSet.remove((Note) nodeElement);
        } else if (nodeElement instanceof InsertedImage) {
            this.insertedImageSet.remove((InsertedImage) nodeElement);
        } else {
            throw new RuntimeException("not support " + nodeElement);
        }
        nodeElementList.remove(nodeElement);
        firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public boolean contains(NodeElement nodeElement) {
        return this.nodeElementList.contains(nodeElement);
    }

    public void clear() {
        this.tableSet.getList().clear();
        this.viewSet.getList().clear();
        this.noteSet.getList().clear();
        this.insertedImageSet.getList().clear();
        this.nodeElementList.clear();
    }

    public boolean isEmpty() {
        return nodeElementList.isEmpty();
    }

    public List<NodeElement> getNodeElementList() {
        return nodeElementList;
    }

    public List<TableView> getTableViewList() {
        final List<TableView> nodeElementList = new ArrayList<TableView>();
        nodeElementList.addAll(this.tableSet.getList());
        nodeElementList.addAll(this.viewSet.getList());
        return nodeElementList;
    }

    public Set<NodeElement> getPersistentSet() { // #for_erflute
        final List<NodeElement> elementList = getNodeElementList();
        final TreeSet<NodeElement> treeSet = new TreeSet<NodeElement>(new Comparator<NodeElement>() {
            @Override
            public int compare(NodeElement o1, NodeElement o2) {
                if (o1.getPersistentOrder() != o2.getPersistentOrder()) {
                    return o1.getPersistentOrder() - o2.getPersistentOrder();
                } else {
                    if (!o1.getClass().getName().equals(o2.getClass().getName())) { // just in case
                        return o1.getClass().getName().compareTo(o2.getClass().getName());
                    } else {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
            }
        });
        treeSet.addAll(elementList);
        return treeSet;
    }

    @Override
    public Iterator<NodeElement> iterator() { // not sorted so cannot use for persistent
        return this.getNodeElementList().iterator();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + tableSet + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ViewSet getViewSet() {
        return viewSet;
    }

    public NoteSet getNoteSet() {
        return noteSet;
    }

    public TableSet getTableSet() {
        return tableSet;
    }

    public InsertedImageSet getInsertedImageSet() {
        return insertedImageSet;
    }
}

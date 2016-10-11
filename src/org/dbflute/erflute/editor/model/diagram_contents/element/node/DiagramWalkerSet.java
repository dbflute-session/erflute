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
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNoteSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ViewSet;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class DiagramWalkerSet extends AbstractModel implements Iterable<DiagramWalker> {

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
    private final WalkerNoteSet noteSet;
    private final InsertedImageSet insertedImageSet;
    private final List<DiagramWalker> walkerList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DiagramWalkerSet() {
        this.tableSet = new TableSet();
        this.viewSet = new ViewSet();
        this.noteSet = new WalkerNoteSet();
        this.insertedImageSet = new InsertedImageSet();
        this.walkerList = new ArrayList<DiagramWalker>();
    }

    // ===================================================================================
    //                                                                            Add Node
    //                                                                            ========
    public void addNodeElement(DiagramWalker element) {
        if (element instanceof ERTable) {
            this.tableSet.add((ERTable) element);
        } else if (element instanceof ERView) {
            this.viewSet.add((ERView) element);
        } else if (element instanceof WalkerNote) {
            this.noteSet.add((WalkerNote) element);
        } else if (element instanceof InsertedImage) {
            this.insertedImageSet.add((InsertedImage) element);
        } else {
            System.out.println("*Unsupported node element: " + element);
        }
        walkerList.add(element);
        firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public void remove(DiagramWalker element) {
        if (element instanceof ERTable) {
            this.tableSet.remove((ERTable) element);
        } else if (element instanceof ERView) {
            this.viewSet.remove((ERView) element);
        } else if (element instanceof WalkerNote) {
            this.noteSet.remove((WalkerNote) element);
        } else if (element instanceof InsertedImage) {
            this.insertedImageSet.remove((InsertedImage) element);
        } else {
            System.out.println("*Unsupported node element: " + element);
        }
        walkerList.remove(element);
        firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public boolean contains(DiagramWalker nodeElement) {
        return this.walkerList.contains(nodeElement);
    }

    public void clear() {
        this.tableSet.getList().clear();
        this.viewSet.getList().clear();
        this.noteSet.getList().clear();
        this.insertedImageSet.getList().clear();
        this.walkerList.clear();
    }

    public boolean isEmpty() {
        return walkerList.isEmpty();
    }

    public List<DiagramWalker> getDiagramWalkerList() {
        return walkerList;
    }

    public List<TableView> getTableViewList() {
        final List<TableView> nodeElementList = new ArrayList<TableView>();
        nodeElementList.addAll(this.tableSet.getList());
        nodeElementList.addAll(this.viewSet.getList());
        return nodeElementList;
    }

    public Set<DiagramWalker> getPersistentSet() { // #for_erflute
        final List<DiagramWalker> elementList = getDiagramWalkerList();
        final TreeSet<DiagramWalker> treeSet = new TreeSet<DiagramWalker>(new Comparator<DiagramWalker>() {
            @Override
            public int compare(DiagramWalker o1, DiagramWalker o2) {
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
    public Iterator<DiagramWalker> iterator() { // not sorted so cannot use for persistent
        return this.getDiagramWalkerList().iterator();
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

    public WalkerNoteSet getNoteSet() {
        return noteSet;
    }

    public TableSet getTableSet() {
        return tableSet;
    }

    public InsertedImageSet getInsertedImageSet() {
        return insertedImageSet;
    }
}

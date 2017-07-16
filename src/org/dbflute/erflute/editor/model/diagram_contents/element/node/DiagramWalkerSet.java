package org.dbflute.erflute.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroupSet;
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
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_DIAGRAM_WALKER = "diagram_walkers";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final TableSet tableSet;
    private final ViewSet viewSet;
    private final WalkerGroupSet walkerGroupSet;
    private final WalkerNoteSet walkerNoteSet;
    private final InsertedImageSet insertedImageSet;
    private final List<DiagramWalker> walkerList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DiagramWalkerSet() {
        this.tableSet = new TableSet();
        this.viewSet = new ViewSet();
        this.walkerGroupSet = new WalkerGroupSet();
        this.walkerNoteSet = new WalkerNoteSet();
        this.insertedImageSet = new InsertedImageSet();
        this.walkerList = new ArrayList<>();
    }

    // ===================================================================================
    //                                                                            Add Node
    //                                                                            ========
    public void add(DiagramWalker walker) {
        if (contains(walker.toMaterialize())) {
            return;
        }

        if (walker instanceof ERTable) {
            tableSet.add((ERTable) walker);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (walker instanceof ERView) {
            viewSet.add((ERView) walker);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (walker instanceof WalkerGroup) {
            walkerGroupSet.add((WalkerGroup) walker);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (walker instanceof WalkerNote) {
            walkerNoteSet.add((WalkerNote) walker);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (walker instanceof InsertedImage) {
            insertedImageSet.add((InsertedImage) walker);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else {
            System.out.println("*Unsupported diagram walker: " + walker);
        }
        if (walker instanceof WalkerGroup) {
            // エディタ上で、テーブルグループをテーブルの背面に配置するため。
            // テーブルの後にテーブルグループを追加すると、テーブルグループの後ろにテーブルが隠れてしまう。
            walkerList.add(0, walker);
        } else {
            walkerList.add(walker);
        }
    }

    public void remove(DiagramWalker element) {
        if (element instanceof ERTable) {
            tableSet.remove((ERTable) element);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (element instanceof ERView) {
            viewSet.remove((ERView) element);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (element instanceof WalkerGroup) {
            walkerGroupSet.remove((WalkerGroup) element);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (element instanceof WalkerNote) {
            walkerNoteSet.remove((WalkerNote) element);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else if (element instanceof InsertedImage) {
            insertedImageSet.remove((InsertedImage) element);
            firePropertyChange(PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
        } else {
            System.out.println("*Unsupported diagram walker: " + element);
        }
        walkerList.remove(element);
    }

    public boolean contains(DiagramWalker nodeElement) {
        return walkerList.contains(nodeElement);
    }

    public void clear() {
        tableSet.getList().clear();
        viewSet.getList().clear();
        walkerGroupSet.getList().clear();
        walkerNoteSet.getList().clear();
        insertedImageSet.getList().clear();
        walkerList.clear();
    }

    public boolean isEmpty() {
        return walkerList.isEmpty();
    }

    public List<DiagramWalker> getDiagramWalkerList() {
        return walkerList;
    }

    public List<TableView> getTableViewList() {
        final List<TableView> nodeElementList = new ArrayList<>();
        nodeElementList.addAll(tableSet.getList());
        nodeElementList.addAll(viewSet.getList());
        return nodeElementList;
    }

    public Set<DiagramWalker> getPersistentSet() { // #for_erflute
        final List<DiagramWalker> elementList = getDiagramWalkerList();
        final TreeSet<DiagramWalker> treeSet = new TreeSet<>(new Comparator<DiagramWalker>() {

            @Override
            public int compare(DiagramWalker p1, DiagramWalker p2) {
                final DiagramWalker o1 = p1.toMaterialize();
                final DiagramWalker o2 = p2.toMaterialize();
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
        return getDiagramWalkerList().iterator();
    }

    public Stream<DiagramWalker> stream() {
        return getDiagramWalkerList().stream();
    }

    public DiagramWalker last() {
        if (walkerList.size() <= 0) {
            return null;
        }

        return walkerList.get(walkerList.size() - 1);
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
    public TableSet getTableSet() {
        return tableSet;
    }

    public ViewSet getViewSet() {
        return viewSet;
    }

    public WalkerGroupSet getWalkerGroupSet() {
        return walkerGroupSet;
    }

    public WalkerNoteSet getWalkerNoteSet() {
        return walkerNoteSet;
    }

    public InsertedImageSet getInsertedImageSet() {
        return insertedImageSet;
    }
}

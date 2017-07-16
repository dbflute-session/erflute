package org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.controller.editpart.element.node.IResizable;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WalkerGroup extends DiagramWalker implements IResizable, Comparable<WalkerGroup> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_WALKER_GROUP = "walker_group";

    private String name;
    private List<DiagramWalker> walkerList;
    private ERVirtualDiagram vdiagram;

    public WalkerGroup() {
        this.walkerList = new ArrayList<>();
    }

    public void setWalkers(List<DiagramWalker> walkerList) {
        this.walkerList = walkerList;
        if (getWidth() == 0) {
            int categoryX = 0;
            int categoryY = 0;
            int categoryWidth = 300;
            int categoryHeight = 400;
            if (!walkerList.isEmpty()) {
                categoryX = walkerList.get(0).getX();
                categoryY = walkerList.get(0).getY();
                categoryWidth = walkerList.get(0).getWidth();
                categoryHeight = walkerList.get(0).getHeight();
                for (final DiagramWalker walker : walkerList) {
                    final int x = walker.getX();
                    final int y = walker.getY();
                    int width = walker.getWidth();
                    int height = walker.getHeight();
                    if (categoryX > x) {
                        width += categoryX - x;
                        categoryX = x;
                    }
                    if (categoryY > y) {
                        height += categoryY - y;
                        categoryY = y;
                    }
                    if (x - categoryX + width > categoryWidth) {
                        categoryWidth = x - categoryX + width;
                    }
                    if (y - categoryY + height > categoryHeight) {
                        categoryHeight = y - categoryY + height;
                    }
                }
            }
            setLocation(new Location(categoryX, categoryY, categoryWidth, categoryHeight));
        }
    }

    public boolean contains(DiagramWalker walker) {
        return walkerList.contains(walker);
    }

    public boolean isVisible(DiagramWalker walker, ERDiagram diagram) {
        return true;
    }

    public List<ERTable> getTableContents() {
        final List<ERTable> tableList = new ArrayList<>();
        for (final DiagramWalker walker : walkerList) {
            if (walker instanceof ERTable) {
                tableList.add((ERTable) walker);
            }
        }
        return tableList;
    }

    public List<ERView> getViewContents() {
        final List<ERView> viewList = new ArrayList<>();

        for (final DiagramWalker walker : walkerList) {
            if (walker instanceof ERView) {
                viewList.add((ERView) walker);
            }
        }

        return viewList;
    }

    public List<TableView> getTableViewContents() {
        final List<TableView> tableList = new ArrayList<>();
        for (final DiagramWalker walker : walkerList) {
            if (walker instanceof TableView) {
                tableList.add((TableView) walker);
            }
        }
        return tableList;
    }

    // ===================================================================================
    //                                                                     as Object Model
    //                                                                     ===============
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getObjectType() {
        return "walker_group";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return false;
    }

    // ===================================================================================
    //                                                                           as Walker
    //                                                                           =========
    @Override
    public int getPersistentOrder() {
        return 14;
    }

    @Override
    public boolean isUsePersistentId() {
        return false;
    }

    @Override
    public boolean isIndenpendentOnModel() {
        return true;
    }

    public boolean isVirtualDiagramGroup() {
        return vdiagram != null;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int compareTo(WalkerGroup other) {
        return Format.null2blank(name).compareTo(Format.null2blank(other.name));
    }

    @Override
    public WalkerGroup clone() {
        final WalkerGroup clone = (WalkerGroup) super.clone();
        return clone;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + name + ", " + walkerList + ", " + vdiagram + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setName(String name) {
        this.name = name;
        firePropertyChange(PROPERTY_CHANGE_WALKER_GROUP, null, null);
    }

    public List<DiagramWalker> getDiagramWalkerList() {
        return walkerList;
    }

    public ERVirtualDiagram getVirtualDiagram() {
        return vdiagram;
    }

    public void setVirtualDiagram(ERVirtualDiagram vdiagram) {
        this.vdiagram = vdiagram;
    }
}

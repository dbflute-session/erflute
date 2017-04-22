package org.dbflute.erflute.editor.model.diagram_contents.element.node.category;

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
import org.dbflute.erflute.editor.model.settings.CategorySettings;

public class Category extends DiagramWalker implements IResizable, Comparable<Category> {

    private static final long serialVersionUID = -7691417386790834828L;

    private List<DiagramWalker> walkerList;
    private String name;

    public Category() {
        this.walkerList = new ArrayList<DiagramWalker>();
    }

    public void setContents(List<DiagramWalker> contetns) {
        this.walkerList = contetns;
        if (this.getWidth() == 0) {
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

            this.setLocation(new Location(categoryX, categoryY, categoryWidth, categoryHeight));
        }
    }

    public boolean contains(DiagramWalker nodeElement) {
        return this.walkerList.contains(nodeElement);
    }

    public boolean isVisible(DiagramWalker nodeElement, ERDiagram diagram) {
        boolean isVisible = false;
        if (this.contains(nodeElement)) {
            isVisible = true;
        } else {
            final CategorySettings categorySettings = diagram.getDiagramContents().getSettings().getCategorySetting();
            if (categorySettings.isShowReferredTables()) {
                for (final DiagramWalker referringElement : nodeElement.getReferringElementList()) {
                    if (this.contains(referringElement)) {
                        isVisible = true;
                        break;
                    }
                }
            }
        }
        return isVisible;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DiagramWalker> getContents() {
        return walkerList;
    }

    public List<ERTable> getTableContents() {
        final List<ERTable> tableList = new ArrayList<ERTable>();
        for (final DiagramWalker walker : this.walkerList) {
            if (walker instanceof ERTable) {
                tableList.add((ERTable) walker);
            }
        }
        return tableList;
    }

    public List<ERView> getViewContents() {
        final List<ERView> viewList = new ArrayList<ERView>();
        for (final DiagramWalker walker : this.walkerList) {
            if (walker instanceof ERView) {
                viewList.add((ERView) walker);
            }
        }
        return viewList;
    }

    public List<TableView> getTableViewContents() {
        final List<TableView> tableList = new ArrayList<TableView>();
        for (final DiagramWalker walker : this.walkerList) {
            if (walker instanceof TableView) {
                tableList.add((TableView) walker);
            }
        }
        return tableList;
    }

    @Override
    public int compareTo(Category other) {
        return Format.null2blank(this.name).compareTo(Format.null2blank(other.name));
    }

    @Override
    public Category clone() {
        final Category clone = (Category) super.clone();
        return clone;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getObjectType() {
        return "category";
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return false;
    }

    @Override
    public int getPersistentOrder() {
        return 10;
    }

    @Override
    public boolean isUsePersistentId() {
        return true;
    }

    @Override
    public boolean isIndenpendentOnModel() {
        return false;
    }
}

package org.dbflute.erflute.editor.model;

import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GlobalColumnGroupSet;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.model.settings.PageSettings;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagram extends ViewableModel {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 8729319470770699498L;
    public static final String PROPERTY_CHANGE_ALL = "all";
    public static final String PROPERTY_CHANGE_DATABASE = "database";
    public static final String PROPERTY_CHANGE_SETTINGS = "settings";
    public static final String PROPERTY_CHANGE_ADD = "add";
    public static final String PROPERTY_CHANGE_ERMODEL = "ermodel";
    public static final String PROPERTY_CHANGE_TABLE = "table";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DiagramContents diagramContents; // may be replaced, not null
    private ERFluteMultiPageEditor editor;
    private int[] defaultColor;
    private boolean tooltip;
    private boolean showMainColumn;
    private boolean disableSelectColumn;
    private Category currentCategory;
    private int currentCategoryIndex;
    private ERVirtualDiagram currentVirtualDiagram;
    private double zoom = 1.0d;
    private int x;
    private int y;
    private DBSettings dbSettings;
    private PageSettings pageSetting;
    public Point mousePoint = new Point();
    private String defaultModelName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ERDiagram(String database) {
        this.diagramContents = new DiagramContents();
        this.diagramContents.getSettings().setDatabase(database);
        this.pageSetting = new PageSettings();
        this.setDefaultColor(DesignResources.ERDIAGRAM_DEFAULT_COLOR);
        this.setColor(DesignResources.WHITE);
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void init() {
        diagramContents.setColumnGroupSet(GlobalColumnGroupSet.load());
        final DiagramSettings settings = getDiagramContents().getSettings();
        settings.getModelProperties().init();
    }

    // ===================================================================================
    //                                                                    Content Handling
    //                                                                    ================
    public void addNewWalker(DiagramWalker walker) {
        Activator.debug(this, "addNewWalker()", "...Adding new walker: " + walker);
        if (walker instanceof WalkerNote) {
            walker.setColor(DesignResources.NOTE_DEFAULT_COLOR);
        } else {
            walker.setColor(DesignResources.ERDIAGRAM_DEFAULT_COLOR);
        }
        walker.setFontName(getFontName());
        walker.setFontSize(getFontSize());
        addWalkerPlainly(walker);
    }

    public void addWalkerPlainly(DiagramWalker walker) {
        walker.setDiagram(this);
        diagramContents.getDiagramWalkers().addDiagramWalker(walker);
        if (editor != null) {
            final Category category = editor.getCurrentPageCategory();
            if (category != null) {
                category.getContents().add(walker);
            }
        }
        if (walker instanceof TableView) {
            for (final NormalColumn normalColumn : ((TableView) walker).getNormalColumns()) {
                getDiagramContents().getDictionary().add(normalColumn);
            }
        }
        if (walker instanceof ERTable) {
            final ERTable table = (ERTable) walker;
            if (getCurrentVirtualDiagram() != null) {
                // ビュー上に仮想テーブルを追加する
                final ERVirtualDiagram model = getCurrentVirtualDiagram();
                final ERVirtualTable virtualTable = new ERVirtualTable(model, table);
                virtualTable.setPoint(walker.getX(), walker.getY());

                // メインビュー上では左上に配置
                walker.setLocation(new Location(0, 0, walker.getWidth(), walker.getHeight()));

                model.addTable(virtualTable);
            }
        }
        if (walker instanceof ERVirtualTable) {
            final ERVirtualTable virtualTable = (ERVirtualTable) walker;
            if (getCurrentVirtualDiagram() != null) {
                // ビュー上に仮想テーブルを追加する
                final ERVirtualDiagram model = getCurrentVirtualDiagram();
                //ERVirtualTable virtualTable = new ERVirtualTable(model, table);
                virtualTable.setPoint(walker.getX(), walker.getY());

                // メインビュー上では左上に配置
                walker.setLocation(new Location(0, 0, walker.getWidth(), walker.getHeight()));

                model.addTable(virtualTable);
            }
        }
        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void removeContent(DiagramWalker element) {
        if (element instanceof ERVirtualTable) {
            // メインビューのノードは残して仮想テーブルだけ削除
            currentVirtualDiagram.remove((ERVirtualTable) element);
        } else if (element instanceof WalkerGroup && currentVirtualDiagram != null) {
            currentVirtualDiagram.remove((WalkerGroup) element);
        } else if (element instanceof WalkerNote && currentVirtualDiagram != null) {
            currentVirtualDiagram.remove((WalkerNote) element);
        } else {
            this.diagramContents.getDiagramWalkers().remove(element);
            if (element instanceof ERTable) {
                // メインビューのテーブルを削除したときは、ビューのノードも削除（線が消えずに残ってしまう）
                for (final ERVirtualDiagram model : getDiagramContents().getVirtualDiagramSet()) {
                    final ERVirtualTable vtable = model.findVirtualTable((TableView) element);
                    model.remove(vtable);
                }
            }
        }
        if (element instanceof TableView) {
            this.diagramContents.getDictionary().remove((TableView) element);
        }
        for (final Category category : this.diagramContents.getSettings().getCategorySetting().getAllCategories()) {
            category.getContents().remove(element);
        }
        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void replaceContents(DiagramContents newDiagramContents) {
        this.diagramContents = newDiagramContents;
        this.firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    // ===================================================================================
    //                                                                   ER/Table Handling
    //                                                                   =================
    public void addVirtualDiagram(ERVirtualDiagram ermodel) {
        diagramContents.getVirtualDiagramSet().add(ermodel);
        firePropertyChange(PROPERTY_CHANGE_ADD, null, ermodel);
    }

    public void changeTable(TableView tableView) {
        this.firePropertyChange(PROPERTY_CHANGE_TABLE, null, tableView);
    }

    /**
     * メインビューでテーブルを更新したときに呼ばれます。
     * サブビューのテーブルを更新します。
     * @param table テーブルビュー
     */
    public void doChangeTable(TableView table) {
        for (final ERVirtualDiagram model : getDiagramContents().getVirtualDiagramSet()) {
            final ERVirtualTable vtable = model.findVirtualTable(table);
            if (vtable != null) {
                vtable.changeTable();
            }
        }
    }

    public ERVirtualDiagram findModelByTable(ERTable table) {
        for (final ERVirtualDiagram model : diagramContents.getVirtualDiagramSet()) {
            for (final ERVirtualTable vtable : model.getVirtualTables()) {
                if (vtable.getRawTable().equals(table)) {
                    return model;
                }
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                   Database Handling
    //                                                                   =================
    public void setDatabase(String str) {
        final String oldDatabase = getDatabase();
        this.getDiagramContents().getSettings().setDatabase(str);
        if (str != null && !str.equals(oldDatabase)) {
            this.firePropertyChange(PROPERTY_CHANGE_DATABASE, oldDatabase, getDatabase());
            this.changeAll();
        }
    }

    public String getDatabase() {
        return this.getDiagramContents().getSettings().getDatabase();
    }

    public void restoreDatabase(String str) {
        this.getDiagramContents().getSettings().setDatabase(str);
    }

    // ===================================================================================
    //                                                                   Category Handling
    //                                                                   =================
    public void setCurrentCategoryPageName() {
        this.editor.setCurrentCategoryPageName();
    }

    public void addCategory(Category category) {
        category.setColor(this.defaultColor[0], this.defaultColor[1], this.defaultColor[2]);
        this.getDiagramContents().getSettings().getCategorySetting().addCategoryAsSelected(category);
        this.editor.initVirtualPages();
        this.firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void removeCategory(Category category) {
        this.getDiagramContents().getSettings().getCategorySetting().removeCategory(category);
        this.editor.initVirtualPages();
        this.firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void restoreCategories() {
        this.editor.initVirtualPages();
        this.firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    // ===================================================================================
    //                                                                    Various Handling
    //                                                                    ================
    public void change() {
        this.firePropertyChange(PROPERTY_CHANGE_SETTINGS, null, null);
    }

    public void changeAll() {
        this.firePropertyChange(PROPERTY_CHANGE_ALL, null, null);
    }

    public void changeAll(List<DiagramWalker> nodeElementList) {
        this.firePropertyChange(PROPERTY_CHANGE_ALL, null, nodeElementList);
    }

    public void setSettings(DiagramSettings settings) {
        this.getDiagramContents().setSettings(settings);
        this.editor.initVirtualPages();

        this.firePropertyChange(PROPERTY_CHANGE_SETTINGS, null, null);
        this.firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    //	/**
    //	 * 全体ビューもしくは通常ビューで更新された内容を、全てのビューに展開します。
    //	 * @param event 発生したイベント
    //	 * @param nodeElement 更新したモデル
    //	 */
    //	public void refreshAllModel(PropertyChangeEvent event, NodeElement nodeElement) {
    //		if (nodeElement instanceof ERVirtualTable) {
    //			ERTable table = ((ERVirtualTable)nodeElement).getRawTable();
    //			table.getDiagram().doChangeTable(table);
    //			for (ERModel model : getDiagramContents().getModelSet()) {
    //				ERVirtualTable vtable = model.findVirtualTable(table);
    //				if (vtable != null) {
    //					vtable.doChangeTable();
    ////					vtable.firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
    //				}
    //			}
    //		} else if (nodeElement instanceof ERTable) {
    //			ERTable table = (ERTable)nodeElement;
    //			table.getDiagram().doChangeTable(table);
    //			for (ERModel model : getDiagramContents().getModelSet()) {
    //				ERVirtualTable vtable = model.findVirtualTable(table);
    //				if (vtable != null) {
    //					vtable.doChangeTable();
    ////					vtable.firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
    //				}
    //			}
    //		}
    //
    //	}

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + diagramContents + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DiagramContents getDiagramContents() {
        return diagramContents;
    }

    public void setEditor(ERFluteMultiPageEditor editor) {
        this.editor = editor;
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

    public void setCurrentCategory(Category currentCategory, int currentCategoryIndex) {
        this.currentCategory = currentCategory;
        this.currentCategoryIndex = currentCategoryIndex;
        this.changeAll();
    }

    public Category getCurrentCategory() {
        return currentCategory;
    }

    public int getCurrentCategoryIndex() {
        return currentCategoryIndex;
    }

    public boolean isTooltip() {
        return tooltip;
    }

    public void setTooltip(boolean tooltip) {
        this.tooltip = tooltip;
    }

    public ERVirtualDiagram getCurrentVirtualDiagram() {
        return currentVirtualDiagram;
    }

    public void setCurrentVirtualDiagram(ERVirtualDiagram vdiagram, String defaultModelName) {
        this.currentVirtualDiagram = vdiagram;
        this.defaultModelName = defaultModelName;
        if (vdiagram != null) {
            vdiagram.changeAll();
        }
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public DBSettings getDbSettings() {
        return dbSettings;
    }

    public void setDbSettings(DBSettings dbSetting) {
        this.dbSettings = dbSetting;
    }

    public PageSettings getPageSetting() {
        return pageSetting;
    }

    public void setPageSetting(PageSettings pageSetting) {
        this.pageSetting = pageSetting;
    }

    public ERFluteMultiPageEditor getEditor() {
        return editor;
    }

    public String filter(String str) {
        if (str == null) {
            return str;
        }
        final DiagramSettings settings = this.getDiagramContents().getSettings();
        if (settings.isCapital()) {
            return str.toUpperCase();
        }
        return str;
    }

    public void setShowMainColumn(boolean showMainColumn) {
        this.showMainColumn = showMainColumn;
    }

    public boolean isShowMainColumn() {
        return showMainColumn;
    }

    public boolean isDisableSelectColumn() {
        return disableSelectColumn;
    }

    public void setDisableSelectColumn(boolean disableSelectColumn) {
        this.disableSelectColumn = disableSelectColumn;
    }

    public String getDefaultModelName() {
        return defaultModelName;
    }
}

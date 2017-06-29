package org.dbflute.erflute.editor.model;

import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DesignResources;
import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
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
 *
 * TODO ymd 現状2つの債務を持っている。
 * 1.メインダイアグラムのモデル
 * 2.メインダイアグラムと仮想ダイアグラムのコントローラ
 *
 * この2つの債務を分離したい。ERDiagramにはコントローラの債務だけ残す。
 */
public class ERDiagram extends ViewableModel {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_ALL = "all";
    public static final String PROPERTY_CHANGE_DATABASE = "database";
    public static final String PROPERTY_CHANGE_SETTINGS = "settings";
    public static final String PROPERTY_CHANGE_ADD = "add";
    public static final String PROPERTY_CHANGE_DELETE = "delete";
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
    private Point mousePoint = new Point();
    private String defaultModelName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ERDiagram(String database) {
        this.diagramContents = new DiagramContents();
        this.diagramContents.getSettings().setDatabase(database);
        this.pageSetting = new PageSettings();
        setDefaultColor(DesignResources.ERDIAGRAM_DEFAULT_COLOR);
        setColor(DesignResources.WHITE);
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

    public void addWalkerPlainly(final DiagramWalker walker) {
        if (getCurrentVirtualDiagram() != null) {
            getCurrentVirtualDiagram().addWalkerPlainly(walker);
        }

        walker.setDiagram(this);

        // 仮想ダイアグラムへのノート追加でない場合
        if (!(getCurrentVirtualDiagram() != null && walker instanceof WalkerNote)) {
            diagramContents.getDiagramWalkers().add(walker);
        }

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

        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void removeWalker(DiagramWalker walker) {
        if (getCurrentVirtualDiagram() != null) {
            getCurrentVirtualDiagram().removeWalker(walker);
        }

        diagramContents.getDiagramWalkers().remove(walker);
        if (walker instanceof ERTable) {
            // メインビューのテーブルを削除したときは、仮想ビューのノードも削除（線が消えずに残ってしまう）
            for (final ERVirtualDiagram vdiagram : getDiagramContents().getVirtualDiagramSet()) {
                final ERVirtualTable vtable = vdiagram.findVirtualTable((TableView) walker);
                vdiagram.removeWalker(vtable);
            }
        }

        if (walker instanceof TableView) {
            diagramContents.getDictionary().remove((TableView) walker);
        }

        for (final Category category : diagramContents.getSettings().getCategorySetting().getAllCategories()) {
            category.getContents().remove(walker);
        }

        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void replaceContents(DiagramContents newDiagramContents) {
        this.diagramContents = newDiagramContents;
        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public boolean virtualDiagramContains(DiagramWalker walker) {
        if (getCurrentVirtualDiagram() == null) {
            return false;
        }

        return getCurrentCategory().contains(walker);
    }

    // ===================================================================================
    //                                                                   ER/Table Handling
    //                                                                   =================
    public void addVirtualDiagram(ERVirtualDiagram ermodel) {
        diagramContents.getVirtualDiagramSet().add(ermodel);
        firePropertyChange(PROPERTY_CHANGE_ADD, null, ermodel);
    }

    public void removeVirtualDiagram(ERVirtualDiagram ermodel) {
        diagramContents.getVirtualDiagramSet().removeByName(ermodel.getName());
        firePropertyChange(PROPERTY_CHANGE_DELETE, ermodel, null);
    }

    public void changeTable(TableView tableView) {
        firePropertyChange(PROPERTY_CHANGE_TABLE, null, tableView);
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
        getDiagramContents().getSettings().setDatabase(str);
        if (str != null && !str.equals(oldDatabase)) {
            firePropertyChange(PROPERTY_CHANGE_DATABASE, oldDatabase, getDatabase());
            changeAll();
        }
    }

    public String getDatabase() {
        return getDiagramContents().getSettings().getDatabase();
    }

    public void restoreDatabase(String str) {
        getDiagramContents().getSettings().setDatabase(str);
    }

    // ===================================================================================
    //                                                                   Category Handling
    //                                                                   =================
    public void setCurrentCategoryPageName() {
        editor.setCurrentCategoryPageName();
    }

    public void addCategory(Category category) {
        category.setColor(defaultColor[0], defaultColor[1], defaultColor[2]);
        getDiagramContents().getSettings().getCategorySetting().addCategoryAsSelected(category);
        editor.initVirtualPages();
        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void removeCategory(Category category) {
        getDiagramContents().getSettings().getCategorySetting().removeCategory(category);
        editor.initVirtualPages();
        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    public void restoreCategories() {
        editor.initVirtualPages();
        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

    // ===================================================================================
    //                                                                    Various Handling
    //                                                                    ================
    public void change() {
        firePropertyChange(PROPERTY_CHANGE_SETTINGS, null, null);
    }

    public void changeAll() {
        firePropertyChange(PROPERTY_CHANGE_ALL, null, null);
    }

    public void changeAll(List<DiagramWalker> nodeElementList) {
        firePropertyChange(PROPERTY_CHANGE_ALL, null, nodeElementList);
    }

    public void setSettings(DiagramSettings settings) {
        getDiagramContents().setSettings(settings);
        editor.initVirtualPages();

        firePropertyChange(PROPERTY_CHANGE_SETTINGS, null, null);
        firePropertyChange(DiagramWalkerSet.PROPERTY_CHANGE_DIAGRAM_WALKER, null, null);
    }

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

    /*
     * TODO ymd なるべく使わない方向で。直接触らないでERDiagramを介する。
     * クライアントクラスは、getDiagram().getCurrentVirtualDiagram().目的の操作()ではなく、
     * getDiagram().目的の操作()を実行する。
     */
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
        final DiagramSettings settings = getDiagramContents().getSettings();
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

    public Point getMousePoint() {
        return mousePoint;
    }

    public void setMousePoint(Point mousePoint) {
        this.mousePoint = mousePoint;
    }
}

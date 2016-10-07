package org.dbflute.erflute.editor.model;

import java.util.List;

import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.dbflute.erflute.editor.model.settings.DBSetting;
import org.dbflute.erflute.editor.model.settings.PageSetting;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.eclipse.draw2d.geometry.Point;

/**
 * #analyzed is one ERD
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
    private ERModel currentErmodel;
    private double zoom = 1.0d;
    private int x;
    private int y;
    private DBSetting dbSetting;
    private PageSetting pageSetting;
    public Point mousePoint = new Point();
    private String defaultModelName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ERDiagram(String database) {
        this.diagramContents = new DiagramContents();
        this.diagramContents.getSettings().setDatabase(database);
        this.pageSetting = new PageSetting();
        this.setDefaultColor(128, 128, 192);
        this.setColor(255, 255, 255);
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void init() {
        diagramContents.setColumnGroups(GlobalGroupSet.load());
        final Settings settings = this.getDiagramContents().getSettings();
        settings.getModelProperties().init();
    }

    // ===================================================================================
    //                                                                    Content Handling
    //                                                                    ================
    public void addNewContent(NodeElement element) {
        element.setColor(this.defaultColor[0], this.defaultColor[1], this.defaultColor[2]);
        element.setFontName(this.getFontName());
        element.setFontSize(this.getFontSize());
        addContent(element);
    }

    public void addContent(NodeElement element) {
        element.setDiagram(this);
        this.diagramContents.getContents().addNodeElement(element);
        if (this.editor != null) {
            final Category category = this.editor.getCurrentPageCategory();
            if (category != null) {
                category.getContents().add(element);
            }
        }
        if (element instanceof TableView) {
            for (final NormalColumn normalColumn : ((TableView) element).getNormalColumns()) {
                this.getDiagramContents().getDictionary().add(normalColumn);
            }
        }
        if (element instanceof ERTable) {
            final ERTable table = (ERTable) element;
            if (getCurrentErmodel() != null) {
                // ビュー上に仮想テーブルを追加する
                final ERModel model = getCurrentErmodel();
                final ERVirtualTable virtualTable = new ERVirtualTable(model, table);
                virtualTable.setPoint(element.getX(), element.getY());

                // メインビュー上では左上に配置
                element.setLocation(new Location(0, 0, element.getWidth(), element.getHeight()));

                model.addTable(virtualTable);
            }
        }
        if (element instanceof ERVirtualTable) {
            final ERVirtualTable virtualTable = (ERVirtualTable) element;
            if (getCurrentErmodel() != null) {
                // ビュー上に仮想テーブルを追加する
                final ERModel model = getCurrentErmodel();
                //ERVirtualTable virtualTable = new ERVirtualTable(model, table);
                virtualTable.setPoint(element.getX(), element.getY());

                // メインビュー上では左上に配置
                element.setLocation(new Location(0, 0, element.getWidth(), element.getHeight()));

                model.addTable(virtualTable);
            }
        }
        this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public void removeContent(NodeElement element) {
        if (element instanceof ERVirtualTable) {
            // メインビューのノードは残して仮想テーブルだけ削除
            currentErmodel.remove((ERVirtualTable) element);
        } else if (element instanceof VGroup) {
            currentErmodel.remove((VGroup) element);
        } else if (element instanceof Note) {
            currentErmodel.remove((Note) element);
        } else {
            this.diagramContents.getContents().remove(element);
            if (element instanceof ERTable) {
                // メインビューのテーブルを削除したときは、ビューのノードも削除（線が消えずに残ってしまう）
                for (final ERModel model : getDiagramContents().getModelSet()) {
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

        this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public void replaceContents(DiagramContents newDiagramContents) {
        this.diagramContents = newDiagramContents;
        this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
    }

    // ===================================================================================
    //                                                                   ER/Table Handling
    //                                                                   =================
    public void addErmodel(ERModel ermodel) {
        diagramContents.getModelSet().add(ermodel);
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
        for (final ERModel model : getDiagramContents().getModelSet()) {
            final ERVirtualTable vtable = model.findVirtualTable(table);
            if (vtable != null) {
                vtable.doChangeTable();
            }
        }
    }

    public ERModel findModelByTable(ERTable table) {
        for (final ERModel model : diagramContents.getModelSet()) {
            for (final ERVirtualTable vtable : model.getTables()) {
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
        this.editor.initCategoryPages();
        this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public void removeCategory(Category category) {
        this.getDiagramContents().getSettings().getCategorySetting().removeCategory(category);
        this.editor.initCategoryPages();
        this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
    }

    public void restoreCategories() {
        this.editor.initCategoryPages();
        this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
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

    public void changeAll(List<NodeElement> nodeElementList) {
        this.firePropertyChange(PROPERTY_CHANGE_ALL, null, nodeElementList);
    }

    public void setSettings(Settings settings) {
        this.getDiagramContents().setSettings(settings);
        this.editor.initCategoryPages();

        this.firePropertyChange(PROPERTY_CHANGE_SETTINGS, null, null);
        this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
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

    public void setDefaultColor(int red, int green, int blue) {
        this.defaultColor = new int[3];
        this.defaultColor[0] = red;
        this.defaultColor[1] = green;
        this.defaultColor[2] = blue;
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

    public ERModel getCurrentErmodel() {
        return currentErmodel;
    }

    public void setCurrentErmodel(ERModel model, String defaultModelName) {
        this.currentErmodel = model;
        this.defaultModelName = defaultModelName;
        if (model != null) {
            model.changeAll();
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

    public DBSetting getDbSetting() {
        return dbSetting;
    }

    public void setDbSetting(DBSetting dbSetting) {
        this.dbSetting = dbSetting;
    }

    public PageSetting getPageSetting() {
        return pageSetting;
    }

    public void setPageSetting(PageSetting pageSetting) {
        this.pageSetting = pageSetting;
    }

    public ERFluteMultiPageEditor getEditor() {
        return editor;
    }

    public String filter(String str) {
        if (str == null) {
            return str;
        }

        final Settings settings = this.getDiagramContents().getSettings();

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

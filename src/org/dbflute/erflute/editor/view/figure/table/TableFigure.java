package org.dbflute.erflute.editor.view.figure.table;

import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.action.option.notation.design.ChangeDesignToFrameAction;
import org.dbflute.erflute.editor.view.action.option.notation.design.ChangeDesignToSimpleAction;
import org.dbflute.erflute.editor.view.figure.table.column.GroupColumnFigure;
import org.dbflute.erflute.editor.view.figure.table.column.NormalColumnFigure;
import org.dbflute.erflute.editor.view.figure.table.style.StyleSupport;
import org.dbflute.erflute.editor.view.figure.table.style.frame.FrameStyleSupport;
import org.dbflute.erflute.editor.view.figure.table.style.funny.FunnyStyleSupport;
import org.dbflute.erflute.editor.view.figure.table.style.simple.SimpleStyleSupport;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class TableFigure extends RoundedRectangle {

    private final Figure columns;
    private StyleSupport styleSupport;
    private Color foregroundColor;
    private Font largeFont;

    public TableFigure(DiagramSettings settings) {
        this.columns = new Figure();
        setLayoutManager(new BorderLayout());
        setSettings(settings);
    }

    public void setSettings(DiagramSettings settings) {
        final String tableStyle = settings.getTableStyle();
        if (ChangeDesignToSimpleAction.TYPE.equals(tableStyle)) {
            this.styleSupport = new SimpleStyleSupport(this, settings);
        } else if (ChangeDesignToFrameAction.TYPE.equals(tableStyle)) {
            this.styleSupport = new FrameStyleSupport(this, settings);
        } else {
            this.styleSupport = new FunnyStyleSupport(this, settings);
        }
        styleSupport.init();
        create(null);
    }

    public void create(int[] color) {
        decideColor(color);
        removeAll();
        styleSupport.createTitleBar();
        columns.removeAll();
        styleSupport.createColumnArea(columns);
        styleSupport.createFooter();
    }

    private void decideColor(int[] color) {
        if (color != null) {
            final int sum = color[0] + color[1] + color[2];
            if (sum > 255) {
                this.foregroundColor = ColorConstants.black;
            } else {
                this.foregroundColor = ColorConstants.white;
            }
        }
    }

    public void setName(String name) {
        styleSupport.setName(name);
    }

    public void setFont(Font font, Font titleFont) {
        setFont(font);
        styleSupport.setFont(font, titleFont);
    }

    public void clearColumns() {
        columns.removeAll();
    }

    public void addColumn(ERTable table, NormalColumn normalColumn, NormalColumnFigure columnFigure, int viewMode, String physicalName,
            String logicalName, String type, boolean primaryKey, boolean foreignKey, boolean isNotNull, boolean uniqueKey,
            boolean displayKey, boolean displayDetail, boolean displayType, boolean isSelectedReferenced, boolean isSelectedForeignKey,
            boolean isAdded, boolean isUpdated, boolean isRemoved) {
        columnFigure.removeAll();
        columnFigure.setBackgroundColor(null);
        styleSupport.addColumn(table, normalColumn, columnFigure, viewMode, physicalName, logicalName, type, primaryKey, foreignKey,
                isNotNull, uniqueKey, displayKey, displayDetail, displayType, isSelectedReferenced, isSelectedForeignKey, isAdded,
                isUpdated, isRemoved);
    }

    public void addColumnGroup(GroupColumnFigure columnFigure, int viewMode, String name, boolean isAdded, boolean isUpdated,
            boolean isRemoved) {
        columnFigure.removeAll();
        columnFigure.setBackgroundColor(null);
        styleSupport.addColumnGroup(columnFigure, viewMode, name, isAdded, isUpdated, isRemoved);
    }

    public void addIndex(IndexFigure indexFigure, int viewMode, String physicalName, String logicalName, boolean isFirst) {
        indexFigure.removeAll();
        indexFigure.setBackgroundColor(null);
        styleSupport.addIndex(indexFigure, physicalName, isFirst);
    }

    @Override
    public Rectangle getBounds() {
        final Rectangle bounds = super.getBounds();
        styleSupport.adjustBounds(bounds);
        return bounds;
    }

    public Color getTextColor() {
        return foregroundColor;
    }

    @Override
    protected void fillShape(Graphics graphics) {
        graphics.setAlpha(200);
        super.fillShape(graphics);
    }

    public Figure getColumns() {
        return columns;
    }

    public String getImageKey() {
        return ImageKey.TABLE;
    }

    public void setLargeFont(Font largeFont) {
        this.largeFont = largeFont;
    }

    public Font getLargeFont() {
        return largeFont;
    }
}

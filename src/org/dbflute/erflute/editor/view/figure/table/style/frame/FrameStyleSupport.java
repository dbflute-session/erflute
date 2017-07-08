package org.dbflute.erflute.editor.view.figure.table.style.frame;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.figure.table.IndexFigure;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.dbflute.erflute.editor.view.figure.table.column.NormalColumnFigure;
import org.dbflute.erflute.editor.view.figure.table.style.AbstractStyleSupport;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;

public class FrameStyleSupport extends AbstractStyleSupport {

    private ImageFrameBorder border;

    private TitleBarBorder titleBarBorder;

    public FrameStyleSupport(TableFigure tableFigure, DiagramSettings settings) {
        super(tableFigure, settings);
    }

    @Override
    public void init(TableFigure tableFigure) {
        this.border = new ImageFrameBorder();
        border.setFont(tableFigure.getFont());

        tableFigure.setBorder(border);
    }

    @Override
    public void initTitleBar(Figure top) {
        this.titleBarBorder = (TitleBarBorder) border.getInnerBorder();
        titleBarBorder.setTextAlignment(PositionConstants.CENTER);
        titleBarBorder.setPadding(new Insets(5, 20, 5, 20));
    }

    @Override
    public void setName(String name) {
        titleBarBorder.setTextColor(getTextColor());
        titleBarBorder.setLabel(name);
    }

    @Override
    public void setFont(Font font, Font titleFont) {
        titleBarBorder.setFont(titleFont);
    }

    @Override
    public void adjustBounds(Rectangle rect) {
        final int width = border.getTitleBarWidth(getTableFigure());

        if (width > rect.width) {
            rect.width = width;
        }
    }

    @Override
    public void addColumn(ERTable table, NormalColumn normalColumn, NormalColumnFigure columnFigure, int viewMode, String physicalName,
            String logicalName, String type, boolean primaryKey, boolean foreignKey, boolean isNotNull, boolean uniqueKey,
            boolean displayKey, boolean displayDetail, boolean displayType, boolean isSelectedReferenced, boolean isSelectedForeignKey,
            boolean isAdded, boolean isUpdated, boolean isRemoved) {

        final Label label = createColumnLabel();
        label.setForegroundColor(getTextColor());

        final StringBuilder text = new StringBuilder();
        text.append(getColumnText(table, normalColumn, viewMode, physicalName, logicalName,
                type, isNotNull, uniqueKey, displayDetail, displayType));

        if (displayKey) {
            if (primaryKey && foreignKey) {
                label.setForegroundColor(ColorConstants.blue);

                text.append(" ");
                text.append("(PFK)");
            } else if (primaryKey) {
                label.setForegroundColor(ColorConstants.red);

                text.append(" ");
                text.append("(PK)");
            } else if (foreignKey) {
                label.setForegroundColor(ColorConstants.darkGreen);

                text.append(" ");
                text.append("(FK)");
            }
        }

        setColumnFigureColor(columnFigure, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);

        label.setText(text.toString());

        columnFigure.add(label);
    }

    @Override
    public void addIndex(IndexFigure indexFigure, String name, boolean isFirst) {
        // TODO Auto-generated method stub
    }
}

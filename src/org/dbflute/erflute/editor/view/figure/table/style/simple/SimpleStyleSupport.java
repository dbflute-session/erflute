package org.dbflute.erflute.editor.view.figure.table.style.simple;

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
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;

public class SimpleStyleSupport extends AbstractStyleSupport {

    private Label nameLabel;

    public SimpleStyleSupport(TableFigure tableFigure, DiagramSettings settings) {
        super(tableFigure, settings);
    }

    @Override
    public void init(TableFigure tableFigure) {
        tableFigure.setCornerDimensions(new Dimension(10, 10));
        tableFigure.setBorder(null);
    }

    @Override
    public void initTitleBar(Figure top) {
        final ToolbarLayout topLayout = new ToolbarLayout();

        topLayout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
        topLayout.setStretchMinorAxis(true);
        top.setLayoutManager(topLayout);

        this.nameLabel = new Label();
        nameLabel.setBorder(new MarginBorder(new Insets(5, 20, 5, 20)));
        top.add(nameLabel);

        final Figure separater = new Figure();
        separater.setSize(-1, 1);
        separater.setBackgroundColor(getTextColor());
        separater.setOpaque(true);

        top.add(separater);
    }

    @Override
    public void setName(String name) {
        nameLabel.setForegroundColor(getTextColor());
        nameLabel.setText(name);
    }

    @Override
    public void setFont(Font font, Font titleFont) {
        nameLabel.setFont(titleFont);
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

        label.setText(text.toString());

        setColumnFigureColor(columnFigure, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);

        columnFigure.add(label);
    }

    @Override
    public void addIndex(IndexFigure indexFigure, String name, boolean isFirst) {
        // TODO Auto-generated method stub
    }
}

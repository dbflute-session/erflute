package org.dbflute.erflute.editor.view.figure.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class TableLayout extends AbstractHintLayout {

    private int colnum;
    private final int separatorWidth;
    private final List<IFigure> separators;

    public TableLayout(int colnum) {
        super();

        this.colnum = colnum;
        if (colnum <= 0) {
            this.colnum = 1;
        }

        this.separators = new ArrayList<>();
        this.separatorWidth = 1;
    }

    public void setSeparator() {

    }

    @Override
    public void layout(IFigure parent) {

        final List<IFigure> children = clearSeparator(parent);

        final List<List<IFigure>> table = getTable(children);
        final int[] columnWidth = getColumnWidth(table);
        final int[] rowHeight = getRowHeight(table);

        final Rectangle rect = parent.getBounds();

        int x = rect.x + 1;
        int y = rect.y + 1;

        for (int i = 0; i < table.size(); i++) {
            final List<IFigure> tableRow = table.get(i);

            for (int j = 0; j < tableRow.size(); j++) {
                final Rectangle childRect = new Rectangle(x, y, columnWidth[j], rowHeight[i]);

                final IFigure figure = tableRow.get(j);
                figure.setBounds(childRect);

                x += columnWidth[j];

                if (j != tableRow.size() - 1) {
                    final Rectangle separetorRect = new Rectangle(x, y, separatorWidth, rowHeight[i]);
                    addVerticalSeparator(parent, separetorRect);

                    x += separatorWidth;
                }
            }

            x = rect.x + 1;
            y += rowHeight[i];

            if (i != table.size() - 1) {
                final Rectangle separetorRect = new Rectangle(x, y, rect.width, separatorWidth);

                addHorizontalSeparator(parent, separetorRect);

                y += separatorWidth;
            }
        }
    }

    private List<List<IFigure>> getTable(List<IFigure> children) {
        final int numChildren = children.size();

        final List<List<IFigure>> table = new ArrayList<>();

        List<IFigure> row = null;

        for (int i = 0; i < numChildren; i++) {
            if (i % colnum == 0) {
                row = new ArrayList<>();
                table.add(row);
            }

            row.add(children.get(i));
        }

        return table;
    }

    private int[] getColumnWidth(List<List<IFigure>> table) {
        final int[] columnWidth = new int[colnum];

        for (int i = 0; i < colnum; i++) {
            for (final List<IFigure> tableRow : table) {
                if (tableRow.size() > i) {
                    final IFigure figure = tableRow.get(i);

                    final int width = figure.getPreferredSize().width;

                    if (width > columnWidth[i]) {
                        columnWidth[i] = (int) (width * 1.3);
                    }
                }
            }
        }

        return columnWidth;
    }

    private int[] getRowHeight(List<List<IFigure>> table) {
        final int[] rowHeight = new int[table.size()];

        for (int i = 0; i < rowHeight.length; i++) {
            for (final IFigure cell : table.get(i)) {
                final int height = cell.getPreferredSize().height;

                if (height > rowHeight[i]) {
                    rowHeight[i] = height;
                }
            }
        }

        return rowHeight;
    }

    private List<IFigure> getChildren(IFigure parent) {
        final List<IFigure> children = new ArrayList<>();

        for (@SuppressWarnings("unchecked")
        final Iterator<Polyline> iter = parent.getChildren().iterator(); iter.hasNext();) {
            final IFigure child = iter.next();

            if (!separators.contains(child)) {
                children.add(child);
            }
        }

        return children;
    }

    @SuppressWarnings("unchecked")
    private List<IFigure> clearSeparator(IFigure parent) {
        for (final Iterator<Polyline> iter = parent.getChildren().iterator(); iter.hasNext();) {
            final IFigure child = iter.next();

            if (separators.contains(child)) {
                iter.remove();
            }
        }

        separators.clear();

        return parent.getChildren();
    }

    @Override
    protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
        final List<IFigure> children = getChildren(container);

        final List<List<IFigure>> table = getTable(children);
        final int[] columnWidth = getColumnWidth(table);
        final int[] rowHeight = getRowHeight(table);

        int width = 0;
        for (int i = 0; i < columnWidth.length; i++) {
            width += columnWidth[i];
            if (i != columnWidth.length - 1) {
                width += separatorWidth;
            }
        }
        width++;
        width++;

        int height = 0;
        for (int i = 0; i < rowHeight.length; i++) {
            height += rowHeight[i];
            if (i != rowHeight.length - 1) {
                height += separatorWidth;
            }
        }
        height++;
        height++;

        return new Dimension(width, height);
    }

    @SuppressWarnings("unchecked")
    private void addVerticalSeparator(IFigure figure, Rectangle rect) {
        final Polyline separator = new Polyline();
        separator.setLineWidth(separatorWidth);
        separator.addPoint(new Point(rect.x, rect.y));
        separator.addPoint(new Point(rect.x, rect.y + rect.height));

        figure.getChildren().add(separator);
        separator.setParent(figure);

        separators.add(separator);
    }

    @SuppressWarnings("unchecked")
    private void addHorizontalSeparator(IFigure figure, Rectangle rect) {
        final Polyline separator = new Polyline();
        separator.setLineWidth(separatorWidth);
        separator.addPoint(new Point(rect.x, rect.y));
        separator.addPoint(new Point(rect.x + rect.width, rect.y));
        figure.getChildren().add(separator);
        separator.setParent(figure);

        separators.add(separator);
    }
}

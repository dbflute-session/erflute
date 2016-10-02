package org.dbflute.erflute.editor.view.figure.table.style;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.view.figure.table.IndexFigure;
import org.dbflute.erflute.editor.view.figure.table.column.GroupColumnFigure;
import org.dbflute.erflute.editor.view.figure.table.column.NormalColumnFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;

public interface StyleSupport {

    public void init();

    public void createTitleBar();

    public void createColumnArea(IFigure columns);

    public void createFooter();

    public void setName(String name);

    public void setFont(Font font, Font titleFont);

    public void adjustBounds(Rectangle rect);

    public void addColumn(ERTable table, NormalColumn normalColumn, NormalColumnFigure columnFigure, int viewMode, String physicalName,
            String logicalName, String type, boolean primaryKey, boolean foreignKey, boolean isNotNull, boolean uniqueKey,
            boolean displayKey, boolean displayDetail, boolean displayType, boolean isSelectedReferenced, boolean isSelectedForeignKey,
            boolean isAdded, boolean isUpdated, boolean isRemoved);

    public void addColumnGroup(GroupColumnFigure columnFigure, int viewMode, String name, boolean isAdded, boolean isUpdated,
            boolean isRemoved);

    public void addIndex(IndexFigure indexFigure, String name, boolean isFirst);

}

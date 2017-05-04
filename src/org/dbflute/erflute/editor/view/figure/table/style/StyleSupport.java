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

    void init();

    void createTitleBar();

    void createColumnArea(IFigure columns);

    void createFooter();

    void setName(String name);

    void setFont(Font font, Font titleFont);

    void adjustBounds(Rectangle rect);

    void addColumn(ERTable table, NormalColumn normalColumn, NormalColumnFigure columnFigure, int viewMode, String physicalName,
            String logicalName, String type, boolean primaryKey, boolean foreignKey, boolean isNotNull, boolean uniqueKey,
            boolean displayKey, boolean displayDetail, boolean displayType, boolean isSelectedReferenced, boolean isSelectedForeignKey,
            boolean isAdded, boolean isUpdated, boolean isRemoved);

    void addColumnGroup(GroupColumnFigure columnFigure, int viewMode, String name, boolean isAdded, boolean isUpdated, boolean isRemoved);

    void addIndex(IndexFigure indexFigure, String name, boolean isFirst);
}

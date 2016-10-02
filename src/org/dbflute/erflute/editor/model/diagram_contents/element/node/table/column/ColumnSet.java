package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class ColumnSet implements ObjectListModel {

    public String getDescription() {
        return "";
    }

    public String getName() {
        return DisplayMessages.getMessage("label.object.type.column_list");
    }

    public String getObjectType() {
        return "list";
    }

}

package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import org.insightech.er.DisplayMessages;
import org.insightech.er.editor.model.ObjectListModel;

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

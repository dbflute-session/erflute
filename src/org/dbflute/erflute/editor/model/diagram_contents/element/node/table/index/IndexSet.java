package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class IndexSet extends AbstractModel implements ObjectListModel {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_INDEXES = "indexes";

    public void update() {
        firePropertyChange(PROPERTY_CHANGE_INDEXES, null, null);
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.index_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}

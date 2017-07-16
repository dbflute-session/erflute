package org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger;

import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.WithSchemaModel;

public class Trigger extends WithSchemaModel implements ObjectModel {

    private static final long serialVersionUID = 1L;

    private String sql;
    private String description;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getObjectType() {
        return "trigger";
    }
}

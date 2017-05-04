package org.dbflute.erflute.editor.model.diagram_contents.not_element.custom_type;

import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.WithSchemaModel;

public class CustomType extends WithSchemaModel implements ObjectModel {

    private static final long serialVersionUID = 1L;

    private String description;

    @Override
    public String getObjectType() {
        return "sequence";
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

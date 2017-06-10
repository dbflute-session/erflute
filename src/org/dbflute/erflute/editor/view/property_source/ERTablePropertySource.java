package org.dbflute.erflute.editor.view.property_source;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ERTablePropertySource implements IPropertySource {

    private ERTable table;

    public ERTablePropertySource(ERTable table) {
        this.table = table;
    }

    public Object getEditableValue() {
        return this.table;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("physicalName", DisplayMessages.getMessage("label.physical.name")),
                new TextPropertyDescriptor("logicalName", DisplayMessages.getMessage("label.logical.name")) };
    }

    public Object getPropertyValue(Object id) {
        if (id.equals("physicalName")) {
            return this.table.getPhysicalName() != null ? this.table.getPhysicalName() : "";
        }
        if (id.equals("logicalName")) {
            return this.table.getLogicalName() != null ? this.table.getLogicalName() : "";
        }
        return null;
    }

    public boolean isPropertySet(Object id) {
        if (id.equals("physicalName")) {
            return true;
        }
        if (id.equals("logicalName")) {
            return true;
        }
        return false;
    }

    public void resetPropertyValue(Object id) {
    }

    public void setPropertyValue(Object id, Object value) {
        if (id.equals("physicalName")) {
            this.table.setPhysicalName(String.valueOf(value));

        } else if (id.equals("logicalName")) {
            this.table.setLogicalName(String.valueOf(value));
        }
    }
}

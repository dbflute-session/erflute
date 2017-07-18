package org.dbflute.erflute.editor.view.property_source;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ERTablePropertySource implements IPropertySource {

    private final ERTable table;

    public ERTablePropertySource(ERTable table) {
        this.table = table;
    }

    @Override
    public Object getEditableValue() {
        return table;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("physicalName", DisplayMessages.getMessage("label.physical.name")),
                new TextPropertyDescriptor("logicalName", DisplayMessages.getMessage("label.logical.name")) };
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (id.equals("physicalName")) {
            return table.getPhysicalName() != null ? table.getPhysicalName() : "";
        }
        if (id.equals("logicalName")) {
            return table.getLogicalName() != null ? table.getLogicalName() : "";
        }
        return null;
    }

    @Override
    public boolean isPropertySet(Object id) {
        if (id.equals("physicalName")) {
            return true;
        }
        if (id.equals("logicalName")) {
            return true;
        }
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        if (id.equals("physicalName")) {
            table.setPhysicalName(String.valueOf(value));
        } else if (id.equals("logicalName")) {
            table.setLogicalName(String.valueOf(value));
        }
    }
}

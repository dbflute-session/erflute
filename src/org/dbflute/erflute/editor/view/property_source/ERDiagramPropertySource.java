package org.dbflute.erflute.editor.view.property_source;

import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class ERDiagramPropertySource implements IPropertySource {

    public static final String PROPERTY_INIT_DATABASE = "initDatabase";

    private final ERDiagram diagram;

    public ERDiagramPropertySource(ERDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public Object getEditableValue() {
        return diagram;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        final List<String> dbList = DBManagerFactory.getAllDBList();

        return new IPropertyDescriptor[] { new ComboBoxPropertyDescriptor("database", DisplayMessages.getMessage("label.database"),
                dbList.toArray(new String[dbList.size()])) };
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (id.equals("database")) {
            final List<String> dbList = DBManagerFactory.getAllDBList();

            for (int i = 0; i < dbList.size(); i++) {
                if (dbList.get(i).equals(diagram.getDatabase())) {
                    return new Integer(i);
                }
            }

            return new Integer(0);
        }

        return null;
    }

    @Override
    public boolean isPropertySet(Object id) {
        if (id.equals("database")) {
            return true;
        }
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        if (id.equals("database")) {
            final List<String> dbList = DBManagerFactory.getAllDBList();

            final int index = Integer.parseInt(String.valueOf(value));

            diagram.setDatabase(dbList.get(index));
        }
    }
}

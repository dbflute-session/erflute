package org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ModelProperties extends DiagramWalker implements Cloneable {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_MODEL_PROPERTIES = "model_properties";

    private boolean display; // show model properties table on editor, true if menu "show stamp"
    private List<NameValue> properties;

    public ModelProperties() {
        setLocation(new Location(50, 50, -1, -1));
        this.properties = new ArrayList<>();
    }

    public void init() { // when new diagram
        // #for_erflute remove minor function to be simple
        //properties.add(new NameValue(DisplayMessages.getMessage("label.project.name"), ""));
        //properties.add(new NameValue(DisplayMessages.getMessage("label.model.name"), ""));
        //properties.add(new NameValue(DisplayMessages.getMessage("label.version"), ""));
        //properties.add(new NameValue(DisplayMessages.getMessage("label.company.name"), ""));
        //properties.add(new NameValue(DisplayMessages.getMessage("label.author"), ""));
        properties.add(new NameValue("title", ""));
        properties.add(new NameValue("author", ""));
    }

    public void clear() {
        properties.clear();
    }

    public List<NameValue> getProperties() {
        return properties;
    }

    public void addProperty(NameValue property) {
        properties.add(property);
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
        firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
    }

    @Override
    public void setLocation(Location location) {
        location.width = -1;
        location.height = -1;
        super.setLocation(location);
    }

    @Override
    public ModelProperties clone() {
        final ModelProperties clone = (ModelProperties) super.clone();
        final List<NameValue> list = new ArrayList<>();
        for (final NameValue nameValue : properties) {
            list.add(nameValue.clone());
        }
        clone.properties = list;
        return clone;
    }

    public void setProperties(List<NameValue> properties) {
        this.properties = properties;
        firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getObjectType() {
        return "model_properties";
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return false;
    }

    @Override
    public int getPersistentOrder() {
        return 8;
    }

    @Override
    public boolean isUsePersistentId() {
        return true;
    }

    @Override
    public boolean isIndenpendentOnModel() {
        return false;
    }
}

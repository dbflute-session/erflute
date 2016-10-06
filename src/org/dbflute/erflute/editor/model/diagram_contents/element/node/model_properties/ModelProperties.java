package org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;

public class ModelProperties extends NodeElement implements Cloneable {

    private static final long serialVersionUID = 5311013351131568260L;
    public static final String PROPERTY_CHANGE_MODEL_PROPERTIES = "model_properties";

    private boolean display;
    private List<NameValue> properties;
    private Date creationDate;
    private Date updatedDate;

    public ModelProperties() {
        this.creationDate = new Date();
        this.updatedDate = new Date();
        this.setLocation(new Location(50, 50, -1, -1));
        this.properties = new ArrayList<NameValue>();
    }

    public void init() {
        properties.add(new NameValue(DisplayMessages.getMessage("label.project.name"), ""));
        properties.add(new NameValue(DisplayMessages.getMessage("label.model.name"), ""));
        properties.add(new NameValue(DisplayMessages.getMessage("label.version"), ""));
        properties.add(new NameValue(DisplayMessages.getMessage("label.company.name"), ""));
        properties.add(new NameValue(DisplayMessages.getMessage("label.author"), ""));
    }

    public void clear() {
        this.properties.clear();
    }

    public List<NameValue> getProperties() {
        return properties;
    }

    public void addProperty(NameValue property) {
        this.properties.add(property);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
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
        final List<NameValue> list = new ArrayList<NameValue>();
        for (final NameValue nameValue : this.properties) {
            list.add(nameValue.clone());
        }
        clone.properties = list;
        return clone;
    }

    public void setProperties(List<NameValue> properties) {
        this.properties = properties;
        this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
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
}

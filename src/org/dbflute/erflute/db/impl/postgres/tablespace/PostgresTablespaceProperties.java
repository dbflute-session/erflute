package org.dbflute.erflute.db.impl.postgres.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class PostgresTablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = -1168759105844875794L;

    private String location;

    private String owner;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public TablespaceProperties clone() {
        final PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

        properties.location = this.location;
        properties.owner = this.owner;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("label.tablespace.location", this.getLocation());
        map.put("label.tablespace.owner", this.getOwner());

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<>();

        if (Check.isEmptyTrim(this.getLocation())) {
            errorMessage.add("error.tablespace.location.empty");
        }

        return errorMessage;
    }
}

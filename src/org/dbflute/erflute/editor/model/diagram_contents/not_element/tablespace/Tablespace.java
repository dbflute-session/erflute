package org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace;

import java.util.HashMap;
import java.util.Map;

import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.settings.Environment;

public class Tablespace extends AbstractModel implements ObjectModel, Comparable<Tablespace> {

    private static final long serialVersionUID = 1L;

    private String name;
    private Map<Environment, TablespaceProperties> propertiesMap = new HashMap<>();

    @Override
    public int compareTo(Tablespace other) {
        return name.toUpperCase().compareTo(other.name.toUpperCase());
    }

    public void copyTo(Tablespace to) {
        to.name = name;

        to.propertiesMap = new HashMap<>();
        for (final Map.Entry<Environment, TablespaceProperties> entry : propertiesMap.entrySet()) {
            to.propertiesMap.put(entry.getKey(), entry.getValue().clone());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TablespaceProperties getProperties(Environment environment, ERDiagram diagram) {
        return DBManagerFactory.getDBManager(diagram).checkTablespaceProperties(propertiesMap.get(environment));
    }

    public void putProperties(Environment environment, TablespaceProperties tablespaceProperties) {
        propertiesMap.put(environment, tablespaceProperties);
    }

    public Map<Environment, TablespaceProperties> getPropertiesMap() {
        return propertiesMap;
    }

    @Override
    public Tablespace clone() {
        final Tablespace clone = (Tablespace) super.clone();
        copyTo(clone);

        return clone;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getObjectType() {
        return "tablespace";
    }
}

package org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public interface TablespaceProperties extends Serializable, Cloneable {

    TablespaceProperties clone();

    LinkedHashMap<String, String> getPropertiesMap();

    List<String> validate();
}

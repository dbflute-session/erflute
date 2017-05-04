package org.dbflute.erflute.db.impl.mysql.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class MySQLTablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = 1L;

    private String dataFile;

    private String logFileGroup;

    private String extentSize;

    private String initialSize;

    private String engine;

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getLogFileGroup() {
        return logFileGroup;
    }

    public void setLogFileGroup(String logFileGroup) {
        this.logFileGroup = logFileGroup;
    }

    public String getExtentSize() {
        return extentSize;
    }

    public void setExtentSize(String extentSize) {
        this.extentSize = extentSize;
    }

    public String getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(String initialSize) {
        this.initialSize = initialSize;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    @Override
    public TablespaceProperties clone() {
        final MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

        properties.dataFile = this.dataFile;
        properties.engine = this.engine;
        properties.extentSize = this.extentSize;
        properties.initialSize = this.initialSize;
        properties.logFileGroup = this.logFileGroup;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("label.tablespace.data.file", this.getDataFile());
        map.put("label.tablespace.log.file.group", this.getLogFileGroup());
        map.put("label.tablespace.extent.size", this.getExtentSize());
        map.put("label.tablespace.initial.size", this.getInitialSize());
        map.put("label.storage.engine", this.getEngine());

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<>();

        if (Check.isEmptyTrim(this.getDataFile())) {
            errorMessage.add("error.tablespace.data.file.empty");
        }
        if (Check.isEmptyTrim(this.getLogFileGroup())) {
            errorMessage.add("error.tablespace.log.file.group.empty");
        }
        if (Check.isEmptyTrim(this.getEngine())) {
            errorMessage.add("error.tablespace.storage.engine.empty");
        }

        return errorMessage;
    }
}

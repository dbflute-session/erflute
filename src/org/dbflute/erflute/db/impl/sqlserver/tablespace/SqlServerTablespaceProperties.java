package org.dbflute.erflute.db.impl.sqlserver.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SqlServerTablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = 1L;

    // (REGULAR/LARGI/SYSTEM TEMPORARY/USER TEMPORARY)
    private String type;
    private String pageSize;
    private String managedBy;
    private String container;
    private String extentSize;
    private String prefetchSize;
    private String bufferPoolName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }

    public String getExtentSize() {
        return extentSize;
    }

    public void setExtentSize(String extentSize) {
        this.extentSize = extentSize;
    }

    public String getPrefetchSize() {
        return prefetchSize;
    }

    public void setPrefetchSize(String prefetchSize) {
        this.prefetchSize = prefetchSize;
    }

    public String getBufferPoolName() {
        return bufferPoolName;
    }

    public void setBufferPoolName(String bufferPoolName) {
        this.bufferPoolName = bufferPoolName;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    @Override
    public TablespaceProperties clone() {
        final SqlServerTablespaceProperties properties = new SqlServerTablespaceProperties();

        properties.bufferPoolName = bufferPoolName;
        properties.container = container;
        // properties.containerDevicePath = this.containerDevicePath;
        // properties.containerDirectoryPath = this.containerDirectoryPath;
        // properties.containerFilePath = this.containerFilePath;
        // properties.containerPageNum = this.containerPageNum;
        properties.extentSize = extentSize;
        properties.managedBy = managedBy;
        properties.pageSize = pageSize;
        properties.prefetchSize = prefetchSize;
        properties.type = type;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("label.tablespace.type", getType());
        map.put("label.tablespace.page.size", getPageSize());
        map.put("label.tablespace.managed.by", getManagedBy());
        map.put("label.tablespace.container", getContainer());
        map.put("label.tablespace.extent.size", getExtentSize());
        map.put("label.tablespace.prefetch.size", getPrefetchSize());
        map.put("label.tablespace.buffer.pool.name", getBufferPoolName());

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<>();
        return errorMessage;
    }
}

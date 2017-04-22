package org.dbflute.erflute.db.impl.db2.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class DB2TablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = 3581869274788998047L;

    // (REGULAR/LARGI/SYSTEM TEMPORARY/USER TEMPORARY)
    private String type;

    private String pageSize;

    private String managedBy;

    private String container;

    // private String containerDirectoryPath;
    //
    // private String containerFilePath;
    //
    // private String containerPageNum;
    //
    // private String containerDevicePath;

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
        final DB2TablespaceProperties properties = new DB2TablespaceProperties();

        properties.bufferPoolName = this.bufferPoolName;
        properties.container = this.container;
        // properties.containerDevicePath = this.containerDevicePath;
        // properties.containerDirectoryPath = this.containerDirectoryPath;
        // properties.containerFilePath = this.containerFilePath;
        // properties.containerPageNum = this.containerPageNum;
        properties.extentSize = this.extentSize;
        properties.managedBy = this.managedBy;
        properties.pageSize = this.pageSize;
        properties.prefetchSize = this.prefetchSize;
        properties.type = this.type;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("label.tablespace.type", this.getType());
        map.put("label.tablespace.page.size", this.getPageSize());
        map.put("label.tablespace.managed.by", this.getManagedBy());
        map.put("label.tablespace.container", this.getContainer());
        map.put("label.tablespace.extent.size", this.getExtentSize());
        map.put("label.tablespace.prefetch.size", this.getPrefetchSize());
        map.put("label.tablespace.buffer.pool.name", this.getBufferPoolName());

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<>();
        return errorMessage;
    }
}

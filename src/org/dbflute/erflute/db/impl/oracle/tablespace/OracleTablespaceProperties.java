package org.dbflute.erflute.db.impl.oracle.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class OracleTablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = 1L;

    private String dataFile;

    private String fileSize;

    private boolean autoExtend;

    private String autoExtendSize;

    private String autoExtendMaxSize;

    private String minimumExtentSize;

    private String initial;

    private String next;

    private String minExtents;

    private String maxExtents;

    private String pctIncrease;

    private boolean logging;

    private boolean offline;

    private boolean temporary;

    private boolean autoSegmentSpaceManagement;

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isAutoExtend() {
        return autoExtend;
    }

    public void setAutoExtend(boolean autoExtend) {
        this.autoExtend = autoExtend;
    }

    public String getAutoExtendSize() {
        return autoExtendSize;
    }

    public void setAutoExtendSize(String autoExtendSize) {
        this.autoExtendSize = autoExtendSize;
    }

    public String getAutoExtendMaxSize() {
        return autoExtendMaxSize;
    }

    public void setAutoExtendMaxSize(String autoExtendMaxSize) {
        this.autoExtendMaxSize = autoExtendMaxSize;
    }

    public String getMinimumExtentSize() {
        return minimumExtentSize;
    }

    public void setMinimumExtentSize(String minimumExtentSize) {
        this.minimumExtentSize = minimumExtentSize;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public boolean isAutoSegmentSpaceManagement() {
        return autoSegmentSpaceManagement;
    }

    public void setAutoSegmentSpaceManagement(boolean autoSegmentSpaceManagement) {
        this.autoSegmentSpaceManagement = autoSegmentSpaceManagement;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getMinExtents() {
        return minExtents;
    }

    public void setMinExtents(String minExtents) {
        this.minExtents = minExtents;
    }

    public String getMaxExtents() {
        return maxExtents;
    }

    public void setMaxExtents(String maxExtents) {
        this.maxExtents = maxExtents;
    }

    public String getPctIncrease() {
        return pctIncrease;
    }

    public void setPctIncrease(String pctIncrease) {
        this.pctIncrease = pctIncrease;
    }

    @Override
    public TablespaceProperties clone() {
        final OracleTablespaceProperties properties = new OracleTablespaceProperties();

        properties.autoExtend = this.autoExtend;
        properties.autoExtendMaxSize = this.autoExtendMaxSize;
        properties.autoExtendSize = this.autoExtendSize;
        properties.autoSegmentSpaceManagement = this.autoSegmentSpaceManagement;
        properties.dataFile = this.dataFile;
        properties.fileSize = this.fileSize;
        properties.initial = this.initial;
        properties.logging = this.logging;
        properties.maxExtents = this.maxExtents;
        properties.minExtents = this.minExtents;
        properties.minimumExtentSize = this.minimumExtentSize;
        properties.next = this.next;
        properties.offline = this.offline;
        properties.pctIncrease = this.pctIncrease;
        properties.temporary = this.temporary;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("label.tablespace.data.file", this.getDataFile());
        map.put("label.size", this.getFileSize());
        map.put("label.tablespace.auto.extend", String.valueOf(this.isAutoExtend()));
        map.put("label.size", this.getAutoExtendSize());
        map.put("label.max.size", this.getAutoExtendMaxSize());
        map.put("label.tablespace.minimum.extent.size", this.getMinimumExtentSize());
        map.put("label.tablespace.initial", this.getInitial());
        map.put("label.tablespace.next", this.getNext());
        map.put("label.tablespace.min.extents", this.getMinExtents());
        map.put("label.tablespace.pct.increase", this.getPctIncrease());
        map.put("label.tablespace.logging", String.valueOf(this.isLogging()));
        map.put("label.tablespace.offline", String.valueOf(this.isOffline()));
        map.put("label.tablespace.temporary", String.valueOf(this.isTemporary()));
        map.put("label.tablespace.auto.segment.space.management", String.valueOf(this.isAutoSegmentSpaceManagement()));

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<>();

        if (this.isAutoExtend() && Check.isEmptyTrim(this.getAutoExtendSize())) {
            errorMessage.add("error.tablespace.auto.extend.size.empty");
        }

        return errorMessage;
    }
}

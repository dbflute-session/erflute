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

        properties.autoExtend = autoExtend;
        properties.autoExtendMaxSize = autoExtendMaxSize;
        properties.autoExtendSize = autoExtendSize;
        properties.autoSegmentSpaceManagement = autoSegmentSpaceManagement;
        properties.dataFile = dataFile;
        properties.fileSize = fileSize;
        properties.initial = initial;
        properties.logging = logging;
        properties.maxExtents = maxExtents;
        properties.minExtents = minExtents;
        properties.minimumExtentSize = minimumExtentSize;
        properties.next = next;
        properties.offline = offline;
        properties.pctIncrease = pctIncrease;
        properties.temporary = temporary;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("label.tablespace.data.file", getDataFile());
        map.put("label.size", getFileSize());
        map.put("label.tablespace.auto.extend", String.valueOf(isAutoExtend()));
        map.put("label.size", getAutoExtendSize());
        map.put("label.max.size", getAutoExtendMaxSize());
        map.put("label.tablespace.minimum.extent.size", getMinimumExtentSize());
        map.put("label.tablespace.initial", getInitial());
        map.put("label.tablespace.next", getNext());
        map.put("label.tablespace.min.extents", getMinExtents());
        map.put("label.tablespace.pct.increase", getPctIncrease());
        map.put("label.tablespace.logging", String.valueOf(isLogging()));
        map.put("label.tablespace.offline", String.valueOf(isOffline()));
        map.put("label.tablespace.temporary", String.valueOf(isTemporary()));
        map.put("label.tablespace.auto.segment.space.management", String.valueOf(isAutoSegmentSpaceManagement()));

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<>();

        if (isAutoExtend() && Check.isEmptyTrim(getAutoExtendSize())) {
            errorMessage.add("error.tablespace.auto.extend.size.empty");
        }

        return errorMessage;
    }
}

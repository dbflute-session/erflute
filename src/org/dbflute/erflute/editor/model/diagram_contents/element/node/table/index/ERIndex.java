package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class ERIndex extends AbstractModel implements ObjectModel, Comparable<ERIndex> {

    private static final long serialVersionUID = 1L;

    private String name;

    private boolean nonUnique;

    private boolean fullText;

    private String type;

    private String description;

    private List<Boolean> descs;

    private List<NormalColumn> columns;

    private List<String> columnNames;

    private ERTable table;

    public ERIndex(ERTable table, String name, boolean nonUnique, String type, String description) {
        this.table = table;

        this.nonUnique = nonUnique;
        this.type = type;
        this.description = description;

        this.descs = new ArrayList<>();

        this.columns = new ArrayList<>();
        this.columnNames = new ArrayList<>();

        this.name = name;
    }

    public void setDescs(List<Boolean> descs) {
        this.descs = descs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public void setColumns(List<NormalColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(NormalColumn column) {
        this.columns.add(column);
    }

    public void addColumn(NormalColumn column, Boolean desc) {
        this.columns.add(column);
        this.descs.add(desc);
    }

    public List<NormalColumn> getColumns() {
        return this.columns;
    }

    public boolean isNonUnique() {
        return nonUnique;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void addColumnName(String columnName, Boolean desc) {
        this.columnNames.add(columnName);
        this.descs.add(desc);
    }

    public List<Boolean> getDescs() {
        return descs;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isFullText() {
        return fullText;
    }

    public void setFullText(boolean fullText) {
        this.fullText = fullText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public ERIndex clone() {
        final ERIndex clone = (ERIndex) super.clone();

        final List<Boolean> cloneDescs = new ArrayList<>();
        for (final Boolean desc : this.descs) {
            cloneDescs.add(desc);
        }

        clone.descs = cloneDescs;

        final List<String> cloneColumnNames = new ArrayList<>();
        for (final String columnName : this.columnNames) {
            cloneColumnNames.add(columnName);
        }

        clone.columnNames = cloneColumnNames;

        return clone;
    }

    @Override
    public int compareTo(ERIndex other) {
        return this.name.toUpperCase().compareTo(other.name.toUpperCase());
    }

    public ERTable getTable() {
        return table;
    }

    protected void setTable(ERTable table) {
        this.table = table;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getObjectType() {
        return "index";
    }
}

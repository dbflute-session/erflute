package org.dbflute.erflute.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.editor.model.AbstractModel;

public class ColumnGroupSet extends AbstractModel implements Iterable<ColumnGroup> {

    private static final long serialVersionUID = 6192280105150073360L;
    public static final String PROPERTY_CHANGE_GROUP_SET = "column_group_set";

    private String database;
    private final List<ColumnGroup> columnGroupList;

    public ColumnGroupSet() {
        this.columnGroupList = new ArrayList<ColumnGroup>();
    }

    public void add(ColumnGroup group) {
        this.columnGroupList.add(group);
        Collections.sort(this.columnGroupList);
        firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);
    }

    public void remove(ColumnGroup group) {
        this.columnGroupList.remove(group);
        firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);
    }

    @Override
    public Iterator<ColumnGroup> iterator() {
        return this.columnGroupList.iterator();
    }

    public List<ColumnGroup> getGroupList() {
        return this.columnGroupList;
    }

    public void clear() {
        this.columnGroupList.clear();
    }

    public boolean contains(ColumnGroup group) {
        return this.columnGroupList.contains(group);
    }

    public ColumnGroup get(int index) {
        return this.columnGroupList.get(index);
    }

    public int indexOf(ColumnGroup group) {
        return this.columnGroupList.indexOf(group);
    }

    /**
     * database ���擾���܂�.
     * @return database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * database ��ݒ肵�܂�.
     * @param database
     *            database
     */
    public void setDatabase(String database) {
        this.database = database;
    }
}

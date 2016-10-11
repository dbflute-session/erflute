package org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

public class WalkerGroupSet extends AbstractModel implements ObjectListModel, Iterable<WalkerGroup> {

    private static final long serialVersionUID = 5264397678674390103L;

    public static final String PROPERTY_CHANGE_WALKER_GROUP_SET = "GroupSet";

    private List<WalkerGroup> groupList;

    public WalkerGroupSet() {
        this.groupList = new ArrayList<WalkerGroup>();
    }

    public void add(WalkerGroup table) {
        this.groupList.add(table);
        this.firePropertyChange(PROPERTY_CHANGE_WALKER_GROUP_SET, null, null);
    }

    public int remove(WalkerGroup table) {
        int index = this.groupList.indexOf(table);
        this.groupList.remove(index);
        this.firePropertyChange(PROPERTY_CHANGE_WALKER_GROUP_SET, null, null);

        return index;
    }

    public void setDirty() {
        this.firePropertyChange(PROPERTY_CHANGE_WALKER_GROUP_SET, null, null);
    }

    public List<WalkerGroup> getList() {
        Collections.sort(this.groupList);

        return this.groupList;
    }

    public Iterator<WalkerGroup> iterator() {
        Collections.sort(this.groupList);

        return this.groupList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WalkerGroupSet clone() {
        WalkerGroupSet groupSet = (WalkerGroupSet) super.clone();
        List<WalkerGroup> newTableList = new ArrayList<WalkerGroup>();

        for (WalkerGroup table : this.groupList) {
            WalkerGroup newTable = (WalkerGroup) table.clone();
            newTableList.add(newTable);
        }

        groupSet.groupList = newTableList;

        return groupSet;
    }

    //	public List<String> getAutoSequenceNames(String database) {
    //		List<String> autoSequenceNames = new ArrayList<String>();
    //
    //		for (VGroup group : this.groupList) {
    //			String prefix = group.getNameWithSchema(database) + "_";
    //
    //			for (NormalColumn column : group.getNormalColumns()) {
    //				SqlType sqlType = column.getType();
    //
    //				if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(
    //						sqlType)
    //						|| SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL)
    //								.equals(sqlType)) {
    //					autoSequenceNames
    //							.add((prefix + column.getPhysicalName() + "_seq")
    //									.toUpperCase());
    //				}
    //			}
    //		}
    //
    //		return autoSequenceNames;
    //	}

    public String getDescription() {
        return "";
    }

    public String getName() {
        return DisplayMessages.getMessage("label.object.type.table_list");
    }

    public String getObjectType() {
        return "list";
    }

}

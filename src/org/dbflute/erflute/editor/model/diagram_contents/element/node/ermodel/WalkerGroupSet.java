package org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WalkerGroupSet extends AbstractModel implements ObjectListModel, Iterable<WalkerGroup> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_WALKER_GROUP_SET = "GroupSet";

    private List<WalkerGroup> groupList;

    public WalkerGroupSet() {
        this.groupList = new ArrayList<>();
    }

    public void add(WalkerGroup table) {
        groupList.add(table);
        firePropertyChange(PROPERTY_CHANGE_WALKER_GROUP_SET, null, null);
    }

    public int remove(WalkerGroup table) {
        final int index = groupList.indexOf(table);
        groupList.remove(index);
        firePropertyChange(PROPERTY_CHANGE_WALKER_GROUP_SET, null, null);

        return index;
    }

    public void setDirty() {
        firePropertyChange(PROPERTY_CHANGE_WALKER_GROUP_SET, null, null);
    }

    public List<WalkerGroup> getList() {
        Collections.sort(groupList);
        return groupList;
    }

    @Override
    public Iterator<WalkerGroup> iterator() {
        Collections.sort(groupList);
        return groupList.iterator();
    }

    public void overrideAll(List<WalkerGroup> newList) {
        groupList.clear();
        groupList.addAll(newList);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public WalkerGroupSet clone() {
        final WalkerGroupSet groupSet = (WalkerGroupSet) super.clone();
        final List<WalkerGroup> newTableList = new ArrayList<>();
        for (final WalkerGroup table : groupList) {
            final WalkerGroup newTable = table.clone();
            newTableList.add(newTable);
        }
        groupSet.groupList = newTableList;
        return groupSet;
    }

    // ===================================================================================
    //                                                                        Object Model
    //                                                                        ============
    @Override
    public String getObjectType() {
        return "list";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.table_list");
    }

    @Override
    public String getDescription() {
        return "";
    }
}

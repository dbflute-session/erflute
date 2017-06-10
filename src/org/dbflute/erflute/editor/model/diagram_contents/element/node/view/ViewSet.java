package org.dbflute.erflute.editor.model.diagram_contents.element.node.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ObjectListModel;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ViewSet extends AbstractModel implements ObjectListModel, Iterable<ERView> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_VIEW_SET = "ViewSet";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private List<ERView> viewList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ViewSet() {
        this.viewList = new ArrayList<ERView>();
    }

    // ===================================================================================
    //                                                                       List Handling
    //                                                                       =============
    public void add(ERView view) {
        this.viewList.add(view);
        this.firePropertyChange(PROPERTY_CHANGE_VIEW_SET, null, null);
    }

    public void add(int index, ERView view) {
        this.viewList.add(index, view);
        this.firePropertyChange(PROPERTY_CHANGE_VIEW_SET, null, null);
    }

    public int remove(ERView view) {
        final int index = this.viewList.indexOf(view);
        this.viewList.remove(index);
        this.firePropertyChange(PROPERTY_CHANGE_VIEW_SET, null, null);
        return index;
    }

    public List<ERView> getList() {
        Collections.sort(viewList);
        return viewList;
    }

    @Override
    public Iterator<ERView> iterator() {
        Collections.sort(viewList);
        return viewList.iterator();
    }

    public List<ERView> getCreateDDLSortedList() {
        final List<ERView> sortedList = new ArrayList<ERView>();
        gatherSortedViewList(sortedList, viewList);
        return sortedList;
    }

    public List<ERView> getDropDDLSortedList() {
        final List<ERView> sortedList = new ArrayList<ERView>();
        gatherSortedViewList(sortedList, viewList);
        return sortedList;
    }

    private void gatherSortedViewList(List<ERView> sortedViewList, List<ERView> targetViewList) {
        if (targetViewList.isEmpty()) {
            return;
        }
        final List<ERView> remainingViewList = new ArrayList<ERView>();
        for (final ERView view : targetViewList) {
            if (isDependingOnOther(targetViewList, view)) {
                remainingViewList.add(view);
            } else { // independent in this section
                sortedViewList.add(view);
            }
        }
        gatherSortedViewList(sortedViewList, remainingViewList);
    }

    private boolean isDependingOnOther(List<ERView> targetViewList, ERView view) {
        final String currentSql = view.getSql();
        if (Srl.is_Null_or_TrimmedEmpty(currentSql)) {
            return true;
        }
        boolean depending = false;
        for (final ERView otherView : targetViewList) {
            if (view == otherView) { // self
                continue;
            }
            final String otherName = otherView.getPhysicalName();
            if (Srl.is_Null_or_TrimmedEmpty(otherName)) { // just in case
                continue;
            }
            if (Srl.containsIgnoreCase(currentSql, otherName)) { // not perfect
                depending = true;
                break;
            }
        }
        return depending;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public ViewSet clone() {
        final ViewSet viewSet = (ViewSet) super.clone();
        final List<ERView> newViewList = new ArrayList<ERView>();
        for (final ERView view : viewList) {
            final ERView newView = view.clone();
            newViewList.add(newView);
        }
        viewSet.viewList = newViewList;
        return viewSet;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public String getObjectType() {
        return "list";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.view_list");
    }

    @Override
    public String getDescription() {
        return "";
    }
}

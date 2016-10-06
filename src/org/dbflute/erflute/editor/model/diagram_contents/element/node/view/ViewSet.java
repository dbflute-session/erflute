package org.dbflute.erflute.editor.model.diagram_contents.element.node.view;

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
public class ViewSet extends AbstractModel implements ObjectListModel, Iterable<ERView> {

    private static final long serialVersionUID = -120487815554383179L;

    public static final String PROPERTY_CHANGE_VIEW_SET = "ViewSet";

    private List<ERView> viewList;

    public ViewSet() {
        this.viewList = new ArrayList<ERView>();
    }

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
        Collections.sort(this.viewList);
        return this.viewList;
    }

    @Override
    public Iterator<ERView> iterator() {
        Collections.sort(this.viewList);
        return this.viewList.iterator();
    }

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

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return DisplayMessages.getMessage("label.object.type.view_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}

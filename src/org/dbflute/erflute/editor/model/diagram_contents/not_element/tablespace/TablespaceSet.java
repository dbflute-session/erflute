package org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.editor.model.AbstractModel;

public class TablespaceSet extends AbstractModel implements Iterable<Tablespace> {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_TABLESPACE_SET = "TablespaceSet";

    private List<Tablespace> tablespaceList;

    public TablespaceSet() {
        this.tablespaceList = new ArrayList<>();
    }

    public void addTablespace(Tablespace tablespace) {
        tablespaceList.add(tablespace);
        Collections.sort(tablespaceList);

        firePropertyChange(PROPERTY_CHANGE_TABLESPACE_SET, null, null);
    }

    public int remove(Tablespace tablespace) {
        final int index = tablespaceList.indexOf(tablespace);
        tablespaceList.remove(index);
        firePropertyChange(PROPERTY_CHANGE_TABLESPACE_SET, null, null);

        return index;
    }

    public boolean contains(String name) {
        for (final Tablespace tablespace : tablespaceList) {
            if (name.equalsIgnoreCase(tablespace.getName())) {
                return true;
            }
        }

        return false;
    }

    public List<Tablespace> getTablespaceList() {
        return tablespaceList;
    }

    @Override
    public Iterator<Tablespace> iterator() {
        return tablespaceList.iterator();
    }

    @Override
    public TablespaceSet clone() {
        final TablespaceSet tablespaceSet = (TablespaceSet) super.clone();
        final List<Tablespace> newTablespaceList = new ArrayList<>();

        for (final Tablespace tablespace : tablespaceList) {
            final Tablespace newTablespace = (Tablespace) tablespace.clone();
            newTablespaceList.add(newTablespace);
        }

        tablespaceSet.tablespaceList = newTablespaceList;

        return tablespaceSet;
    }
}

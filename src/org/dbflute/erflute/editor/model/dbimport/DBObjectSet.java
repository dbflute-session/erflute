package org.dbflute.erflute.editor.model.dbimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dbflute.erflute.core.util.Format;

public class DBObjectSet implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Map<String, List<DBObject>> schemaDbObjectListMap;
    private final List<DBObject> tablespaceList;
    private final List<DBObject> noteList;
    private final List<DBObject> groupList;

    public DBObjectSet() {
        this.schemaDbObjectListMap = new TreeMap<>();
        this.tablespaceList = new ArrayList<>();
        this.noteList = new ArrayList<>();
        this.groupList = new ArrayList<>();
    }

    public Map<String, List<DBObject>> getSchemaDbObjectListMap() {
        return schemaDbObjectListMap;
    }

    public List<DBObject> getTablespaceList() {
        return tablespaceList;
    }

    public List<DBObject> getNoteList() {
        return noteList;
    }

    public List<DBObject> getGroupList() {
        return groupList;
    }

    public void addAll(List<DBObject> dbObjectList) {
        for (final DBObject dbObject : dbObjectList) {
            add(dbObject);
        }
    }

    public void add(DBObject dbObject) {
        if (DBObject.TYPE_TABLESPACE.equals(dbObject.getType())) {
            tablespaceList.add(dbObject);
        } else if (DBObject.TYPE_NOTE.equals(dbObject.getType())) {
            noteList.add(dbObject);
        } else if (DBObject.TYPE_GROUP.equals(dbObject.getType())) {
            groupList.add(dbObject);
        } else {
            final String schema = Format.null2blank(dbObject.getSchema());
            List<DBObject> dbObjectList = schemaDbObjectListMap.get(schema);
            if (dbObjectList == null) {
                dbObjectList = new ArrayList<>();
                schemaDbObjectListMap.put(schema, dbObjectList);
            }

            dbObjectList.add(dbObject);
        }
    }
}

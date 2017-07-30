package org.dbflute.erflute.editor.model.settings;

import java.io.Serializable;

import org.dbflute.erflute.core.util.Format;

public class JDBCDriverSetting implements Serializable, Comparable<JDBCDriverSetting> {

    private static final long serialVersionUID = 1L;

    private final String db;
    private final String className;
    private final String path;

    public JDBCDriverSetting(String db, String className, String path) {
        this.db = db;
        this.className = className;
        this.path = path;
    }

    public String getDb() {
        return db;
    }

    public String getClassName() {
        return className;
    }

    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((db == null) ? 0 : db.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final JDBCDriverSetting other = (JDBCDriverSetting) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (db == null) {
            if (other.db != null)
                return false;
        } else if (!db.equals(other.db))
            return false;
        return true;
    }

    @Override
    public int compareTo(JDBCDriverSetting another) {
        int compareTo = 0;

        String s1 = Format.null2blank(db);
        String s2 = Format.null2blank(another.db);

        compareTo = s1.compareTo(s2);
        if (compareTo != 0) {
            return compareTo;
        }

        s1 = Format.null2blank(className);
        s2 = Format.null2blank(another.className);

        compareTo = s1.compareTo(s2);
        if (compareTo != 0) {
            return compareTo;
        }

        s1 = Format.null2blank(path);
        s2 = Format.null2blank(another.path);

        compareTo = s1.compareTo(s2);
        if (compareTo != 0) {
            return compareTo;
        }

        return 0;
    }
}

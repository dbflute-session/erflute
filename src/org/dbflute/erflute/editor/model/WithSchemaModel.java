package org.dbflute.erflute.editor.model;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;

public abstract class WithSchemaModel extends AbstractModel implements Comparable<WithSchemaModel> {

    private static final long serialVersionUID = 1L;

    private String schema;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getNameWithSchema(String database) {
        if (schema == null) {
            return Format.null2blank(name);
        }

        final DBManager dbManager = DBManagerFactory.getDBManager(database);

        if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
            return Format.null2blank(name);
        }

        return schema + "." + Format.null2blank(name);
    }

    @Override
    public int compareTo(WithSchemaModel other) {
        int compareTo = 0;

        compareTo = Format.null2blank(schema).toUpperCase().compareTo(Format.null2blank(other.schema).toUpperCase());
        if (compareTo != 0) {
            return compareTo;
        }

        compareTo = Format.null2blank(name).toUpperCase().compareTo(Format.null2blank(other.name).toUpperCase());
        if (compareTo != 0) {
            return compareTo;
        }

        return compareTo;
    }
}

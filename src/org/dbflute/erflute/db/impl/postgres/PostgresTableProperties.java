package org.dbflute.erflute.db.impl.postgres;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class PostgresTableProperties extends TableProperties {

    private static final long serialVersionUID = 1L;

    private boolean withoutOIDs;

    public PostgresTableProperties() {
        this.withoutOIDs = true;
    }

    public boolean isWithoutOIDs() {
        return withoutOIDs;
    }

    public void setWithoutOIDs(boolean withoutOIDs) {
        this.withoutOIDs = withoutOIDs;
    }
}

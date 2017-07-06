package org.dbflute.erflute.editor.model.dbimport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DBSettings;

public interface ImportFromDBManager {

    void init(Connection con, DBSettings dbSetting, ERDiagram diagram, List<DBObject> dbObjectList,
            boolean useCommentAsLogicalNameButton, boolean mergeWord) throws SQLException;
}

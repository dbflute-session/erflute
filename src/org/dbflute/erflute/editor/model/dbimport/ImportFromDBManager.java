package org.dbflute.erflute.editor.model.dbimport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DBSetting;

public interface ImportFromDBManager {

    public void init(Connection con, DBSetting dbSetting, ERDiagram diagram, List<DBObject> dbObjectList,
            boolean useCommentAsLogicalNameButton, boolean mergeWord) throws SQLException;

}

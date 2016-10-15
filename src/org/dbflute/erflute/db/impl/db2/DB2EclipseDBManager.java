package org.dbflute.erflute.db.impl.db2;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.db.impl.db2.tablespace.DB2TablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedComposite;
import org.eclipse.swt.widgets.Composite;

public class DB2EclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return DB2DBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new DB2AdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return new DB2TablespaceDialog();
    }

}

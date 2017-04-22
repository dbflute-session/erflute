package org.dbflute.erflute.db.impl.sqlite;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedComposite;
import org.eclipse.swt.widgets.Composite;

public class SQLiteEclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return SQLiteDBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new SQLiteAdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return null;
    }
}

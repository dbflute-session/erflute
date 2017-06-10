package org.dbflute.erflute.db.impl.sqlserver;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.db.impl.sqlserver.tablespace.SqlServerTablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedComposite;
import org.eclipse.swt.widgets.Composite;

public class SqlServerEclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return SqlServerDBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new SqlServerAdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return new SqlServerTablespaceDialog();
    }
}

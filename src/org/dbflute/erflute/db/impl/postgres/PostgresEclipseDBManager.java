package org.dbflute.erflute.db.impl.postgres;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.db.impl.postgres.tablespace.PostgresTablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;

public class PostgresEclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return PostgresDBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new PostgresAdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return new PostgresTablespaceDialog();
    }

}

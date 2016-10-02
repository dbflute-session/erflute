package org.dbflute.erflute.db.impl.standard_sql;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;

public class StandardSQLEclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return StandardSQLDBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new StandardSQLAdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return null;
    }

}

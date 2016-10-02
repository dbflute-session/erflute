package org.dbflute.erflute.db.impl.mysql;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.db.impl.mysql.tablespace.MySQLTablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class MySQLEclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return MySQLDBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new MySQLAdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return new MySQLTablespaceDialog();
    }
}

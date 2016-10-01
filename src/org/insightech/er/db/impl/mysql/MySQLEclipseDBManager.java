package org.insightech.er.db.impl.mysql;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

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

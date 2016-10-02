package org.dbflute.erflute.db.impl.oracle;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.db.impl.oracle.tablespace.OracleTablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;

public class OracleEclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return OracleDBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new OracleAdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return new OracleTablespaceDialog();
    }

}

package org.dbflute.erflute.db.impl.hsqldb;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedComposite;
import org.eclipse.swt.widgets.Composite;

public class HSQLDBEclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return HSQLDBDBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new HSQLDBAdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return null;
    }

}

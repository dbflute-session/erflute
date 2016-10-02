package org.dbflute.erflute.db.impl.access;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;

public class AccessEclipseDBManager extends EclipseDBManagerBase {

    public String getId() {
        return AccessDBManager.ID;
    }

    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new AccessAdvancedComposite(composite);
    }

    public TablespaceDialog createTablespaceDialog() {
        return null;
    }

}

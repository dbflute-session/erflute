package org.dbflute.erflute.db.impl.h2;

import org.dbflute.erflute.db.EclipseDBManagerBase;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedComposite;
import org.eclipse.swt.widgets.Composite;

public class H2EclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return H2DBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(Composite composite) {
        return new H2AdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return null;
    }

}

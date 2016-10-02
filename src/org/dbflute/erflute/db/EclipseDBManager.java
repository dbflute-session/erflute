package org.dbflute.erflute.db;

import org.dbflute.erflute.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;

/**
 * @author modified by jflute (originated in ermaster)
 */
public interface EclipseDBManager {

    String getId();

    AdvancedComposite createAdvancedComposite(Composite composite);

    TablespaceDialog createTablespaceDialog();
}

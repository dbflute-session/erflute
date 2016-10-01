package org.insightech.er.db;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

/**
 * @author modified by jflute (originated in ermaster)
 */
public interface EclipseDBManager {

    String getId();

    AdvancedComposite createAdvancedComposite(Composite composite);

    TablespaceDialog createTablespaceDialog();
}

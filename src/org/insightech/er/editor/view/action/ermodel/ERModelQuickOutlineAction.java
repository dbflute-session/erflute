package org.insightech.er.editor.view.action.ermodel;

import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.editor.MainModelEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.information.ERDiagramInformationControl;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERModelQuickOutlineAction extends AbstractBaseAction {

    public static final String ID = ERModelQuickOutlineAction.class.getName();

    public ERModelQuickOutlineAction(MainModelEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.ermodel.outline"), editor);
        setActionDefinitionId("org.dbflute.erflute.quickOutline"); // synchronized with plugin.xml
        setAccelerator(SWT.CTRL | 'O');
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = this.getDiagram();
        final ERDiagramInformationControl quickOutline =
                new ERDiagramInformationControl(diagram, getEditorPart().getSite().getShell(), getEditorPart().getGraphicalViewer()
                        .getControl());
        quickOutline.setVisible(true);
    }
}

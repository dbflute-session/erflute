package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.information.ERDiagramInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERModelQuickOutlineAction extends AbstractBaseAction {

    public static final String ID = ERModelQuickOutlineAction.class.getName();

    public ERModelQuickOutlineAction(MainDiagramEditor editor) {
        super(ID, "Quick Outline", editor);
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

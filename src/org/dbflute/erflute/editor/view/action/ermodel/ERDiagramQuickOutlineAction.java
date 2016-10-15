package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.information.ERDiagramInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramQuickOutlineAction extends AbstractBaseAction {

    public static final String ID = ERDiagramQuickOutlineAction.class.getName();

    public ERDiagramQuickOutlineAction(MainDiagramEditor editor) {
        super(ID, "Quick Outline", editor);
        setActionDefinitionId("org.dbflute.erflute.quickOutline"); // synchronized with plugin.xml
        setAccelerator(SWT.CTRL | 'O');
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = getDiagram();
        Activator.debug(this, "execute()", "...Executing quick outline: diagram=" + diagram);
        final Shell shell = getEditorPart().getSite().getShell();
        final Control control = getEditorPart().getGraphicalViewer().getControl();
        final ERDiagramInformationControl quickOutline = new ERDiagramInformationControl(diagram, shell, control);
        quickOutline.setVisible(true);
    }
}

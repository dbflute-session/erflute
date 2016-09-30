package org.insightech.er.editor.view.action.ermodel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.DisplayMessages;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.information.ERDiagramInformationControl;

public class ERModelQuickOutlineAction extends AbstractBaseAction {

    public static final String ID = ERModelQuickOutlineAction.class.getName();

    public ERModelQuickOutlineAction(ERDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.ermodel.outline"), editor);
        this.setActionDefinitionId("org.insightech.er.quickOutline");
        setAccelerator(SWT.CTRL | 'O');

    }

    @Override
    public void execute(Event event) throws Exception {
        ERDiagram diagram = this.getDiagram();

        ERDiagramInformationControl quickOutline =
                new ERDiagramInformationControl(diagram, getEditorPart().getSite().getShell(), getEditorPart().getGraphicalViewer()
                        .getControl());

        quickOutline.setVisible(true);

    }

}

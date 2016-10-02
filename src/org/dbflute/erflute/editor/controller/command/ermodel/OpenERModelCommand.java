package org.dbflute.erflute.editor.controller.command.ermodel;

import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.dbflute.erflute.editor.SubModelEditor;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;

public class OpenERModelCommand extends AbstractCommand {

    private ERModel model;
    private ERDiagram diagram;
    //	private EditPart editPart;
    private ERTable table;

    public OpenERModelCommand(ERDiagram diagram, ERModel model) {
        this.diagram = diagram;
        this.model = model;
    }

    @Override
    protected void doExecute() {
        ERFluteMultiPageEditor editor = diagram.getEditor();

        editor.setCurrentErmodel(model);

        ((SubModelEditor) editor.getActiveEditor()).reveal(table);
        //		IEditorInput editorInput = editor2.getEditorInput();
        //
        //
        //		if (editPart != null) {
        //			editor2.getGraphicalViewer().reveal(editPart);
        //		}

        //		editor.setActiveEditor(editorPart);
        //
        //		EROneDiagramEditor diagramEditor = new EROneDiagramEditor(
        //				this.diagram, model, editor.getEditPartFactory(),
        //				editor.getZoomComboContributionItem(), editor.getOutlinePage());
        //
        //		try {
        //			editor.addPage(diagramEditor, editor.getEditorInput(), model.getName());
        //			editor.setActiveEditor(diagramEditor);
        //
        //		} catch (PartInitException e) {
        //			Activator.showExceptionDialog(e);
        //		}
    }

    @Override
    protected void doUndo() {
        // not support
    }

    /**
     * table��ݒ肵�܂��B
     * @param table table
     */
    public void setTable(ERTable table) {
        this.table = table;
    }

}

package org.dbflute.erflute.editor.controller.command.ermodel;

import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;

public class OpenERModelCommand extends AbstractCommand {

    private final ERVirtualDiagram model;
    private final ERDiagram diagram;
    //	private EditPart editPart;
    private ERTable table;

    public OpenERModelCommand(ERDiagram diagram, ERVirtualDiagram model) {
        this.diagram = diagram;
        this.model = model;
    }

    @Override
    protected void doExecute() {
        final ERFluteMultiPageEditor editor = diagram.getEditor();

        editor.setCurrentERModel(model);

        ((VirtualDiagramEditor) editor.getActiveEditor()).reveal(table);
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

    public void setTable(ERTable table) {
        this.table = table;
    }
}

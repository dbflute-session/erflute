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
    }

    @Override
    protected void doUndo() {
        // not support
    }

    public void setTable(ERTable table) {
        this.table = table;
    }
}

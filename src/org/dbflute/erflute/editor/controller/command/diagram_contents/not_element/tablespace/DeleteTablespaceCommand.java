package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.tablespace;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public class DeleteTablespaceCommand extends AbstractCommand {

    private final TablespaceSet tablespaceSet;
    private final Tablespace tablespace;

    public DeleteTablespaceCommand(ERDiagram diagram, Tablespace tablespace) {
        this.tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
        this.tablespace = tablespace;
    }

    @Override
    protected void doExecute() {
        tablespaceSet.remove(tablespace);
    }

    @Override
    protected void doUndo() {
        tablespaceSet.addTablespace(tablespace);
    }
}

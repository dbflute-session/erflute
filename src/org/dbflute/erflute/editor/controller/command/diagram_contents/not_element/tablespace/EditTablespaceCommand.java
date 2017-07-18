package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.tablespace;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public class EditTablespaceCommand extends AbstractCommand {

    private final TablespaceSet tablespaceSet;
    private final Tablespace tablespace;
    private final Tablespace oldTablespace;
    private final Tablespace newTablespace;

    public EditTablespaceCommand(ERDiagram diagram, Tablespace tablespace, Tablespace newTablespace) {
        this.tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
        this.tablespace = tablespace;
        this.oldTablespace = (Tablespace) tablespace.clone();
        this.newTablespace = newTablespace;
    }

    @Override
    protected void doExecute() {
        newTablespace.copyTo(tablespace);
        tablespaceSet.addTablespace(tablespace);
    }

    @Override
    protected void doUndo() {
        oldTablespace.copyTo(tablespace);
        tablespaceSet.addTablespace(tablespace);
    }
}

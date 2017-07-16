package org.dbflute.erflute.editor.view.action.dbimport;

import java.util.List;

import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.dbimport.ImportTableCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;

public abstract class AbstractImportAction extends AbstractBaseAction {

    protected List<DiagramWalker> importedNodeElements;
    protected List<Sequence> importedSequences;
    protected List<Trigger> importedTriggers;
    protected List<Tablespace> importedTablespaces;
    protected List<ColumnGroup> importedColumnGroups;

    public AbstractImportAction(String id, String text, MainDiagramEditor editor) {
        super(id, text, editor);
    }

    protected void showData() {
        final ERDiagram diagram = getDiagram();

        if (importedNodeElements != null) {
            final ImportTableCommand command = new ImportTableCommand(diagram, importedNodeElements,
                    importedSequences, importedTriggers, importedTablespaces, importedColumnGroups);
            execute(command);
        }
    }
}

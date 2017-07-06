package org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;

public class AddWordCommand extends AbstractCommand {

    private final TableView tableView;
    private final Dictionary dictionary;
    private final Word word;
    private final NormalColumn column;
    private final int index;

    public AddWordCommand(TableView tableView, Word word, int index) {
        this.tableView = tableView;
        this.word = word;
        this.index = index;
        this.dictionary = tableView.getDiagram().getDiagramContents().getDictionary();
        this.column = new NormalColumn(word, true, false, false, false, null, null, null, null, null);
    }

    @Override
    protected void doExecute() {
        tableView.addColumn(index, column);
        dictionary.add(column);
    }

    @Override
    protected void doUndo() {
        tableView.removeColumn(column);
        dictionary.remove(column);
    }
}

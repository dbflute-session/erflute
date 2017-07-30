package org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.dictionary;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;

public class EditWordCommand extends AbstractCommand {

    private final Word oldWord;
    private final Word word;
    private final Word newWord;
    private final ERDiagram diagram;
    private final Dictionary dictionary;

    public EditWordCommand(Word word, Word newWord, ERDiagram diagram) {
        this.oldWord = new Word(word.getPhysicalName(), word.getLogicalName(), word.getType(),
                word.getTypeData().clone(), word.getDescription(), diagram.getDatabase());
        this.diagram = diagram;
        this.word = word;
        this.newWord = newWord;
        this.dictionary = diagram.getDiagramContents().getDictionary();
    }

    @Override
    protected void doExecute() {
        dictionary.copyTo(newWord, word);
        diagram.changeAll();
    }

    @Override
    protected void doUndo() {
        dictionary.copyTo(oldWord, word);
        diagram.changeAll();
    }
}

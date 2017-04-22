package org.dbflute.erflute.editor.controller.command.common;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.search.ReplaceManager;
import org.dbflute.erflute.editor.model.search.ReplaceResult;

public class ReplaceCommand extends AbstractCommand {

    private final int type;

    private final Object object;

    private final String keyword;

    private final String replaceWord;

    private ReplaceResult result;

    private final ERDiagram diagram;

    public ReplaceCommand(ERDiagram diagram, int type, Object object, String keyword, String replaceWord) {
        this.diagram = diagram;

        this.type = type;
        this.object = object;
        this.keyword = keyword;
        this.replaceWord = replaceWord;
    }

    @Override
    protected void doExecute() {
        this.result = ReplaceManager.replace(this.type, this.object, this.keyword, this.replaceWord, this.diagram.getDatabase());

        this.diagram.change();
    }

    @Override
    protected void doUndo() {
        if (this.result != null) {
            ReplaceManager.undo(this.type, this.object, this.result.getOriginal());

            this.diagram.change();
        }
    }
}

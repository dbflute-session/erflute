package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;

public class ReconnectSourceCommand extends AbstractCommand {

    private final Relationship relation;
    private final int xp;
    private final int yp;
    private int oldXp;
    private int oldYp;

    public ReconnectSourceCommand(Relationship relation, int xp, int yp) {
        this.relation = relation;
        this.xp = xp;
        this.yp = yp;
    }

    @Override
    protected void doExecute() {
        this.oldXp = relation.getSourceXp();
        this.oldYp = relation.getSourceYp();
        relation.setSourceLocationp(xp, yp);
        relation.setParentMove();
    }

    @Override
    protected void doUndo() {
        relation.setSourceLocationp(oldXp, oldYp);
        relation.setParentMove();
    }
}

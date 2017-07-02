package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;

public class ReconnectTargetCommand extends AbstractCommand {

    private final Relationship relation;
    private final int xp;
    private final int yp;
    private int oldXp;
    private int oldYp;

    public ReconnectTargetCommand(Relationship relation, int xp, int yp) {
        this.relation = relation;
        this.xp = xp;
        this.yp = yp;
    }

    @Override
    protected void doExecute() {
        this.oldXp = relation.getTargetXp();
        this.oldYp = relation.getTargetYp();
        relation.setTargetLocationp(xp, yp);
        relation.setParentMove();
    }

    @Override
    protected void doUndo() {
        relation.setTargetLocationp(oldXp, oldYp);
        relation.setParentMove();
    }
}

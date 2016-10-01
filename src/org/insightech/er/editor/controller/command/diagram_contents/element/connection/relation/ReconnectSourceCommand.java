package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relationship;

public class ReconnectSourceCommand extends AbstractCommand {

    private Relationship relation;

    int xp;

    int yp;

    int oldXp;

    int oldYp;

    public ReconnectSourceCommand(Relationship relation, int xp, int yp) {
        this.relation = relation;

        this.xp = xp;
        this.yp = yp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        this.oldXp = relation.getSourceXp();
        this.oldYp = relation.getSourceYp();

        relation.setSourceLocationp(this.xp, this.yp);
        relation.setParentMove();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        relation.setSourceLocationp(this.oldXp, this.oldYp);
        relation.setParentMove();
    }

}

package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;

public class DefaultLineCommand extends AbstractCommand {

    private int sourceXp;

    private int sourceYp;

    private int targetXp;

    private int targetYp;

    private WalkerConnection connection;

    private List<Bendpoint> oldBendpointList;

    public DefaultLineCommand(ERDiagram diagram, WalkerConnection connection) {
        if (connection instanceof Relationship) {
            Relationship relation = (Relationship) connection;

            this.sourceXp = relation.getSourceXp();
            this.sourceYp = relation.getSourceYp();
            this.targetXp = relation.getTargetXp();
            this.targetYp = relation.getTargetYp();
        }

        this.connection = connection;
        this.oldBendpointList = this.connection.getBendpoints();
    }

    @Override
    protected void doExecute() {
        this.connection.setBendpoints(new ArrayList<Bendpoint>());
        if (connection instanceof Relationship) {
            Relationship relation = (Relationship) connection;

            relation.setSourceLocationp(-1, -1);
            relation.setTargetLocationp(-1, -1);
            relation.setParentMove();
        }
    }

    @Override
    protected void doUndo() {
        this.connection.setBendpoints(this.oldBendpointList);
        if (connection instanceof Relationship) {
            Relationship relation = (Relationship) connection;

            relation.setSourceLocationp(this.sourceXp, this.sourceYp);
            relation.setTargetLocationp(this.targetXp, this.targetYp);
            relation.setParentMove();
        }
    }
}

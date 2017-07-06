package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.eclipse.gef.EditPart;

public abstract class AbstractCreateConnectionCommand extends AbstractCommand {

    protected EditPart source;
    protected EditPart target;

    public AbstractCreateConnectionCommand() {
        super();
    }

    public void setSource(EditPart source) {
        this.source = source;
    }

    public void setTarget(EditPart target) {
        this.target = target;
    }

    public DiagramWalker getSourceModel() {
        return (DiagramWalker) source.getModel();
    }

    public DiagramWalker getTargetModel() {
        return (DiagramWalker) target.getModel();
    }

    @Override
    public boolean canExecute() {
        return source != null && target != null && source != target;
    }

    abstract public String validate();
}

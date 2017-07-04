package org.dbflute.erflute.editor.controller.command;

import org.dbflute.erflute.Activator;
import org.eclipse.gef.commands.Command;

public abstract class AbstractCommand extends Command {

    @Override
    final public void execute() {
        try {
            Activator.debug(this, "doExecute", "Before");
            doExecute();
            Activator.debug(this, "doExecute", "After");
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    @Override
    final public void undo() {
        try {
            doUndo();
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    abstract protected void doExecute();

    abstract protected void doUndo();
}

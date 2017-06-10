package org.dbflute.erflute.editor.controller.command;

import org.dbflute.erflute.Activator;
import org.eclipse.gef.commands.Command;

public abstract class AbstractCommand extends Command {

    @Override
    final public void execute() {
        try {
            doExecute();

        } catch (Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    @Override
    final public void undo() {
        try {
            doUndo();
        } catch (Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    abstract protected void doExecute();

    abstract protected void doUndo();
}

package org.dbflute.erflute.editor.view.action;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;

public abstract class AbstractBaseSelectionAction extends SelectionAction {

    private final MainDiagramEditor editor;

    public AbstractBaseSelectionAction(String id, String text, MainDiagramEditor editor) {
        this(id, text, SWT.NONE, editor);
    }

    public AbstractBaseSelectionAction(String id, String text, int style, MainDiagramEditor editor) {
        super(editor, style);
        setId(id);
        setText(text);

        this.editor = editor;
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = editor.getGraphicalViewer().getContents();
        final ERDiagram diagram = ERModelUtil.getDiagram(editPart);

        return diagram;
    }

    protected GraphicalViewer getGraphicalViewer() {
        return editor.getGraphicalViewer();
    }

    @Override
    public final void runWithEvent(Event event) {
        try {
            execute(event);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    @Override
    protected void execute(Command command) {
        editor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    protected IEditorPart getEditorPart() {
        return editor;
    }

    protected void execute(Event event) {
        final GraphicalViewer viewer = getGraphicalViewer();

        final List<Command> commandList = new ArrayList<>();

        for (final Object object : viewer.getSelectedEditParts()) {
            final List<Command> subCommandList = getCommand((EditPart) object, event);
            commandList.addAll(subCommandList);
        }

        if (!commandList.isEmpty()) {
            final CompoundCommand compoundCommand = new CompoundCommand();
            for (final Command command : commandList) {
                compoundCommand.add(command);
            }

            execute(compoundCommand);
        }
    }

    abstract protected List<Command> getCommand(EditPart editPart, Event event);

    @Override
    protected boolean calculateEnabled() {
        final GraphicalViewer viewer = getGraphicalViewer();

        if (viewer.getSelectedEditParts().isEmpty()) {
            return false;
        }

        return true;
    }
}

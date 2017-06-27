package org.dbflute.erflute.editor.controller.editpolicy.element.node;

import java.util.HashSet;
import java.util.Set;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship.DeleteRelationshipCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.DeleteElementCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.category.DeleteCategoryCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class DiagramWalkerComponentEditPolicy extends ComponentEditPolicy {

    @Override
    protected Command createDeleteCommand(GroupRequest request) {
        try {
            if (getHost() instanceof DeleteableEditPart) {
                final DeleteableEditPart editPart = (DeleteableEditPart) getHost();
                if (!editPart.isDeleteable()) {
                    return null;
                }
            } else {
                return null;
            }

            final Set<DiagramWalker> targets = new HashSet<>();
            for (final Object object : request.getEditParts()) {
                final EditPart editPart = (EditPart) object;
                final Object model = editPart.getModel();
                if (model instanceof DiagramWalker) {
                    targets.add((DiagramWalker) model);
                }
            }

            final ERDiagram diagram = ERModelUtil.getDiagram(getHost().getRoot().getContents());
            DiagramWalker walker = (DiagramWalker) getHost().getModel();

            if (walker instanceof Category) {
                return new DeleteCategoryCommand(diagram, (Category) walker);
            }

            ERVirtualTable virtualTable = null;
            if (walker instanceof ERVirtualTable) {
                virtualTable = (ERVirtualTable) walker;
                walker = ((ERVirtualTable) walker).getRawTable();
            }

            if (!diagram.getDiagramContents().getDiagramWalkers().contains(walker) && !(walker instanceof Category)
                    && !(walker instanceof WalkerNote) && !(walker instanceof WalkerGroup)) {
                return null;
            }

            final CompoundCommand command = new CompoundCommand();
            if (virtualTable == null) {
                command.add(new DeleteElementCommand(diagram, walker));

                for (final WalkerConnection connection : walker.getIncomings()) {
                    if (connection instanceof Relationship) {
                        command.add(new DeleteRelationshipCommand((Relationship) connection, true));

                    } else {
                        command.add(new DeleteConnectionCommand(connection));
                    }
                }

                for (final WalkerConnection connection : walker.getOutgoings()) {
                    final DiagramWalker target = connection.getWalkerTarget();
                    if (!targets.contains(target)) {
                        if (connection instanceof Relationship) {
                            command.add(new DeleteRelationshipCommand((Relationship) connection, true));
                        } else {
                            command.add(new DeleteConnectionCommand(connection));
                        }
                    }
                }
            } else {
                command.add(new DeleteElementCommand(diagram, virtualTable));
            }

            return command.unwrap();
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
            return null;
        }
    }
}

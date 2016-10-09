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
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class NodeElementComponentEditPolicy extends ComponentEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createDeleteCommand(GroupRequest request) {
        try {
            if (this.getHost() instanceof DeleteableEditPart) {
                DeleteableEditPart editPart = (DeleteableEditPart) this.getHost();

                if (!editPart.isDeleteable()) {
                    return null;
                }

            } else {
                return null;
            }

            Set<NodeElement> targets = new HashSet<NodeElement>();

            for (Object object : request.getEditParts()) {
                EditPart editPart = (EditPart) object;

                Object model = editPart.getModel();

                if (model instanceof NodeElement) {
                    targets.add((NodeElement) model);
                }
            }

            ERDiagram diagram = ERModelUtil.getDiagram(this.getHost().getRoot().getContents());
            NodeElement element = (NodeElement) this.getHost().getModel();

            if (element instanceof Category) {
                return new DeleteCategoryCommand(diagram, (Category) element);
            }

            ERVirtualTable virtualTable = null;
            if (element instanceof ERVirtualTable) {
                virtualTable = (ERVirtualTable) element;
                element = ((ERVirtualTable) element).getRawTable();
            }
            if (!diagram.getDiagramContents().getContents().contains(element) && !(element instanceof Category)) {
                return null;
            }

            CompoundCommand command = new CompoundCommand();

            if (virtualTable == null) {
                for (ConnectionElement connection : element.getIncomings()) {
                    if (connection instanceof Relationship) {
                        command.add(new DeleteRelationshipCommand((Relationship) connection, true));

                    } else {
                        command.add(new DeleteConnectionCommand(connection));
                    }
                }

                for (ConnectionElement connection : element.getOutgoings()) {

                    NodeElement target = connection.getTarget();

                    if (!targets.contains(target)) {
                        if (connection instanceof Relationship) {
                            command.add(new DeleteRelationshipCommand((Relationship) connection, true));
                        } else {
                            command.add(new DeleteConnectionCommand(connection));
                        }
                    }
                }

                command.add(new DeleteElementCommand(diagram, element));
            } else {
                // �r���[��Ńe�[�u���������Ă����ۂɂ͏������A�r���[������������ɂ���
                command.add(new DeleteElementCommand(diagram, virtualTable));
                // �����������[�V�����͏���
            }

            return command.unwrap();

        } catch (Exception e) {
            Activator.showExceptionDialog(e);
        }

        return null;
    }

}

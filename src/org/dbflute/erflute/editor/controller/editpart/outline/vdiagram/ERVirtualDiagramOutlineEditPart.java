package org.dbflute.erflute.editor.controller.editpart.outline.vdiagram;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.command.ermodel.OpenERModelCommand;
import org.dbflute.erflute.editor.controller.editpart.DeleteableEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpolicy.element.node.DiagramWalkerComponentEditPolicy;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERVirtualDiagramOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ERVirtualDiagram.PROPERTY_CHANGE_VTABLES)) {
            refresh();
        }
    }

    @Override
    public void refresh() {
        refreshChildren();
        refreshVisuals();
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SelectEditPartTracker(this);
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    protected void refreshOutlineVisuals() {
        this.refreshName();
        for (final Object child : this.getChildren()) {
            final EditPart part = (EditPart) child;
            part.refresh();
        }
    }

    private void refreshName() {
        final ERVirtualDiagram vdiagram = (ERVirtualDiagram) this.getModel();
        setWidgetText(vdiagram.getName());
        setWidgetImage(Activator.getImage(ImageKey.DIAGRAM));
    }

    @Override
    public void performRequest(Request request) {
        final ERVirtualDiagram model = (ERVirtualDiagram) this.getModel();
        final ERDiagram diagram = this.getDiagram();
        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final OpenERModelCommand command = new OpenERModelCommand(diagram, model);
            // TODO ymd 仮想ダイアグラムを開くだけで、編集中になる問題を修正するために"this.execute(command);"から下記コードに書き換えた。
            // コマンドスタックに積んで実行すると、doExecuteで何もしなくてもファイル変更とみなされるらしい。
            // そもそも、オブジェクトの状態を変更しない(undoを実装できない)手続きは、コマンドで実装しない方が良いかもしれない。
            command.execute();
        }
        super.performRequest(request);
    }

    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.COMPONENT_ROLE, new DiagramWalkerComponentEditPolicy());
    }
}

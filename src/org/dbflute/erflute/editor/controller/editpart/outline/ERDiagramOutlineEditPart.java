package org.dbflute.erflute.editor.controller.editpart.outline;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ChangeBoundsRequest;

public class ERDiagramOutlineEditPart extends AbstractOutlineEditPart {

    private boolean quickMode;

    public ERDiagramOutlineEditPart(boolean quickMode) {
        this.quickMode = quickMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final List<AbstractModel> modelChildren = new ArrayList<AbstractModel>();
        final ERDiagram diagram = (ERDiagram) this.getModel();
        final DiagramContents diagramContents = diagram.getDiagramContents();

        if (quickMode) {
            modelChildren.add(diagramContents.getDiagramWalkers().getTableSet());
        } else {
            modelChildren.add(diagramContents.getVirtualDiagramSet());
            //			modelChildren.add(diagramContents.getContents().getErmodelSet());
            //			modelChildren.add(diagramContents.getDictionary());
            modelChildren.add(diagramContents.getColumnGroupSet());
            modelChildren.add(diagramContents.getDiagramWalkers().getTableSet());
            modelChildren.add(diagramContents.getDiagramWalkers().getViewSet());
            modelChildren.add(diagramContents.getTriggerSet());
            modelChildren.add(diagramContents.getSequenceSet());
            //			modelChildren.add(diagramContents.getIndexSet());
            modelChildren.add(diagramContents.getTablespaceSet());
        }

        return modelChildren;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_ALL) || evt.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_SETTINGS)) {
            refresh();
        }
        //		if (evt.getPropertyName().equals(ERModelSet.PROPERTY_CHANGE_MODEL_SET)) {
        //			Object newValue = evt.getNewValue();
        //			if (newValue != null) {
        //
        //				Set<Entry<NodeElement, EditPart>> entrySet = getModelToEditPart().entrySet();
        //				for (Entry<NodeElement, EditPart> entry : entrySet) {
        //					if (entry.getKey().equals(newValue)) {
        //						// �G�������g�̍X�V
        //						entry.getValue().refresh();
        //					}
        //				}
        //
        //			} else {
        //				refresh();
        //			}
        //		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        for (final Object child : this.getChildren()) {
            final EditPart part = (EditPart) child;
            part.refresh();
        }
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        if (request instanceof ChangeBoundsRequest) {
            final ChangeBoundsRequest breq = (ChangeBoundsRequest) request;
        }
        return super.getTargetEditPart(request);
    }

    private Map<DiagramWalker, EditPart> getModelToEditPart() {
        final Map<DiagramWalker, EditPart> modelToEditPart = new HashMap<DiagramWalker, EditPart>();
        final List children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            final EditPart editPart = (EditPart) children.get(i);
            modelToEditPart.put((DiagramWalker) editPart.getModel(), editPart);
        }

        return modelToEditPart;
    }

    public void setQuickMode(boolean quickMode) {
        this.quickMode = quickMode;
    }
}

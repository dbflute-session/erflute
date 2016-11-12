package org.dbflute.erflute.editor.controller.editpart.outline;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramOutlineEditPart extends AbstractOutlineEditPart {

    private boolean quickMode;

    public ERDiagramOutlineEditPart(boolean quickMode) {
        this.quickMode = quickMode;
    }

    @Override
    protected List<AbstractModel> getModelChildren() {
        final List<AbstractModel> modelChildren = new ArrayList<AbstractModel>();
        final ERDiagram diagram = (ERDiagram) this.getModel();
        final DiagramContents diagramContents = diagram.getDiagramContents();
        if (quickMode) {
            modelChildren.add(diagramContents.getDiagramWalkers().getTableSet());
        } else {
            modelChildren.add(diagramContents.getVirtualDiagramSet());
            modelChildren.add(diagramContents.getDiagramWalkers().getTableSet());
            if (diagram.getDiagramContents().getSettings().isUseViewObject()) { // #for_erflute view is option
                modelChildren.add(diagramContents.getDiagramWalkers().getViewSet());
            }
            modelChildren.add(diagramContents.getColumnGroupSet());
            if (OracleDBManager.ID.equals(diagram.getDatabase())) { // Oracle only for now
                modelChildren.add(diagramContents.getTablespaceSet());
            }
            // #deleted sequence, trigger
            //modelChildren.add(diagramContents.getSequenceSet());
            //modelChildren.add(diagramContents.getTriggerSet());
        }
        return modelChildren;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_ALL) || evt.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_SETTINGS)) {
            refresh();
        }
        // what is this? by jflute
        //		if (evt.getPropertyName().equals(ERModelSet.PROPERTY_CHANGE_MODEL_SET)) {
        //			Object newValue = evt.getNewValue();
        //			if (newValue != null) {
        //
        //				Set<Entry<NodeElement, EditPart>> entrySet = getModelToEditPart().entrySet();
        //				for (Entry<NodeElement, EditPart> entry : entrySet) {
        //					if (entry.getKey().equals(newValue)) {
        //						entry.getValue().refresh();
        //					}
        //				}
        //
        //			} else {
        //				refresh();
        //			}
        //		}
    }

    //private Map<DiagramWalker, EditPart> getModelToEditPart() {
    //    final Map<DiagramWalker, EditPart> modelToEditPart = new HashMap<DiagramWalker, EditPart>();
    //    @SuppressWarnings("unchecked")
    //    final List<EditPart> children = getChildren();
    //    for (int i = 0; i < children.size(); i++) {
    //        final EditPart editPart = children.get(i);
    //        modelToEditPart.put((DiagramWalker) editPart.getModel(), editPart);
    //    }
    //    return modelToEditPart;
    //}

    @Override
    protected void refreshOutlineVisuals() {
        for (final Object child : this.getChildren()) {
            final EditPart part = (EditPart) child;
            part.refresh();
        }
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        // unused? by jflute
        //if (request instanceof ChangeBoundsRequest) {
        //    final ChangeBoundsRequest breq = (ChangeBoundsRequest) request;
        //}
        return super.getTargetEditPart(request);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setQuickMode(boolean quickMode) {
        this.quickMode = quickMode;
    }
}

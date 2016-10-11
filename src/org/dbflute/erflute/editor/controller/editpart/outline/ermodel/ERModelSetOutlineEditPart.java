package org.dbflute.erflute.editor.controller.editpart.outline.ermodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;

public class ERModelSetOutlineEditPart extends AbstractOutlineEditPart {

    @Override
    protected List<ERVirtualDiagram> getModelChildren() {
        final ERVirtualDiagramSet modelSet = (ERVirtualDiagramSet) this.getModel();
        final List<ERVirtualDiagram> list = new ArrayList<ERVirtualDiagram>();
        for (final ERVirtualDiagram table : modelSet) {
            list.add(table);
        }
        Collections.sort(list, new Comparator<ERVirtualDiagram>() {
            @Override
            public int compare(ERVirtualDiagram o1, ERVirtualDiagram o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        //		if (this.getDiagram().getDiagramContents().getSettings()
        //				.getViewOrderBy() == Settings.VIEW_MODE_LOGICAL) {
        //			Collections.sort(list, TableView.LOGICAL_NAME_COMPARATOR);
        //
        //		} else {
        //			Collections.sort(list, TableView.PHYSICAL_NAME_COMPARATOR);
        //
        //		}
        return list;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ERVirtualDiagramSet.PROPERTY_CHANGE_MODEL_SET)) {
            refresh();
        }
    }

    @Override
    protected void refreshOutlineVisuals() {
        this.setWidgetText(DisplayMessages.getMessage("label.ermodel") + " (" + this.getModelChildren().size() + ")");
        this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

}

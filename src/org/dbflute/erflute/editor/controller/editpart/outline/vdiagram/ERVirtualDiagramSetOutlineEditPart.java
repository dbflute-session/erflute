package org.dbflute.erflute.editor.controller.editpart.outline.vdiagram;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERVirtualDiagramSetOutlineEditPart extends AbstractOutlineEditPart {

    @Override
    protected List<ERVirtualDiagram> getModelChildren() {
        final ERVirtualDiagramSet modelSet = (ERVirtualDiagramSet) getModel();
        final List<ERVirtualDiagram> list = new ArrayList<>();
        for (final ERVirtualDiagram table : modelSet) {
            list.add(table);
        }
        Collections.sort(list, new Comparator<ERVirtualDiagram>() {
            @Override
            public int compare(ERVirtualDiagram o1, ERVirtualDiagram o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
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
        setWidgetText("Virtual Diagram" + " (" + getModelChildren().size() + ")");
        setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }
}

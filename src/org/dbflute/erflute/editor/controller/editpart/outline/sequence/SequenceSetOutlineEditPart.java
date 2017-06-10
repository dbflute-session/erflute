package org.dbflute.erflute.editor.controller.editpart.outline.sequence;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.widgets.TreeItem;

public class SequenceSetOutlineEditPart extends AbstractOutlineEditPart {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(SequenceSet.PROPERTY_CHANGE_SEQUENCE_SET)) {
            refresh();
        }
    }

    @Override
    protected List getModelChildren() {
        SequenceSet sequenceSet = (SequenceSet) this.getModel();

        List<Sequence> sequenceList = sequenceSet.getSequenceList();

        Collections.sort(sequenceList);

        return sequenceList;
    }

    @Override
    protected void refreshOutlineVisuals() {
        if (!DBManagerFactory.getDBManager(this.getDiagram()).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            ((TreeItem) getWidget()).setForeground(ColorConstants.lightGray);

        } else {
            ((TreeItem) getWidget()).setForeground(ColorConstants.black);

        }

        this.setWidgetText(DisplayMessages.getMessage("label.sequence") + " (" + this.getModelChildren().size() + ")");
        this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();

        for (Object child : this.getChildren()) {
            EditPart part = (EditPart) child;
            part.refresh();
        }
    }
}

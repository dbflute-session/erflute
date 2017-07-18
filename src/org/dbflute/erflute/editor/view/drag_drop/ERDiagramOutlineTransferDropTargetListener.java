package org.dbflute.erflute.editor.view.drag_drop;

import java.util.Map;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.swt.dnd.Transfer;

public class ERDiagramOutlineTransferDropTargetListener extends AbstractTransferDropTargetListener {

    public ERDiagramOutlineTransferDropTargetListener(EditPartViewer dropTargetViewer, Transfer xfer) {
        super(dropTargetViewer, xfer);
    }

    @Override
    protected void updateTargetRequest() {
    }

    @Override
    protected Request createTargetRequest() {
        final Object object = getTargetModel();

        if (object instanceof Map) {
            final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP);
            request.setDirectEditFeature(object);
            request.setLocation(getDropLocation());
            return request;

        } else if (object instanceof Word) {
            final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD);
            request.setDirectEditFeature(object);
            request.setLocation(getDropLocation());
            return request;

        } else if (object instanceof NormalColumn) {
            final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN);
            request.setDirectEditFeature(object);
            request.setLocation(getDropLocation());
            return request;
        }

        return super.createTargetRequest();
    }

    private Object getTargetModel() {
        final TemplateTransfer transfer = (TemplateTransfer) getTransfer();
        return transfer.getObject();
    }
}

package org.dbflute.erflute.editor.view.drag_drop;

import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramTransferDropTargetListener extends AbstractTransferDropTargetListener {

    public ERDiagramTransferDropTargetListener(EditPartViewer dropTargetViewer, Transfer xfer) {
        super(dropTargetViewer, xfer);
    }

    @Override
    protected void updateTargetRequest() {
    }

    @Override
    public void drop(DropTargetEvent event) {
        super.drop(event);
    }

    @Override
    protected Request createTargetRequest() {
        final Object object = this.getTargetModel();

        if (object instanceof Map) {
            if (((Map<?, ?>) object).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT) instanceof TableView) {
                final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP);
                request.setDirectEditFeature(object);
                request.setLocation(this.getDropLocation());
                return request;
            } else {
                final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP);
                request.setDirectEditFeature(object);
                request.setLocation(this.getDropLocation());
                return request;
            }
        } else if (object instanceof Word) {
            final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD);
            request.setDirectEditFeature(object);
            request.setLocation(this.getDropLocation());
            return request;

        } else if (object instanceof NormalColumn) {
            final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN);
            request.setDirectEditFeature(object);
            request.setLocation(this.getDropLocation());
            return request;
        } else if (object instanceof ERTable) {
            final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE);
            request.setDirectEditFeature(object);
            request.setLocation(this.getDropLocation());
            return request;
        } else if (object instanceof List) {
            final DirectEditRequest request = new DirectEditRequest(ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE);
            request.setDirectEditFeature(object);
            request.setLocation(this.getDropLocation());
            return request;
        }

        return super.createTargetRequest();
    }

    private Object getTargetModel() {
        final TemplateTransfer transfer = (TemplateTransfer) this.getTransfer();
        return transfer.getObject();
    }
}

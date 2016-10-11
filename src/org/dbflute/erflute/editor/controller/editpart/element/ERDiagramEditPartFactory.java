package org.dbflute.erflute.editor.controller.editpart.element;

import org.dbflute.erflute.editor.controller.editpart.element.connection.CommentConnectionEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.connection.RelationEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.CategoryEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERModelEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ERVirtualTableEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.InsertedImageEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ModelPropertiesEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.WalkerNoteEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.WalkerGroupEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.ViewEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.GroupColumnEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.index.IndexEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramEditPartFactory implements EditPartFactory {

    public ERDiagramEditPartFactory() {
    }

    @Override
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart editPart = null;
        if (model instanceof ERVirtualDiagram) {
            editPart = new ERModelEditPart();
        } else if (model instanceof ERVirtualTable) {
            editPart = new ERVirtualTableEditPart();
        } else if (model instanceof ERTable) {
            editPart = new ERTableEditPart();
        } else if (model instanceof ERView) {
            editPart = new ViewEditPart();
        } else if (model instanceof ERDiagram) {
            editPart = new ERDiagramEditPart();
        } else if (model instanceof Relationship) {
            editPart = new RelationEditPart();
        } else if (model instanceof WalkerNote) {
            editPart = new WalkerNoteEditPart();
        } else if (model instanceof ERIndex) {
            editPart = new IndexEditPart();
        } else if (model instanceof ModelProperties) {
            editPart = new ModelPropertiesEditPart();
        } else if (model instanceof CommentConnection) {
            editPart = new CommentConnectionEditPart();
        } else if (model instanceof Category) {
            editPart = new CategoryEditPart();
        } else if (model instanceof NormalColumn) {
            editPart = new NormalColumnEditPart();
        } else if (model instanceof ColumnGroup) {
            editPart = new GroupColumnEditPart();
        } else if (model instanceof InsertedImage) {
            editPart = new InsertedImageEditPart();
        } else if (model instanceof WalkerGroup) {
            editPart = new WalkerGroupEditPart();
        }
        if (editPart != null) {
            editPart.setModel(model);
        } else {
            System.out.println("error");
        }
        return editPart;
    }
}

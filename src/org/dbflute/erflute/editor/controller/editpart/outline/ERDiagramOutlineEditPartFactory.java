package org.dbflute.erflute.editor.controller.editpart.outline;

import java.util.HashMap;
import java.util.Map;

import org.dbflute.erflute.editor.controller.editpart.outline.columngroup.ColumnGroupOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.columngroup.GroupSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.dictionary.DictionaryOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.dictionary.WordOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.index.IndexOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.index.IndexSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.sequence.SequenceOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.sequence.SequenceSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.table.RelationOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.table.TableOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.table.TableSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.tablespace.TablespaceOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.tablespace.TablespaceSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.trigger.TriggerOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.trigger.TriggerSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.vdiagram.ERVirtualDiagramOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.vdiagram.ERVirtualDiagramSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.view.ViewOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.view.ViewSetOutlineEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ViewSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramOutlineEditPartFactory implements EditPartFactory {

    public static Map<String, EditPart> tableParts = new HashMap<String, EditPart>();

    private String filterText;
    private boolean quickMode;

    @Override
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart editPart = null;
        if (model instanceof ERVirtualDiagram) {
            editPart = new ERVirtualDiagramOutlineEditPart();
        } else if (model instanceof ERVirtualDiagramSet) {
            editPart = new ERVirtualDiagramSetOutlineEditPart();
        } else if (model instanceof ERTable) {
            editPart = new TableOutlineEditPart(quickMode);
            tableParts.put(((ERTable) model).getLogicalName(), editPart);
        } else if (model instanceof ERDiagram) {
            editPart = new ERDiagramOutlineEditPart(quickMode);
        } else if (model instanceof Relationship) {
            editPart = new RelationOutlineEditPart();
        } else if (model instanceof Word) {
            editPart = new WordOutlineEditPart();
        } else if (model instanceof Dictionary) {
            editPart = new DictionaryOutlineEditPart();
        } else if (model instanceof ColumnGroup) {
            editPart = new ColumnGroupOutlineEditPart();
        } else if (model instanceof ColumnGroupSet) {
            editPart = new GroupSetOutlineEditPart();
        } else if (model instanceof SequenceSet) {
            editPart = new SequenceSetOutlineEditPart();
        } else if (model instanceof Sequence) {
            editPart = new SequenceOutlineEditPart();
        } else if (model instanceof ViewSet) {
            editPart = new ViewSetOutlineEditPart();
        } else if (model instanceof ERView) {
            editPart = new ViewOutlineEditPart();
        } else if (model instanceof TriggerSet) {
            editPart = new TriggerSetOutlineEditPart();
        } else if (model instanceof Trigger) {
            editPart = new TriggerOutlineEditPart();
        } else if (model instanceof TablespaceSet) {
            editPart = new TablespaceSetOutlineEditPart();
        } else if (model instanceof Tablespace) {
            editPart = new TablespaceOutlineEditPart();
        } else if (model instanceof TableSet) {
            editPart = new TableSetOutlineEditPart();
        } else if (model instanceof IndexSet) {
            editPart = new IndexSetOutlineEditPart();
        } else if (model instanceof ERIndex) {
            editPart = new IndexOutlineEditPart();
        }
        if (editPart != null) {
            editPart.setModel(model);
            ((FilteringEditPart) editPart).setFilterText(filterText);
        } else {
            System.out.println("error");
        }
        return editPart;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public void setQuickMode(boolean quickMode) {
        this.quickMode = quickMode;
    }
}

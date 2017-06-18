package org.dbflute.erflute.editor.view.outline;

import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.ermodel.OpenERModelCommand;
import org.dbflute.erflute.editor.controller.editpart.outline.ERDiagramOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.ERDiagramOutlineEditPartFactory;
import org.dbflute.erflute.editor.controller.editpart.outline.table.TableOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.vdiagram.ERVirtualDiagramOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.vdiagram.ERVirtualDiagramSetOutlineEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.view.action.outline.ChangeNameAction;
import org.dbflute.erflute.editor.view.action.outline.DeleteVirtualDiagramAction;
import org.dbflute.erflute.editor.view.action.outline.index.CreateIndexAction;
import org.dbflute.erflute.editor.view.action.outline.notation.type.ChangeOutlineViewToBothAction;
import org.dbflute.erflute.editor.view.action.outline.notation.type.ChangeOutlineViewToLogicalAction;
import org.dbflute.erflute.editor.view.action.outline.notation.type.ChangeOutlineViewToPhysicalAction;
import org.dbflute.erflute.editor.view.action.outline.orderby.ChangeOutlineViewOrderByLogicalNameAction;
import org.dbflute.erflute.editor.view.action.outline.orderby.ChangeOutlineViewOrderByPhysicalNameAction;
import org.dbflute.erflute.editor.view.action.outline.sequence.CreateSequenceAction;
import org.dbflute.erflute.editor.view.action.outline.tablespace.CreateTablespaceAction;
import org.dbflute.erflute.editor.view.action.outline.trigger.CreateTriggerAction;
import org.dbflute.erflute.editor.view.drag_drop.ERDiagramOutlineTransferDropTargetListener;
import org.dbflute.erflute.editor.view.drag_drop.ERDiagramTransferDragSourceListener;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramOutlinePage extends ContentOutlinePage {

    private SashForm sash;
    private final TreeViewer viewer;
    private final ERDiagram diagram;
    private LightweightSystem lws;
    private ScrollableThumbnail thumbnail;
    private GraphicalViewer graphicalViewer;
    private final ActionRegistry outlineActionRegistory;
    private ActionRegistry registry;
    private boolean quickMode;
    private ERDiagramOutlineEditPartFactory editPartFactory;

    public ERDiagramOutlinePage(ERDiagram diagram) {
        super(new TreeViewer());
        this.viewer = (TreeViewer) this.getViewer();
        this.diagram = diagram;
        this.outlineActionRegistory = new ActionRegistry();
        this.registerAction(this.viewer, outlineActionRegistory);
    }

    @Override
    public void createControl(Composite parent) {
        this.sash = new SashForm(parent, SWT.VERTICAL);
        this.viewer.createControl(this.sash);
        editPartFactory = new ERDiagramOutlineEditPartFactory();
        editPartFactory.setQuickMode(quickMode);
        this.viewer.setEditPartFactory(editPartFactory);
        this.viewer.setContents(this.diagram);
        if (!quickMode) {
            final Canvas canvas = new Canvas(this.sash, SWT.BORDER);
            this.lws = new LightweightSystem(canvas);
        }
        this.resetView(this.registry);
        final AbstractTransferDragSourceListener dragSourceListener =
                new ERDiagramTransferDragSourceListener(this.viewer, TemplateTransfer.getInstance());
        this.viewer.addDragSourceListener(dragSourceListener);
    }

    @Override
    public Control getControl() {
        return sash;
    }

    private void showThumbnail() {
        if (quickMode) {
            return;
        }
        final ScalableFreeformRootEditPart editPart = (ScalableFreeformRootEditPart) this.graphicalViewer.getRootEditPart();
        if (this.thumbnail != null) {
            this.thumbnail.deactivate();
        }
        this.thumbnail = new ScrollableThumbnail((Viewport) editPart.getFigure());
        this.thumbnail.setSource(editPart.getLayer(LayerConstants.PRINTABLE_LAYERS));
        this.lws.setContents(this.thumbnail);
    }

    private void initDropTarget() {
        final AbstractTransferDropTargetListener dropTargetListener =
                new ERDiagramOutlineTransferDropTargetListener(this.graphicalViewer, TemplateTransfer.getInstance());
        this.graphicalViewer.addDropTargetListener(dropTargetListener);
    }

    public void setContextMenu(MenuManager outlineMenuMgr) {
        this.viewer.setContextMenu(outlineMenuMgr);
    }

    public void setCategory(EditDomain editDomain, GraphicalViewer graphicalViewer, ActionRegistry registry) {
        this.graphicalViewer = graphicalViewer;

        this.viewer.setEditDomain(editDomain);
        this.registry = registry;

        if (this.getSite() != null) {
            this.resetView(registry);
        }
    }

    private void resetAction(ActionRegistry registry) {
        if (getSite() == null) {
            return;
        }
        final IActionBars bars = this.getSite().getActionBars();

        String id = ActionFactory.UNDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        id = ActionFactory.REDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        id = ActionFactory.DELETE.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        bars.updateActionBars();
    }

    private void resetView(ActionRegistry registry) {
        this.showThumbnail();
        this.initDropTarget();
        this.resetAction(registry);
    }

    private void registerAction(TreeViewer treeViewer, ActionRegistry actionRegistry) {
        final IAction[] actions = { new CreateIndexAction(treeViewer), new CreateSequenceAction(treeViewer),
                new CreateTriggerAction(treeViewer), new CreateTablespaceAction(treeViewer),
                new ChangeOutlineViewToPhysicalAction(treeViewer), new ChangeOutlineViewToLogicalAction(treeViewer),
                new ChangeOutlineViewToBothAction(treeViewer), new ChangeOutlineViewOrderByPhysicalNameAction(treeViewer),
                new ChangeOutlineViewOrderByLogicalNameAction(treeViewer), new ChangeNameAction(treeViewer),
                new DeleteVirtualDiagramAction(treeViewer), };
        for (final IAction action : actions) {
            actionRegistry.registerAction(action);
        }
    }

    public ActionRegistry getOutlineActionRegistory() {
        return outlineActionRegistory;
    }

    @Override
    public EditPartViewer getViewer() {
        return super.getViewer();
    }

    public void update() {
        viewer.flush();
        //		gettr
        //		if (model != null) {
        //			try {
        //				model.update(editor.getDocumentProvider()
        //						.getDocument(editor.getEditorInput()).get());
        //			} catch (Throwable t) {
        //				t.printStackTrace();
        //			}
        //		}
    }

    public void setFilterText(String filterText) {
        editPartFactory.setFilterText(filterText);
        viewer.setContents(diagram);
        final Tree tree = (Tree) viewer.getControl();
        final TreeItem[] items = tree.getItems();
        expand(items);
        final TreeItem[] tableItems = items[0].getItems();
        if (tableItems.length >= 1) {
            tree.setSelection(tableItems[0]);
        }
        //		viewer.getContents().getChildren();

        //		viewer.flush();
        //		viewer.getEditPartFactory()
        //		if (filterText == null) {
        //			filterText = "";
        //		}
        //		this.filterText = filterText;
        //		getTreeViewer().refresh();
        //		getTreeViewer().expandAll();
        //		JavaScriptElement element = getFirstElement(model, filterText);
        //		if(element != null){
        //			getViewer().setSelection(new StructuredSelection(element), true);
        //		}
    }

    private void expand(TreeItem[] items) {
        for (int i = 0; i < items.length; i++) {
            expand(items[i].getItems());
            items[i].setExpanded(true);
        }
    }

    public void setQuickMode(boolean quickMode) {
        this.quickMode = quickMode;
    }

    public void selectSelection() {
        final IStructuredSelection sel = (IStructuredSelection) getViewer().getSelection();
        Object firstElement = sel.getFirstElement();
        if (firstElement instanceof ERDiagramOutlineEditPart) {
            final Tree tree = (Tree) viewer.getControl();
            final TreeItem[] items = tree.getItems();
            expand(items);
            final TreeItem[] tableItems = items[0].getItems();
            if (tableItems.length >= 1) {
                final Object data = tableItems[0].getData();
                firstElement = data;
            }
        }
        if (firstElement instanceof TableOutlineEditPart) {
            final Object model = ((TableOutlineEditPart) firstElement).getModel();
            final ERTable table = (ERTable) model;

            if (diagram.getCurrentVirtualDiagram() == null) {
                final MainDiagramEditor editor = ((MainDiagramEditor) diagram.getEditor().getActiveEditor());
                editor.reveal(table);
                return;
            }
            final ERVirtualDiagram erModel = table.getDiagram().findModelByTable(table);
            if (erModel != null) {
                final OpenERModelCommand command = new OpenERModelCommand(diagram, erModel);
                command.setTable(table);
                this.getViewer().getEditDomain().getCommandStack().execute(command);

                final ERDiagramOutlineEditPart contents =
                        (ERDiagramOutlineEditPart) diagram.getEditor().getOutlinePage().getViewer().getContents();
                if (contents != null) {
                    final ERVirtualDiagramSetOutlineEditPart virtualDiagramSetOutlineEditPart =
                            (ERVirtualDiagramSetOutlineEditPart) contents.getChildren().get(0);
                    @SuppressWarnings("unchecked")
                    final List<ERVirtualDiagramOutlineEditPart> parts = virtualDiagramSetOutlineEditPart.getChildren();
                    for (final ERVirtualDiagramOutlineEditPart part : parts) {
                        if (part.getModel().equals(erModel)) {
                            final ISelection selection = new StructuredSelection(part);
                            diagram.getEditor().getOutlinePage().setSelection(selection);
                        }
                    }
                }
            } else {
                Activator.showMessageDialog(table.getPhysicalName());
            }
        }
    }
}

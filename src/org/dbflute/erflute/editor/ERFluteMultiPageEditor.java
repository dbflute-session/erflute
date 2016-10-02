package org.dbflute.erflute.editor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.controller.command.category.ChangeCategoryNameCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.Validator;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.persistent.Persistent;
import org.dbflute.erflute.editor.view.dialog.category.CategoryNameChangeDialog;
import org.dbflute.erflute.editor.view.outline.ERDiagramOutlinePage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * #analyze defined at plugins.xml
 * @author modified by jflute (originated in ermaster)
 */
public class ERFluteMultiPageEditor extends MultiPageEditorPart {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ERDiagram diagram;
    private ERDiagramEditPartFactory editPartFactory;
    private ERDiagramOutlinePage outlinePage;
    private ZoomComboContributionItem zoomComboContributionItem;
    private ErfluteElementStateListener elementStateListener;
    private boolean dirty;

    // ===================================================================================
    //                                                                        Create Pages
    //                                                                        ============
    @Override
    protected void createPages() {
        prepareDiagram();
        editPartFactory = new ERDiagramEditPartFactory();
        outlinePage = new ERDiagramOutlinePage(this.diagram);
        try {
            this.zoomComboContributionItem = new ZoomComboContributionItem(this.getSite().getPage());
            final MainModelEditor editor = new MainModelEditor(diagram, this.editPartFactory, zoomComboContributionItem, this.outlinePage);
            final int index = addPage(editor, this.getEditorInput()); // as main
            this.setPageText(index, DisplayMessages.getMessage("label.all"));
        } catch (final PartInitException e) {
            Activator.showExceptionDialog(e);
        }
        initCategoryPages();
        initStartPage();
        addMouseListenerToTabFolder();
        validate();
        if (diagram.getCurrentErmodel() == null) {
            final MainModelEditor diagramEditor = (MainModelEditor) getActiveEditor();
            diagramEditor.getGraphicalViewer().setContents(diagram);
        }
    }

    private void prepareDiagram() {
        try {
            final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
            this.setPartName(file.getName());
            final Persistent persistent = Persistent.getInstance();
            if (!file.isSynchronized(IResource.DEPTH_ONE)) {
                file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
            }
            final InputStream in = file.getContents();
            this.diagram = persistent.read(in);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        if (this.diagram == null) {
            this.diagram = new ERDiagram(DBManagerFactory.getAllDBList().get(0));
            this.diagram.init();
        }
        this.diagram.setEditor(this);
    }

    public void initCategoryPages() { // called by ERDiagram
        final String modelName = diagram.getDefaultModelName();
        if (modelName != null) {
            try {
                final ERModel model = diagram.getDiagramContents().getModelSet().getModel(modelName);
                diagram.setCurrentErmodel(model, model.getName());
                final SubModelEditor modelEditor =
                        new SubModelEditor(diagram, model, editPartFactory, zoomComboContributionItem, outlinePage);
                final int pageNo = addPage(modelEditor, this.getEditorInput()); // as view
                this.setPageText(pageNo, Format.null2blank(model.getName()));
            } catch (final PartInitException e) {
                Activator.showExceptionDialog(e);
            }
        }
    }

    private void initStartPage() {
        final ERModel model = diagram.getCurrentErmodel();
        if (model != null) {
            setActivePage(1);
        } else {
            setActivePage(0);
        }
        final MainModelEditor activeEditor = (MainModelEditor) this.getActiveEditor();
        final ZoomManager zoomManager = (ZoomManager) activeEditor.getAdapter(ZoomManager.class);
        zoomManager.setZoom(this.diagram.getZoom());
        activeEditor.setLocation(this.diagram.getX(), this.diagram.getY());
    }

    private void addMouseListenerToTabFolder() {
        final CTabFolder tabFolder = (CTabFolder) this.getContainer();
        tabFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent mouseevent) {
                final Category category = getCurrentPageCategory();
                if (category != null) {
                    final CategoryNameChangeDialog dialog =
                            new CategoryNameChangeDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), category);
                    if (dialog.open() == IDialogConstants.OK_ID) {
                        final ChangeCategoryNameCommand command =
                                new ChangeCategoryNameCommand(diagram, category, dialog.getCategoryName());
                        execute(command);
                    }
                }
                super.mouseDoubleClick(mouseevent);
            }
        });
    }

    private void validate() {
        final IFile file = ((IFileEditorInput) this.getEditorInput()).getFile();

        if (this.diagram.getDiagramContents().getSettings().isSuspendValidator()) {
            try {
                file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
            } catch (final CoreException e) {
                Activator.showExceptionDialog(e);
            }
        } else {
            final IWorkspaceRunnable editorMarker = new IWorkspaceRunnable() {
                @Override
                public void run(IProgressMonitor monitor) throws CoreException {
                    final MainModelEditor editor = (MainModelEditor) getActiveEditor();
                    file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
                    editor.clearMarkedObject();
                    final Validator validator = new Validator();
                    final List<ValidateResult> errorList = validator.validate(diagram);
                    for (final ValidateResult error : errorList) {
                        final IMarker marker = file.createMarker(IMarker.PROBLEM);
                        marker.setAttribute(IMarker.MESSAGE, error.getMessage());
                        marker.setAttribute(IMarker.TRANSIENT, true);
                        marker.setAttribute(IMarker.LOCATION, error.getLocation());
                        marker.setAttribute(IMarker.SEVERITY, error.getSeverity());
                        editor.setMarkedObject(marker, error.getObject());
                    }
                    final List<ValidateResult> todoList = validateTodo();
                    for (final ValidateResult todo : todoList) {
                        final IMarker marker = file.createMarker(IMarker.TASK);
                        marker.setAttribute(IMarker.MESSAGE, todo.getMessage());
                        marker.setAttribute(IMarker.TRANSIENT, true);
                        marker.setAttribute(IMarker.LOCATION, todo.getLocation());
                        marker.setAttribute(IMarker.SEVERITY, todo.getSeverity());
                        editor.setMarkedObject(marker, todo.getObject());
                    }
                }
            };
            try {
                ResourcesPlugin.getWorkspace().run(editorMarker, null);
            } catch (final CoreException e) {
                Activator.showExceptionDialog(e);
            }
        }
    }

    private List<ValidateResult> validateTodo() {
        final List<ValidateResult> resultList = new ArrayList<ValidateResult>();
        for (final ERTable table : this.diagram.getDiagramContents().getContents().getTableSet()) {
            String description = table.getDescription();
            resultList.addAll(this.createTodo(description, table.getLogicalName(), table));
            for (final NormalColumn column : table.getNormalColumns()) {
                description = column.getDescription();
                resultList.addAll(this.createTodo(description, table.getLogicalName(), table));
            }
            for (final ERIndex index : table.getIndexes()) {
                description = index.getDescription();
                resultList.addAll(this.createTodo(description, index.getName(), index));
            }
        }
        for (final ERView view : this.diagram.getDiagramContents().getContents().getViewSet().getList()) {
            String description = view.getDescription();
            resultList.addAll(this.createTodo(description, view.getName(), view));
            for (final NormalColumn column : view.getNormalColumns()) {
                description = column.getDescription();
                resultList.addAll(this.createTodo(description, view.getLogicalName(), view));
            }
        }
        for (final Trigger trigger : this.diagram.getDiagramContents().getTriggerSet().getTriggerList()) {
            final String description = trigger.getDescription();
            resultList.addAll(this.createTodo(description, trigger.getName(), trigger));
        }
        for (final Sequence sequence : this.diagram.getDiagramContents().getSequenceSet().getSequenceList()) {
            final String description = sequence.getDescription();
            resultList.addAll(this.createTodo(description, sequence.getName(), sequence));
        }
        return resultList;
    }

    private List<ValidateResult> createTodo(String description, String location, Object object) {
        final List<ValidateResult> resultList = new ArrayList<ValidateResult>();
        if (description != null) {
            final StringTokenizer tokenizer = new StringTokenizer(description, "\n\r");
            while (tokenizer.hasMoreElements()) {
                final String token = tokenizer.nextToken();
                final int startIndex = token.indexOf("// TODO");
                if (startIndex != -1) {
                    final String message = token.substring(startIndex + "// TODO".length()).trim();
                    final ValidateResult result = new ValidateResult();
                    result.setLocation(location);
                    result.setMessage(message);
                    result.setObject(object);
                    resultList.add(result);
                }
            }
        }
        return resultList;
    }

    // ===================================================================================
    //                                                                      Page Container
    //                                                                      ==============
    @Override
    protected Composite createPageContainer(Composite parent) {
        try {
            final IWorkbenchPage page = this.getSite().getWorkbenchWindow().getActivePage();
            if (page != null) {
                page.showView(IPageLayout.ID_OUTLINE);
            }
        } catch (final PartInitException e) {
            Activator.showExceptionDialog(e);
        }
        return super.createPageContainer(parent);
    }

    // ===================================================================================
    //                                                                               Save
    //                                                                              ======
    @Override
    public void doSave(IProgressMonitor monitor) {
        monitor.setTaskName("save initialize...");
        final ZoomManager zoomManager = (ZoomManager) this.getActiveEditor().getAdapter(ZoomManager.class);
        final double zoom = zoomManager.getZoom();
        this.diagram.setZoom(zoom);

        final MainModelEditor activeEditor = (MainModelEditor) this.getActiveEditor();
        final Point location = activeEditor.getLocation();
        this.diagram.setLocation(location.x, location.y);
        final Persistent persistent = Persistent.getInstance();
        final IFile file = ((IFileEditorInput) this.getEditorInput()).getFile();
        try {
            monitor.setTaskName("create stream...");
            diagram.getDiagramContents().getSettings().getModelProperties().setUpdatedDate(new Date());
            final InputStream source = persistent.write(this.diagram);
            if (!file.exists()) {
                file.create(source, true, monitor);
            } else {
                file.setContents(source, true, false, monitor);
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        monitor.beginTask("saving...", this.getPageCount());
        for (int i = 0; i < this.getPageCount(); i++) {
            final IEditorPart editor = this.getEditor(i);
            editor.doSave(monitor);
            monitor.worked(i + 1);
        }
        monitor.done();
        monitor.setTaskName("finalize...");

        validate();
        monitor.done();
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    // ===================================================================================
    //                                                                         Page Change
    //                                                                         ===========
    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        for (int i = 0; i < getPageCount(); i++) {
            final MainModelEditor editor = (MainModelEditor) getEditor(i);
            editor.removeSelection();
        }
        final MainModelEditor selectedEditor = (MainModelEditor) getActiveEditor();
        selectedEditor.changeCategory();
        if (selectedEditor instanceof SubModelEditor) { // sub editor
            final SubModelEditor editor = (SubModelEditor) selectedEditor;
            this.diagram.setCurrentErmodel(editor.getModel(), editor.getModel().getName());
        } else { // main editor
            this.diagram.setCurrentErmodel(null, null);
            this.diagram.changeAll();
        }
    }

    // ===================================================================================
    //                                                                       Active Editor
    //                                                                       =============
    @Override
    public IEditorPart getActiveEditor() { // to be public
        return super.getActiveEditor();
    }

    public Category getCurrentPageCategory() {
        return null;
    }

    // ===================================================================================
    //                                                                             Various
    //                                                                             =======
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        this.elementStateListener = new ErfluteElementStateListener(this);
    }

    @Override
    public void dispose() {
        this.elementStateListener.disposeDocumentProvider();
        super.dispose();
    }

    @Override
    protected void setInputWithNotify(IEditorInput input) {
        super.setInputWithNotify(input);
    }

    public void setCurrentCategoryPageName() {
        final Category category = this.getCurrentPageCategory();
        this.setPageText(this.getActivePage(), Format.null2blank(category.getName()));
    }

    private void execute(Command command) {
        final MainModelEditor selectedEditor = (MainModelEditor) this.getActiveEditor();
        selectedEditor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
        if (type == ERDiagram.class) {
            return this.diagram;
        }
        return super.getAdapter(type);
    }

    public int addPage(IEditorPart editor, IEditorInput input, String name) throws PartInitException {
        final int pageNo = super.addPage(editor, input);
        setPageText(pageNo, Format.null2blank(name));
        return pageNo;
    }

    public void setCurrentErmodel(ERModel model) {
        if (getPageCount() == 1) {
            final SubModelEditor diagramEditor =
                    new SubModelEditor(this.diagram, model, getEditPartFactory(), getZoomComboContributionItem(), getOutlinePage());
            try {
                addPage(diagramEditor, getEditorInput(), model.getName());
                setActiveEditor(diagramEditor);
            } catch (final PartInitException e) {
                Activator.showExceptionDialog(e);
            }
        } else {
            final SubModelEditor diagramEditor = (SubModelEditor) getEditor(1);
            setPageText(1, Format.null2blank(model.getName()));
            diagramEditor.setContents(model);
            model.getDiagram().setCurrentErmodel(model, model.getName());
            setActiveEditor(diagramEditor);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ERDiagramEditPartFactory getEditPartFactory() {
        return editPartFactory;
    }

    public ZoomComboContributionItem getZoomComboContributionItem() {
        return zoomComboContributionItem;
    }

    public ERDiagramOutlinePage getOutlinePage() {
        return outlinePage;
    }

    @Override
    public boolean isDirty() {
        return this.dirty || super.isDirty();
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

package org.dbflute.erflute.editor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Predicate;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.controller.command.category.ChangeCategoryNameCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.Validator;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
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

    private static final int NEW_PAGE_INDEX = 1;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ERDiagram diagram;
    private ERDiagramEditPartFactory editPartFactory;
    private ERDiagramOutlinePage outlinePage;
    private ZoomComboContributionItem zoomComboContributionItem;
    private ERDiagramElementStateListener elementStateListener;
    private boolean dirty;

    // ===================================================================================
    //                                                                        Create Pages
    //                                                                        ============
    @Override
    protected void createPages() {
        prepareDiagram();
        editPartFactory = new ERDiagramEditPartFactory();
        outlinePage = new ERDiagramOutlinePage(diagram);
        initMainPage();
        activatePage();
        addMouseListenerToTabFolder();
        validate();
        if (!diagram.isVirtual()) {
            final MainDiagramEditor editor = (MainDiagramEditor) getActiveEditor();
            editor.getGraphicalViewer().setContents(diagram);
        }
    }

    private void prepareDiagram() {
        try {
            final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
            setPartName(file.getName());
            final Persistent persistent = Persistent.getInstance();
            if (!file.isSynchronized(IResource.DEPTH_ONE)) {
                file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
            }
            final InputStream in = file.getContents();
            diagram = persistent.read(in);
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        if (diagram == null) {
            diagram = new ERDiagram(DBManagerFactory.getAllDBList().get(0));
            diagram.init();
        }
        diagram.setEditor(this);
    }

    private void initMainPage() {
        try {
            zoomComboContributionItem = new ZoomComboContributionItem(getSite().getPage());
            final MainDiagramEditor editor = new MainDiagramEditor(diagram, editPartFactory, zoomComboContributionItem, outlinePage);
            final int index = addPage(editor, getEditorInput()); // as main
            setPageText(index, diagram.getName());
        } catch (final PartInitException e) {
            Activator.showExceptionDialog(e);
        }
    }

    public void activatePage() {
        initVirtualPage();
        initStartPage();
    }

    public void initVirtualPage() { // called by ERDiagram
        final String diagramName = diagram.getDefaultDiagramName();
        if (diagramName != null) {
            try {
                final int pageIndex = removePage(diagramName);

                final ERVirtualDiagram vdiagram = diagram.getDiagramContents().getVirtualDiagramSet().getVdiagramByName(diagramName);
                diagram.setCurrentVirtualDiagram(vdiagram);

                final VirtualDiagramEditor vdiagramEditor =
                        new VirtualDiagramEditor(diagram, vdiagram, editPartFactory, zoomComboContributionItem, outlinePage);
                addPage(getNewPageIndexIfLessThanZero(pageIndex), vdiagramEditor, getEditorInput());
                setPageText(getNewPageIndexIfLessThanZero(pageIndex), Format.null2blank(vdiagram.getName()));
            } catch (final PartInitException e) {
                Activator.showExceptionDialog(e);
            }
        }
    }

    private int getNewPageIndexIfLessThanZero(int pageIndex) {
        return pageIndex < 0 ? NEW_PAGE_INDEX : pageIndex;
    }

    private void initStartPage() {
        final ERVirtualDiagram vdiagram = diagram.getCurrentVirtualDiagram();
        if (vdiagram != null) {
            setActivePage(getPageIndex(vdiagram));
        } else {
            setActivePage(0);
        }
        final MainDiagramEditor activeEditor = (MainDiagramEditor) getActiveEditor();
        final ZoomManager zoomManager = (ZoomManager) activeEditor.getAdapter(ZoomManager.class);
        zoomManager.setZoom(diagram.getZoom());
        activeEditor.setLocation(diagram.getX(), diagram.getY());
    }

    private void addMouseListenerToTabFolder() {
        final CTabFolder tabFolder = (CTabFolder) getContainer();
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
        final IFile file = ((IFileEditorInput) getEditorInput()).getFile();

        if (diagram.getDiagramContents().getSettings().isSuspendValidator()) {
            try {
                file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
            } catch (final CoreException e) {
                Activator.showExceptionDialog(e);
            }
        } else {
            final IWorkspaceRunnable editorMarker = new IWorkspaceRunnable() {
                @Override
                public void run(IProgressMonitor monitor) throws CoreException {
                    final MainDiagramEditor editor = (MainDiagramEditor) getActiveEditor();
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
        final List<ValidateResult> resultList = new ArrayList<>();
        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            String description = table.getDescription();
            resultList.addAll(createTodo(description, table.getLogicalName(), table));
            for (final NormalColumn column : table.getNormalColumns()) {
                description = column.getDescription();
                resultList.addAll(createTodo(description, table.getLogicalName(), table));
            }
            for (final ERIndex index : table.getIndexes()) {
                description = index.getDescription();
                resultList.addAll(createTodo(description, index.getName(), index));
            }
        }
        for (final ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet().getList()) {
            String description = view.getDescription();
            resultList.addAll(createTodo(description, view.getName(), view));
            for (final NormalColumn column : view.getNormalColumns()) {
                description = column.getDescription();
                resultList.addAll(createTodo(description, view.getLogicalName(), view));
            }
        }
        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet().getTriggerList()) {
            final String description = trigger.getDescription();
            resultList.addAll(createTodo(description, trigger.getName(), trigger));
        }
        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet().getSequenceList()) {
            final String description = sequence.getDescription();
            resultList.addAll(createTodo(description, sequence.getName(), sequence));
        }
        return resultList;
    }

    private List<ValidateResult> createTodo(String description, String location, Object object) {
        final List<ValidateResult> resultList = new ArrayList<>();
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
            final IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
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
        final ZoomManager zoomManager = (ZoomManager) getActiveEditor().getAdapter(ZoomManager.class);
        final double zoom = zoomManager.getZoom();
        diagram.setZoom(zoom);

        final MainDiagramEditor activeEditor = (MainDiagramEditor) getActiveEditor();
        final Point location = activeEditor.getLocation();
        diagram.setLocation(location.x, location.y);
        final Persistent persistent = Persistent.getInstance();
        final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
        try {
            monitor.setTaskName("create stream...");
            final InputStream source = persistent.write(diagram);
            if (!file.exists()) {
                file.create(source, true, monitor);
            } else {
                file.setContents(source, true, false, monitor);
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
        monitor.beginTask("saving...", getPageCount());
        for (int i = 0; i < getPageCount(); i++) {
            final IEditorPart editor = getEditor(i);
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
        final MainDiagramEditor selectedEditor = (MainDiagramEditor) getActiveEditor();
        selectedEditor.changeCategory();
        if (selectedEditor instanceof VirtualDiagramEditor) { // sub editor
            final VirtualDiagramEditor editor = (VirtualDiagramEditor) selectedEditor;
            diagram.setCurrentVirtualDiagram(editor.getVirtualDiagram());
            diagram.refreshVirtualDiagram();
        } else { // main editor
            selectedEditor.clearSelection();
            // 以下がないと、アウトラインコンテキストメニューのDeleteが無効になる。
            selectedEditor.prepareERDiagramOutlinePopupMenu();
            diagram.setCurrentVirtualDiagram(null);
            diagram.changeAll();
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
        this.elementStateListener = new ERDiagramElementStateListener(this);
    }

    @Override
    public void dispose() {
        elementStateListener.disposeDocumentProvider();
        super.dispose();
    }

    @Override
    protected void setInputWithNotify(IEditorInput input) {
        super.setInputWithNotify(input);
    }

    public void setCurrentCategoryPageName() {
        final Category category = getCurrentPageCategory();
        setPageText(getActivePage(), Format.null2blank(category.getName()));
    }

    private void execute(Command command) {
        final MainDiagramEditor selectedEditor = (MainDiagramEditor) getActiveEditor();
        selectedEditor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
        if (type == ERDiagram.class) {
            return diagram;
        }
        return super.getAdapter(type);
    }

    public void setCurrentERModel(ERVirtualDiagram vdiagram) {
        final int pageIndex = getPageIndex(vdiagram);
        if (0 <= pageIndex) {
            removePage(pageIndex);
        }

        final int MAX_PAGE_COUNT = 10;
        if (getPageCount() < MAX_PAGE_COUNT) {
            addVirtualDiagramPage(vdiagram);
        } else {
            removePage(MAX_PAGE_COUNT - 1);
            addVirtualDiagramPage(vdiagram);
        }
    }

    private void addVirtualDiagramPage(ERVirtualDiagram vdiagram) {
        final VirtualDiagramEditor vdiagramEditor =
                new VirtualDiagramEditor(diagram, vdiagram, getEditPartFactory(), getZoomComboContributionItem(), outlinePage);
        try {
            addPage(vdiagramEditor, getEditorInput(), vdiagram.getName());
        } catch (final PartInitException e) {
            Activator.showExceptionDialog(e);
        }
        setActiveEditor(vdiagramEditor);
    }

    private void addPage(IEditorPart editor, IEditorInput input, String name) throws PartInitException {
        super.addPage(NEW_PAGE_INDEX, editor, input);
        setPageText(NEW_PAGE_INDEX, Format.null2blank(name));
    }

    public int removePage(String diagramName) {
        final int pangeIndex = getPageIndex(diagramName);
        if (pangeIndex < 0) {
            return -1;
        }

        removePage(pangeIndex);
        return pangeIndex;
    }

    private int getPageIndex(ERVirtualDiagram vdiagram) {
        return getPageIndex(e -> e.have(vdiagram));
    }

    private int getPageIndex(String diagramName) {
        return getPageIndex(editor -> editor.getName().equals(diagramName));
    }

    private int getPageIndex(Predicate<MainDiagramEditor> test) {
        for (int i = 0; i < getPageCount(); i++) {
            final MainDiagramEditor editor = (MainDiagramEditor) getEditor(i);
            if (test.test(editor)) {
                return i;
            }
        }

        return -1;
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
        return dirty || super.isDirty();
    }

    public void setPageText(String text) {
        setPageText(1, text);
    }

    public void setPageText(ERVirtualDiagram vdiagram, String text) {
        final int pangeIndex = getPageIndex(vdiagram);
        if (pangeIndex < 0) {
            return;
        }

        setPageText(pangeIndex, text);
    }
}

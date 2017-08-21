package org.dbflute.erflute.editor.view.action.dbimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbimport.DBObject;
import org.dbflute.erflute.editor.model.dbimport.DBObjectSet;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.dbflute.erflute.editor.model.edit.CopyManager;
import org.dbflute.erflute.editor.persistent.Persistent;
import org.dbflute.erflute.editor.view.dialog.dbimport.AbstractSelectImportedObjectDialog;
import org.dbflute.erflute.editor.view.dialog.dbimport.SelectImportedObjectFromFileDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class ImportFromFileAction extends AbstractImportAction {

    public static final String ID = ImportFromFileAction.class.getName();

    private ERDiagram loadedDiagram;

    public ImportFromFileAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.import.file"), editor);
        setImageDescriptor(Activator.getImageDescriptor(ImageKey.TABLE));
    }

    protected DBObjectSet preImport() throws Exception {
        final String fileName = getLoadFilePath(getEditorPart());
        if (fileName == null) {
            return null;
        }

        final Persistent persistent = Persistent.getInstance();
        final Path path = new Path(fileName);
        InputStream in = null;
        try {
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);

            if (file == null || !file.exists()) {
                final File realFile = path.toFile();
                if (realFile == null || !realFile.exists()) {
                    Activator.showErrorDialog("error.import.file");
                    return null;
                }

                in = new FileInputStream(realFile);

            } else {
                if (!file.isSynchronized(IResource.DEPTH_ONE)) {
                    file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
                }

                in = file.getContents();
            }

            this.loadedDiagram = persistent.read(in);
        } finally {
            in.close();
        }

        return getAllObjects(loadedDiagram);
    }

    protected AbstractSelectImportedObjectDialog createSelectImportedObjectDialog(DBObjectSet dbObjectSet) {
        final ERDiagram diagram = getDiagram();

        return new SelectImportedObjectFromFileDialog(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                diagram, dbObjectSet);
    }

    protected String getLoadFilePath(IEditorPart editorPart) {
        final IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
        final FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.OPEN);
        final IProject project = file.getProject();

        fileDialog.setFilterPath(project.getLocation().toString());

        final String[] filterExtensions = getFilterExtensions();
        fileDialog.setFilterExtensions(filterExtensions);

        return fileDialog.open();
    }

    protected String[] getFilterExtensions() {
        return new String[] { "*.erm" };
    }

    private DBObjectSet getAllObjects(ERDiagram loadedDiagram) {
        final DBObjectSet dbObjects = new DBObjectSet();

        for (final ERTable table : loadedDiagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            final DBObject dbObject = new DBObject(table.getTableViewProperties().getSchema(), table.getName(), DBObject.TYPE_TABLE);
            dbObject.setModel(table);
            dbObjects.add(dbObject);
        }

        for (final ERView view : loadedDiagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            final DBObject dbObject = new DBObject(view.getTableViewProperties().getSchema(), view.getName(), DBObject.TYPE_VIEW);
            dbObject.setModel(view);
            dbObjects.add(dbObject);
        }

        for (final Sequence sequence : loadedDiagram.getDiagramContents().getSequenceSet()) {
            final DBObject dbObject = new DBObject(sequence.getSchema(), sequence.getName(), DBObject.TYPE_SEQUENCE);
            dbObject.setModel(sequence);
            dbObjects.add(dbObject);
        }

        for (final Trigger trigger : loadedDiagram.getDiagramContents().getTriggerSet()) {
            final DBObject dbObject = new DBObject(trigger.getSchema(), trigger.getName(), DBObject.TYPE_TRIGGER);
            dbObject.setModel(trigger);
            dbObjects.add(dbObject);
        }

        for (final Tablespace tablespace : loadedDiagram.getDiagramContents().getTablespaceSet()) {
            final DBObject dbObject = new DBObject(null, tablespace.getName(), DBObject.TYPE_TABLESPACE);
            dbObject.setModel(tablespace);
            dbObjects.add(dbObject);
        }

        for (final ColumnGroup columnGroup : loadedDiagram.getDiagramContents().getColumnGroupSet()) {
            final DBObject dbObject = new DBObject(null, columnGroup.getName(), DBObject.TYPE_GROUP);
            dbObject.setModel(columnGroup);
            dbObjects.add(dbObject);
        }

        return dbObjects;
    }

    protected void loadData(List<DBObject> selectedObjectList, boolean useCommentAsLogicalName, boolean mergeWord, boolean mergeGroup) {
        final Set<AbstractModel> selectedSets = new HashSet<>();
        for (final DBObject dbObject : selectedObjectList) {
            selectedSets.add(dbObject.getModel());
        }

        final DiagramContents contents = loadedDiagram.getDiagramContents();
        final ColumnGroupSet columnGroupSet = contents.getColumnGroupSet();
        for (final Iterator<ColumnGroup> iter = columnGroupSet.iterator(); iter.hasNext();) {
            final ColumnGroup columnGroup = iter.next();

            if (!selectedSets.contains(columnGroup)) {
                iter.remove();
            }
        }

        this.importedColumnGroups = columnGroupSet.getGroupList();

        final SequenceSet sequenceSet = contents.getSequenceSet();
        for (final Iterator<Sequence> iter = sequenceSet.iterator(); iter.hasNext();) {
            final Sequence sequence = iter.next();

            if (!selectedSets.contains(sequence)) {
                iter.remove();
            }
        }

        this.importedSequences = sequenceSet.getSequenceList();

        final TriggerSet triggerSet = contents.getTriggerSet();
        for (final Iterator<Trigger> iter = triggerSet.iterator(); iter.hasNext();) {
            final Trigger trigger = iter.next();

            if (!selectedSets.contains(trigger)) {
                iter.remove();
            }
        }

        this.importedTriggers = triggerSet.getTriggerList();

        final TablespaceSet tablespaceSet = contents.getTablespaceSet();
        for (final Iterator<Tablespace> iter = tablespaceSet.iterator(); iter.hasNext();) {
            final Tablespace tablespace = iter.next();

            if (!selectedSets.contains(tablespace)) {
                iter.remove();
            }
        }

        this.importedTablespaces = tablespaceSet.getTablespaceList();

        final DiagramWalkerSet nodeSet = contents.getDiagramWalkers();
        final List<DiagramWalker> nodeElementList = nodeSet.getDiagramWalkerList();
        for (final Iterator<DiagramWalker> iter = nodeElementList.iterator(); iter.hasNext();) {
            final DiagramWalker nodeElement = iter.next();

            if (!selectedSets.contains(nodeElement)) {
                iter.remove();
            }
        }

        final DiagramWalkerSet selectedNodeSet = new DiagramWalkerSet();
        final Map<UniqueWord, Word> dictionary = new HashMap<>();
        if (mergeWord) {
            for (final Word word : getDiagram().getDiagramContents().getDictionary().getWordList()) {
                dictionary.put(new UniqueWord(word), word);
            }
        }

        for (final DiagramWalker nodeElement : nodeElementList) {
            if (mergeWord) {
                if (nodeElement instanceof TableView) {
                    final TableView tableView = (TableView) nodeElement;
                    for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
                        final Word word = normalColumn.getWord();
                        if (word != null) {
                            final UniqueWord uniqueWord = new UniqueWord(word);
                            final Word replaceWord = dictionary.get(uniqueWord);

                            if (replaceWord != null) {
                                normalColumn.setWord(replaceWord);
                            }
                        }
                    }
                }
            }

            selectedNodeSet.add(nodeElement);
        }

        for (final DiagramWalker nodeElement : selectedNodeSet) {
            if (nodeElement instanceof TableView) {
                final TableView tableView = (TableView) nodeElement;
                for (final Iterator<ERColumn> iter = tableView.getColumns().iterator(); iter.hasNext();) {
                    final ERColumn column = iter.next();

                    if (column instanceof ColumnGroup) {
                        if (!importedColumnGroups.contains(column)) {
                            iter.remove();
                        }
                    }
                }
            }
        }

        if (mergeGroup) {
            final Map<String, ColumnGroup> groupMap = new HashMap<>();

            for (final ColumnGroup columnGroup : getDiagram().getDiagramContents().getColumnGroupSet()) {
                groupMap.put(columnGroup.getGroupName(), columnGroup);
            }

            for (final Iterator<ColumnGroup> iter = importedColumnGroups.iterator(); iter.hasNext();) {
                final ColumnGroup columnGroup = iter.next();
                final ColumnGroup replaceColumnGroup = groupMap.get(columnGroup.getGroupName());
                if (replaceColumnGroup != null) {
                    iter.remove();

                    for (final DiagramWalker nodeElement : selectedNodeSet) {
                        if (nodeElement instanceof TableView) {
                            final TableView tableView = (TableView) nodeElement;
                            tableView.replaceColumnGroup(columnGroup, replaceColumnGroup);
                        }
                    }
                }
            }
        }

        final CopyManager copyManager = new CopyManager();
        final DiagramWalkerSet copyList = copyManager.copyNodeElementList(selectedNodeSet);

        this.importedNodeElements = copyList.getDiagramWalkerList();
    }

    @Override
    public void execute(Event event) throws Exception {
        final DBObjectSet dbObjectSet = preImport();

        if (dbObjectSet != null) {
            final AbstractSelectImportedObjectDialog importDialog = createSelectImportedObjectDialog(dbObjectSet);
            final int result = importDialog.open();
            if (result == IDialogConstants.OK_ID) {
                loadData(importDialog.getSelectedDbObjects(), importDialog.isUseCommentAsLogicalName(),
                        importDialog.isMergeWord(), importDialog.isMergeGroup());
                showData();
            } else if (result == IDialogConstants.BACK_ID) {
                execute(event);
            }
        }
    }
}

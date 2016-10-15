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
        this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.TABLE));
    }

    protected DBObjectSet preImport() throws Exception {
        String fileName = this.getLoadFilePath(this.getEditorPart());
        if (fileName == null) {
            return null;
        }

        Persistent persistent = Persistent.getInstance();

        Path path = new Path(fileName);

        InputStream in = null;

        try {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);

            if (file == null || !file.exists()) {
                File realFile = path.toFile();
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

        return this.getAllObjects(loadedDiagram);
    }

    protected AbstractSelectImportedObjectDialog createSelectImportedObjectDialog(DBObjectSet dbObjectSet) {
        ERDiagram diagram = this.getDiagram();

        return new SelectImportedObjectFromFileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram, dbObjectSet);
    }

    protected String getLoadFilePath(IEditorPart editorPart) {

        IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();

        FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.OPEN);

        IProject project = file.getProject();

        fileDialog.setFilterPath(project.getLocation().toString());

        String[] filterExtensions = this.getFilterExtensions();
        fileDialog.setFilterExtensions(filterExtensions);

        return fileDialog.open();
    }

    protected String[] getFilterExtensions() {
        return new String[] { "*.erm" };
    }

    private DBObjectSet getAllObjects(ERDiagram loadedDiagram) {
        DBObjectSet dbObjects = new DBObjectSet();

        for (ERTable table : loadedDiagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            DBObject dbObject = new DBObject(table.getTableViewProperties().getSchema(), table.getName(), DBObject.TYPE_TABLE);
            dbObject.setModel(table);
            dbObjects.add(dbObject);
        }

        for (ERView view : loadedDiagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            DBObject dbObject = new DBObject(view.getTableViewProperties().getSchema(), view.getName(), DBObject.TYPE_VIEW);
            dbObject.setModel(view);
            dbObjects.add(dbObject);
        }

        //		for (Note note : loadedDiagram.getDiagramContents().getContents()
        //				.getNoteSet()) {
        //			DBObject dbObject = new DBObject(null, note.getName(),
        //					DBObject.TYPE_NOTE);
        //			dbObject.setModel(note);
        //			dbObjects.add(dbObject);
        //		}

        for (Sequence sequence : loadedDiagram.getDiagramContents().getSequenceSet()) {
            DBObject dbObject = new DBObject(sequence.getSchema(), sequence.getName(), DBObject.TYPE_SEQUENCE);
            dbObject.setModel(sequence);
            dbObjects.add(dbObject);
        }

        for (Trigger trigger : loadedDiagram.getDiagramContents().getTriggerSet()) {
            DBObject dbObject = new DBObject(trigger.getSchema(), trigger.getName(), DBObject.TYPE_TRIGGER);
            dbObject.setModel(trigger);
            dbObjects.add(dbObject);
        }

        for (Tablespace tablespace : loadedDiagram.getDiagramContents().getTablespaceSet()) {
            DBObject dbObject = new DBObject(null, tablespace.getName(), DBObject.TYPE_TABLESPACE);
            dbObject.setModel(tablespace);
            dbObjects.add(dbObject);
        }

        for (ColumnGroup columnGroup : loadedDiagram.getDiagramContents().getColumnGroupSet()) {
            DBObject dbObject = new DBObject(null, columnGroup.getName(), DBObject.TYPE_GROUP);
            dbObject.setModel(columnGroup);
            dbObjects.add(dbObject);
        }

        return dbObjects;
    }

    protected void loadData(List<DBObject> selectedObjectList, boolean useCommentAsLogicalName, boolean mergeWord, boolean mergeGroup) {

        Set<AbstractModel> selectedSets = new HashSet<AbstractModel>();
        for (DBObject dbObject : selectedObjectList) {
            selectedSets.add(dbObject.getModel());
        }

        DiagramContents contents = loadedDiagram.getDiagramContents();

        ColumnGroupSet columnGroupSet = contents.getColumnGroupSet();

        for (Iterator<ColumnGroup> iter = columnGroupSet.iterator(); iter.hasNext();) {
            ColumnGroup columnGroup = iter.next();

            if (!selectedSets.contains(columnGroup)) {
                iter.remove();
            }
        }

        this.importedColumnGroups = columnGroupSet.getGroupList();

        SequenceSet sequenceSet = contents.getSequenceSet();

        for (Iterator<Sequence> iter = sequenceSet.iterator(); iter.hasNext();) {
            Sequence sequence = iter.next();

            if (!selectedSets.contains(sequence)) {
                iter.remove();
            }
        }

        this.importedSequences = sequenceSet.getSequenceList();

        TriggerSet triggerSet = contents.getTriggerSet();

        for (Iterator<Trigger> iter = triggerSet.iterator(); iter.hasNext();) {
            Trigger trigger = iter.next();

            if (!selectedSets.contains(trigger)) {
                iter.remove();
            }
        }

        this.importedTriggers = triggerSet.getTriggerList();

        TablespaceSet tablespaceSet = contents.getTablespaceSet();

        for (Iterator<Tablespace> iter = tablespaceSet.iterator(); iter.hasNext();) {
            Tablespace tablespace = iter.next();

            if (!selectedSets.contains(tablespace)) {
                iter.remove();
            }
        }

        this.importedTablespaces = tablespaceSet.getTablespaceList();

        DiagramWalkerSet nodeSet = contents.getDiagramWalkers();
        List<DiagramWalker> nodeElementList = nodeSet.getDiagramWalkerList();

        for (Iterator<DiagramWalker> iter = nodeElementList.iterator(); iter.hasNext();) {
            DiagramWalker nodeElement = iter.next();

            if (!selectedSets.contains(nodeElement)) {
                iter.remove();
            }
        }

        DiagramWalkerSet selectedNodeSet = new DiagramWalkerSet();

        Map<UniqueWord, Word> dictionary = new HashMap<UniqueWord, Word>();

        if (mergeWord) {
            for (Word word : this.getDiagram().getDiagramContents().getDictionary().getWordList()) {
                dictionary.put(new UniqueWord(word), word);
            }
        }

        for (DiagramWalker nodeElement : nodeElementList) {
            if (mergeWord) {
                if (nodeElement instanceof TableView) {
                    TableView tableView = (TableView) nodeElement;

                    for (NormalColumn normalColumn : tableView.getNormalColumns()) {
                        Word word = normalColumn.getWord();
                        if (word != null) {
                            UniqueWord uniqueWord = new UniqueWord(word);
                            Word replaceWord = dictionary.get(uniqueWord);

                            if (replaceWord != null) {
                                normalColumn.setWord(replaceWord);
                            }
                        }
                    }
                }
            }

            selectedNodeSet.addDiagramWalker(nodeElement);
        }

        for (DiagramWalker nodeElement : selectedNodeSet) {
            if (nodeElement instanceof TableView) {
                TableView tableView = (TableView) nodeElement;

                for (Iterator<ERColumn> iter = tableView.getColumns().iterator(); iter.hasNext();) {
                    ERColumn column = iter.next();

                    if (column instanceof ColumnGroup) {
                        if (!this.importedColumnGroups.contains(column)) {
                            iter.remove();
                        }
                    }
                }
            }
        }

        if (mergeGroup) {
            Map<String, ColumnGroup> groupMap = new HashMap<String, ColumnGroup>();

            for (ColumnGroup columnGroup : this.getDiagram().getDiagramContents().getColumnGroupSet()) {
                groupMap.put(columnGroup.getGroupName(), columnGroup);
            }

            for (Iterator<ColumnGroup> iter = this.importedColumnGroups.iterator(); iter.hasNext();) {
                ColumnGroup columnGroup = iter.next();

                ColumnGroup replaceColumnGroup = groupMap.get(columnGroup.getGroupName());

                if (replaceColumnGroup != null) {
                    iter.remove();

                    for (DiagramWalker nodeElement : selectedNodeSet) {
                        if (nodeElement instanceof TableView) {
                            TableView tableView = (TableView) nodeElement;
                            tableView.replaceColumnGroup(columnGroup, replaceColumnGroup);
                        }
                    }
                }
            }
        }

        CopyManager copyManager = new CopyManager();
        DiagramWalkerSet copyList = copyManager.copyNodeElementList(selectedNodeSet);

        this.importedNodeElements = copyList.getDiagramWalkerList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Event event) throws Exception {
        DBObjectSet dbObjectSet = this.preImport();

        if (dbObjectSet != null) {
            AbstractSelectImportedObjectDialog importDialog = this.createSelectImportedObjectDialog(dbObjectSet);

            int result = importDialog.open();

            if (result == IDialogConstants.OK_ID) {
                this.loadData(importDialog.getSelectedDbObjects(), importDialog.isUseCommentAsLogicalName(), importDialog.isMergeWord(),
                        importDialog.isMergeGroup());
                this.showData();

            } else if (result == IDialogConstants.BACK_ID) {
                this.execute(event);
            }
        }
    }
}

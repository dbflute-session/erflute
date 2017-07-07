package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.view.dialog.relationship.RelationshipByExistingColumnsDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class CreateRelationshipByExistingColumnsCommand extends AbstractCreateRelationshipCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Relationship relationship;
    private List<NormalColumn> selectedReferredColumnList;
    private List<NormalColumn> selectedForeignKeyColumnList;
    private final List<Word> wordList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public CreateRelationshipByExistingColumnsCommand() {
        this.wordList = new ArrayList<>();
    }

    // ===================================================================================
    //                                                                      Select Columns
    //                                                                      ==============
    public boolean selectColumns() { // open dialog and get result
        if (target == null) {
            return false;
        }
        final ERTable sourceTable = (ERTable) source.getModel();
        final TableView targetTable = (TableView) target.getModel();
        final List<NormalColumn> candidateForeignKeyColumns = prepareCandidateForeignKeyColumns(targetTable);
        if (candidateForeignKeyColumns.isEmpty()) {
            Activator.showErrorDialog("error.no.candidate.of.foreign.key.exist");
            return false;
        }
        final Map<NormalColumn, List<NormalColumn>> existingRootReferredToFkColumnsMap = new HashMap<>();
        final Map<Relationship, Set<NormalColumn>> existingRelationshipToFkColumnsMap = new HashMap<>();
        prepareExistingForeignColumnsMapping(targetTable, existingRootReferredToFkColumnsMap, existingRelationshipToFkColumnsMap);
        final RelationshipByExistingColumnsDialog dialog = createDialog(sourceTable, targetTable, candidateForeignKeyColumns,
                existingRootReferredToFkColumnsMap, existingRelationshipToFkColumnsMap);
        if (dialog.open() == IDialogConstants.OK_ID) {
            selectedReferredColumnList = dialog.getSelectedReferencedColumnList();
            selectedForeignKeyColumnList = dialog.getSelectedForeignKeyColumnList();
            relationship = dialog.getNewCreatedRelationship();
            return true;
        } else {
            return false;
        }
    }

    private List<NormalColumn> prepareCandidateForeignKeyColumns(final TableView targetTable) {
        final List<NormalColumn> candidateForeignKeyColumns = new ArrayList<>();
        for (final NormalColumn column : targetTable.getNormalColumns()) {
            if (!column.isForeignKey()) {
                candidateForeignKeyColumns.add(column);
            }
        }
        return candidateForeignKeyColumns;
    }

    private void prepareExistingForeignColumnsMapping(TableView targetTable,
            Map<NormalColumn, List<NormalColumn>> existingRootReferredToFkColumnsMap,
            Map<Relationship, Set<NormalColumn>> existingRelationshipToFkColumnsMap) {
        for (final NormalColumn normalColumn : targetTable.getNormalColumns()) {
            final NormalColumn firstRootReferredColumn = normalColumn.getFirstRootReferredColumn();
            if (firstRootReferredColumn != null) {
                List<NormalColumn> foreignKeyColumnList = existingRootReferredToFkColumnsMap.get(firstRootReferredColumn);
                if (foreignKeyColumnList == null) {
                    foreignKeyColumnList = new ArrayList<>();
                    existingRootReferredToFkColumnsMap.put(firstRootReferredColumn, foreignKeyColumnList);
                }
                foreignKeyColumnList.add(normalColumn);
                for (final Relationship relationship : normalColumn.getRelationshipList()) {
                    Set<NormalColumn> foreignKeyColumnSet = existingRelationshipToFkColumnsMap.get(relationship);
                    if (foreignKeyColumnSet == null) {
                        foreignKeyColumnSet = new HashSet<>();
                        existingRelationshipToFkColumnsMap.put(relationship, foreignKeyColumnSet);
                    }
                    foreignKeyColumnSet.add(normalColumn);
                }
            }
        }
    }

    private RelationshipByExistingColumnsDialog createDialog(ERTable sourceTable, TableView targetTable,
            List<NormalColumn> candidateForeignKeyColumns, Map<NormalColumn, List<NormalColumn>> existingRootReferredToFkColumnsMap,
            Map<Relationship, Set<NormalColumn>> existingRelationshipToFkColumnsMap) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        return new RelationshipByExistingColumnsDialog(shell, sourceTable, targetTable, candidateForeignKeyColumns,
                existingRootReferredToFkColumnsMap, existingRelationshipToFkColumnsMap);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        final ERTable sourceTable = prepareSourceTable(); // foreign table e.g. MEMBER_STATUS
        final TableView targetTable = prepareTargetTable(); // local table e.g. MEMBER
        relationship.setSourceWalker(sourceTable);
        relationship.setTargetWithoutForeignKey(targetTable);
        for (int i = 0; i < selectedForeignKeyColumnList.size(); i++) {
            final NormalColumn referredColumn = selectedReferredColumnList.get(i);
            final NormalColumn foreignKeyColumn = selectedForeignKeyColumnList.get(i);
            setupAsForeignKeyColumn(sourceTable, referredColumn, foreignKeyColumn);
        }
        tellChangeToVirtualDiagram();
        targetTable.setDirty();
        ERModelUtil.refreshDiagram(sourceTable.getDiagram(), sourceTable);
    }

    private ERTable prepareSourceTable() {
        ERTable sourceTable = (ERTable) source.getModel();
        if (sourceTable instanceof ERVirtualTable) {
            sourceTable = ((ERVirtualTable) sourceTable).getRawTable();
        }
        return sourceTable;
    }

    private TableView prepareTargetTable() {
        TableView targetTable = (TableView) target.getModel();
        if (targetTable instanceof ERVirtualTable) {
            targetTable = ((ERVirtualTable) targetTable).getRawTable();
        }
        return targetTable;
    }

    private void setupAsForeignKeyColumn(ERTable sourceTable, NormalColumn referredColumn, NormalColumn foreignKeyColumn) {
        wordList.add(foreignKeyColumn.getWord());
        removeForeignKeyColumnFromDictionary(sourceTable, foreignKeyColumn);
        foreignKeyColumn.addReference(referredColumn, relationship);
        foreignKeyColumn.setWord(null);
    }

    private void removeForeignKeyColumnFromDictionary(final ERTable sourceTable, final NormalColumn foreignKeyColumn) {
        sourceTable.getDiagram().getDiagramContents().getDictionary().remove(foreignKeyColumn);
    }

    private void tellChangeToVirtualDiagram() {
        if (relationship.getSourceWalker() instanceof ERTable || relationship.getTargetWalker() instanceof ERTable) {
            final ERVirtualDiagramSet vdiagramSet = relationship.getSourceWalker().getDiagram().getDiagramContents().getVirtualDiagramSet();
            vdiagramSet.createRelationship(relationship);
        }
    }

    // ===================================================================================
    //                                                                               Undo
    //                                                                              ======
    @Override
    protected void doUndo() {
        final ERTable sourceTable = (ERTable) source.getModel();
        final ERTable targetTable = (ERTable) target.getModel();
        relationship.setSourceWalker(null);
        relationship.setTargetWithoutForeignKey(null);
        for (int i = 0; i < selectedForeignKeyColumnList.size(); i++) {
            final NormalColumn foreignKeyColumn = selectedForeignKeyColumnList.get(i);
            foreignKeyColumn.removeReference(relationship);
            foreignKeyColumn.setWord(wordList.get(i));
            sourceTable.getDiagram().getDiagramContents().getDictionary().add(foreignKeyColumn);
        }
        targetTable.setDirty();
        //ERModelUtil.refreshDiagram(sourceTable.getDiagram(), sourceTable);
    }
}

package org.insightech.er.editor.model.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relationship;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ERView;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.settings.Settings;

public class CopyManager {

    private static NodeSet copyList = new NodeSet();

    private static int numberOfCopy;

    private Map<NodeElement, NodeElement> nodeElementMap;

    public static void copy(NodeSet nodeElementList) {
        CopyManager copyManager = new CopyManager();
        copyList = copyManager.copyNodeElementList(nodeElementList);
    }

    public static NodeSet paste() {
        numberOfCopy++;
        CopyManager copyManager = new CopyManager();
        return copyManager.copyNodeElementList(copyList);
    }

    public static void clear() {
        copyList.clear();
        numberOfCopy = 0;
    }

    public static boolean canCopy() {
        if (copyList != null && !copyList.isEmpty()) {
            return true;
        }

        return false;
    }

    public static int getNumberOfCopy() {
        return numberOfCopy;
    }

    public Map<NodeElement, NodeElement> getNodeElementMap() {
        return nodeElementMap;
    }

    public NodeSet copyNodeElementList(NodeSet nodeElementList) {
        NodeSet copyList = new NodeSet();

        this.nodeElementMap = new HashMap<NodeElement, NodeElement>();
        Map<ERColumn, ERColumn> columnMap = new HashMap<ERColumn, ERColumn>();
        Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap = new HashMap<ComplexUniqueKey, ComplexUniqueKey>();

        // �I������Ă���m�[�h��EditPart�ɑ΂��ď������J��Ԃ��܂�
        for (NodeElement nodeElement : nodeElementList) {

            if (nodeElement instanceof ModelProperties) {
                // ���f���v���p�e�B�̏ꍇ�A�������܂���
                continue;
            }

            // �m�[�h�𕡐����āA�R�s�[���ɒǉ����܂�
            NodeElement cloneNodeElement = (NodeElement) nodeElement.clone();
            copyList.addNodeElement(cloneNodeElement);

            nodeElementMap.put(nodeElement, cloneNodeElement);

            if (nodeElement instanceof ERTable) {
                // �m�[�h���e�[�u���̏ꍇ
                // ��ƃC���f�b�N�X�ƕ�����ӃL�[�𕡐����܂��B
                copyColumnAndIndex((ERTable) nodeElement, (ERTable) cloneNodeElement, columnMap, complexUniqueKeyMap);

            } else if (nodeElement instanceof ERView) {
                // �m�[�h���r���[�̏ꍇ
                // ��𕡐����܂��B
                copyColumn((ERView) nodeElement, (ERView) cloneNodeElement, columnMap);
            }
        }

        // ������̃m�[�h�ɑ΂��āA�ڑ������Ȃ����܂�
        Map<ConnectionElement, ConnectionElement> connectionElementMap = new HashMap<ConnectionElement, ConnectionElement>();

        // �ڑ��𒣂�Ȃ����܂�
        for (NodeElement nodeElement : nodeElementMap.keySet()) {
            NodeElement cloneNodeElement = nodeElementMap.get(nodeElement);

            // �������m�[�h�ɓ����Ă���ڑ��𕡐���ɒ���Ȃ����܂�
            replaceIncoming(nodeElement, cloneNodeElement, connectionElementMap, nodeElementMap);
        }

        // �O���L�[�̎Q�Ƃ���蒼���܂�
        for (NodeElement nodeElement : nodeElementMap.keySet()) {

            if (nodeElement instanceof ERTable) {
                ERTable table = (ERTable) nodeElement;

                // �������e�[�u���̗�ɑ΂��ď������J��Ԃ��܂�
                for (ERColumn column : table.getColumns()) {
                    if (column instanceof NormalColumn) {
                        NormalColumn oldColumn = (NormalColumn) column;

                        // �O���L�[�̏ꍇ
                        if (oldColumn.isForeignKey()) {
                            NormalColumn newColumn = (NormalColumn) columnMap.get(oldColumn);
                            newColumn.renewRelationList();

                            for (Relationship oldRelation : oldColumn.getRelationshipList()) {

                                // �������ꂽ�֘A�̎擾
                                Relationship newRelation = (Relationship) connectionElementMap.get(oldRelation);

                                if (newRelation != null) {
                                    // �֘A����������Ă���ꍇ

                                    NormalColumn oldReferencedColumn = newRelation.getReferencedColumn();

                                    // ���j�[�N�L�[���Q�Ƃ��Ă���ꍇ
                                    if (oldReferencedColumn != null) {
                                        NormalColumn newReferencedColumn = (NormalColumn) columnMap.get(oldReferencedColumn);

                                        newRelation.setReferencedColumn(newReferencedColumn);

                                    }

                                    ComplexUniqueKey oldReferencedComplexUniqueKey = newRelation.getReferencedComplexUniqueKey();

                                    // �������j�[�N�L�[���Q�Ƃ��Ă���ꍇ
                                    if (oldReferencedComplexUniqueKey != null) {
                                        ComplexUniqueKey newReferencedComplexUniqueKey =
                                                (ComplexUniqueKey) complexUniqueKeyMap.get(oldReferencedComplexUniqueKey);
                                        if (newReferencedComplexUniqueKey != null) {
                                            newRelation.setReferencedComplexUniqueKey(newReferencedComplexUniqueKey);
                                        }
                                    }

                                    NormalColumn targetReferencedColumn = null;

                                    for (NormalColumn referencedColumn : oldColumn.getReferencedColumnList()) {
                                        if (referencedColumn.getColumnHolder() == oldRelation.getSourceTableView()) {
                                            targetReferencedColumn = referencedColumn;
                                            break;
                                        }
                                    }
                                    NormalColumn newReferencedColumn = (NormalColumn) columnMap.get(targetReferencedColumn);

                                    newColumn.removeReference(oldRelation);
                                    newColumn.addReference(newReferencedColumn, newRelation);

                                } else {
                                    // ������̗���O���L�[�ł͂Ȃ��A�ʏ�̗�ɍ�蒼���܂�
                                    newColumn.removeReference(oldRelation);
                                }
                            }
                        }
                    }
                }

            }
        }

        return copyList;
    }

    /**
     * �������m�[�h�ɓ����Ă���ڑ��𕡐���ɒ���Ȃ����܂�
     */
    private static void replaceIncoming(NodeElement from, NodeElement to, Map<ConnectionElement, ConnectionElement> connectionElementMap,
            Map<NodeElement, NodeElement> nodeElementMap) {
        List<ConnectionElement> cloneIncomings = new ArrayList<ConnectionElement>();

        // �������m�[�h�ɓ����Ă���ڑ��ɑ΂��ď������J��Ԃ��܂�
        for (ConnectionElement incoming : from.getIncomings()) {
            NodeElement oldSource = incoming.getSource();

            // �ڑ����̕������擾���܂�
            NodeElement newSource = nodeElementMap.get(oldSource);

            // �ڑ�������������Ă���ꍇ
            if (newSource != null) {

                // �ڑ��𕡐����܂��B
                ConnectionElement cloneIncoming = (ConnectionElement) incoming.clone();

                cloneIncoming.setSourceAndTarget(newSource, to);

                connectionElementMap.put(incoming, cloneIncoming);

                cloneIncomings.add(cloneIncoming);

                newSource.addOutgoing(cloneIncoming);
            }
        }

        to.setIncoming(cloneIncomings);
    }

    /**
     * ��ƃC���f�b�N�X�̏��𕡐����܂��B
     * 
     * @param from
     *            ���̃e�[�u��
     * @param to
     *            �������ꂽ�e�[�u��
     * @param columnMap
     *            �L�[�F���̗�A�l�F������̗�
     */
    private static void copyColumnAndIndex(ERTable from, ERTable to, Map<ERColumn, ERColumn> columnMap,
            Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap) {
        copyColumn(from, to, columnMap);
        copyIndex(from, to, columnMap);
        copyComplexUniqueKey(from, to, columnMap, complexUniqueKeyMap);
    }

    private static void copyColumn(TableView from, TableView to, Map<ERColumn, ERColumn> columnMap) {
        // ������̗�̈ꗗ
        List<ERColumn> cloneColumns = new ArrayList<ERColumn>();

        // ���̃e�[�u���̗�ɑ΂��āA�������J��Ԃ��܂��B
        for (ERColumn column : from.getColumns()) {

            ERColumn cloneColumn = null;

            if (column instanceof ColumnGroup) {
                // �O���[�v��̏ꍇ
                // �����͓��ɂ��܂���B
                cloneColumn = column;

            } else {
                // ���ʂ̗�̏ꍇ
                // ��𕡐����܂��B
                cloneColumn = (NormalColumn) column.clone();
            }

            cloneColumns.add(cloneColumn);

            columnMap.put(column, cloneColumn);
        }

        // ������̃e�[�u���ɁA������̗�ꗗ��ݒ肵�܂��B
        to.setColumns(cloneColumns);
    }

    private static void copyComplexUniqueKey(ERTable from, ERTable to, Map<ERColumn, ERColumn> columnMap,
            Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap) {
        List<ComplexUniqueKey> cloneComplexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

        // ���̃e�[�u���̕�����ӃL�[�ɑ΂��āA�������J��Ԃ��܂��B
        for (ComplexUniqueKey complexUniqueKey : from.getComplexUniqueKeyList()) {

            // ������ӃL�[�𕡐����܂��B
            ComplexUniqueKey cloneComplexUniqueKey = (ComplexUniqueKey) complexUniqueKey.clone();
            complexUniqueKeyMap.put(complexUniqueKey, cloneComplexUniqueKey);

            List<NormalColumn> cloneColumns = new ArrayList<NormalColumn>();

            // ������̕�����ӃL�[�̗�ɑ΂��āA�������J��Ԃ��܂��B
            for (NormalColumn column : cloneComplexUniqueKey.getColumnList()) {
                // ������̗���擾���āA������̕�����ӃL�[�̗�ꗗ�ɒǉ����܂��B
                cloneColumns.add((NormalColumn) columnMap.get(column));
            }

            // ������̕�����ӃL�[�ɁA������̕�����ӃL�[�̗�ꗗ��ݒ肵�܂��B
            cloneComplexUniqueKey.setColumnList(cloneColumns);

            cloneComplexUniqueKeyList.add(cloneComplexUniqueKey);
        }

        // ������̃e�[�u���ɁA������̃C���f�b�N�X�ꗗ��ݒ肵�܂��B
        to.setComplexUniqueKeyList(cloneComplexUniqueKeyList);
    }

    private static void copyIndex(ERTable from, ERTable to, Map<ERColumn, ERColumn> columnMap) {
        List<ERIndex> cloneIndexes = new ArrayList<ERIndex>();

        // ���̃e�[�u���̃C���f�b�N�X�ɑ΂��āA�������J��Ԃ��܂��B
        for (ERIndex index : from.getIndexes()) {

            // �C���f�b�N�X�𕡐����܂��B
            ERIndex cloneIndex = (ERIndex) index.clone();

            List<NormalColumn> cloneIndexColumns = new ArrayList<NormalColumn>();

            // ������̃C���f�b�N�X�̗�ɑ΂��āA�������J��Ԃ��܂��B
            for (NormalColumn indexColumn : cloneIndex.getColumns()) {
                // ������̗���擾���āA������̃C���f�b�N�X��ꗗ�ɒǉ����܂��B
                cloneIndexColumns.add((NormalColumn) columnMap.get(indexColumn));
            }

            // ������̃C���f�b�N�X�ɁA������̃C���f�b�N�X��ꗗ��ݒ肵�܂��B
            cloneIndex.setColumns(cloneIndexColumns);

            cloneIndexes.add(cloneIndex);
        }

        // ������̃e�[�u���ɁA������̃C���f�b�N�X�ꗗ��ݒ肵�܂��B
        to.setIndexes(cloneIndexes);
    }

    public DiagramContents copy(DiagramContents originalDiagramContents) {
        DiagramContents copyDiagramContents = new DiagramContents();

        copyDiagramContents.setContents(this.copyNodeElementList(originalDiagramContents.getContents()));
        Map<NodeElement, NodeElement> nodeElementMap = this.getNodeElementMap();

        Settings settings = (Settings) originalDiagramContents.getSettings().clone();
        this.setSettings(nodeElementMap, settings);
        copyDiagramContents.setSettings(settings);

        this.setColumnGroup(copyDiagramContents, originalDiagramContents);

        copyDiagramContents.setSequenceSet(originalDiagramContents.getSequenceSet().clone());
        copyDiagramContents.setTriggerSet(originalDiagramContents.getTriggerSet().clone());

        this.setWord(copyDiagramContents, originalDiagramContents);
        this.setTablespace(copyDiagramContents, originalDiagramContents);

        return copyDiagramContents;
    }

    private void setSettings(Map<NodeElement, NodeElement> nodeElementMap, Settings settings) {
        for (Category category : settings.getCategorySetting().getAllCategories()) {
            List<NodeElement> newContents = new ArrayList<NodeElement>();
            for (NodeElement nodeElement : category.getContents()) {
                newContents.add(nodeElementMap.get(nodeElement));
            }

            category.setContents(newContents);
        }
    }

    private void setColumnGroup(DiagramContents copyDiagramContents, DiagramContents originalDiagramContents) {

        Map<ColumnGroup, ColumnGroup> columnGroupMap = new HashMap<ColumnGroup, ColumnGroup>();

        for (ColumnGroup columnGroup : originalDiagramContents.getGroups()) {
            ColumnGroup newColumnGroup = (ColumnGroup) columnGroup.clone();
            copyDiagramContents.getGroups().add(newColumnGroup);

            columnGroupMap.put(columnGroup, newColumnGroup);
        }

        for (TableView tableView : copyDiagramContents.getContents().getTableViewList()) {
            List<ERColumn> newColumns = new ArrayList<ERColumn>();

            for (ERColumn column : tableView.getColumns()) {
                if (column instanceof ColumnGroup) {
                    newColumns.add(columnGroupMap.get((ColumnGroup) column));

                } else {
                    newColumns.add(column);
                }
            }

            tableView.setColumns(newColumns);
        }
    }

    private void setWord(DiagramContents copyDiagramContents, DiagramContents originalDiagramContents) {

        Map<Word, Word> wordMap = new HashMap<Word, Word>();
        Dictionary copyDictionary = copyDiagramContents.getDictionary();

        for (Word word : originalDiagramContents.getDictionary().getWordList()) {
            Word newWord = (Word) word.clone();
            wordMap.put(word, newWord);
        }

        for (TableView tableView : copyDiagramContents.getContents().getTableViewList()) {
            for (NormalColumn normalColumn : tableView.getNormalColumns()) {
                Word oldWord = normalColumn.getWord();
                if (oldWord != null) {
                    Word newWord = wordMap.get(oldWord);
                    normalColumn.setWord(newWord);

                    copyDictionary.add(normalColumn);
                }
            }
        }

        for (ColumnGroup columnGroup : copyDiagramContents.getGroups()) {
            for (NormalColumn normalColumn : columnGroup.getColumns()) {
                Word oldWord = normalColumn.getWord();
                if (oldWord != null) {
                    Word newWord = wordMap.get(oldWord);
                    normalColumn.setWord(newWord);

                    copyDictionary.add(normalColumn);
                }
            }
        }

    }

    private void setTablespace(DiagramContents copyDiagramContents, DiagramContents originalDiagramContents) {

        Map<Tablespace, Tablespace> tablespaceMap = new HashMap<Tablespace, Tablespace>();
        TablespaceSet copyTablespaceSet = copyDiagramContents.getTablespaceSet();

        for (Tablespace tablespace : originalDiagramContents.getTablespaceSet()) {
            Tablespace newTablespace = (Tablespace) tablespace.clone();
            tablespaceMap.put(tablespace, newTablespace);

            copyTablespaceSet.addTablespace(newTablespace);
        }

        for (TableView tableView : copyDiagramContents.getContents().getTableViewList()) {
            TableViewProperties tableProperties = tableView.getTableViewProperties();
            Tablespace oldTablespace = tableProperties.getTableSpace();

            Tablespace newTablespace = tablespaceMap.get(oldTablespace);
            tableProperties.setTableSpace(newTablespace);
        }

        TableViewProperties defaultTableProperties = copyDiagramContents.getSettings().getTableViewProperties();
        Tablespace oldDefaultTablespace = defaultTableProperties.getTableSpace();

        Tablespace newDefaultTablespace = tablespaceMap.get(oldDefaultTablespace);
        defaultTableProperties.setTableSpace(newDefaultTablespace);
    }
}

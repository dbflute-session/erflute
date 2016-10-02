package org.insightech.er.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relationship;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.settings.Environment;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class LoadContext {

    public final Map<String, NodeElement> nodeElementMap;
    public final Map<String, NormalColumn> columnMap;
    public final Map<String, ComplexUniqueKey> complexUniqueKeyMap;
    public final Map<NormalColumn, String[]> columnRelationMap;
    public final Map<NormalColumn, String[]> columnReferencedColumnMap;
    public final Map<String, ColumnGroup> columnGroupMap;
    public final Map<String, ERModel> ermodelMap;
    public final Map<Relationship, String> referencedColumnMap;
    public final Map<Relationship, String> referencedComplexUniqueKeyMap;
    public final Map<ConnectionElement, String> connectionSourceMap;
    public final Map<ConnectionElement, String> connectionTargetMap;
    public final Map<String, ConnectionElement> connectionMap;
    public final Map<String, Word> wordMap;
    public final Map<String, Tablespace> tablespaceMap;
    public final Map<String, Environment> environmentMap;
    public final Map<UniqueWord, Word> uniqueWordMap;
    public final Dictionary dictionary;

    public LoadContext(Dictionary dictionary) {
        this.nodeElementMap = new HashMap<String, NodeElement>();
        this.columnMap = new HashMap<String, NormalColumn>();
        this.complexUniqueKeyMap = new HashMap<String, ComplexUniqueKey>();
        this.columnRelationMap = new HashMap<NormalColumn, String[]>();
        this.columnReferencedColumnMap = new HashMap<NormalColumn, String[]>();
        this.ermodelMap = new HashMap<String, ERModel>();
        this.columnGroupMap = new HashMap<String, ColumnGroup>();
        this.referencedColumnMap = new HashMap<Relationship, String>();
        this.referencedComplexUniqueKeyMap = new HashMap<Relationship, String>();
        this.connectionMap = new HashMap<String, ConnectionElement>();
        this.connectionSourceMap = new HashMap<ConnectionElement, String>();
        this.connectionTargetMap = new HashMap<ConnectionElement, String>();
        this.wordMap = new HashMap<String, Word>();
        this.tablespaceMap = new HashMap<String, Tablespace>();
        this.environmentMap = new HashMap<String, Environment>();
        this.uniqueWordMap = new HashMap<UniqueWord, Word>();

        this.dictionary = dictionary;
        this.dictionary.clear();
    }

    public void resolve() {
        for (final ConnectionElement connection : this.connectionSourceMap.keySet()) {
            final String id = this.connectionSourceMap.get(connection);
            final NodeElement nodeElement = this.nodeElementMap.get(id);
            if (nodeElement == null) {
                System.out.println("error");
            }
            connection.setSource(nodeElement);
        }

        for (final ConnectionElement connection : this.connectionTargetMap.keySet()) {
            final String id = this.connectionTargetMap.get(connection);
            final NodeElement nodeElement = this.nodeElementMap.get(id);
            if (nodeElement == null) {
                System.out.println("error");
            }
            connection.setTarget(nodeElement);
        }

        for (final Relationship relation : this.referencedColumnMap.keySet()) {
            final String id = this.referencedColumnMap.get(relation);
            final NormalColumn column = this.columnMap.get(id);
            if (column == null) {
                System.out.println("error");
            }
            relation.setReferencedColumn(column);
        }

        for (final Relationship relation : this.referencedComplexUniqueKeyMap.keySet()) {
            final String id = this.referencedComplexUniqueKeyMap.get(relation);
            final ComplexUniqueKey complexUniqueKey = this.complexUniqueKeyMap.get(id);
            relation.setReferencedComplexUniqueKey(complexUniqueKey);
        }

        final Set<NormalColumn> foreignKeyColumnSet = this.columnReferencedColumnMap.keySet();
        while (!foreignKeyColumnSet.isEmpty()) {
            final NormalColumn foreignKeyColumn = foreignKeyColumnSet.iterator().next();
            reduce(foreignKeyColumnSet, foreignKeyColumn);
        }
    }

    private void reduce(Set<NormalColumn> foreignKeyColumnSet, NormalColumn foreignKeyColumn) {
        final String[] referencedColumnIds = this.columnReferencedColumnMap.get(foreignKeyColumn);
        final String[] relationIds = this.columnRelationMap.get(foreignKeyColumn);
        final List<NormalColumn> referencedColumnList = new ArrayList<NormalColumn>();
        if (referencedColumnIds != null) {
            for (final String referencedColumnId : referencedColumnIds) {
                try {
                    Integer.parseInt(referencedColumnId);
                    final NormalColumn referencedColumn = this.columnMap.get(referencedColumnId);
                    referencedColumnList.add(referencedColumn);
                    if (foreignKeyColumnSet.contains(referencedColumn)) {
                        reduce(foreignKeyColumnSet, referencedColumn);
                    }
                } catch (final NumberFormatException e) {}
            }
        }

        if (relationIds != null) {
            for (final String relationId : relationIds) {
                try {
                    Integer.parseInt(relationId);
                    final Relationship relation = (Relationship) this.connectionMap.get(relationId);
                    for (final NormalColumn referencedColumn : referencedColumnList) {
                        if (referencedColumn.getColumnHolder() == relation.getSourceTableView()) {
                            foreignKeyColumn.addReference(referencedColumn, relation);
                            break;
                        }
                    }
                } catch (final NumberFormatException e) {}
            }
        }
        foreignKeyColumnSet.remove(foreignKeyColumn);
    }
}

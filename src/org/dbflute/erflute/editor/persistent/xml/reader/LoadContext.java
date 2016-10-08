package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.settings.Environment;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class LoadContext {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    public final Map<String, NodeElement> nodeElementMap; // ID = node
    public final Map<String, NormalColumn> columnMap; // ID = column
    public final Map<String, ComplexUniqueKey> complexUniqueKeyMap;
    public final Map<NormalColumn, String[]> columnRelationMap;
    public final Map<NormalColumn, String[]> columnReferencedColumnMap;
    public final Map<String, ColumnGroup> columnGroupMap;
    public final Map<String, ERModel> ermodelMap;
    public final Map<Relationship, String> referencedColumnMap; // relationship = column ID
    public final Map<Relationship, String> referencedComplexUniqueKeyMap;
    public final Map<ConnectionElement, String> connectionSourceMap;
    public final Map<ConnectionElement, String> connectionTargetMap;
    public final Map<String, ConnectionElement> connectionMap;
    public final Map<String, Tablespace> tablespaceMap;
    public final Map<String, Environment> environmentMap;
    public final Dictionary dictionary;
    public final Map<String, Word> wordMap;
    public final Map<UniqueWord, Word> uniqueWordMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
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
        this.tablespaceMap = new HashMap<String, Tablespace>();
        this.environmentMap = new HashMap<String, Environment>();
        this.dictionary = dictionary;
        this.dictionary.clear();
        this.wordMap = new HashMap<String, Word>();
        this.uniqueWordMap = new HashMap<UniqueWord, Word>();
    }

    // ===================================================================================
    //                                                                  Resolve ID Mapping
    //                                                                  ==================
    public void resolve() { // called by reader
        for (final ConnectionElement connection : connectionSourceMap.keySet()) {
            final String id = connectionSourceMap.get(connection);
            final NodeElement nodeElement = nodeElementMap.get(id);
            if (nodeElement == null) { // what should I do? by jflute
                System.out.println("*error, Not found the source ID: " + id + ", connection=" + connection + ", existingKeys="
                        + nodeElementMap.keySet());
            }
            connection.setSource(nodeElement);
        }
        for (final ConnectionElement connection : connectionTargetMap.keySet()) {
            final String id = connectionTargetMap.get(connection);
            final NodeElement nodeElement = nodeElementMap.get(id);
            if (nodeElement == null) {
                System.out.println("*error, Not found the target ID: " + id + ", connection=" + connection + ", existingKeys="
                        + nodeElementMap.keySet());
            }
            connection.setTarget(nodeElement);
        }
        for (final Relationship relation : referencedColumnMap.keySet()) {
            final String id = referencedColumnMap.get(relation);
            if (id != null) { // null allowed when migration from ERMaster...?
                final NormalColumn column = columnMap.get(id);
                if (column == null) {
                    System.out.println("*error, Not found the column ID: " + id + ", relation=" + relation + ", existingKeys="
                            + columnMap.keySet());
                }
                relation.setReferencedColumn(column);
            }
        }
        for (final Relationship relation : referencedComplexUniqueKeyMap.keySet()) {
            final String id = referencedComplexUniqueKeyMap.get(relation);
            final ComplexUniqueKey complexUniqueKey = complexUniqueKeyMap.get(id);
            relation.setReferencedComplexUniqueKey(complexUniqueKey);
        }
        final Set<NormalColumn> foreignKeyColumnSet = columnReferencedColumnMap.keySet();
        while (!foreignKeyColumnSet.isEmpty()) {
            final NormalColumn foreignKeyColumn = foreignKeyColumnSet.iterator().next();
            reduce(foreignKeyColumnSet, foreignKeyColumn);
        }
    }

    private void reduce(Set<NormalColumn> foreignKeyColumnSet, NormalColumn foreignKeyColumn) {
        final String[] referencedColumnIds = columnReferencedColumnMap.get(foreignKeyColumn);
        final String[] relationIds = columnRelationMap.get(foreignKeyColumn);
        final List<NormalColumn> referencedColumnList = new ArrayList<NormalColumn>();
        if (referencedColumnIds != null) {
            for (final String referencedColumnId : referencedColumnIds) {
                final NormalColumn referencedColumn = columnMap.get(referencedColumnId);
                referencedColumnList.add(referencedColumn);
                if (foreignKeyColumnSet.contains(referencedColumn)) {
                    reduce(foreignKeyColumnSet, referencedColumn);
                }
            }
        }
        if (relationIds != null) {
            for (final String relationId : relationIds) {
                final Relationship relation = (Relationship) connectionMap.get(relationId);
                for (final NormalColumn referencedColumn : referencedColumnList) {
                    if (referencedColumn.getColumnHolder() == relation.getSourceTableView()) {
                        foreignKeyColumn.addReference(referencedColumn, relation);
                        break;
                    }
                }
            }
        }
        foreignKeyColumnSet.remove(foreignKeyColumn);
    }
}

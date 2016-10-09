package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import org.dbflute.erflute.editor.persistent.xml.reader.exception.PersistentXmlReadingFailureException;

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
        this.nodeElementMap = new LinkedHashMap<String, NodeElement>();
        this.columnMap = new LinkedHashMap<String, NormalColumn>();
        this.complexUniqueKeyMap = new LinkedHashMap<String, ComplexUniqueKey>();
        this.columnRelationMap = new LinkedHashMap<NormalColumn, String[]>();
        this.columnReferencedColumnMap = new LinkedHashMap<NormalColumn, String[]>();
        this.ermodelMap = new LinkedHashMap<String, ERModel>();
        this.columnGroupMap = new LinkedHashMap<String, ColumnGroup>();
        this.referencedColumnMap = new LinkedHashMap<Relationship, String>();
        this.referencedComplexUniqueKeyMap = new LinkedHashMap<Relationship, String>();
        this.connectionMap = new LinkedHashMap<String, ConnectionElement>();
        this.connectionSourceMap = new LinkedHashMap<ConnectionElement, String>();
        this.connectionTargetMap = new LinkedHashMap<ConnectionElement, String>();
        this.tablespaceMap = new LinkedHashMap<String, Tablespace>();
        this.environmentMap = new LinkedHashMap<String, Environment>();
        this.dictionary = dictionary;
        this.dictionary.clear();
        this.wordMap = new LinkedHashMap<String, Word>();
        this.uniqueWordMap = new LinkedHashMap<UniqueWord, Word>();
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
                if (referencedColumn == null) {
                    throwReferencedColumnNotFoundException(foreignKeyColumn, referencedColumnId);
                }
                referencedColumnList.add(referencedColumn);
                if (foreignKeyColumnSet.contains(referencedColumn)) {
                    reduce(foreignKeyColumnSet, referencedColumn);
                }
            }
        }
        if (relationIds != null) {
            for (final String relationId : relationIds) {
                final Relationship relationship = (Relationship) connectionMap.get(relationId);
                if (relationship == null) {
                    throwRelationshipNotFoundException(foreignKeyColumn, relationId);
                }
                for (final NormalColumn referencedColumn : referencedColumnList) {
                    if (referencedColumn.getColumnHolder() == relationship.getSourceTableView()) {
                        foreignKeyColumn.addReference(referencedColumn, relationship);
                        break;
                    }
                }
            }
        }
        foreignKeyColumnSet.remove(foreignKeyColumn);
    }

    private void throwReferencedColumnNotFoundException(NormalColumn foreignKeyColumn, final String referencedColumnId) {
        final Set<String> keys = columnMap.keySet();
        final String msg = "Not found the referencedColumn: id=" + referencedColumnId + ", fk=" + foreignKeyColumn + ", existing=" + keys;
        throw new PersistentXmlReadingFailureException(msg);
    }

    private void throwRelationshipNotFoundException(NormalColumn foreignKeyColumn, final String relationId) {
        final Set<String> keys = connectionMap.keySet();
        final String msg = "Not found the relationship: id=" + relationId + ", fk=" + foreignKeyColumn + ", existing=" + keys;
        throw new PersistentXmlReadingFailureException(msg);
    }
}

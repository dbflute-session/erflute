package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
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
    public final Map<String, DiagramWalker> walkerMap; // ID = node
    public final Map<String, NormalColumn> columnMap; // ID = column
    public final Map<String, ComplexUniqueKey> complexUniqueKeyMap;
    public final Map<NormalColumn, String[]> columnRelationshipMap;
    public final Map<NormalColumn, String[]> columnReferredColumnMap;
    public final Map<String, ColumnGroup> columnGroupMap;
    public final Map<String, ERVirtualDiagram> virtualDiagramMap;
    public final Map<Relationship, String> referredComplexUniqueKeyMap; // relationship = unique key ID
    public final Map<Relationship, String> referredSimpleUniqueColumnMap; // relationship = unique column ID
    public final Map<WalkerConnection, String> connectionSourceMap;
    public final Map<WalkerConnection, String> connectionTargetMap;
    public final Map<String, WalkerConnection> connectionMap;
    public final Map<String, Tablespace> tablespaceMap;
    public final Map<String, Environment> environmentMap;
    public final Dictionary dictionary;
    public final Map<String, Word> wordMap;
    public final Map<UniqueWord, Word> uniqueWordMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public LoadContext(Dictionary dictionary) {
        this.walkerMap = new LinkedHashMap<String, DiagramWalker>();
        this.columnMap = new LinkedHashMap<String, NormalColumn>();
        this.complexUniqueKeyMap = new LinkedHashMap<String, ComplexUniqueKey>();
        this.columnRelationshipMap = new LinkedHashMap<NormalColumn, String[]>();
        this.columnReferredColumnMap = new LinkedHashMap<NormalColumn, String[]>();
        this.virtualDiagramMap = new LinkedHashMap<String, ERVirtualDiagram>();
        this.columnGroupMap = new LinkedHashMap<String, ColumnGroup>();
        this.referredSimpleUniqueColumnMap = new LinkedHashMap<Relationship, String>();
        this.referredComplexUniqueKeyMap = new LinkedHashMap<Relationship, String>();
        this.connectionMap = new LinkedHashMap<String, WalkerConnection>();
        this.connectionSourceMap = new LinkedHashMap<WalkerConnection, String>();
        this.connectionTargetMap = new LinkedHashMap<WalkerConnection, String>();
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
        for (final Entry<WalkerConnection, String> entry : connectionSourceMap.entrySet()) {
            final WalkerConnection connection = entry.getKey();
            final String id = entry.getValue();
            DiagramWalker sourceWalker = !"$$owner$$".equals(id) ? walkerMap.get(id) : null;
            if (sourceWalker == null) { // owner or not-found
                final DiagramWalker ownerWalker = connection.getOwnerWalker();
                if (ownerWalker != null) { // e.g. walker note
                    sourceWalker = ownerWalker;
                } else {
                    System.out.println("*error, Not found the source ID: " + id + ", connection=" + connection + ", existingKeys="
                            + walkerMap.keySet());
                }
            }
            connection.setSourceWalker(sourceWalker);
        }
        for (final Entry<WalkerConnection, String> entry : connectionTargetMap.entrySet()) {
            final WalkerConnection connection = entry.getKey();
            final String id = entry.getValue();
            final DiagramWalker walker = walkerMap.get(id);
            if (walker == null) {
                System.out.println("*error, Not found the target ID: " + id + ", connection=" + connection + ", existingKeys="
                        + walkerMap.keySet());
            }
            connection.setTargetWalker(walker);
        }
        doResolveRelationship();
    }

    private void doResolveRelationship() {
        for (final Relationship relationship : referredSimpleUniqueColumnMap.keySet()) {
            final String uniqueColumnId = referredSimpleUniqueColumnMap.get(relationship);
            if (uniqueColumnId != null && !uniqueColumnId.equals("null")) { // null allowed when migration from ERMaster...?
                final NormalColumn column = columnMap.get(uniqueColumnId);
                if (column == null) {
                    System.out.println("*error, Not found the column ID: " + uniqueColumnId + ", relationship=" + relationship
                            + ", existingKeys=" + columnMap.keySet());
                }
                relationship.setReferredSimpleUniqueColumn(column);
            }
        }
        for (final Relationship relationship : referredComplexUniqueKeyMap.keySet()) {
            final String uniqueKeyId = referredComplexUniqueKeyMap.get(relationship);
            final ComplexUniqueKey complexUniqueKey = complexUniqueKeyMap.get(uniqueKeyId);
            relationship.setReferencedComplexUniqueKey(complexUniqueKey);
        }
        final Set<NormalColumn> foreignKeyColumnSet = columnReferredColumnMap.keySet();
        while (!foreignKeyColumnSet.isEmpty()) {
            final NormalColumn foreignKeyColumn = foreignKeyColumnSet.iterator().next();
            reduceRelationship(foreignKeyColumnSet, foreignKeyColumn);
        }
    }

    private void reduceRelationship(Set<NormalColumn> foreignKeyColumnSet, NormalColumn foreignKeyColumn) {
        final String[] referredColumnIds = columnReferredColumnMap.get(foreignKeyColumn);
        final String[] relationshipIds = columnRelationshipMap.get(foreignKeyColumn);
        final List<NormalColumn> referredColumnList = new ArrayList<NormalColumn>();
        if (referredColumnIds != null) {
            for (final String referredColumnId : referredColumnIds) {
                final NormalColumn referredColumn = columnMap.get(referredColumnId);
                if (referredColumn == null) {
                    throwReferredColumnNotFoundException(foreignKeyColumn, referredColumnId);
                }
                referredColumnList.add(referredColumn);
                if (foreignKeyColumnSet.contains(referredColumn)) {
                    reduceRelationship(foreignKeyColumnSet, referredColumn);
                }
            }
        }
        if (relationshipIds != null) {
            for (final String relationshipId : relationshipIds) {
                final Relationship relationship = (Relationship) connectionMap.get(relationshipId);
                if (relationship == null) {
                    throwRelationshipNotFoundException(foreignKeyColumn, relationshipId);
                }
                for (final NormalColumn referredColumn : referredColumnList) {
                    if (referredColumn.getColumnHolder() == relationship.getSourceTableView()) {
                        foreignKeyColumn.addReference(referredColumn, relationship);
                        break;
                    }
                }
            }
        }
        foreignKeyColumnSet.remove(foreignKeyColumn);
    }

    private void throwReferredColumnNotFoundException(NormalColumn foreignKeyColumn, final String referredColumnId) {
        final Set<String> keys = columnMap.keySet();
        final String msg = "Not found the referencedColumn: id=" + referredColumnId + ", fk=" + foreignKeyColumn + ", existing=" + keys;
        throw new PersistentXmlReadingFailureException(msg);
    }

    private void throwRelationshipNotFoundException(NormalColumn foreignKeyColumn, final String relationshipId) {
        final Set<String> keys = connectionMap.keySet();
        final String msg = "Not found the relationship: id=" + relationshipId + ", fk=" + foreignKeyColumn + ", existing=" + keys;
        throw new PersistentXmlReadingFailureException(msg);
    }
}

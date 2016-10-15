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
    public final Map<NormalColumn, String[]> columnRelationMap;
    public final Map<NormalColumn, String[]> columnReferencedColumnMap;
    public final Map<String, ColumnGroup> columnGroupMap;
    public final Map<String, ERVirtualDiagram> virtualDiagramMap;
    public final Map<Relationship, String> referencedColumnMap; // relationship = column ID
    public final Map<Relationship, String> referencedComplexUniqueKeyMap;
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
        this.columnRelationMap = new LinkedHashMap<NormalColumn, String[]>();
        this.columnReferencedColumnMap = new LinkedHashMap<NormalColumn, String[]>();
        this.virtualDiagramMap = new LinkedHashMap<String, ERVirtualDiagram>();
        this.columnGroupMap = new LinkedHashMap<String, ColumnGroup>();
        this.referencedColumnMap = new LinkedHashMap<Relationship, String>();
        this.referencedComplexUniqueKeyMap = new LinkedHashMap<Relationship, String>();
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
        for (final Relationship relation : referencedColumnMap.keySet()) {
            final String id = referencedColumnMap.get(relation);
            if (id != null && !id.equals("null")) { // null allowed when migration from ERMaster...?
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
            reduceRelationship(foreignKeyColumnSet, foreignKeyColumn);
        }
    }

    private void reduceRelationship(Set<NormalColumn> foreignKeyColumnSet, NormalColumn foreignKeyColumn) {
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
                    reduceRelationship(foreignKeyColumnSet, referencedColumn);
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

package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadDiagramWalkerLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadDiagramWalkerLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                              Walker
    //                                                                              ======
    public void loadWalker(DiagramWalker walker, Element element, LoadContext context) {
        String id = getStringValue(element, "id");
        if (Srl.is_Null_or_TrimmedEmpty(id)) {
            if (walker instanceof TableView) {
                id = ((TableView) walker).buildTableViewId(); // #for_erflute
            } else {
                id = walker.getClass().getSimpleName() + "_" + walker.hashCode();
            }
        }
        assistLogic.loadLocation(walker, element);
        assistLogic.loadColor(walker, element);
        assistLogic.loadFont(walker, element);
        context.walkerMap.put(id, walker);
        loadConnections(walker, element, context);
    }

    private void loadConnections(DiagramWalker walker, Element parent, LoadContext context) {
        final Element element = getElement(parent, "connections");
        if (element != null) {
            final NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                final Element connectionElement = (Element) nodeList.item(i);
                if ("relation".equals(connectionElement.getTagName())) { // migration from ERMaster
                    loadRelationship(walker, connectionElement, context);
                } else if ("relationship".equals(connectionElement.getTagName())) { // #for_erflute rename to relationship
                    loadRelationship(walker, connectionElement, context);
                } else if ("comment_connection".equals(connectionElement.getTagName())) {
                    loadCommentConnection(walker, connectionElement, context);
                }
            }
        }
    }

    private void loadRelationship(DiagramWalker walker, Element element, LoadContext context) {
        final boolean referenceForPK = getBooleanValue(element, "reference_for_pk");
        final Relationship relationship = new Relationship(referenceForPK, null, null);
        relationship.setForeignKeyName(getStringValue(element, "name"));
        relationship.setChildCardinality(getStringValue(element, "child_cardinality"));
        relationship.setParentCardinality(getStringValue(element, "parent_cardinality"));
        relationship.setOnDeleteAction(getStringValue(element, "on_delete_action", "NO ACTION"));
        relationship.setOnUpdateAction(getStringValue(element, "on_update_action", "NO ACTION"));
        relationship.setSourceLocationp(getIntValue(element, "source_xp", -1), getIntValue(element, "source_yp", -1));
        relationship.setTargetLocationp(getIntValue(element, "target_xp", -1), getIntValue(element, "target_yp", -1));
        String referredCompoundUniqueKeyId = getStringValue(element, "referenced_complex_unique_key");
        if (Srl.is_Null_or_Empty(referredCompoundUniqueKeyId)) {
            referredCompoundUniqueKeyId = getStringValue(element, "referred_compound_unique_key"); // #for_erflute rename
        }
        if (referredCompoundUniqueKeyId != null) {
            context.referredCompoundUniqueKeyMap.put(relationship, referredCompoundUniqueKeyId);
        }
        String referredSimpleUniqueColumnId = getStringValue(element, "referenced_column"); // simple unique key
        if (Srl.is_Null_or_Empty(referredSimpleUniqueColumnId)) {
            referredSimpleUniqueColumnId = getStringValue(element, "referred_simple_unique_column"); // #for_erflute rename
        }
        if (referredSimpleUniqueColumnId != null) {
            context.referredSimpleUniqueColumnMap.put(relationship, referredSimpleUniqueColumnId);
        }
        loadConnectionElement(walker, element, context, relationship);
    }

    private void loadCommentConnection(DiagramWalker walker, Element element, LoadContext context) {
        final CommentConnection connection = new CommentConnection();
        loadConnectionElement(walker, element, context, connection);
    }

    private void loadConnectionElement(DiagramWalker walker, Element element, LoadContext context, WalkerConnection connection) {
        connection.setOwnerWalker(walker);
        final String source = getStringValue(element, "source");
        final String target = getStringValue(element, "target");
        String connectionId = getStringValue(element, "id");
        if (Srl.is_Null_or_TrimmedEmpty(connectionId)) {
            if (walker instanceof TableView && connection instanceof Relationship) { // table determination just in case
                connectionId = buildRelationshipId((TableView) walker, element, context, (Relationship) connection);
            } else {
                connectionId = "#error:unknownId_for_" + target + "_to_" + source;
            }
        }
        context.connectionMap.put(connectionId, connection);
        context.connectionSourceMap.put(connection, source);
        context.connectionTargetMap.put(connection, target);
        final NodeList nodeList = element.getElementsByTagName("bendpoint");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element bendPointElement = (Element) nodeList.item(i);
            final Bendpoint bendpoint = new Bendpoint(getIntValue(bendPointElement, "x"), this.getIntValue(bendPointElement, "y"));
            bendpoint.setRelative(getBooleanValue(bendPointElement, "relative"));
            connection.addBendpoint(i, bendpoint);
        }
    }

    private String buildRelationshipId(TableView targetTable, Element element, LoadContext context, Relationship relationship) {
        final NodeList columnsNodeList = element.getElementsByTagName("fk_columns");
        final List<String> physicalColumnNameList = new ArrayList<>();
        for (int i = 0; i < columnsNodeList.getLength(); i++) {
            final Element columnsElement = (Element) columnsNodeList.item(i);
            final NodeList columnNodeList = columnsElement.getElementsByTagName("fk_column");
            for (int j = 0; j < columnNodeList.getLength(); j++) {
                final Element columnElement = (Element) columnNodeList.item(j);
                final String column = getStringValue(columnElement, "fk_column_name");
                physicalColumnNameList.add(column);
            }
        }
        return relationship.buildRelationshipId(targetTable, physicalColumnNameList); // #for_erflute
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private String getStringValue(Element element, String tagname, String defaultValue) {
        return assistLogic.getStringValue(element, tagname, defaultValue);
    }

    private boolean getBooleanValue(Element element, String tagname) {
        return assistLogic.getBooleanValue(element, tagname);
    }

    private int getIntValue(Element element, String tagname) {
        return assistLogic.getIntValue(element, tagname);
    }

    private int getIntValue(Element element, String tagname, int defaultValue) {
        return assistLogic.getIntValue(element, tagname, defaultValue);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}

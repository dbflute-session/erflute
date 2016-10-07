package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenNodeElementBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenNodeElementBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                        Node Element
    //                                                                        ============
    public String buildNodeElement(NodeElement nodeElement, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<id>").append(Format.toString(context.nodeElementMap.get(nodeElement))).append("</id>\n");
        final int height = nodeElement.getHeight();
        if (height >= 0) {
            xml.append("<height>").append(height).append("</height>\n");
        }
        final int width = nodeElement.getWidth();
        if (width >= 0) {
            xml.append("<width>").append(width).append("</width>\n");
        }
        xml.append("<font_name>").append(escape(nodeElement.getFontName())).append("</font_name>\n");
        xml.append("<font_size>").append(nodeElement.getFontSize()).append("</font_size>\n");
        xml.append("<x>").append(nodeElement.getX()).append("</x>\n");
        xml.append("<y>").append(nodeElement.getY()).append("</y>\n");
        xml.append(assistLogic.buildColor(nodeElement.getColor()));
        xml.append(buildConnections(nodeElement.getIncomings(), context));
        return xml.toString();
    }

    // ===================================================================================
    //                                                                         Connections
    //                                                                         ===========
    private String buildConnections(List<ConnectionElement> incomings, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<connections>\n");
        for (final ConnectionElement connection : incomings) {
            if (connection instanceof CommentConnection) {
                xml.append(tab(buildCommentConnection((CommentConnection) connection, context)));
            } else if (connection instanceof Relationship) {
                xml.append(tab(buildRelationship((Relationship) connection, context)));
            }
        }
        xml.append("</connections>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                  Comment Connection
    //                                                                  ==================
    private String buildCommentConnection(CommentConnection connection, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<comment_connection>\n");
        xml.append(tab(buildConnectionElement(connection, context)));
        xml.append("</comment_connection>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Relationship
    //                                                                        ============
    private String buildRelationship(Relationship relation, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<relation>\n");
        xml.append("\t<name>").append(escape(relation.getName())).append("</name>\n");
        xml.append(tab(buildConnectionElement(relation, context)));
        xml.append("\t<parent_cardinality>").append(escape(relation.getParentCardinality())).append("</parent_cardinality>\n");
        xml.append("\t<child_cardinality>").append(escape(relation.getChildCardinality())).append("</child_cardinality>\n");
        xml.append("\t<reference_for_pk>").append(relation.isReferenceForPK()).append("</reference_for_pk>\n");
        setupOnDeleteUpdate(relation, xml);
        setupSourceTargetXy(relation, xml);
        setupReferenced(relation, context, xml);
        xml.append("</relation>\n");
        return xml.toString();
    }

    private void setupOnDeleteUpdate(Relationship relation, final StringBuilder xml) {
        // not write if empty or false to slim XML
        final String onDeleteAction = relation.getOnDeleteAction();
        if (onDeleteAction != null && !"NO ACTION".equals(onDeleteAction)) {
            xml.append("\t<on_delete_action>").append(escape(onDeleteAction)).append("</on_delete_action>\n");
        }
        final String onUpdateAction = relation.getOnUpdateAction();
        if (onUpdateAction != null && !"NO ACTION".equals(onUpdateAction)) {
            xml.append("\t<on_update_action>").append(escape(onUpdateAction)).append("</on_update_action>\n");
        }
    }

    private void setupSourceTargetXy(Relationship relation, StringBuilder xml) {
        // not write if empty or false to slim XML
        final int sourceXp = relation.getSourceXp(); // e.g. MEMBER_STATUS
        if (sourceXp >= 0) {
            xml.append("\t<source_xp>").append(sourceXp).append("</source_xp>\n");
        }
        final int sourceYp = relation.getSourceYp();
        if (sourceYp >= 0) {
            xml.append("\t<source_yp>").append(sourceYp).append("</source_yp>\n");
        }
        final int targetXp = relation.getTargetXp(); // e.g. MEMBER
        if (targetXp >= 0) {
            xml.append("\t<target_xp>").append(targetXp).append("</target_xp>\n");
        }
        final int targetYp = relation.getTargetYp();
        if (targetYp >= 0) {
            xml.append("\t<target_yp>").append(targetYp).append("</target_yp>\n");
        }
    }

    private void setupReferenced(Relationship relation, PersistentContext context, final StringBuilder xml) {
        // not write if empty or false to slim XML
        final String referencecdColumnId = context.columnMap.get(relation.getReferencedColumn());
        if (referencecdColumnId != null) {
            xml.append("\t<referenced_column>").append(referencecdColumnId).append("</referenced_column>\n");
        }
        final Integer complexUniqueKeyId = context.complexUniqueKeyMap.get(relation.getReferencedComplexUniqueKey());
        if (complexUniqueKeyId != null) {
            xml.append("\t<referenced_complex_unique_key>").append(complexUniqueKeyId).append("</referenced_complex_unique_key>\n");
        }
    }

    // ===================================================================================
    //                                                                  Connection Element
    //                                                                  ==================
    private String buildConnectionElement(ConnectionElement connection, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<id>").append(context.connectionMap.get(connection)).append("</id>\n");
        final Integer sourceId = context.nodeElementMap.get(connection.getSource());
        final Integer targetId = context.nodeElementMap.get(connection.getTarget());
        xml.append("<source>").append(sourceId).append("</source>\n"); // e.g. MEMBER_STATUS
        xml.append("<target>").append(targetId).append("</target>\n"); // e.g. MEMBER
        for (final Bendpoint bendpoint : connection.getBendpoints()) {
            xml.append(tab(buildBendPoint(bendpoint)));
        }
        return xml.toString();
    }

    private String buildBendPoint(Bendpoint bendpoint) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<bendpoint>\n");
        xml.append("\t<relative>").append(bendpoint.isRelative()).append("</relative>\n");
        xml.append("\t<x>").append(bendpoint.getX()).append("</x>\n");
        xml.append("\t<y>").append(bendpoint.getY()).append("</y>\n");
        xml.append("</bendpoint>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String tab(String str) {
        return assistLogic.tab(str);
    }

    private String escape(String s) {
        return assistLogic.escape(s);
    }
}
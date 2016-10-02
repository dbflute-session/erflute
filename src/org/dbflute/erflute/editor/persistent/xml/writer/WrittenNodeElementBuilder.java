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
        xml.append("<height>").append(nodeElement.getHeight()).append("</height>\n");
        xml.append("<width>").append(nodeElement.getWidth()).append("</width>\n");
        xml.append("\t<font_name>").append(escape(nodeElement.getFontName())).append("</font_name>\n");
        xml.append("\t<font_size>").append(nodeElement.getFontSize()).append("</font_size>\n");
        xml.append("<x>").append(nodeElement.getX()).append("</x>\n");
        xml.append("<y>").append(nodeElement.getY()).append("</y>\n");
        xml.append(assistLogic.buildColor(nodeElement.getColor()));
        final List<ConnectionElement> incomings = nodeElement.getIncomings();
        xml.append(buildConnections(incomings, context));
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
        xml.append(tab(buildConnectionElement(relation, context)));
        xml.append("\t<child_cardinality>").append(escape(relation.getChildCardinality())).append("</child_cardinality>\n");
        xml.append("\t<parent_cardinality>").append(escape(relation.getParentCardinality())).append("</parent_cardinality>\n");
        xml.append("\t<reference_for_pk>").append(relation.isReferenceForPK()).append("</reference_for_pk>\n");
        xml.append("\t<name>").append(escape(relation.getName())).append("</name>\n");
        xml.append("\t<on_delete_action>").append(escape(relation.getOnDeleteAction())).append("</on_delete_action>\n");
        xml.append("\t<on_update_action>").append(escape(relation.getOnUpdateAction())).append("</on_update_action>\n");
        xml.append("\t<source_xp>").append(relation.getSourceXp()).append("</source_xp>\n");
        xml.append("\t<source_yp>").append(relation.getSourceYp()).append("</source_yp>\n");
        xml.append("\t<target_xp>").append(relation.getTargetXp()).append("</target_xp>\n");
        xml.append("\t<target_yp>").append(relation.getTargetYp()).append("</target_yp>\n");
        xml.append("\t<referenced_column>").append(context.columnMap.get(relation.getReferencedColumn())).append("</referenced_column>\n");
        xml.append("\t<referenced_complex_unique_key>")
                .append(context.complexUniqueKeyMap.get(relation.getReferencedComplexUniqueKey()))
                .append("</referenced_complex_unique_key>\n");
        xml.append("</relation>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                  Connection Element
    //                                                                  ==================
    private String buildConnectionElement(ConnectionElement connection, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<id>").append(context.connectionMap.get(connection)).append("</id>\n");
        xml.append("<source>").append(context.nodeElementMap.get(connection.getSource())).append("</source>\n");
        xml.append("<target>").append(context.nodeElementMap.get(connection.getTarget())).append("</target>\n");
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
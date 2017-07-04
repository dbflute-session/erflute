package org.dbflute.erflute.editor.persistent.xml.writer;

import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml.PersistentContext;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class WrittenDiagramWalkerBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final WrittenAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WrittenDiagramWalkerBuilder(PersistentXml persistentXml, WrittenAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                      Walker Element
    //                                                                      ==============
    public String buildWalker(DiagramWalker walker, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        if (walker.isUsePersistentId()) {
            final String id = context.walkerMap.get(walker);
            if (id != null) { // null allowed when e.g. modelProperties
                xml.append("<id>").append(Format.toString(id)).append("</id>\n");
            }
        }
        final int height = walker.getHeight();
        if (height >= 0) {
            xml.append("<height>").append(height).append("</height>\n");
        }
        final int width = walker.getWidth();
        if (width >= 0) {
            xml.append("<width>").append(width).append("</width>\n");
        }
        xml.append("<font_name>").append(escape(walker.getFontName())).append("</font_name>\n");
        xml.append("<font_size>").append(walker.getFontSize()).append("</font_size>\n");
        xml.append("<x>").append(walker.getX()).append("</x>\n");
        xml.append("<y>").append(walker.getY()).append("</y>\n");
        xml.append(assistLogic.buildColor(walker.getColor()));
        xml.append(buildConnections(walker.getPersistentConnections(), context));
        return xml.toString();
    }

    // ===================================================================================
    //                                                                         Connections
    //                                                                         ===========
    private String buildConnections(List<WalkerConnection> incomings, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<connections>\n");
        for (final WalkerConnection connection : incomings) {
            if (connection instanceof Relationship) {
                xml.append(tab(buildRelationship((Relationship) connection, context)));
            } else if (connection instanceof CommentConnection) {
                xml.append(tab(buildCommentConnection((CommentConnection) connection, context)));
            }
        }
        xml.append("</connections>\n");
        return xml.toString();
    }

    // ===================================================================================
    //                                                                        Relationship
    //                                                                        ============
    private String buildRelationship(Relationship relationship, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<relationship>\n"); // #for_erflute rename to relationship
        xml.append("\t<name>").append(escape(relationship.getForeignKeyName())).append("</name>\n");
        xml.append(tab(buildConnectionElement(relationship, context)));
        xml.append("\t<parent_cardinality>").append(escape(relationship.getParentCardinality())).append("</parent_cardinality>\n");
        xml.append("\t<child_cardinality>").append(escape(relationship.getChildCardinality())).append("</child_cardinality>\n");
        xml.append("\t<reference_for_pk>").append(relationship.isReferenceForPK()).append("</reference_for_pk>\n");
        setupOnDeleteUpdate(relationship, xml);
        setupSourceTargetXy(relationship, xml);
        setupReferred(relationship, context, xml);
        xml.append("</relationship>\n");
        return xml.toString();
    }

    private void setupOnDeleteUpdate(Relationship relation, StringBuilder xml) {
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

    private void setupReferred(Relationship relationship, PersistentContext context, StringBuilder xml) {
        // not write if empty or false to slim XML
        final CompoundUniqueKey referredComplexUniqueKey = relationship.getReferredCompoundUniqueKey();
        if (referredComplexUniqueKey != null) {
            final TableView table = relationship.getTargetTableView(); // local table e.g. MEMBER
            final String uniqueKeyId = referredComplexUniqueKey.buildUniqueKeyId(table); // #for_erflute not use incremental ID
            xml.append("\t<referred_compound_unique_key>").append(uniqueKeyId).append("</referred_compound_unique_key>\n"); // #for_erflute rename
        }
        final NormalColumn referredSimpleUniqueColumn = relationship.getReferredSimpleUniqueColumn(); // simple unique key
        if (referredSimpleUniqueColumn != null) {
            final TableView table = relationship.getTargetTableView(); // local table e.g. MEMBER
            final String uniqueColumnId = referredSimpleUniqueColumn.buildSimpleUniqueColumnId(table);
            xml.append("\t<referred_simple_unique_column>").append(uniqueColumnId).append("</referred_simple_unique_column>\n"); // #for_erflute rename to 'referred'
        }
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
    //                                                                  Connection Element
    //                                                                  ==================
    private String buildConnectionElement(WalkerConnection connection, PersistentContext context) {
        final StringBuilder xml = new StringBuilder();
        // #for_erflute unneeded ID for connection
        //xml.append("<id>").append(context.connectionMap.get(connection)).append("</id>\n");
        final String sourceId = context.walkerMap.get(connection.getSourceWalker());
        final String targetId = context.walkerMap.get(connection.getTargetWalker());
        xml.append("<source>").append(sourceId != null ? sourceId : "$$owner$$").append("</source>\n"); // e.g. MEMBER_STATUS
        xml.append("<target>").append(targetId).append("</target>\n"); // e.g. MEMBER
        for (final Bendpoint bendpoint : connection.getBendpoints()) {
            xml.append(buildBendPoint(bendpoint));
        }
        if (connection instanceof Relationship) {
            xml.append(buildFKColumns(((Relationship) connection))); // #for_erflute to build relation ID
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

    private String buildFKColumns(Relationship relationship) {
        final StringBuilder xml = new StringBuilder();
        xml.append("<fk_columns>\n");
        final List<NormalColumn> foreignKeyColumns = relationship.getForeignKeyColumns();
        for (final NormalColumn column : foreignKeyColumns) {
            xml.append("\t<fk_column>\n");
            xml.append("\t\t<fk_column_name>").append(column.getPhysicalName()).append("</fk_column_name>\n");
            xml.append("\t</fk_column>\n");
        }
        xml.append("</fk_columns>\n");
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

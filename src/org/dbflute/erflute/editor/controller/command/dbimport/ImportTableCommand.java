package org.dbflute.erflute.editor.controller.command.dbimport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;

public class ImportTableCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final SequenceSet sequenceSet;

    private final TriggerSet triggerSet;

    private final TablespaceSet tablespaceSet;

    private final ColumnGroupSet columnGroupSet;

    private final List<DiagramWalker> walkerList;

    private final List<Sequence> sequences;

    private final List<Trigger> triggers;

    private final List<Tablespace> tablespaces;

    private final List<ColumnGroup> columnGroups;

    private DirectedGraph graph = new DirectedGraph();

    private static final int AUTO_GRAPH_LIMIT = 100;

    private static final int ORIGINAL_X = 20;
    private static final int ORIGINAL_Y = 20;

    private static final int DISTANCE_X = 300;
    private static final int DISTANCE_Y = 300;

    private static final int SIZE_X = 6;

    public ImportTableCommand(ERDiagram diagram, List<DiagramWalker> walkerList, List<Sequence> sequences, List<Trigger> triggers,
            List<Tablespace> tablespaces, List<ColumnGroup> columnGroups) {
        this.diagram = diagram;
        this.walkerList = walkerList;
        this.sequences = sequences;
        this.triggers = triggers;
        this.tablespaces = tablespaces;
        this.columnGroups = columnGroups;

        final DiagramContents diagramContents = this.diagram.getDiagramContents();

        this.sequenceSet = diagramContents.getSequenceSet();
        this.triggerSet = diagramContents.getTriggerSet();
        this.tablespaceSet = diagramContents.getTablespaceSet();
        this.columnGroupSet = diagramContents.getColumnGroupSet();

        this.decideLocation();
    }

    @SuppressWarnings("unchecked")
    private void decideLocation() {
        this.graph = new DirectedGraph();

        if (this.walkerList.size() < AUTO_GRAPH_LIMIT) {
            final Map<DiagramWalker, Node> nodeElementNodeMap = new HashMap<>();
            final int fontSize = this.diagram.getFontSize();
            final Insets insets = new Insets(5 * fontSize, 10 * fontSize, 35 * fontSize, 20 * fontSize);
            for (final DiagramWalker walker : this.walkerList) {
                final Node node = new Node();
                node.setPadding(insets);
                this.graph.nodes.add(node);
                nodeElementNodeMap.put(walker, node);
            }

            for (final DiagramWalker walker : this.walkerList) {
                for (final WalkerConnection outgoing : walker.getOutgoings()) {
                    final Node sourceNode = nodeElementNodeMap.get(outgoing.getWalkerSource());
                    final Node targetNode = nodeElementNodeMap.get(outgoing.getWalkerTarget());
                    if (sourceNode != targetNode) {
                        final Edge edge = new Edge(sourceNode, targetNode);
                        this.graph.edges.add(edge);
                    }
                }
            }
            final DirectedGraphLayout layout = new DirectedGraphLayout();
            layout.visit(this.graph);
            for (final DiagramWalker walker : nodeElementNodeMap.keySet()) {
                final Node node = nodeElementNodeMap.get(walker);
                if (walker.getWidth() == 0) {
                    walker.setLocation(new Location(node.x, node.y, -1, -1));
                }
            }
        } else {
            int x = ORIGINAL_X;
            int y = ORIGINAL_Y;

            for (final DiagramWalker nodeElement : this.walkerList) {
                if (nodeElement.getWidth() == 0) {
                    nodeElement.setLocation(new Location(x, y, -1, -1));

                    x += DISTANCE_X;
                    if (x > DISTANCE_X * SIZE_X) {
                        x = ORIGINAL_X;
                        y += DISTANCE_Y;
                    }
                }
            }
        }
    }

    @Override
    protected void doExecute() {
        if (this.columnGroups != null) {
            for (final ColumnGroup columnGroup : columnGroups) {
                this.columnGroupSet.add(columnGroup);
            }
        }

        ERDiagramEditPart.setUpdateable(false);

        for (final DiagramWalker walker : this.walkerList) {
            this.diagram.addNewWalker(walker);
            if (walker instanceof TableView) {
                for (final NormalColumn normalColumn : ((TableView) walker).getNormalColumns()) {
                    if (normalColumn.isForeignKey()) {
                        for (final Relationship relation : normalColumn.getRelationshipList()) {
                            if (relation.getSourceTableView() == walker) {
                                this.setSelfRelation(relation);
                            }
                        }
                    }
                }
            }
        }

        for (final Sequence sequence : sequences) {
            this.sequenceSet.addSequence(sequence);
        }

        for (final Trigger trigger : triggers) {
            this.triggerSet.addTrigger(trigger);
        }

        for (final Tablespace tablespace : tablespaces) {
            this.tablespaceSet.addTablespace(tablespace);
        }

        ERDiagramEditPart.setUpdateable(true);

        this.diagram.changeAll(this.walkerList);
    }

    private void setSelfRelation(Relationship relation) {
        boolean anotherSelfRelation = false;

        final TableView sourceTable = relation.getSourceTableView();
        for (final Relationship otherRelation : sourceTable.getOutgoingRelationshipList()) {
            if (otherRelation == relation) {
                continue;
            }
            if (otherRelation.getWalkerSource() == otherRelation.getWalkerTarget()) {
                anotherSelfRelation = true;
                break;
            }
        }

        int rate = 0;

        if (anotherSelfRelation) {
            rate = 50;

        } else {
            rate = 100;
        }

        final Bendpoint bendpoint0 = new Bendpoint(rate, rate);
        bendpoint0.setRelative(true);

        final int xp = 100 - (rate / 2);
        final int yp = 100 - (rate / 2);

        relation.setSourceLocationp(100, yp);
        relation.setTargetLocationp(xp, 100);

        relation.addBendpoint(0, bendpoint0);
    }

    @Override
    protected void doUndo() {
        ERDiagramEditPart.setUpdateable(false);
        for (final DiagramWalker walker : this.walkerList) {
            this.diagram.removeContent(walker);
            if (walker instanceof TableView) {
                for (final NormalColumn normalColumn : ((TableView) walker).getNormalColumns()) {
                    this.diagram.getDiagramContents().getDictionary().remove(normalColumn);
                }
            }
        }
        for (final Sequence sequence : sequences) {
            this.sequenceSet.remove(sequence);
        }
        for (final Trigger trigger : triggers) {
            this.triggerSet.remove(trigger);
        }
        for (final Tablespace tablespace : tablespaces) {
            this.tablespaceSet.remove(tablespace);
        }
        if (this.columnGroups != null) {
            for (final ColumnGroup columnGroup : columnGroups) {
                this.columnGroupSet.remove(columnGroup);
            }
        }
        ERDiagramEditPart.setUpdateable(true);
        this.diagram.changeAll();
    }
}

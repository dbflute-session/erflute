package org.dbflute.erflute.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERVirtualTable extends ERTable {

    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final ERVirtualDiagram vdiagram;
    private ERTable rawTable;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ERVirtualTable(ERVirtualDiagram vdiagram, ERTable rawTable) {
        this.vdiagram = vdiagram;
        this.rawTable = rawTable;
    }

    // ===================================================================================
    //                                                                        Change Table
    //                                                                        ============
    public void changeTable() {
        firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    // ===================================================================================
    //                                                               Delegate to Raw Table
    //                                                               =====================
    @Override
    public void setColor(int red, int green, int blue) {
        rawTable.setColor(red, green, blue);
    }

    @Override
    public int[] getColor() {
        return rawTable.getColor();
    }

    @Override
    public ERDiagram getDiagram() {
        return rawTable.getDiagram();
    }

    public void setPoint(int x, int y) {
        super.setLocation(new Location(x, y, getWidth(), getHeight()));
    }

    @Override
    public int getWidth() {
        return rawTable.getWidth();
    }

    @Override
    public int getHeight() {
        return rawTable.getHeight();
    }

    @Override
    public List<WalkerConnection> getIncomings() {
        final List<WalkerConnection> connectionList = new ArrayList<>();
        final List<ERVirtualTable> vtables = vdiagram.getVirtualTables();
        for (final WalkerConnection connection : rawTable.getIncomings()) {
            final DiagramWalker walker = connection.getWalkerSource();
            if (walker instanceof WalkerNote) {
                final WalkerNote note = (WalkerNote) walker;
                if (note.getVirtualDiagram() != null && note.getVirtualDiagram().equals(vdiagram)) {
                    connectionList.add(connection);
                }
            } else {
                for (final ERVirtualTable vtable : vtables) {
                    if (vtable.getRawTable().equals(walker)) {
                        connectionList.add(connection);
                        break;
                    }
                }
            }
        }
        return connectionList;
    }

    @Override
    public List<WalkerConnection> getOutgoings() {
        final List<WalkerConnection> connectionList = new ArrayList<>();
        final List<ERVirtualTable> vtables = vdiagram.getVirtualTables();
        for (final WalkerConnection connection : rawTable.getOutgoings()) {
            final DiagramWalker walker = connection.getWalkerTarget();
            if (walker instanceof WalkerNote) {
                if (((WalkerNote) walker).getVirtualDiagram().equals(vdiagram)) {
                    connectionList.add(connection);
                }
            } else {
                for (final ERVirtualTable vtable : vtables) {
                    if (vtable.getRawTable().equals(walker)) {
                        connectionList.add(connection);
                        break;
                    }
                }
            }
        }
        return connectionList;
    }

    @Override
    public NormalColumn getAutoIncrementColumn() {
        return rawTable.getAutoIncrementColumn();
    }

    @Override
    public TableViewProperties getTableViewProperties() {
        return rawTable.getTableViewProperties();
    }

    @Override
    public String getPhysicalName() {
        return rawTable.getPhysicalName();
    }

    @Override
    public List<DiagramWalker> getReferringElementList() {
        return rawTable.getReferringElementList();
    }

    @Override
    public TableViewProperties getTableViewProperties(String database) {
        return rawTable.getTableViewProperties(database);
    }

    @Override
    public String getLogicalName() {
        return rawTable.getLogicalName();
    }

    @Override
    public List<DiagramWalker> getReferedElementList() {
        return rawTable.getReferedElementList();
    }

    @Override
    public String getName() {
        return rawTable.getName();
    }

    @Override
    public String getDescription() {
        return rawTable.getDescription();
    }

    @Override
    public List<ERColumn> getColumns() {
        return rawTable.getColumns();
    }

    @Override
    public List<NormalColumn> getExpandedColumns() {
        return rawTable.getExpandedColumns();
    }

    @Override
    public List<Relationship> getIncomingRelationshipList() {
        final List<Relationship> relationships = new ArrayList<>();
        final List<ERVirtualTable> vtables = vdiagram.getVirtualTables();
        for (final Relationship relationship : rawTable.getIncomingRelationshipList()) {
            final DiagramWalker walker = relationship.getWalkerSource();
            for (final ERVirtualTable vtable : vtables) {
                if (vtable.getRawTable().equals(walker)) {
                    relationships.add(relationship);
                    break;
                }
            }
        }
        return relationships;
    }

    @Override
    public List<Relationship> getOutgoingRelationshipList() {
        final List<Relationship> relationships = new ArrayList<>();
        final List<ERVirtualTable> vtables = vdiagram.getVirtualTables();
        for (final Relationship relationship : rawTable.getOutgoingRelationshipList()) {
            final DiagramWalker walker = relationship.getWalkerSource();
            for (final ERVirtualTable vtable : vtables) {
                if (vtable.getRawTable().equals(walker)) {
                    relationships.add(relationship);
                    break;
                }
            }
        }
        return relationships;
    }

    @Override
    public List<NormalColumn> getNormalColumns() {
        return rawTable.getNormalColumns();
    }

    @Override
    public int getPrimaryKeySize() {
        return rawTable.getPrimaryKeySize();
    }

    @Override
    public ERColumn getColumn(int index) {
        return rawTable.getColumn(index);
    }

    @Override
    public List<NormalColumn> getPrimaryKeys() {
        return rawTable.getPrimaryKeys();
    }

    @Override
    public ERIndex getIndex(int index) {
        return rawTable.getIndex(index);
    }

    @Override
    public List<ERIndex> getIndexes() {
        return rawTable.getIndexes();
    }

    @Override
    public List<CompoundUniqueKey> getCompoundUniqueKeyList() {
        return rawTable.getCompoundUniqueKeyList();
    }

    @Override
    public String getConstraint() {
        return rawTable.getConstraint();
    }

    @Override
    public String getPrimaryKeyName() {
        return rawTable.getPrimaryKeyName();
    }

    @Override
    public String getOption() {
        return rawTable.getOption();
    }

    @Override
    public String getNameWithSchema(String database) {
        return rawTable.getNameWithSchema(database);
    }

    @Override
    public void setColumns(List<ERColumn> columns) {
        rawTable.setColumns(columns);
    }

    @Override
    public void setLocation(Location location) {
        rawTable.setWidth(location.width);
        rawTable.setHeight(location.height);
        super.setLocation(location);
    }

    @Override
    public ERTable toMaterialize() {
        return this.rawTable;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public String getObjectType() {
        return "vtable";
    }

    public ERVirtualDiagram getVirtualDiagram() {
        return vdiagram;
    }

    public ERTable getRawTable() {
        return rawTable;
    }

    @Override
    public void changeTableViewProperty(final TableView sourceTableView) {
        // メインビューを更新（枠の再生成）
        sourceTableView.restructureData(getRawTable());

        // サブビューも更新
        changeTable();

        // テーブルの更新（線も含めた再生成）
        getDiagram().changeTable(sourceTableView);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public ERVirtualTable clone() {
        final ERVirtualTable clone = (ERVirtualTable) super.clone();
        clone.rawTable = rawTable.clone();
        return clone;
    }
}

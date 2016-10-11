package org.dbflute.erflute.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.Location;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.Note;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERVirtualTable extends ERTable {

    private static final long serialVersionUID = 1L;

    private final ERVirtualDiagram model;
    private ERTable rawTable;

    public ERVirtualTable(ERVirtualDiagram model, ERTable rawTable) {
        super();
        this.model = model;
        this.rawTable = rawTable;
        //		setDiagram(rawTable.getDiagram());
        //		this.tableViewProperties = DBManagerFactory.getDBManager(
        //				this.getDiagram()).createTableProperties(
        //				(TableProperties) this.tableViewProperties);
        //
        //		Dictionary dictionary = this.getDiagram().getDiagramContents()
        //				.getDictionary();
        //
        //		setPhysicalName(rawTable.getPhysicalName());
        //		setLogicalName(this.getLogicalName());
        //		setDescription(this.getDescription());
        //
        //		for (NormalColumn toColumn : to.getNormalColumns()) {
        //			dictionary.remove(toColumn);
        //		}
        //
        //		List<Column> columns = new ArrayList<Column>();
        //
        //		List<NormalColumn> newPrimaryKeyColumns = new ArrayList<NormalColumn>();
        //
        //		for (Column fromColumn : this.getColumns()) {
        //			if (fromColumn instanceof NormalColumn) {
        //				CopyColumn copyColumn = (CopyColumn) fromColumn;
        //
        //				CopyWord copyWord = copyColumn.getWord();
        //				if (copyColumn.isForeignKey()) {
        //					copyWord = null;
        //				}
        //
        //				if (copyWord != null) {
        //					Word originalWord = copyColumn.getOriginalWord();
        //					dictionary.copyTo(copyWord, originalWord);
        //				}
        //
        //				NormalColumn restructuredColumn = copyColumn
        //						.getRestructuredColumn();
        //
        //				restructuredColumn.setColumnHolder(this);
        //				if (copyWord == null) {
        //					restructuredColumn.setWord(null);
        //				}
        //				columns.add(restructuredColumn);
        //
        //				if (restructuredColumn.isPrimaryKey()) {
        //					newPrimaryKeyColumns.add(restructuredColumn);
        //				}
        //
        //				dictionary.add(restructuredColumn);
        //
        //			} else {
        //				columns.add(fromColumn);
        //			}
        //		}
        //
        //		this.setTargetTableRelation(to, newPrimaryKeyColumns);
        //
        //		to.setColumns(columns);

        //		rawTable.copyTableViewData(this).restructureData(this);
        //		rawTable.clone().restructureData(this);
        //		rawTable.restructureData(this);
        //		restructureData(rawTable);
    }

    // ---------------------------------------------------------------- Delegete Methods

    @Override
    public void setFontSize(int fontSize) {
        super.setFontSize(fontSize);
    }

    @Override
    public int getFontSize() {
        return super.getFontSize();
    }

    //	@Override
    //	public int getFontSize() {
    //		if (super.getFontSize() == 0) {
    //			return super.getFontSize();
    //		}
    //		return rawTable.getFontSize();
    //	}
    //	
    //	@Override
    //	public String getFontName() {
    //		if (super.getFontName() == null) {
    //			return super.getFontName();
    //		}
    //		return rawTable.getFontName();
    //	}

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
        this.setLocation(new Location(x, y, getWidth(), getHeight()));
    }

    //	@Override
    //	public int getX() {
    //		return rawTable.getX();
    //	}
    //
    //	@Override
    //	public int getY() {
    //		return rawTable.getY();
    //	}

    @Override
    public int getWidth() {
        return rawTable.getWidth();
    }

    @Override
    public int getHeight() {
        return rawTable.getHeight();
    }

    @Override
    public List<ConnectionElement> getIncomings() {
        final List<ConnectionElement> elements = new ArrayList<ConnectionElement>();
        final List<ERVirtualTable> modelTables = model.getTables();
        for (final ConnectionElement el : rawTable.getIncomings()) {
            final DiagramWalker findEl = el.getSource();
            if (findEl instanceof Note) {
                if (((Note) findEl).getVirtualDiagram().equals(model)) {
                    elements.add(el);
                }
                //				elements.add(el);
            } else {
                for (final ERVirtualTable vtable : modelTables) {
                    if (vtable.getRawTable().equals(findEl)) {
                        elements.add(el);
                        break;
                    }
                }
            }
        }
        return elements;
    }

    @Override
    public List<ConnectionElement> getOutgoings() {
        final List<ConnectionElement> elements = new ArrayList<ConnectionElement>();
        final List<ERVirtualTable> modelTables = model.getTables();
        for (final ConnectionElement el : rawTable.getOutgoings()) {
            final DiagramWalker findEl = el.getTarget();
            if (findEl instanceof Note) {
                if (((Note) findEl).getVirtualDiagram().equals(model)) {
                    elements.add(el);
                }
                elements.add(el);
            } else {
                for (final ERVirtualTable vtable : modelTables) {
                    if (vtable.getRawTable().equals(findEl)) {
                        elements.add(el);
                        break;
                    }
                }
            }
        }
        return elements;
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
        final List<Relationship> elements = new ArrayList<Relationship>();
        final List<ERVirtualTable> modelTables = model.getTables();
        for (final Relationship el : rawTable.getIncomingRelationshipList()) {
            final DiagramWalker findEl = el.getSource();
            for (final ERVirtualTable vtable : modelTables) {
                if (vtable.getRawTable().equals(findEl)) {
                    elements.add(el);
                    break;
                }
            }
        }
        return elements;
        //		return rawTable.getIncomingRelations();
    }

    @Override
    public List<Relationship> getOutgoingRelationshipList() {
        final List<Relationship> elements = new ArrayList<Relationship>();
        final List<ERVirtualTable> modelTables = model.getTables();
        for (final Relationship el : rawTable.getOutgoingRelationshipList()) {
            final DiagramWalker findEl = el.getSource();
            for (final ERVirtualTable vtable : modelTables) {
                if (vtable.getRawTable().equals(findEl)) {
                    elements.add(el);
                    break;
                }
            }
        }
        return elements;
        //		return rawTable.getOutgoingRelations();
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
    public List<ComplexUniqueKey> getComplexUniqueKeyList() {
        return rawTable.getComplexUniqueKeyList();
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

    public ERTable getRawTable() {
        return rawTable;
    }

    public void setRawTable(ERTable rawTable) {
        this.rawTable = rawTable;
    }

    @Override
    public String getObjectType() {
        return "vtable";
    }

    public void doChangeTable() {
        firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
    }
}

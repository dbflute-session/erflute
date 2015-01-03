package org.insightech.er.editor.model.diagram_contents.element.node.view;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;

public class View extends TableView implements ObjectModel, ColumnHolder {

    private static final long serialVersionUID = -4492787972500741281L;

    public static final String NEW_PHYSICAL_NAME = ResourceString.getResourceString("new.view.physical.name");

    public static final String NEW_LOGICAL_NAME = ResourceString.getResourceString("new.view.logical.name");

    private String sql;

    public View() {
        this.tableViewProperties = new ViewProperties();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public void addColumn(Column column) {
        if (column instanceof NormalColumn) {
            NormalColumn normalColumn = (NormalColumn) column;
            normalColumn.setAutoIncrement(false);
            normalColumn.setPrimaryKey(false);
            normalColumn.setUniqueKey(false);
            normalColumn.setNotNull(false);
        }

        this.columns.add(column);
        column.setColumnHolder(this);

        this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
    }

    @Override
    public View copyData() {
        View to = new View();
        to.setSql(this.getSql());

        super.copyTableViewData(to);

        to.tableViewProperties = this.getTableViewProperties().clone();

        return to;
    }

    @Override
    public void restructureData(TableView to) {
        View view = (View) to;

        view.setSql(this.getSql());

        super.restructureData(to);

        view.tableViewProperties = this.tableViewProperties.clone();

    }

    /**
     * �r���[�𕡐����܂��B<br>
     * ����������́A���O��SQL�ƃv���p�e�B�̂݁B<br>
     * ��͕����ΏۊO�Ƃ��A�ォ�畡������B<br>
     */
    @Override
    public View clone() {
        View clone = (View) super.clone();

        // �v���p�e�B�𕡐����܂��B
        TableViewProperties cloneViewProperties = this.tableViewProperties.clone();
        clone.tableViewProperties = cloneViewProperties;

        return clone;
    }

    public String getObjectType() {
        return "view";
    }

    @Override
    public boolean needsUpdateOtherModel() {
        return true;
    }

}

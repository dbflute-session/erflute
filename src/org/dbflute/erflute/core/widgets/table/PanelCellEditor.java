package org.dbflute.erflute.core.widgets.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public abstract class PanelCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private static final long serialVersionUID = 1L;

    private final JPanel editPanel;

    public PanelCellEditor() {
        this.editPanel = new JPanel();
        this.editPanel.setLayout(null);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return editPanel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return editPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return editPanel;
    }

    protected static Font getAwtFont() {
        final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
        final Font font = new Font(fontData.getName(), Font.PLAIN, 12);
        return font;
    }

    protected void addComponent(Component component, int x, int y, int w, int h) {
        addComponent(editPanel, component, x, y, w, h);
    }

    protected static void addComponent(Container parent, Component component, int x, int y, int w, int h) {
        component.setFont(getAwtFont());
        component.setBounds(x, y, w, h);
        parent.add(component);
    }
}

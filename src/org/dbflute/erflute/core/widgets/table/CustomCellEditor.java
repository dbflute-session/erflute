package org.dbflute.erflute.core.widgets.table;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.dbflute.erflute.core.DisplayMessages;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class CustomCellEditor extends DefaultCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 1L;

    public CustomCellEditor(final JTable table) {
        super(new JTextField());

        final JTextField component = (JTextField) getComponent();
        component.setName("Table.editor");

        component.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    if (e.getKeyCode() == ';') {
                        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        component.setText(format.format(new Date()));

                    } else if (e.getKeyCode() == 'v' || e.getKeyCode() == 'V') {
                        component.paste();

                    } else if (e.getKeyCode() == 'c' || e.getKeyCode() == 'C') {
                        component.copy();

                    } else if (e.getKeyCode() == 'x' || e.getKeyCode() == 'X') {
                        component.cut();

                    }
                }

                super.keyPressed(e);
            }
        });

        component.setComponentPopupMenu(new TextFieldPopupMenu());
    }

    // public Component getTableCellEditorComponent(JTable table, Object value,
    // boolean isSelected, int rowIndex, int vColIndex) {
    // if (value == null) {
    // value = "";
    // }
    //
    // this.component.setText(String.valueOf(value));
    //
    // return this.component;
    // }
    //
    // public Object getCellEditorValue() {
    // return this.component.getText();
    // }

    private static class TextFieldPopupMenu extends JPopupMenu {

        private static final long serialVersionUID = 1L;

        private TextFieldPopupMenu() {
            final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];

            final Font font = new Font(fontData.getName(), Font.PLAIN, 12);

            final JMenuItem cutMenuItem = add(new CutAction());
            cutMenuItem.setFont(font);

            final JMenuItem copyMenuItem = add(new CopyAction());
            copyMenuItem.setFont(font);

            final JMenuItem pasteMenuItem = add(new PasteAction());
            pasteMenuItem.setFont(font);
        }
    }

    private static class CutAction extends TextAction {

        private static final long serialVersionUID = 1L;

        public CutAction() {
            super(DisplayMessages.getMessage("action.title.cut"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.cut();
            }
        }
    }

    private static class CopyAction extends TextAction {

        private static final long serialVersionUID = 1L;

        public CopyAction() {
            super(DisplayMessages.getMessage("action.title.copy"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.copy();
            }
        }
    }

    private static class PasteAction extends TextAction {

        private static final long serialVersionUID = 1L;

        public PasteAction() {
            super(DisplayMessages.getMessage("action.title.paste"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.paste();
            }
        }
    }
}

package org.dbflute.erflute.core.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.table.CellEditWorker;
import org.dbflute.erflute.core.widgets.table.CustomCellEditor;
import org.dbflute.erflute.core.widgets.table.HeaderClickListener;
import org.dbflute.erflute.core.widgets.table.PanelCellEditor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class RowHeaderTable extends JScrollPane implements ClipboardOwner {

    private static final long serialVersionUID = 1L;

    private DefaultListModel listModel;
    private DefaultTableModel tableModel;
    private JTable table;
    private MultiLineHeaderRenderer headerRenderer;
    private MultiLineHeaderRenderer selectedHeaderRenderer;
    private CellEditWorker cellEditWorker;
    private final Map<Integer, PanelCellEditor> cellEditorMap = new HashMap<Integer, PanelCellEditor>();
    private boolean clipbordOn = true;
    private HeaderClickListener headerClickListener;
    private int mouseOverColumn = -1;
    private boolean editable;
    private final Color MODIFIED_COLOR = new Color(0xc7, 0xff, 0xb7);

    public RowHeaderTable(int width, int height, final int rowHeaderWidth, int rowHeight, boolean iconEnable, final boolean editable) {
        this.editable = editable;
        this.table = new JTable() {
            private static final long serialVersionUID = 1L;
            private final JPopupMenu pupupMenu = new TablePopupMenu();

            @Override
            public void editingStopped(ChangeEvent e) {
                if (cellEditWorker != null) {
                    final TableCellEditor editor = getCellEditor();
                    if (editor != null) {
                        final Object value = editor.getCellEditorValue();
                        if (!"".equals(value.toString()) && getEditingRow() == getRowCount() - 1) {
                            cellEditWorker.addNewRow();
                        }

                    }
                }
                super.editingStopped(e);
            }

            @Override
            public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
                final Component c = super.prepareRenderer(tcr, row, column);
                if (cellEditWorker != null) {
                    if (!table.isRowSelected(row)) {
                        if (cellEditWorker.isModified(row, column)) {
                            c.setBackground(MODIFIED_COLOR);
                        } else {
                            c.setBackground(null);
                        }
                    }
                }
                return c;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                if (!editable) {
                    return false;
                }
                return super.isCellEditable(row, column);
            }

            @Override
            protected void processMouseEvent(MouseEvent event) {
                super.processMouseEvent(event);
                if (event.isPopupTrigger()) {
                    // 右クリックがされた場合
                    if (!event.isControlDown() && !event.isShiftDown()) {
                        // Ctrl も Shift も押されていない場合
                        // クリックされた行の取得
                        final Point origin = event.getPoint();
                        final int row = rowAtPoint(origin);
                        if (!isRowSelected(row)) {
                            // この行が選択されていない場合
                            // すべての選択を解除
                            clearSelection();
                        }
                        if (row != -1) {
                            // クリックされた行を選択された状態にする
                            addRowSelectionInterval(row, row);
                        }
                    }
                    this.editingStopped(new ChangeEvent(this));

                    // ポップアップメニューの表示
                    this.pupupMenu.show(event.getComponent(), event.getX(), event.getY());
                }
            }
        };

        this.table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyevent) {
                if (clipbordOn) {
                    if (editable) {
                        if (keyevent.isControlDown() && (keyevent.getKeyCode() == 'v' || keyevent.getKeyCode() == 'V')) {
                            pasteRows();
                        } else if (keyevent.isControlDown() && (keyevent.getKeyCode() == 'x' || keyevent.getKeyCode() == 'X')) {
                            cutRows();
                        }
                    }
                    if (keyevent.isControlDown() && (keyevent.getKeyCode() == 'c' || keyevent.getKeyCode() == 'C')) {
                        copyRows();
                    }
                }
            }
        });
        this.table.setAutoCreateColumnsFromModel(true);
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.headerRenderer = new MultiLineHeaderRenderer(null, iconEnable);
        this.selectedHeaderRenderer = new MultiLineHeaderRenderer(UIManager.getColor("Table.selectionBackground"), iconEnable);
        final JTableHeader tableHeader = this.table.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                final int column = table.getTableHeader().columnAtPoint(e.getPoint());
                if (column != -1) {
                    if (headerClickListener != null) {
                        if (mouseOverColumn != column) {
                            if (mouseOverColumn != -1) {
                                final TableColumn oldTableColumn = table.getColumnModel().getColumn(mouseOverColumn);
                                oldTableColumn.setHeaderRenderer(headerRenderer);
                            }
                            final TableColumn newTableColumn = table.getColumnModel().getColumn(column);
                            newTableColumn.setHeaderRenderer(selectedHeaderRenderer);
                            tableHeader.repaint();
                            mouseOverColumn = column;
                        }
                    }
                }
            }
        });

        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                final int column = table.getTableHeader().columnAtPoint(e.getPoint());
                if (column != -1) {
                    if (headerClickListener != null) {
                        mouseOverColumn = column;
                        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setHeaderRenderer(selectedHeaderRenderer);
                        tableHeader.repaint();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (headerClickListener != null) {
                    if (mouseOverColumn != -1) {
                        final TableColumn tableColumn = table.getColumnModel().getColumn(mouseOverColumn);
                        tableColumn.setHeaderRenderer(headerRenderer);
                        mouseOverColumn = -1;
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                final int column = table.getTableHeader().columnAtPoint(e.getPoint());
                if (headerClickListener != null) {
                    headerClickListener.onHeaderClick(column);
                }
            }
        });
        this.table.setRowHeight(rowHeight);
        this.table.setGridColor(new Color(230, 230, 230));
        this.tableModel = new DefaultTableModel();
        this.table.setModel(this.tableModel);

        if (rowHeaderWidth > 0) {
            final JList rowHeader = new JList() {

                private static final long serialVersionUID = 1L;

                @Override
                protected void processMouseEvent(MouseEvent event) {
                    super.processMouseEvent(event);

                    if (event.isPopupTrigger()) {
                        event.setSource(table);
                        event.translatePoint(-rowHeaderWidth, 0);
                        final EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
                        eventQueue.postEvent(event);
                    }
                }

                @Override
                protected void processKeyEvent(KeyEvent event) {
                    super.processKeyEvent(event);
                    if (event.getID() == KeyEvent.KEY_PRESSED) {
                        if (editable) {
                            if (event.isControlDown() && (event.getKeyCode() == 'v' || event.getKeyCode() == 'V')) {
                                pasteRows();
                            } else if (event.isControlDown() && (event.getKeyCode() == 'x' || event.getKeyCode() == 'X')) {
                                cutRows();
                            }
                        }
                        if (event.isControlDown() && (event.getKeyCode() == 'c' || event.getKeyCode() == 'C')) {
                            copyRows();
                        }
                    }
                }
            };

            rowHeader.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    table.editingStopped(new ChangeEvent(this));
                    final int[] selectedIndices = rowHeader.getSelectedIndices();
                    table.clearSelection();
                    for (int i = 0; i < selectedIndices.length; i++) {
                        table.addRowSelectionInterval(selectedIndices[i], selectedIndices[i]);
                    }
                }
            });
            rowHeader.setFixedCellWidth(rowHeaderWidth);
            rowHeader.setFixedCellHeight(this.table.getRowHeight());
            rowHeader.setCellRenderer(new RowHeaderRenderer(this.table));
            rowHeader.setBackground(this.table.getTableHeader().getBackground());
            this.listModel = new DefaultListModel();
            rowHeader.setModel(this.listModel);
            this.setRowHeaderView(rowHeader);
        }

        this.setViewportView(this.table);
        this.setPreferredSize(new Dimension(width, height - 10));
    }

    public void addColumnHeader(String value, int width) {
        final TableColumnModel columnModel = this.table.getColumnModel();
        final int[] oldWidth = new int[columnModel.getColumnCount()];
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            final TableColumn column = columnModel.getColumn(i);
            oldWidth[i] = column.getPreferredWidth();
        }
        this.tableModel.addColumn(value);
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            final TableColumn column = columnModel.getColumn(i);
            column.setHeaderRenderer(this.headerRenderer);
            final PanelCellEditor cellEditor = this.cellEditorMap.get(i);
            if (cellEditor != null) {
                column.setCellEditor(cellEditor);
                column.setCellRenderer(cellEditor);
            } else {
                column.setCellEditor(new CustomCellEditor(this.table));
            }
            if (i == columnModel.getColumnCount() - 1) {
                column.setPreferredWidth(width);
            } else {
                column.setPreferredWidth(oldWidth[i]);
            }
        }
    }

    public void addRow(final String headerValue, final Object[] values) {
        if (EventQueue.isDispatchThread()) {
            if (listModel != null) {
                listModel.addElement(headerValue);
                if (listModel.size() >= 2) {
                    listModel.set(listModel.size() - 2, String.valueOf(listModel.size() - 1));
                }
            }
            tableModel.addRow(values);

        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        if (listModel != null) {
                            listModel.addElement(headerValue);
                            if (listModel.size() >= 2) {
                                listModel.set(listModel.size() - 2, String.valueOf(listModel.size() - 1));
                            }
                        }
                        tableModel.addRow(values);

                    }
                });

            } catch (final InterruptedException e) {} catch (final InvocationTargetException e) {}
        }

        if (cellEditWorker != null) {
            cellEditWorker.changeRowNum();
        }
    }

    public void addRow(final int row, final String headerValue, final Object[] values) {
        if (EventQueue.isDispatchThread()) {
            if (listModel != null) {
                listModel.add(row, headerValue);
                for (int i = row; i < listModel.getSize() - 1; i++) {
                    listModel.set(i, String.valueOf(i + 1));
                }
                listModel.set(listModel.getSize() - 1, "+");
            }
            tableModel.insertRow(row, values);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        if (listModel != null) {
                            listModel.add(row, headerValue);
                            for (int i = row; i < listModel.getSize() - 1; i++) {
                                listModel.set(i, String.valueOf(i + 1));
                            }
                            listModel.set(listModel.getSize() - 1, "+");
                        }
                        tableModel.insertRow(row, values);
                    }
                });
            } catch (final InterruptedException e) {} catch (final InvocationTargetException e) {}
        }

        if (cellEditWorker != null) {
            cellEditWorker.changeRowNum();
        }
    }

    public void removeSelectedRows() {
        if (EventQueue.isDispatchThread()) {
            final int[] rows = table.getSelectedRows();

            for (int i = rows.length - 1; i >= 0; i--) {
                removeRow(rows[i]);
            }

        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        final int[] rows = table.getSelectedRows();

                        for (int i = rows.length - 1; i >= 0; i--) {
                            removeRow(rows[i]);
                        }
                    }
                });

            } catch (final InterruptedException e) {} catch (final InvocationTargetException e) {}
        }
    }

    public void removeRow(final int row) {
        if (EventQueue.isDispatchThread()) {
            tableModel.removeRow(row);
            if (listModel != null) {
                listModel.remove(row);
                for (int i = row; i < listModel.getSize() - 1; i++) {
                    listModel.set(i, String.valueOf(i + 1));
                }
            }

        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        tableModel.removeRow(row);
                        if (listModel != null) {
                            listModel.remove(row);
                            for (int i = row; i < listModel.getSize() - 1; i++) {
                                listModel.set(i, String.valueOf(i + 1));
                            }
                        }
                    }
                });
            } catch (final InterruptedException e) {} catch (final InvocationTargetException e) {}
        }
        if (cellEditWorker != null) {
            cellEditWorker.changeRowNum();
        }
    }

    public void removeAllRow() {
        while (tableModel.getRowCount() > 0) {
            removeRow(0);
        }
    }

    public void removeData() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    if (listModel != null) {
                        listModel.removeAllElements();
                    }

                    tableModel.setRowCount(0);
                    tableModel.setColumnCount(0);
                }
            });

        } catch (final InterruptedException e) {} catch (final InvocationTargetException e) {}

        if (cellEditWorker != null) {
            cellEditWorker.changeRowNum();
        }
    }

    public int getItemCount() {
        return this.tableModel.getRowCount();
    }

    public int getColumnCount() {
        return this.tableModel.getColumnCount();
    }

    public Object getValueAt(int row, int column) {
        return this.tableModel.getValueAt(row, column);
    }

    public void setValueAt(Object value, int row, int column) {
        this.tableModel.setValueAt(value, row, column);
    }

    public int[] getSelection() {
        return this.table.getSelectedRows();
    }

    public void setCellEditWorker(CellEditWorker cellEditWorker) {
        this.cellEditWorker = cellEditWorker;
    }

    /**
     * カラムテーブルの選択されている部分をコピーします。
     */
    private void copyToClipboard() {

        // 選択されている行を取得
        final int[] selectedRows = this.getSelection();

        if (selectedRows.length == 0) {
            return;
        }

        final StringBuilder builder = new StringBuilder();

        // 全てが選択されている場合はヘッダもコピー
        // if (selectedRows.length == this.editColumnTable.getItemCount()) {
        // for (TableColumn c : this.editColumnTable.getColumns()) {
        // builder.append(c.getText());
        // builder.append("\t");
        // }
        // builder.deleteCharAt(builder.length() - 1);
        // builder.append("\r\n");
        // }

        final int columnCount = this.getColumnCount();

        for (final int selectedRow : selectedRows) {
            for (int column = 0; column < columnCount; column++) {
                final Object value = this.getValueAt(selectedRow, column);
                builder.append(Format.toString(value));
                builder.append("\t");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("\r\n");
        }

        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(new StringSelection(builder.toString()), this);
    }

    /**
     * カラムテーブルにクリップボードの内容を貼り付けます。
     */
    private int pasteFromClipboard(boolean insert) {
        int count = 0;
        if (this.getSelection().length == 0) {
            return 0;
        }

        int row = this.getSelection()[0];

        // クリップボードから読み込み
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        final Transferable transferable = clipboard.getContents(this);

        if (transferable == null || !transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return count;
        }
        String data;
        try {
            data = (String) transferable.getTransferData(DataFlavor.stringFlavor);

            final Scanner scanner = new Scanner(data);
            Scanner lineScanner = null;
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                lineScanner = new Scanner(line);
                lineScanner.useDelimiter("\t");

                if (!insert) {
                    if (row == this.getItemCount() - 1) {
                        if (this.cellEditWorker == null) {
                            break;
                        }
                        this.cellEditWorker.addNewRow();
                    }

                    int column = 0;
                    while (lineScanner.hasNext()) {
                        final String text = lineScanner.next();
                        this.setValueAt(text, row, column++);
                    }

                } else {
                    final List<String> texts = new ArrayList<String>();

                    while (lineScanner.hasNext()) {
                        final String text = lineScanner.next();
                        texts.add(text);
                    }

                    this.addRow(row, "", texts.toArray(new String[texts.size()]));
                }

                row++;
                count++;
            }

        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }

        return count;
    }

    private class TablePopupMenu extends JPopupMenu {
        private static final long serialVersionUID = 7496925348009972492L;

        private JMenuItem cutMenu;
        private JMenuItem deleteMenu;

        private TablePopupMenu() {
            final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];

            final Font font = new Font(fontData.getName(), Font.PLAIN, 12);

            if (clipbordOn) {
                if (editable) {
                    cutMenu = new JMenuItem(DisplayMessages.getMessage("action.title.cut"));
                    cutMenu.setFont(font);
                    this.add(cutMenu);

                    cutMenu.addActionListener(new ActionListener() {

                        /**
                         * 「切り取り」メニュー選択時処理
                                             * @param even
                         *            イベント
                         */
                        @Override
                        public void actionPerformed(ActionEvent even) {
                            cutRows();
                        }

                    });
                }

                final JMenuItem copyMenu = new JMenuItem(DisplayMessages.getMessage("action.title.copy"));
                copyMenu.setFont(font);
                this.add(copyMenu);

                copyMenu.addActionListener(new ActionListener() {

                    /**
                     * 「コピー」メニュー選択時処理
                                     * @param even
                     *            イベント
                     */
                    @Override
                    public void actionPerformed(ActionEvent even) {
                        copyRows();
                    }

                });

                if (editable) {
                    final JMenuItem pasteMenu = new JMenuItem(DisplayMessages.getMessage("action.title.paste"));
                    pasteMenu.setFont(font);
                    this.add(pasteMenu);

                    pasteMenu.addActionListener(new ActionListener() {

                        /**
                         * 「貼り付け」メニュー選択時処理
                                             * @param even
                         *            イベント
                         */
                        @Override
                        public void actionPerformed(ActionEvent even) {
                            pasteRows();
                        }

                    });

                    this.addSeparator();
                }
            }

            if (editable) {
                final JMenuItem insertMenu = new JMenuItem(DisplayMessages.getMessage("action.title.insert"));
                insertMenu.setFont(font);
                this.add(insertMenu);

                insertMenu.addActionListener(new ActionListener() {

                    /**
                     * 「挿入」メニュー選択時処理
                                     * @param even
                     *            イベント
                     */
                    @Override
                    public void actionPerformed(ActionEvent even) {
                        insertRow();
                    }

                });

                if (clipbordOn) {
                    final JMenuItem insertPasteMenu = new JMenuItem(DisplayMessages.getMessage("action.title.insert.and.paste"));
                    insertPasteMenu.setFont(font);
                    this.add(insertPasteMenu);

                    insertPasteMenu.addActionListener(new ActionListener() {

                        /**
                         * 「挿入 して貼り付け」メニュー選択時処理
                                             * @param even
                         *            イベント
                         */
                        @Override
                        public void actionPerformed(ActionEvent even) {
                            insertAndPasteRows();
                        }

                    });
                }

                this.deleteMenu = new JMenuItem(DisplayMessages.getMessage("label.delete"));
                deleteMenu.setFont(font);
                this.add(deleteMenu);

                deleteMenu.addActionListener(new ActionListener() {

                    /**
                     * 「削除」メニュー選択時処理
                                     * @param even
                     *            イベント
                     */
                    @Override
                    public void actionPerformed(ActionEvent even) {
                        deleteRows();
                    }

                });
            }
        }

        @Override
        public void show(Component invoker, int x, int y) {
            final int[] selectedIndexes = getSelection();
            if (selectedIndexes.length == 0 || selectedIndexes[selectedIndexes.length - 1] == table.getRowCount() - 1) {
                if (this.cutMenu != null) {
                    this.cutMenu.setEnabled(false);
                    this.deleteMenu.setEnabled(false);
                }

            } else {
                if (this.cutMenu != null) {
                    this.cutMenu.setEnabled(true);
                    this.deleteMenu.setEnabled(true);
                }
            }

            super.show(invoker, x, y);
        }

    }

    private static class RowHeaderRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = 1L;

        public RowHeaderRenderer(JTable table) {
            final JTableHeader header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private static class MultiLineHeaderRenderer extends JList implements TableCellRenderer {

        private static final long serialVersionUID = 1L;

        public MultiLineHeaderRenderer(Color backgroundColor, boolean iconEnable) {
            this.setCellRenderer(new IconListCellRenderer(iconEnable));

            setOpaque(true);
            setForeground(UIManager.getColor("TableHeader.foreground"));
            if (backgroundColor == null) {
                backgroundColor = UIManager.getColor("TableHeader.background");
            }
            setBackground(backgroundColor);

            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            final JLabel renderer = (JLabel) getCellRenderer();
            renderer.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            final String str = (value == null) ? "" : value.toString();
            final BufferedReader br = new BufferedReader(new StringReader(str));
            String line;
            final Vector<String> v = new Vector<String>();
            try {
                while ((line = br.readLine()) != null) {
                    v.addElement(line);
                }
            } catch (final IOException ex) {}

            setListData(v);
            return this;
        }

        public static class IconListCellRenderer extends DefaultListCellRenderer {
            private static final long serialVersionUID = -1712884508057784069L;

            private static final ImageIcon ICON;

            static {
                try {
                    final URL iconUrl = RowHeaderTable.class.getResource("/wrench.png");
                    ICON = new ImageIcon(iconUrl);

                } catch (final Exception e) {
                    e.printStackTrace();
                    throw new ExceptionInInitializerError();
                }
            }

            private final boolean iconEnable;

            private IconListCellRenderer(boolean iconEnable) {
                this.iconEnable = iconEnable;
            }

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
                final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                if (this.iconEnable && index == 0) {
                    label.setIcon(ICON);
                } else if (index == 1) {
                    final Font font = label.getFont().deriveFont(10.0f);
                    label.setFont(font);
                    label.setForeground(Color.GRAY);
                }
                return label;
            }
        }

    }

    public void setClipbordOn(boolean clipbordOn) {
        this.clipbordOn = clipbordOn;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public void setCellEditor(int column, PanelCellEditor cellEditor) {
        this.cellEditorMap.put(column, cellEditor);
    }

    public PanelCellEditor getCellEditor(int column) {
        return this.cellEditorMap.get(column);
    }

    public void setHeaderClickListener(HeaderClickListener headerClickListener) {
        this.headerClickListener = headerClickListener;
    }

    private void copyRows() {
        // テーブルからクリップボードへコピー
        copyToClipboard();
    }

    private void cutRows() {
        // テーブルからクリップボードへコピー
        copyToClipboard();
        removeSelectedRows();
    }

    private void pasteRows() {
        // 貼り付け
        final int[] selectedRows = getSelection();
        if (selectedRows.length == 0) {
            return;
        }

        // クリップボードからテーブルへ貼り付け
        final int count = pasteFromClipboard(false);

        table.clearSelection();
        table.addRowSelectionInterval(selectedRows[0], selectedRows[0] + count - 1);
    }

    public void insertRow() {
        final int[] selectedRows = getSelection();

        if (selectedRows.length == 0) {
            return;
        }

        addRow(selectedRows[0], "", null);

        table.clearSelection();
        table.addRowSelectionInterval(selectedRows[0], selectedRows[0]);
    }

    public void insertAndPasteRows() {
        final int[] selectedRows = getSelection();

        if (selectedRows.length == 0) {
            return;
        }

        final int count = pasteFromClipboard(true);

        table.clearSelection();
        table.addRowSelectionInterval(selectedRows[0], selectedRows[0] + count - 1);
    }

    public void deleteRows() {
        removeSelectedRows();
    }
}

package org.dbflute.erflute.core.widgets;

import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.view.dialog.common.EditableTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class ListenerAppender {

    public static void addTextAreaListener(final Text text, final AbstractDialog dialog, boolean selectAll, boolean imeOn) {
        addFocusListener(text, selectAll, imeOn);
        addTraverseListener(text);
        if (dialog != null) {
            addModifyListener(text, dialog);
        }
    }

    public static void addTextListener(final Text text, final AbstractDialog dialog, boolean imeOn) {
        addFocusListener(text, imeOn);
        if (dialog != null) {
            addModifyListener(text, dialog);
        }
    }

    public static void addFocusListener(final Text text, final boolean imeOn) {
        addFocusListener(text, true, imeOn);
    }

    public static void addFocusListener(final Text text, final boolean selectAll, final boolean imeOn) {
        text.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                final ERDiagram diagram = (ERDiagram) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .getActiveEditor()
                        .getAdapter(ERDiagram.class);

                if (diagram != null) {
                    if (diagram.getDiagramContents().getSettings().isAutoImeChange()) {
                        if (imeOn) {
                            text.getShell().setImeInputMode(SWT.DBCS | SWT.NATIVE);

                        } else {
                            text.getShell().setImeInputMode(SWT.ALPHA);
                        }
                    }
                }

                if (selectAll) {
                    text.selectAll();
                }

                super.focusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
            }
        });
    }

    public static void addTraverseListener(final Text textArea) {
        textArea.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                    e.doit = true;
                }
            }
        });
    }

    public static void addModifyListener(final Text text, final AbstractDialog dialog) {
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                dialog.validate();
            }
        });
    }

    public static void addComboListener(final Combo combo, final AbstractDialog dialog, final boolean imeOn) {
        if (dialog != null) {
            combo.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    dialog.validate();
                }
            });
        }

        combo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                final ERDiagram diagram = (ERDiagram) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .getActiveEditor()
                        .getAdapter(ERDiagram.class);

                if (diagram != null) {
                    if (diagram.getDiagramContents().getSettings().isAutoImeChange()) {
                        if (imeOn) {
                            combo.getShell().setImeInputMode(SWT.DBCS | SWT.NATIVE);

                        } else {
                            combo.getShell().setImeInputMode(SWT.ALPHA);
                        }
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
    }

    public static void addCheckBoxListener(final Button button, final AbstractDialog dialog) {
        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.validate();
            }
        });

        button.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                final ERDiagram diagram = (ERDiagram) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .getActiveEditor()
                        .getAdapter(ERDiagram.class);

                if (diagram != null) {
                    if (diagram.getDiagramContents().getSettings().isAutoImeChange()) {
                        button.getShell().setImeInputMode(SWT.ALPHA);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
    }

    public static void addTableEditListener(final Table table, final TableEditor tableEditor, final EditableTable editableTable) {

        table.addMouseListener(new MouseAdapter() {

            private Point getSelectedCell(MouseEvent e) {
                final int vIndex = table.getSelectionIndex();
                if (vIndex != -1) {
                    final TableItem item = table.getItem(vIndex);
                    for (int hIndex = 0; hIndex < table.getColumnCount(); hIndex++) {
                        if (item.getBounds(hIndex).contains(e.x, e.y)) {
                            final Point xy = new Point(hIndex, vIndex);
                            xy.y = vIndex;
                            xy.x = hIndex;
                            return xy;
                        }
                    }
                }
                return null;
            }

            @Override
            public void mouseDown(MouseEvent event) {
                if (!editableTable.validate()) {
                    return;
                }
                try {
                    final Point xy = getSelectedCell(event);
                    if (xy != null) {
                        final TableItem tableItem = table.getItem(xy.y);
                        createEditor(table, tableItem, tableEditor, xy, editableTable);
                    }
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent event) {
                try {
                    final Point xy = getSelectedCell(event);
                    if (xy != null) {
                        editableTable.onDoubleClicked(xy);
                    }
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
    }

    private static void createEditor(final Table table, final TableItem tableItem,
            final TableEditor tableEditor, final Point xy, final EditableTable editableTable) {
        final Control control = editableTable.getControl(xy);
        if (control == null) {
            return;
        }

        if (control instanceof Text) {
            final Text text = (Text) control;
            text.setText(tableItem.getText(xy.x));
        }

        // フォーカスが外れたときの処理
        control.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                setEditValue(control, tableItem, xy, editableTable);
            }
        });

        // ENTERとESCが押されたときの処理
        control.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent keyevent) {
                if (keyevent.character == SWT.CR) {
                    setEditValue(control, tableItem, xy, editableTable);

                } else if (keyevent.character == SWT.ESC) {
                    control.dispose();
                }
            }
        });

        tableEditor.setEditor(control, tableItem, xy.x);
        control.setFocus();
        table.setSelection(new int[0]);

        if (control instanceof Text) {
            final Text text = (Text) control;
            text.selectAll();
        }
    }

    private static void setEditValue(Control control, TableItem tableItem, Point xy, EditableTable editableTable) {
        editableTable.setData(xy, control);
        if (editableTable.validate()) {
            control.dispose();
        }
    }

    public static void addTabListener(final TabFolder tabFolder, final List<ValidatableTabWrapper> tabWrapperList) {
        tabFolder.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                final int index = tabFolder.getSelectionIndex();

                final ValidatableTabWrapper selectedTabWrapper = tabWrapperList.get(index);
                selectedTabWrapper.setInitFocus();
            }
        });
    }

    public static void addModifyListener(final Scale scale, final Spinner spinner, final int diff, final AbstractDialog dialog) {
        if (dialog != null) {
            spinner.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    final int value = spinner.getSelection();
                    scale.setSelection(value - diff);
                    dialog.validate();
                }
            });

            scale.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    final int value = scale.getSelection();
                    spinner.setSelection(value + diff);
                    dialog.validate();
                }
            });
        }
    }
}

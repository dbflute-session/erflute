package org.dbflute.erflute.editor.view.information;

import org.dbflute.erflute.editor.ERFluteMultiPageEditor;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.VirtualDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.view.outline.ERDiagramOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;

public class ERDiagramInformationControl extends AbstractInformationControl {

    private ERDiagramOutlinePage outline;
    private Text search;
    private final ERDiagram diagram;

    public ERDiagramInformationControl(ERDiagram diagram, Shell shell, Control composite) {
        super(shell, true);
        this.diagram = diagram;

        create();

        final int width = 300;
        final int height = 300;

        final Point loc = composite.toDisplay(0, 0);
        final Point size = composite.getSize();

        final int overX = diagram.getMousePoint().x + width - size.x;
        final int overY = diagram.getMousePoint().y + width - size.y;

        final int x = diagram.getMousePoint().x + loc.x - (0 < overX ? overX : 0);
        final int y = diagram.getMousePoint().y + loc.y - (0 < overY ? overY : 0);

        setSize(width, height);
        setLocation(new Point(x, y));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                dispose();
            }
        });
    }

    @Override
    protected void createContent(Composite parent) {
        final Color foreground = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        final Color background = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);

        final Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(1, false));
        composite.setForeground(foreground);
        composite.setBackground(background);

        search = new Text(composite, SWT.NONE);
        search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        search.setForeground(foreground);
        search.setBackground(background);

        new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Dialog.applyDialogFont(search);

        search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        search.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                final String filterText = search.getText();
                outline.setFilterText(filterText);
            }
        });
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    selectAndDispose();
                }
                if (e.keyCode == SWT.ARROW_UP) {
                    outline.getControl().setFocus();
                }
                if (e.keyCode == SWT.ARROW_DOWN) {
                    outline.getControl().setFocus();
                }
            }
        });

        final Composite treeArea = new Composite(composite, SWT.NULL);
        treeArea.setLayout(new FillLayout());
        treeArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        outline = new ERDiagramOutlinePage(diagram);
        outline.setQuickMode(true);

        final IEditorPart activeEditor = ((ERFluteMultiPageEditor) ERModelUtil.getActiveEditor()).getActiveEditor();
        if (activeEditor instanceof VirtualDiagramEditor) {
            final VirtualDiagramEditor editor = (VirtualDiagramEditor) activeEditor;
            outline.setCategory(editor.getDefaultEditDomain(), editor.getGraphicalViewer(), editor.getDefaultActionRegistry());
        } else {
            final MainDiagramEditor editor = (MainDiagramEditor) activeEditor;
            outline.setCategory(editor.getDefaultEditDomain(), editor.getGraphicalViewer(), editor.getDefaultActionRegistry());
        }

        outline.createControl(treeArea);
        outline.update();

        //		treeViewer.getControl()

        //		outline.getViewer().expandAll();
        //		outline.setSelect(false);
        outline.getViewer().getControl().setForeground(foreground);
        outline.getViewer().getControl().setBackground(background);
        outline.getViewer().getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    selectAndDispose();
                }
            }
        });
        outline.getViewer().getControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                final int TABLE_SELECTION_BUTTON = 3;
                if (e.button == TABLE_SELECTION_BUTTON) {
                    selectAndDispose();
                }
            }
        });

        final TreeViewer treeViewer = (TreeViewer) outline.getViewer();
        final Tree tree = (Tree) treeViewer.getControl();
        expand(tree.getItems());

        //		outline.getViewer().addDoubleClickListener(new IDoubleClickListener() {
        //			public void doubleClick(DoubleClickEvent event) {
        //				selectAndDispose();
        //			}
        //		});
    }

    private void selectAndDispose() {
        outline.selectSelection();
        dispose();
    }

    private void expand(TreeItem[] items) {
        for (int i = 0; i < items.length; i++) {
            expand(items[i].getItems());
            items[i].setExpanded(true);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        search.setFocus();
    }

    @Override
    public boolean hasContents() {
        return true;
    }
}

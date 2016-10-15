package org.dbflute.erflute.editor.view.action.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeBackgroundColorCommand;
import org.dbflute.erflute.editor.controller.editpart.element.node.TableViewEditPart;
import org.dbflute.erflute.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.LabelRetargetAction;

public class ChangeBackgroundColorAction extends SelectionAction {

    public static final String ID = ChangeBackgroundColorAction.class.getName();

    private RGB rgb;

    private Image image;

    public ChangeBackgroundColorAction(IWorkbenchPart part, ERDiagram diagram) {
        super(part, Action.AS_DROP_DOWN_MENU);

        this.setId(ID);

        this.setText(DisplayMessages.getMessage("action.title.change.background.color"));
        this.setToolTipText(DisplayMessages.getMessage("action.title.change.background.color"));

        int[] defaultColor = diagram.getDefaultColor();

        this.rgb = new RGB(defaultColor[0], defaultColor[1], defaultColor[2]);
        this.setColorToImage();
    }

    private void setColorToImage() {
        ImageData imageData = Activator.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR).getImageData();
        int blackPixel = imageData.palette.getPixel(new RGB(0, 0, 0));
        imageData.transparentPixel = imageData.palette.getPixel(new RGB(255, 255, 255));
        imageData.palette.colors[blackPixel] = this.rgb;

        if (this.image != null) {
            // this.image.dispose();
        }
        this.image = new Image(Display.getCurrent(), imageData);

        ImageDescriptor descriptor = ImageDescriptor.createFromImage(image);
        this.setImageDescriptor(descriptor);
        if (this.getSelection() instanceof StructuredSelection) {
            for (Object element : ((StructuredSelection) this.getSelection()).toList()) {
                if (element instanceof TableViewEditPart) {
                    ((TableViewEditPart) element).refresh();
                    ((TableViewEditPart) element).refreshVisuals();
                }
            }
        }
    }

    private void setRGB(RGB rgb) {
        this.rgb = rgb;

        EditPart editPart = ((MainDiagramEditor) this.getWorkbenchPart()).getGraphicalViewer().getContents();

        if (editPart.getModel() instanceof ERVirtualDiagram) {
            // �r���[�̔w�i�F�ύX
            ERVirtualDiagram model = (ERVirtualDiagram) editPart.getModel();
            model.setDefaultColor(this.rgb.red, this.rgb.green, this.rgb.blue);
        } else {
            // �S�̃r���[�̔w�i�F�ύX
            ERDiagram diagram = ERModelUtil.getDiagram(editPart);
            diagram.setDefaultColor(this.rgb.red, this.rgb.green, this.rgb.blue);
        }

        this.setColorToImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runWithEvent(Event event) {
        Command command = this.createCommand(this.getSelectedObjects(), rgb);
        this.getCommandStack().execute(command);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List getSelectedObjects() {
        List objects = new ArrayList(super.getSelectedObjects());
        for (Iterator iter = objects.iterator(); iter.hasNext();) {
            if (iter.next() instanceof NormalColumnEditPart) {
                iter.remove();
            }
        }
        return objects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        List objects = this.getSelectedObjects();

        if (objects.isEmpty()) {
            return false;
        }

        if (!(objects.get(0) instanceof GraphicalEditPart)) {
            return false;
        }

        return true;
    }

    private Command createCommand(List objects, RGB rgb) {
        if (objects.isEmpty()) {
            return null;
        }

        if (!(objects.get(0) instanceof GraphicalEditPart)) {
            return null;
        }

        CompoundCommand command = new CompoundCommand();

        for (int i = 0; i < objects.size(); i++) {
            GraphicalEditPart part = (GraphicalEditPart) objects.get(i);
            command.add(new ChangeBackgroundColorCommand((ViewableModel) part.getModel(), rgb.red, rgb.green, rgb.blue));
        }

        return command;
    }

    public static class ChangeBackgroundColorRetargetAction extends LabelRetargetAction {
        public ChangeBackgroundColorRetargetAction() {
            super(ID, DisplayMessages.getMessage("action.title.change.background.color"), Action.AS_DROP_DOWN_MENU);

            this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR));
            this.setDisabledImageDescriptor(Activator.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR_DISABLED));
            this.setToolTipText(DisplayMessages.getMessage("action.title.change.background.color"));

            // �T�u���j���[�̐���
            setMenuCreator(new IMenuCreator() {
                public Menu getMenu(Control parent) {
                    Menu menu = new Menu(parent);

                    try {
                        MenuItem item1 = new MenuItem(menu, SWT.NONE);
                        item1.setText(DisplayMessages.getMessage("action.title.select.color"));
                        item1.setImage(Activator.getImage(ImageKey.PALETTE));

                        item1.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                ColorDialog colorDialog =
                                        new ColorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NULL);

                                colorDialog.setText(DisplayMessages.getMessage("dialog.title.change.background.color"));

                                ChangeBackgroundColorAction action = (ChangeBackgroundColorAction) getActionHandler();

                                RGB rgb = colorDialog.open();

                                action.setRGB(rgb);
                                action.runWithEvent(null);
                            }
                        });
                    } catch (Exception e) {
                        Activator.showExceptionDialog(e);
                    }
                    return menu;
                }

                public Menu getMenu(Menu parent) {
                    return null;
                }

                public void dispose() {

                }
            });
        }
    }

    @Override
    public void dispose() {
        image.dispose();

        super.dispose();
    }
}

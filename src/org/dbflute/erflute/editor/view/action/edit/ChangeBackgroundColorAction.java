package org.dbflute.erflute.editor.view.action.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DesignResources;
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

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ChangeBackgroundColorAction extends SelectionAction {

    public static final String ID = ChangeBackgroundColorAction.class.getName();
    private RGB rgb;
    private Image image;

    public ChangeBackgroundColorAction(IWorkbenchPart part, ERDiagram diagram) {
        super(part, Action.AS_DROP_DOWN_MENU);
        this.setId(ID);
        this.setText(DisplayMessages.getMessage("action.title.change.background.color"));
        this.setToolTipText(DisplayMessages.getMessage("action.title.change.background.color"));
        final int[] defaultColor = diagram.getDefaultColor();
        this.rgb = new RGB(defaultColor[0], defaultColor[1], defaultColor[2]);
        this.setColorToImage();
    }

    private void setColorToImage() {
        final ImageData imageData = Activator.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR).getImageData();
        final int blackPixel = imageData.palette.getPixel(new RGB(0, 0, 0));
        imageData.transparentPixel = imageData.palette.getPixel(new RGB(255, 255, 255));
        imageData.palette.colors[blackPixel] = this.rgb;

        if (this.image != null) {
            // this.image.dispose();
        }
        this.image = new Image(Display.getCurrent(), imageData);

        final ImageDescriptor descriptor = ImageDescriptor.createFromImage(image);
        this.setImageDescriptor(descriptor);
        if (this.getSelection() instanceof StructuredSelection) {
            for (final Object element : ((StructuredSelection) this.getSelection()).toList()) {
                if (element instanceof TableViewEditPart) {
                    ((TableViewEditPart) element).refresh();
                    ((TableViewEditPart) element).refreshVisuals();
                }
            }
        }
    }

    private void setRGB(RGB rgb) {
        this.rgb = rgb;
        final EditPart editPart = ((MainDiagramEditor) this.getWorkbenchPart()).getGraphicalViewer().getContents();
        if (editPart.getModel() instanceof ERVirtualDiagram) {
            final ERVirtualDiagram model = (ERVirtualDiagram) editPart.getModel();
            model.setDefaultColor(DesignResources.getColor(rgb));
        } else {
            final ERDiagram diagram = ERModelUtil.getDiagram(editPart);
            diagram.setDefaultColor(DesignResources.getColor(rgb));
        }
        this.setColorToImage();
    }

    @Override
    public void runWithEvent(Event event) {
        final Command command = this.createCommand(this.getSelectedObjects(), rgb);
        this.getCommandStack().execute(command);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected List getSelectedObjects() {
        final List<?> objects = new ArrayList<Object>(super.getSelectedObjects());
        for (final Iterator<?> iter = objects.iterator(); iter.hasNext();) {
            if (iter.next() instanceof NormalColumnEditPart) {
                iter.remove();
            }
        }
        return objects;
    }

    @Override
    protected boolean calculateEnabled() {
        final List<?> objects = this.getSelectedObjects();
        if (objects.isEmpty()) {
            return false;
        }
        if (!(objects.get(0) instanceof GraphicalEditPart)) {
            return false;
        }
        return true;
    }

    private Command createCommand(List<?> objects, RGB rgb) {
        if (objects.isEmpty()) {
            return null;
        }
        if (!(objects.get(0) instanceof GraphicalEditPart)) {
            return null;
        }
        final CompoundCommand command = new CompoundCommand();
        for (int i = 0; i < objects.size(); i++) {
            final GraphicalEditPart part = (GraphicalEditPart) objects.get(i);
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

            setMenuCreator(new IMenuCreator() { // for sub menu
                @Override
                public Menu getMenu(Control parent) {
                    final Menu menu = new Menu(parent);
                    try {
                        final MenuItem item1 = new MenuItem(menu, SWT.NONE);
                        item1.setText(DisplayMessages.getMessage("action.title.select.color"));
                        item1.setImage(Activator.getImage(ImageKey.PALETTE));
                        item1.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                final ColorDialog colorDialog =
                                        new ColorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NULL);
                                colorDialog.setText(DisplayMessages.getMessage("dialog.title.change.background.color"));
                                final ChangeBackgroundColorAction action = (ChangeBackgroundColorAction) getActionHandler();
                                final RGB rgb = colorDialog.open();
                                action.setRGB(rgb);
                                action.runWithEvent(null);
                            }
                        });
                    } catch (final Exception e) {
                        Activator.showExceptionDialog(e);
                    }
                    return menu;
                }

                @Override
                public Menu getMenu(Menu parent) {
                    return null;
                }

                @Override
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

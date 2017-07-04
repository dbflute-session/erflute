package org.dbflute.erflute.editor.view.contributor;

import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.view.action.dbexport.ExportToDBAction;
import org.dbflute.erflute.editor.view.action.dbexport.ExportToDBAction.ExportToDBRetargetAction;
import org.dbflute.erflute.editor.view.action.dbexport.ExportToDDLAction;
import org.dbflute.erflute.editor.view.action.edit.ChangeBackgroundColorAction;
import org.dbflute.erflute.editor.view.action.edit.ChangeBackgroundColorAction.ChangeBackgroundColorRetargetAction;
import org.dbflute.erflute.editor.view.action.line.HorizontalLineAction;
import org.dbflute.erflute.editor.view.action.line.HorizontalLineAction.HorizontalLineRetargetAction;
import org.dbflute.erflute.editor.view.action.line.VerticalLineAction;
import org.dbflute.erflute.editor.view.action.line.VerticalLineAction.VerticalLineRetargetAction;
import org.dbflute.erflute.editor.view.action.option.notation.LockEditAction;
import org.dbflute.erflute.editor.view.action.option.notation.ToggleMainColumnAction;
import org.dbflute.erflute.editor.view.action.zoom.ZoomAdjustAction;
import org.dbflute.erflute.editor.view.action.zoom.ZoomAdjustRetargetAction;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

public class ERDiagramActionBarContributor extends ActionBarContributor {

    private final ZoomComboContributionItem zoomComboContributionItem;

    public ERDiagramActionBarContributor(ZoomComboContributionItem zoomComboContributionItem) {
        this.zoomComboContributionItem = zoomComboContributionItem;
    }

    @Override
    protected void buildActions() {
        addRetargetAction(new RetargetAction(ActionFactory.SELECT_ALL.getId(), "selectAll"));
        addRetargetAction(new RetargetAction(ActionFactory.PRINT.getId(), "print"));

        addRetargetAction(new DeleteRetargetAction());
        addRetargetAction(new RetargetAction(ActionFactory.COPY.getId(), "copy"));
        addRetargetAction(new RetargetAction(ActionFactory.PASTE.getId(), "paste"));

        addRetargetAction(new UndoRetargetAction());
        addRetargetAction(new RedoRetargetAction());

        final ZoomInRetargetAction zoomInAction = new ZoomInRetargetAction();
        zoomInAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ZOOM_IN));
        final ZoomOutRetargetAction zoomOutAction = new ZoomOutRetargetAction();
        zoomOutAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ZOOM_OUT));
        addRetargetAction(zoomInAction);
        addRetargetAction(zoomOutAction);
        addRetargetAction(new ZoomAdjustRetargetAction());

        final RetargetAction gridAction = new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY,
                DisplayMessages.getMessage("action.title.grid"), IAction.AS_CHECK_BOX);
        gridAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.GRID));
        addRetargetAction(gridAction);

        final RetargetAction tooltipAction =
                new RetargetAction(ToggleMainColumnAction.ID, DisplayMessages.getMessage("action.title.tooltip"), IAction.AS_CHECK_BOX);
        tooltipAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.TOOLTIP));
        addRetargetAction(tooltipAction);

        final RetargetAction toggleMainColumnAction =
                new RetargetAction(ToggleMainColumnAction.ID, DisplayMessages.getMessage("action.title.mainColumn"), IAction.AS_CHECK_BOX);
        toggleMainColumnAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.MAIN_COLUMN));
        addRetargetAction(toggleMainColumnAction);

        final RetargetAction exportDdlAction =
                new RetargetAction(ExportToDDLAction.ID, DisplayMessages.getMessage("dialog.title.export.ddl"), IAction.AS_CHECK_BOX);
        exportDdlAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EXPORT_DDL));
        addRetargetAction(exportDdlAction);

        final RetargetAction lockEditAction =
                new RetargetAction(LockEditAction.ID, DisplayMessages.getMessage("action.title.lock.edit"), IAction.AS_CHECK_BOX);
        lockEditAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.LOCK_EDIT));
        addRetargetAction(lockEditAction);

        addRetargetAction(new ExportToDBRetargetAction());

        final AlignmentRetargetAction alignLeftAction = new AlignmentRetargetAction(PositionConstants.LEFT);
        alignLeftAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ALIGN_LEFT));
        alignLeftAction.setDisabledImageDescriptor(null);
        addRetargetAction(alignLeftAction);
        final AlignmentRetargetAction alignCenterAction = new AlignmentRetargetAction(PositionConstants.CENTER);
        alignCenterAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ALIGN_CENTER));
        alignCenterAction.setDisabledImageDescriptor(null);
        addRetargetAction(alignCenterAction);
        final AlignmentRetargetAction alignRightAction = new AlignmentRetargetAction(PositionConstants.RIGHT);
        alignRightAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ALIGN_RIGHT));
        alignRightAction.setDisabledImageDescriptor(null);
        addRetargetAction(alignRightAction);
        final AlignmentRetargetAction alignTopAction = new AlignmentRetargetAction(PositionConstants.TOP);
        alignTopAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ALIGN_TOP));
        alignTopAction.setDisabledImageDescriptor(null);
        addRetargetAction(alignTopAction);
        final AlignmentRetargetAction alignMiddleAction = new AlignmentRetargetAction(PositionConstants.MIDDLE);
        alignMiddleAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ALIGN_MIDDLE));
        alignMiddleAction.setDisabledImageDescriptor(null);
        addRetargetAction(alignMiddleAction);
        final AlignmentRetargetAction alignBottomAction = new AlignmentRetargetAction(PositionConstants.BOTTOM);
        alignBottomAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.ALIGN_BOTTOM));
        alignBottomAction.setDisabledImageDescriptor(null);
        addRetargetAction(alignBottomAction);

        final MatchWidthRetargetAction matchWidthAction = new MatchWidthRetargetAction();
        matchWidthAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.MATCH_WIDTH));
        matchWidthAction.setDisabledImageDescriptor(null);
        addRetargetAction(matchWidthAction);
        final MatchHeightRetargetAction matchHeightAction = new MatchHeightRetargetAction();
        matchHeightAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.MATCH_HEIGHT));
        matchHeightAction.setDisabledImageDescriptor(null);
        addRetargetAction(matchHeightAction);

        addRetargetAction(new HorizontalLineRetargetAction());
        addRetargetAction(new VerticalLineRetargetAction());

        addRetargetAction(new ChangeBackgroundColorRetargetAction());
    }

    @Override
    public void contributeToToolBar(IToolBarManager toolBarManager) {
        toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
        toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
        toolBarManager.add(getAction(ActionFactory.REDO.getId()));
        toolBarManager.add(new Separator());

        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
        toolBarManager.add(getActionRegistry().getAction(ZoomAdjustAction.ID));

        toolBarManager.add(zoomComboContributionItem);

        toolBarManager.add(new Separator());

        toolBarManager.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
        toolBarManager.add(getAction(ToggleMainColumnAction.ID));
        toolBarManager.add(getAction(LockEditAction.ID));

        toolBarManager.add(new Separator());

        toolBarManager.add(getAction(ExportToDDLAction.ID));
        toolBarManager.add(getAction(ExportToDBAction.ID));

        toolBarManager.add(new Separator());

        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT));
        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER));
        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT));

        toolBarManager.add(new Separator());

        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP));
        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE));
        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM));

        toolBarManager.add(new Separator());

        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.MATCH_WIDTH));
        toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.MATCH_HEIGHT));

        toolBarManager.add(new Separator());

        toolBarManager.add(getActionRegistry().getAction(HorizontalLineAction.ID));
        toolBarManager.add(getActionRegistry().getAction(VerticalLineAction.ID));

        toolBarManager.add(getActionRegistry().getAction(ChangeBackgroundColorAction.ID));

        toolBarManager.add(new Separator());

        final FontNameContributionItem fontNameContributionItem = new FontNameContributionItem(getPage());
        final FontSizeContributionItem fontSizeContributionItem = new FontSizeContributionItem(getPage());

        toolBarManager.add(fontNameContributionItem);
        toolBarManager.add(fontSizeContributionItem);

        getPage().addSelectionListener(new ISelectionListener() {

            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                if (selection instanceof IStructuredSelection) {
                    final List<?> selectedEditParts = ((IStructuredSelection) selection).toList();
                    if (!selectedEditParts.isEmpty()) {
                        if (selectedEditParts.get(0) instanceof EditPart) {
                            final Object model = ((EditPart) selectedEditParts.get(0)).getModel();
                            if (model instanceof ViewableModel) {
                                final ViewableModel viewableModel = (ViewableModel) model;

                                final String fontName = viewableModel.getFontName();
                                if (fontName != null) {
                                    fontNameContributionItem.setText(fontName);
                                } else {
                                    final FontData fonData = Display.getCurrent().getSystemFont().getFontData()[0];
                                    fontNameContributionItem.setText(fonData.getName());
                                    viewableModel.setFontName(fonData.getName());
                                }

                                final int fontSize = viewableModel.getFontSize();
                                if (fontSize > 0) {
                                    fontSizeContributionItem.setText(String.valueOf(fontSize));
                                } else {
                                    fontSizeContributionItem.setText(String.valueOf(ViewableModel.DEFAULT_FONT_SIZE));
                                    viewableModel.setFontSize(fontSize);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void declareGlobalActionKeys() {
        addGlobalActionKey(IWorkbenchActionConstants.PRINT_EXT);
    }
}

package org.dbflute.erflute.editor.view.outline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.controller.editpart.outline.columngroup.GroupSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.sequence.SequenceSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.table.TableOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.tablespace.TablespaceSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.trigger.TriggerSetOutlineEditPart;
import org.dbflute.erflute.editor.controller.editpart.outline.vdiagram.ERVirtualDiagramOutlineEditPart;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.view.action.group.ColumnGroupManageAction;
import org.dbflute.erflute.editor.view.action.outline.ChangeVirtualDiagramNameAction;
import org.dbflute.erflute.editor.view.action.outline.DeleteVirtualDiagramAction;
import org.dbflute.erflute.editor.view.action.outline.index.CreateIndexAction;
import org.dbflute.erflute.editor.view.action.outline.notation.type.ChangeOutlineViewToBothAction;
import org.dbflute.erflute.editor.view.action.outline.notation.type.ChangeOutlineViewToLogicalAction;
import org.dbflute.erflute.editor.view.action.outline.notation.type.ChangeOutlineViewToPhysicalAction;
import org.dbflute.erflute.editor.view.action.outline.orderby.ChangeOutlineViewOrderByLogicalNameAction;
import org.dbflute.erflute.editor.view.action.outline.orderby.ChangeOutlineViewOrderByPhysicalNameAction;
import org.dbflute.erflute.editor.view.action.outline.sequence.CreateSequenceAction;
import org.dbflute.erflute.editor.view.action.outline.tablespace.CreateTablespaceAction;
import org.dbflute.erflute.editor.view.action.outline.trigger.CreateTriggerAction;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERDiagramOutlinePopupMenuManager extends MenuManager {

    private static final Map<Class<?>, String> ACTION_MAP = new HashMap<>();

    static {
        ACTION_MAP.put(SequenceSetOutlineEditPart.class, CreateSequenceAction.ID);
        ACTION_MAP.put(TriggerSetOutlineEditPart.class, CreateTriggerAction.ID);
        ACTION_MAP.put(GroupSetOutlineEditPart.class, ColumnGroupManageAction.ID);
        ACTION_MAP.put(TableOutlineEditPart.class, CreateIndexAction.ID);
        ACTION_MAP.put(TablespaceSetOutlineEditPart.class, CreateTablespaceAction.ID);
        ACTION_MAP.put(ERVirtualDiagramOutlineEditPart.class, ChangeVirtualDiagramNameAction.ID);
        ACTION_MAP.put(ERVirtualDiagramOutlineEditPart.class, DeleteVirtualDiagramAction.ID);
    }

    private ActionRegistry actionRegistry;
    private ActionRegistry outlineActionRegistry;

    public ERDiagramOutlinePopupMenuManager(final ERDiagram diagram, ActionRegistry actionRegistry, ActionRegistry outlineActionRegistry,
            final EditPartViewer editPartViewer) {
        try {
            this.actionRegistry = actionRegistry;
            this.outlineActionRegistry = outlineActionRegistry;

            add(getAction(ChangeVirtualDiagramNameAction.ID));
            add(getAction(ColumnGroupManageAction.ID));
            add(getAction(CreateSequenceAction.ID));
            add(getAction(CreateTriggerAction.ID));
            add(getAction(CreateIndexAction.ID));
            add(getAction(CreateTablespaceAction.ID));
            add(getAction(DeleteVirtualDiagramAction.ID));

            add(new Separator());

            final MenuManager viewModeMenu = new MenuManager(DisplayMessages.getMessage("label.outline.view.mode"));
            viewModeMenu.add(getAction(ChangeOutlineViewToPhysicalAction.ID));
            viewModeMenu.add(getAction(ChangeOutlineViewToLogicalAction.ID));
            viewModeMenu.add(getAction(ChangeOutlineViewToBothAction.ID));
            add(viewModeMenu);

            final MenuManager orderByMenu = new MenuManager(DisplayMessages.getMessage("label.order.by"));
            orderByMenu.add(getAction(ChangeOutlineViewOrderByPhysicalNameAction.ID));
            orderByMenu.add(getAction(ChangeOutlineViewOrderByLogicalNameAction.ID));
            add(orderByMenu);

            add(new Separator());
            add(getAction(ActionFactory.DELETE));

            addMenuListener(new IMenuListener() {
                @Override
                public void menuAboutToShow(IMenuManager manager) {
                    try {
                        @SuppressWarnings("unchecked")
                        final List<EditPart> selectedEditParts = editPartViewer.getSelectedEditParts();
                        if (selectedEditParts.isEmpty()) {
                            for (final IContributionItem menuItem : getItems()) {
                                if (menuItem.getId() != null && !menuItem.getId().equals(ChangeOutlineViewToPhysicalAction.ID)
                                        && !menuItem.getId().equals(ChangeOutlineViewToLogicalAction.ID)
                                        && !menuItem.getId().equals(ChangeOutlineViewToBothAction.ID)
                                        && !menuItem.getId().equals(ChangeOutlineViewOrderByPhysicalNameAction.ID)
                                        && !menuItem.getId().equals(ChangeOutlineViewOrderByLogicalNameAction.ID)) {
                                    enabled(menuItem.getId(), false);
                                }
                            }
                        } else {
                            final EditPart editPart = selectedEditParts.get(0);
                            for (final Class<?> clazz : ACTION_MAP.keySet()) {
                                final String actionId = ACTION_MAP.get(clazz);
                                if (!clazz.isInstance(editPart)) {
                                    enabled(actionId, false);
                                } else {
                                    if (CreateSequenceAction.ID.equals(actionId)
                                            && !DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE)) {
                                        enabled(actionId, false);
                                    } else {
                                        enabled(actionId, true);
                                    }
                                }
                            }
                        }

                        final DiagramSettings settings = diagram.getDiagramContents().getSettings();

                        IAction action0 = getAction(ChangeOutlineViewToPhysicalAction.ID);
                        IAction action1 = getAction(ChangeOutlineViewToLogicalAction.ID);
                        final IAction action2 = getAction(ChangeOutlineViewToBothAction.ID);
                        if (settings.getOutlineViewMode() == DiagramSettings.VIEW_MODE_PHYSICAL) {
                            action0.setChecked(true);
                            action1.setChecked(false);
                            action2.setChecked(false);
                        } else if (settings.getOutlineViewMode() == DiagramSettings.VIEW_MODE_LOGICAL) {
                            action0.setChecked(false);
                            action1.setChecked(true);
                            action2.setChecked(false);
                        } else {
                            action0.setChecked(false);
                            action1.setChecked(false);
                            action2.setChecked(true);
                        }
                        action0 = getAction(ChangeOutlineViewOrderByPhysicalNameAction.ID);
                        action1 = getAction(ChangeOutlineViewOrderByLogicalNameAction.ID);
                        if (settings.getViewOrderBy() == DiagramSettings.VIEW_MODE_PHYSICAL) {
                            action0.setChecked(true);
                            action1.setChecked(false);
                        } else {
                            action0.setChecked(false);
                            action1.setChecked(true);
                        }
                        manager.update(true);
                    } catch (final Exception e) {
                        Activator.showExceptionDialog(e);
                    }
                }
            });
        } catch (final Exception e) {
            Activator.showExceptionDialog(e);
        }
    }

    private IAction getAction(ActionFactory actionFactory) {
        return actionRegistry.getAction(actionFactory.getId());
    }

    private IAction getAction(String id) {
        IAction action = actionRegistry.getAction(id);

        if (action == null) {
            action = outlineActionRegistry.getAction(id);
        }

        return action;
    }

    private void enabled(String id, boolean enabled) {
        final IAction action = getAction(id);
        action.setEnabled(enabled);
    }
}

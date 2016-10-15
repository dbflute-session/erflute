package org.dbflute.erflute.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.db.impl.oracle.OracleDBManager;
import org.dbflute.erflute.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence.CreateSequenceCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.sequence.DeleteSequenceCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger.CreateTriggerCommand;
import org.dbflute.erflute.editor.controller.command.diagram_contents.not_element.trigger.DeleteTriggerCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.dialog.table.TableDialog;
import org.dbflute.erflute.editor.view.figure.table.TableFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ERTableEditPart extends TableViewEditPart implements IResizable {

    public ERTableEditPart() {
    }

    @Override
    protected IFigure createFigure() {
        final ERDiagram diagram = this.getDiagram();
        final Settings settings = diagram.getDiagramContents().getSettings();
        final TableFigure figure = new TableFigure(settings);
        this.changeFont(figure);
        return figure;
    }

    @Override
    public void doPropertyChange(PropertyChangeEvent event) {
        super.doPropertyChange(event);
    }

    @Override
    public void performRequestOpen() {
        final ERTable table = (ERTable) this.getModel();
        final ERDiagram diagram = this.getDiagram();
        final ERTable copyTable = table.copyData();
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final EditPartViewer viewer = getViewer();
        final ColumnGroupSet groups = diagram.getDiagramContents().getColumnGroupSet();
        final TableDialog dialog = new TableDialog(shell, viewer, copyTable, groups);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final CompoundCommand command = createChangeTablePropertyCommand(diagram, table, copyTable);
            executeCommand(command.unwrap());
        }
    }

    public static CompoundCommand createChangeTablePropertyCommand(ERDiagram diagram, ERTable table, ERTable copyTable) {
        final CompoundCommand command = new CompoundCommand();
        final ChangeTableViewPropertyCommand changeTablePropertyCommand = new ChangeTableViewPropertyCommand(table, copyTable);
        command.add(changeTablePropertyCommand);
        final String tableName = copyTable.getPhysicalName();
        if (OracleDBManager.ID.equals(diagram.getDatabase()) && !Check.isEmpty(tableName)) {
            final NormalColumn autoIncrementColumn = copyTable.getAutoIncrementColumn();
            if (autoIncrementColumn != null) {
                final String columnName = autoIncrementColumn.getPhysicalName();
                if (!Check.isEmpty(columnName)) {
                    final String triggerName = "TRI_" + tableName + "_" + columnName;
                    final String sequenceName = "SEQ_" + tableName + "_" + columnName;
                    final TriggerSet triggerSet = diagram.getDiagramContents().getTriggerSet();
                    final SequenceSet sequenceSet = diagram.getDiagramContents().getSequenceSet();
                    if (!triggerSet.contains(triggerName) || !sequenceSet.contains(sequenceName)) {
                        if (Activator.showConfirmDialog("dialog.message.confirm.create.autoincrement.trigger")) {
                            if (!triggerSet.contains(triggerName)) {
                                final Trigger trigger = new Trigger();
                                trigger.setName(triggerName);
                                trigger.setSql("BEFORE INSERT ON " + tableName + "\r\nFOR EACH ROW" + "\r\nBEGIN" + "\r\n\tSELECT "
                                        + sequenceName + ".nextval\r\n\tINTO :new." + columnName + "\r\n\tFROM dual;" + "\r\nEND");
                                final CreateTriggerCommand createTriggerCommand = new CreateTriggerCommand(diagram, trigger);
                                command.add(createTriggerCommand);
                            }
                            if (!sequenceSet.contains(sequenceName)) {
                                final Sequence sequence = new Sequence();
                                sequence.setName(sequenceName);
                                sequence.setStart(1L);
                                sequence.setIncrement(1);
                                final CreateSequenceCommand createSequenceCommand = new CreateSequenceCommand(diagram, sequence);
                                command.add(createSequenceCommand);
                            }
                        }
                    }
                }
            }
            final NormalColumn oldAutoIncrementColumn = table.getAutoIncrementColumn();
            if (oldAutoIncrementColumn != null) {
                if (autoIncrementColumn == null || ((CopyColumn) autoIncrementColumn).getOriginalColumn() != oldAutoIncrementColumn) {
                    final String oldTableName = table.getPhysicalName();
                    final String columnName = oldAutoIncrementColumn.getPhysicalName();
                    if (!Check.isEmpty(columnName)) {
                        final String triggerName = "TRI_" + oldTableName + "_" + columnName;
                        final String sequenceName = "SEQ_" + oldTableName + "_" + columnName;
                        final TriggerSet triggerSet = diagram.getDiagramContents().getTriggerSet();
                        final SequenceSet sequenceSet = diagram.getDiagramContents().getSequenceSet();
                        if (triggerSet.contains(triggerName) || sequenceSet.contains(sequenceName)) {
                            if (Activator.showConfirmDialog("dialog.message.confirm.remove.autoincrement.trigger")) {
                                final Trigger trigger = triggerSet.get(triggerName);
                                if (trigger != null) {
                                    final DeleteTriggerCommand deleteTriggerCommand = new DeleteTriggerCommand(diagram, trigger);
                                    command.add(deleteTriggerCommand);
                                }
                                final Sequence sequence = sequenceSet.get(sequenceName);
                                if (sequence != null) {
                                    final DeleteSequenceCommand deleteSequenceCommand = new DeleteSequenceCommand(diagram, sequence);
                                    command.add(deleteSequenceCommand);
                                }
                            }
                        }
                    }
                }
            }
        }
        return command;
    }
}

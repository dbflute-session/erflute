package org.dbflute.erflute.editor.view.action.dbimport;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.ImageKey;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.MainDiagramEditor;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbimport.DBObjectSet;
import org.dbflute.erflute.editor.model.dbimport.ImportFromDBManagerBase;
import org.dbflute.erflute.editor.model.dbimport.PreImportFromDBManager;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.dbflute.erflute.editor.view.dialog.dbimport.AbstractSelectImportedObjectDialog;
import org.dbflute.erflute.editor.view.dialog.dbimport.ImportDBSettingDialog;
import org.dbflute.erflute.editor.view.dialog.dbimport.SelectImportedObjectFromDBDialog;
import org.dbflute.erflute.editor.view.dialog.dbimport.SelectImportedSchemaDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

public class ImportFromDBAction extends AbstractImportAction {

    public static final String ID = ImportFromDBAction.class.getName();

    private DBSettings dbSetting;

    public ImportFromDBAction(MainDiagramEditor editor) {
        super(ID, DisplayMessages.getMessage("action.title.import.db"), editor);
        setImageDescriptor(Activator.getImageDescriptor(ImageKey.DATABASE));
    }

    protected AbstractSelectImportedObjectDialog createSelectImportedObjectDialog(DBObjectSet dbObjectSet) {
        final ERDiagram diagram = getDiagram();
        return new SelectImportedObjectFromDBDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram, dbObjectSet);
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = getDiagram();

        int step = 0;
        int dialogResult = -1;

        List<String> selectedSchemaList = new ArrayList<>();
        AbstractSelectImportedObjectDialog importDialog = null;

        while (true) {
            if (step == -1) {
                break;
            } else if (step == 0) {
                final ImportDBSettingDialog settingDialog =
                        new ImportDBSettingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);
                dialogResult = settingDialog.open();

                dbSetting = settingDialog.getDbSetting();
            } else {
                final DBManager manager = DBManagerFactory.getDBManager(dbSetting.getDbsystem());

                Connection con = null;
                try {
                    con = dbSetting.connect();

                    if (step == 1) {
                        final List<String> schemaList = manager.getImportSchemaList(con);

                        if (!schemaList.isEmpty()) {
                            final SelectImportedSchemaDialog selectDialog =
                                    new SelectImportedSchemaDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                            diagram, dbSetting.getDbsystem(), schemaList, selectedSchemaList);

                            dialogResult = selectDialog.open();

                            selectedSchemaList = selectDialog.getSelectedSchemas();
                        }
                    } else if (step == 2) {
                        final PreImportFromDBManager preTableImportManager = manager.getPreTableImportManager();
                        preTableImportManager.init(con, dbSetting, diagram, selectedSchemaList);
                        preTableImportManager.run();

                        final Exception e = preTableImportManager.getException();
                        if (e != null) {
                            Activator.showMessageDialog(e.getMessage());
                            throw new InputException("error.jdbc.version");
                        }

                        final DBObjectSet dbObjectSet = preTableImportManager.getImportObjects();

                        importDialog = createSelectImportedObjectDialog(dbObjectSet);

                        dialogResult = importDialog.open();
                    } else if (step == 3) {
                        final ProgressMonitorDialog dialog =
                                new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
                        final ImportFromDBManagerBase tableImportManager = (ImportFromDBManagerBase) manager.getTableImportManager();
                        tableImportManager.init(con, dbSetting, diagram, importDialog.getSelectedDbObjects(),
                                importDialog.isUseCommentAsLogicalName(), importDialog.isMergeWord());

                        try {
                            dialog.run(true, true, tableImportManager);

                            final Exception e1 = tableImportManager.getException();
                            if (e1 != null) {
                                throw e1;
                            } else {
                                this.importedNodeElements = new ArrayList<>();
                                this.importedNodeElements.addAll(tableImportManager.getImportedTables());
                                this.importedNodeElements.addAll(tableImportManager.getImportedViews());
                                this.importedSequences = tableImportManager.getImportedSequences();
                                this.importedTriggers = tableImportManager.getImportedTriggers();
                                this.importedTablespaces = tableImportManager.getImportedTablespaces();
                            }
                        } catch (final InvocationTargetException e1) {
                            Activator.showExceptionDialog(e1);
                        } catch (final InterruptedException e1) {
                            Activator.showExceptionDialog(e1);
                        }

                        showData();

                        break;
                    }
                } finally {
                    if (con != null) {
                        con.close();
                    }
                }
            }

            if (dialogResult == IDialogConstants.OK_ID) {
                step++;
            } else if (dialogResult == IDialogConstants.BACK_ID) {
                step--;
            } else {
                step = -1;
            }
        }
    }
}

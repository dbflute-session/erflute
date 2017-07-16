package org.dbflute.erflute.editor.view.dialog.dbexport;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.core.widgets.FileText;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLTarget;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.Validator;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.model.settings.ExportSettings;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ExportToDDLDialog extends AbstractDialog {

    public static final String DEFAULT_CATEGORY = "All";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Combo environmentCombo;
    private FileText outputFileText;
    private Combo fileEncodingCombo;
    private Combo categoryCombo;
    private Button inlineTableComment;
    private Button inlineColumnComment;
    private final ERDiagram diagram;
    private final IEditorPart editorPart;
    private Button dropTablespace;
    private Button dropSequence;
    private Button dropTrigger;
    private Button dropView;
    private Button dropIndex;
    private Button dropTable;
    private Button createTablespace;
    private Button createSequence;
    private Button createTrigger;
    private Button createView;
    private Button createIndex;
    private Button createTable;
    private Button createForeignKey;
    private Button createComment;
    private Button commentValueDescription;
    private Button commentValueLogicalName;
    private Button commentValueLogicalNameDescription;
    private Button commentReplaceLineFeed;
    private Text commentReplaceString;
    private Button openAfterSavedButton;
    private ExportSettings exportSetting;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ExportToDDLDialog(Shell parentShell, ERDiagram diagram, IEditorPart editorPart, GraphicalViewer viewer) {
        super(parentShell, 3);
        this.diagram = diagram;
        this.editorPart = editorPart;
    }

    // ===================================================================================
    //                                                                               Title
    //                                                                               =====
    @Override
    protected String getTitle() {
        return "dialog.title.export.ddl";
    }

    // ===================================================================================
    //                                                                              Layout
    //                                                                              ======
    @Override
    protected void initLayout(GridLayout layout) {
        super.initLayout(layout);
        layout.verticalSpacing = 15;
    }

    // ===================================================================================
    //                                                                           Component
    //                                                                           =========
    @Override
    protected void initComponent(Composite parent) {
        final GridData gridData = new GridData();
        gridData.widthHint = 200;
        this.environmentCombo = CompositeFactory.createReadOnlyCombo(this, parent, "label.tablespace.environment", 2, -1);
        CompositeFactory.createLabel(parent, "label.output.file");
        this.outputFileText = new FileText(parent, SWT.BORDER, ".sql");
        outputFileText.setLayoutData(gridData);
        final String selectedCharset = "UTF-8";
        initFileEncodingCombo(parent, selectedCharset);
        this.categoryCombo = CompositeFactory.createReadOnlyCombo(this, parent, "label.category", 2, -1);
        initCategoryCombo();
        createCheckboxComposite(parent);
        createCommentComposite(parent);
        final GridData optionCheckGridData = new GridData();
        optionCheckGridData.horizontalSpan = 3;
        this.openAfterSavedButton = new Button(parent, SWT.CHECK);
        openAfterSavedButton.setText(DisplayMessages.getMessage("label.open.after.saved"));
        openAfterSavedButton.setLayoutData(optionCheckGridData);
    }

    private void initFileEncodingCombo(Composite parent, String selectedCharset) {
        final String title = "label.output.file.encoding";
        fileEncodingCombo = CompositeFactory.createFileEncodingCombo(editorPart, this, parent, title, 2, selectedCharset);
    }

    private void initCategoryCombo() {
        categoryCombo.add(DEFAULT_CATEGORY); // #for_erflute use English only
        for (final Category category : diagram.getDiagramContents().getSettings().getCategorySetting().getAllCategories()) {
            categoryCombo.add(category.getName());
        }
    }

    private void createCheckboxComposite(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(gridData);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        createDropCheckboxGroup(composite);
        createCreateCheckboxGroup(composite);
    }

    private void createDropCheckboxGroup(Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        group.setLayoutData(gridData);

        group.setText("DROP");

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        this.dropTablespace = CompositeFactory.createCheckbox(this, group, "label.tablespace");
        this.dropSequence = CompositeFactory.createCheckbox(this, group, "label.sequence");
        this.dropTrigger = CompositeFactory.createCheckbox(this, group, "label.trigger");
        this.dropView = CompositeFactory.createCheckbox(this, group, "label.view");
        this.dropIndex = CompositeFactory.createCheckbox(this, group, "label.index");
        this.dropTable = CompositeFactory.createCheckbox(this, group, "label.table");
    }

    private void createCreateCheckboxGroup(Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        group.setLayoutData(gridData);

        group.setText("CREATE");

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        this.createTablespace = CompositeFactory.createCheckbox(this, group, "label.tablespace");
        this.createSequence = CompositeFactory.createCheckbox(this, group, "label.sequence");
        this.createTrigger = CompositeFactory.createCheckbox(this, group, "label.trigger");
        this.createView = CompositeFactory.createCheckbox(this, group, "label.view");
        this.createIndex = CompositeFactory.createCheckbox(this, group, "label.index");
        this.createTable = CompositeFactory.createCheckbox(this, group, "label.table");
        this.createForeignKey = CompositeFactory.createCheckbox(this, group, "label.foreign.key");
        this.createComment = CompositeFactory.createCheckbox(this, group, "label.comment");
    }

    private void createCommentComposite(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(gridData);

        final GridLayout compositeLayout = new GridLayout();
        composite.setLayout(compositeLayout);

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setText(DisplayMessages.getMessage("label.comment"));

        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        group.setLayout(layout);

        final GridData commentValueGridData = new GridData();
        commentValueGridData.horizontalSpan = 3;
        commentValueGridData.horizontalAlignment = GridData.FILL;
        commentValueGridData.grabExcessHorizontalSpace = true;

        final Group commentValueGroup = new Group(group, SWT.NONE);
        commentValueGroup.setLayoutData(commentValueGridData);
        commentValueGroup.setText(DisplayMessages.getMessage("label.comment.value"));

        final GridLayout commentValueLayout = new GridLayout();
        commentValueLayout.numColumns = 1;
        commentValueGroup.setLayout(commentValueLayout);

        this.commentValueDescription = CompositeFactory.createRadio(this, commentValueGroup, "label.comment.value.description");
        this.commentValueLogicalName = CompositeFactory.createRadio(this, commentValueGroup, "label.comment.value.logical.name");
        this.commentValueLogicalNameDescription =
                CompositeFactory.createRadio(this, commentValueGroup, "label.comment.value.logical.name.description");

        this.commentReplaceLineFeed = CompositeFactory.createCheckbox(this, group, "label.comment.replace.line.feed");
        this.commentReplaceString = CompositeFactory.createText(this, group, "label.comment.replace.string", 1, 20, false);

        this.inlineTableComment = CompositeFactory.createCheckbox(this, group, "label.comment.inline.table", 4);
        this.inlineColumnComment = CompositeFactory.createCheckbox(this, group, "label.comment.inline.column", 4);
    }

    // ===================================================================================
    //                                                                            Listener
    //                                                                            ========
    @Override
    protected void addListener() {
        super.addListener();
        environmentCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                validate();
            }
        });
        outputFileText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });
        fileEncodingCombo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validate();
            }
        });
    }

    // ===================================================================================
    //                                                                         Set up Data
    //                                                                         ===========
    @Override
    protected void setupData() {
        final DiagramSettings settings = diagram.getDiagramContents().getSettings();
        for (final Environment environment : settings.getEnvironmentSettings().getEnvironments()) {
            environmentCombo.add(environment.getName());
        }
        environmentCombo.select(0);
        final ExportSettings exportSetting = settings.getExportSettings();
        outputFileText.setText(buildDefaultOutputFilePath(exportSetting));
        categoryCombo.select(0);

        // set previous selected category
        if (exportSetting.getCategoryNameToExport() != null) {
            for (int i = 1; i < categoryCombo.getItemCount(); i++) {
                if (exportSetting.getCategoryNameToExport().equals(categoryCombo.getItem(i))) {
                    categoryCombo.select(i);
                    break;
                }
            }
        }
        final DDLTarget ddlTarget = exportSetting.getDdlTarget();
        dropIndex.setSelection(ddlTarget.dropIndex);
        dropSequence.setSelection(ddlTarget.dropSequence);
        dropTable.setSelection(ddlTarget.dropTable);
        dropTablespace.setSelection(ddlTarget.dropTablespace);
        dropTrigger.setSelection(ddlTarget.dropTrigger);
        dropView.setSelection(ddlTarget.dropView);
        createComment.setSelection(ddlTarget.createComment);
        createForeignKey.setSelection(ddlTarget.createForeignKey);
        createIndex.setSelection(ddlTarget.createIndex);
        createSequence.setSelection(ddlTarget.createSequence);
        createTable.setSelection(ddlTarget.createTable);
        createTablespace.setSelection(ddlTarget.createTablespace);
        createTrigger.setSelection(ddlTarget.createTrigger);
        createView.setSelection(ddlTarget.createView);
        inlineColumnComment.setSelection(ddlTarget.inlineColumnComment);
        inlineTableComment.setSelection(ddlTarget.inlineTableComment);
        commentReplaceLineFeed.setSelection(ddlTarget.commentReplaceLineFeed);
        commentReplaceString.setText(Format.null2blank(ddlTarget.commentReplaceString));
        commentValueDescription.setSelection(ddlTarget.commentValueDescription);
        commentValueLogicalName.setSelection(ddlTarget.commentValueLogicalName);
        commentValueLogicalNameDescription.setSelection(ddlTarget.commentValueLogicalNameDescription);
        if (!ddlTarget.commentValueDescription && !ddlTarget.commentValueLogicalName && !ddlTarget.commentValueLogicalNameDescription) {
            commentValueDescription.setSelection(true);
        }
        openAfterSavedButton.setSelection(exportSetting.isOpenAfterSaved());
    }

    private String buildDefaultOutputFilePath(final ExportSettings exportSetting) {
        String outputFile = Format.null2blank(exportSetting.getDdlOutput());
        if (Check.isEmpty(outputFile)) {
            final IFile editorFile = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
            final String editorPath = editorFile.getFullPath().toPortableString(); // #for_erflute
            outputFile = editorPath.substring(0, editorPath.lastIndexOf(".")) + ".sql";
        }
        return outputFile;
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        if (isBlank(environmentCombo)) {
            return "error.tablespace.environment.empty";
        }
        if (outputFileText.isBlank()) {
            return "Select your output file for DDL";
        }
        if (!Charset.isSupported(fileEncodingCombo.getText())) {
            return "error.file.encoding.is.not.supported";
        }
        return null;
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() throws InputException {
        final String saveFilePath = outputFileText.getFilePath();
        final DDLTarget ddlTarget = new DDLTarget();
        ddlTarget.dropTablespace = dropTablespace.getSelection();
        ddlTarget.dropSequence = dropSequence.getSelection();
        ddlTarget.dropTrigger = dropTrigger.getSelection();
        ddlTarget.dropView = dropView.getSelection();
        ddlTarget.dropIndex = dropIndex.getSelection();
        ddlTarget.dropTable = dropTable.getSelection();
        ddlTarget.createTablespace = createTablespace.getSelection();
        ddlTarget.createSequence = createSequence.getSelection();
        ddlTarget.createTrigger = createTrigger.getSelection();
        ddlTarget.createView = createView.getSelection();
        ddlTarget.createIndex = createIndex.getSelection();
        ddlTarget.createTable = createTable.getSelection();
        ddlTarget.createForeignKey = createForeignKey.getSelection();
        ddlTarget.createComment = createComment.getSelection();
        ddlTarget.inlineTableComment = inlineTableComment.getSelection();
        ddlTarget.inlineColumnComment = inlineColumnComment.getSelection();
        ddlTarget.commentReplaceLineFeed = commentReplaceLineFeed.getSelection();
        ddlTarget.commentReplaceString = commentReplaceString.getText();
        ddlTarget.commentValueDescription = commentValueDescription.getSelection();
        ddlTarget.commentValueLogicalName = commentValueLogicalName.getSelection();
        ddlTarget.commentValueLogicalNameDescription = commentValueLogicalNameDescription.getSelection();
        final boolean openAfterSaved = openAfterSavedButton.getSelection();
        this.exportSetting = diagram.getDiagramContents().getSettings().getExportSettings().clone();
        exportSetting.setDdlOutput(saveFilePath);
        exportSetting.setDdlTarget(ddlTarget);
        exportSetting.setCategoryNameToExport(categoryCombo.getText());
        exportSetting.setOpenAfterSaved(openAfterSaved);
        final Validator validator = new Validator();
        final List<ValidateResult> errorList = validator.validate(diagram);
        if (!errorList.isEmpty()) {
            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            final ExportWarningDialog dialog = new ExportWarningDialog(shell, errorList);
            if (dialog.open() != IDialogConstants.OK_ID) {
                return;
            }
        }
        final Category currentCategory = diagram.getCurrentCategory();
        final int currentCategoryIndex = diagram.getCurrentCategoryIndex();
        setCurrentCategory();
        PrintWriter out = null;
        IFile file = null;
        try {
            final DDLCreator ddlCreator = DBManagerFactory.getDBManager(diagram).getDDLCreator(diagram, true);
            final int index = environmentCombo.getSelectionIndex();
            final Environment environment =
                    diagram.getDiagramContents().getSettings().getEnvironmentSettings().getEnvironments().get(index);
            ddlCreator.init(environment, ddlTarget);
            file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(saveFilePath));
            final IPath location = file.getLocation();
            if (location == null) {
                final String msg = "Not found the path in the Eclipse workspace: saveFilePath=" + saveFilePath;
                Activator.showErrorDialog(msg);
            } else {
                out = writeDDLFile(file, ddlCreator, location);
            }
        } catch (final Exception e) {
            Activator.showExceptionDialog("Failed to create DDL: saveFilePath=" + saveFilePath, e);
        } finally {
            diagram.setCurrentCategory(currentCategory, currentCategoryIndex);
            if (out != null) {
                out.close();
            }
        }
        if (openAfterSaved && file != null) {
            try {
                final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                IDE.openEditor(page, file);
            } catch (final Exception e) {
                Activator.showExceptionDialog(e);
            }
        }
    }

    private void setCurrentCategory() {
        if (categoryCombo.getSelectionIndex() == 0) {
            diagram.setCurrentCategory(null, 0);
            return;
        }
        final Category currentCategory = diagram.getDiagramContents()
                .getSettings()
                .getCategorySetting()
                .getAllCategories()
                .get(categoryCombo.getSelectionIndex() - 1);

        diagram.setCurrentCategory(currentCategory, categoryCombo.getSelectionIndex());
    }

    private PrintWriter writeDDLFile(IFile file, DDLCreator ddlCreator, IPath location)
            throws CoreException, UnsupportedEncodingException, FileNotFoundException {
        final String absoluteFilePath = location.toString();
        final String encoding = getEncoding();
        final PrintWriter out =
                new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absoluteFilePath), encoding)));
        file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
        // #for_erflute don't use system line separator to be immobilize DDL
        out.print(ddlCreator.prepareDropDDL(diagram));
        out.print(DDLCreator.LN);
        out.print(ddlCreator.prepareCreateDDL(diagram));
        out.print(DDLCreator.LN);
        return out;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    private String getEncoding() throws CoreException {
        return fileEncodingCombo.getText();
    }

    public ExportSettings getExportSetting() {
        return exportSetting;
    }
}

package org.dbflute.erflute.editor.view.dialog.relationship;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class RelationshipDialog extends AbstractDialog {

    private final Relationship relationship;
    private Text foreignKeyNameText;
    private Text parentTableNameText;
    private final String previousForeignKeyName; // null allowed (not required)
    private Combo columnCombo;
    private Combo parentCardinalityCombo;
    private Combo childCardinalityCombo;
    private Combo onUpdateCombo;
    private Combo onDeleteCombo;
    private ReferredColumnState relationshipColumnState;

    public static class ReferredColumnState {
        public List<NormalColumn> candidateColumns;
        public int complexUniqueKeyStartIndex;
        public int columnStartIndex;
        public boolean candidatePK;

        public ReferredColumnState() {
            candidateColumns = new ArrayList<NormalColumn>();
        }
    }

    public RelationshipDialog(Shell parentShell, Relationship relationship) {
        super(parentShell, 2);
        this.relationship = relationship;
        this.previousForeignKeyName = relationship.getForeignKeyName();
    }

    @Override
    protected void initComponent(Composite composite) {
        CompositeFactory.createLabel(composite, "label.constraint.name", 2);
        this.foreignKeyNameText = CompositeFactory.createText(this, composite, null, 2, false);
        createMethodGroup(composite);
        final int size = createParentGroup(composite);
        createChildGroup(composite, size);
    }

    private void createMethodGroup(Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);
        group.setText(DisplayMessages.getMessage("label.reference.operation"));

        final Label label1 = new Label(group, SWT.NONE);
        label1.setText("ON UPDATE");
        createOnUpdateCombo(group);

        final Label label2 = new Label(group, SWT.NONE);
        label2.setText("ON DELETE");
        createOnDeleteCombo(group);
    }

    private void createOnUpdateCombo(Group group) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        onUpdateCombo = new Combo(group, SWT.NONE);
        onUpdateCombo.setLayoutData(gridData);

        onUpdateCombo.add("RESTRICT");
        onUpdateCombo.add("CASCADE");
        onUpdateCombo.add("NO ACTION");
        onUpdateCombo.add("SET NULL");
        onUpdateCombo.add("SET DEFAULT");
    }

    private void createOnDeleteCombo(Group group) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        onDeleteCombo = new Combo(group, SWT.NONE);
        onDeleteCombo.setLayoutData(gridData);

        onDeleteCombo.add("RESTRICT");
        onDeleteCombo.add("CASCADE");
        onDeleteCombo.add("NO ACTION");
        onDeleteCombo.add("SET NULL");
        onDeleteCombo.add("SET DEFAULT");
    }

    private int createParentGroup(Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 10;
        gridLayout.marginHeight = 10;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);
        group.setText(DisplayMessages.getMessage("label.parent"));

        final Composite upperComposite = new Composite(group, SWT.NONE);
        upperComposite.setLayoutData(gridData);
        upperComposite.setLayout(gridLayout);

        final Label label1 = new Label(upperComposite, SWT.NONE);
        label1.setText(DisplayMessages.getMessage("label.reference.table"));
        parentTableNameText = new Text(upperComposite, SWT.BORDER | SWT.READ_ONLY);
        parentTableNameText.setLayoutData(gridData);

        final Label label2 = new Label(upperComposite, SWT.NONE);
        label2.setText("Referred Column");
        this.createColumnCombo(upperComposite);
        this.createParentMandatoryGroup(group);
        upperComposite.pack();
        return upperComposite.getSize().y;
    }

    /**
     * This method initializes group1
     */
    private void createChildGroup(Composite composite, int size) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 10;
        gridLayout.verticalSpacing = 10;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);

        group.setText(DisplayMessages.getMessage("label.child"));

        final Label filler = new Label(group, SWT.NONE);
        filler.setText("");
        final GridData fillerGridData = new GridData();
        fillerGridData.heightHint = size;
        filler.setLayoutData(fillerGridData);

        this.createChildMandatoryGroup(group);
    }

    private void createColumnCombo(Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        columnCombo = new Combo(parent, SWT.READ_ONLY);
        columnCombo.setLayoutData(gridData);
        columnCombo.setVisibleItemCount(20);
        relationshipColumnState = setupReferredColumnComboData(columnCombo, (ERTable) relationship.getSourceTableView());
    }

    public static ReferredColumnState setupReferredColumnComboData(Combo columnCombo, ERTable table) {
        final ReferredColumnState info = new ReferredColumnState();
        final int primaryKeySize = table.getPrimaryKeySize();
        if (primaryKeySize != 0) {
            columnCombo.add("PRIMARY KEY");
            info.complexUniqueKeyStartIndex = 1;
            info.candidatePK = true;
        } else {
            info.complexUniqueKeyStartIndex = 0;
            info.candidatePK = false;
        }
        for (final CompoundUniqueKey complexUniqueKey : table.getCompoundUniqueKeyList()) {
            columnCombo.add(complexUniqueKey.getLabel());
        }
        info.columnStartIndex = info.complexUniqueKeyStartIndex + table.getCompoundUniqueKeyList().size();
        for (final NormalColumn column : table.getNormalColumns()) {
            if (column.isUniqueKey()) {
                columnCombo.add(column.getLogicalName());
                info.candidateColumns.add(column);
            }
        }
        return info;
    }

    private void createParentMandatoryGroup(Group parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 10;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);
        group.setText(DisplayMessages.getMessage("label.mandatory"));

        parentCardinalityCombo = new Combo(group, SWT.NONE);
        parentCardinalityCombo.setLayoutData(gridData);
        parentCardinalityCombo.setVisibleItemCount(5);
        parentCardinalityCombo.add("1");
        parentCardinalityCombo.add("0..1");
    }

    private void createChildMandatoryGroup(Group parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 10;
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);
        group.setText(DisplayMessages.getMessage("label.mandatory"));
        childCardinalityCombo = new Combo(group, SWT.NONE);
        childCardinalityCombo.setLayoutData(gridData);
        childCardinalityCombo.setVisibleItemCount(5);
        childCardinalityCombo.add("1..n");
        childCardinalityCombo.add("0..n");
        childCardinalityCombo.add("1");
        childCardinalityCombo.add("0..1");
    }

    @Override
    protected String getTitle() {
        return "dialog.title.relation";
    }

    @Override
    protected void setupData() {
        final ERTable sourceTable = (ERTable) this.relationship.getSourceTableView();
        this.foreignKeyNameText.setText(Format.null2blank(this.relationship.getForeignKeyName()));
        if (this.relationship.getOnUpdateAction() != null) {
            this.onUpdateCombo.setText(this.relationship.getOnUpdateAction());
        }
        if (this.relationship.getOnDeleteAction() != null) {
            this.onDeleteCombo.setText(this.relationship.getOnDeleteAction());
        }
        if (!Check.isEmpty(this.relationship.getParentCardinality())) {
            this.parentCardinalityCombo.setText(this.relationship.getParentCardinality());
        } else {
            this.parentCardinalityCombo.select(0);
        }
        if (!Check.isEmpty(this.relationship.getChildCardinality())) {
            this.childCardinalityCombo.setText(this.relationship.getChildCardinality());
        } else {
            this.childCardinalityCombo.select(0);
        }
        if (this.relationship.isReferenceForPK()) {
            this.columnCombo.select(0);
        } else if (this.relationship.getReferredCompoundUniqueKey() != null) {
            for (int i = 0; i < sourceTable.getCompoundUniqueKeyList().size(); i++) {
                if (sourceTable.getCompoundUniqueKeyList().get(i) == this.relationship.getReferredCompoundUniqueKey()) {
                    this.columnCombo.select(i + this.relationshipColumnState.complexUniqueKeyStartIndex);
                    break;
                }
            }
        } else {
            for (int i = 0; i < this.relationshipColumnState.candidateColumns.size(); i++) {
                if (this.relationshipColumnState.candidateColumns.get(i) == this.relationship.getReferredSimpleUniqueColumn()) {
                    this.columnCombo.select(i + this.relationshipColumnState.columnStartIndex);
                    break;
                }
            }
        }
        if (this.relationship.isReferedStrictly()) {
            this.columnCombo.setEnabled(false);
        }
        this.parentTableNameText.setText(this.relationship.getSourceTableView().getLogicalName());
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @Override
    protected String doValidate() {
        final String foreignKeyName = foreignKeyNameText.getText().trim(); // not required for compatible
        if (Srl.is_NotNull_and_NotTrimmedEmpty(foreignKeyName)) {
            if (relationship.getDiagramSettings().isValidatePhysicalName() && !Check.isAlphabet(foreignKeyName)) {
                return "error.foreign.key.name.not.alphabet";
            }
            final ERDiagram diagram = relationship.getTargetTableView().getDiagram();
            final List<TableView> tableViewList = diagram.getDiagramContents().getDiagramWalkers().getTableViewList();
            for (final TableView tableView : tableViewList) {
                final List<Relationship> relationshipList = tableView.getIncomingRelationshipList();
                for (final Relationship currentRel : relationshipList) {
                    final String currentForeignKeyName = currentRel.getForeignKeyName();
                    if (currentForeignKeyName != null) {
                        if (!currentForeignKeyName.equalsIgnoreCase(previousForeignKeyName)) {
                            if (currentForeignKeyName.equalsIgnoreCase(foreignKeyName)) {
                                return "error.foreign.key.name.already.exists";
                            }
                        }
                    }
                }
            }
        }
        // #thiking also needs to check same source and target? by jflute
        return null;
    }

    // ===================================================================================
    //                                                                          Perform OK
    //                                                                          ==========
    @Override
    protected void performOK() {
        this.relationship.setForeignKeyName(this.foreignKeyNameText.getText());
        this.relationship.setOnDeleteAction(this.onDeleteCombo.getText());
        this.relationship.setOnUpdateAction(this.onUpdateCombo.getText());
        this.relationship.setChildCardinality(this.childCardinalityCombo.getText());
        this.relationship.setParentCardinality(this.parentCardinalityCombo.getText());
        final int index = this.columnCombo.getSelectionIndex();
        if (index < this.relationshipColumnState.complexUniqueKeyStartIndex) {
            this.relationship.setReferenceForPK(true);
            this.relationship.setReferredCompoundUniqueKey(null);
            this.relationship.setReferredSimpleUniqueColumn(null);
        } else if (index < this.relationshipColumnState.columnStartIndex) {
            final CompoundUniqueKey complexUniqueKey =
                    ((ERTable) this.relationship.getSourceTableView()).getCompoundUniqueKeyList().get(
                            index - this.relationshipColumnState.complexUniqueKeyStartIndex);
            this.relationship.setReferenceForPK(false);
            this.relationship.setReferredCompoundUniqueKey(complexUniqueKey);
            this.relationship.setReferredSimpleUniqueColumn(null);
        } else {
            final NormalColumn sourceColumn =
                    this.relationshipColumnState.candidateColumns.get(index - this.relationshipColumnState.columnStartIndex);
            this.relationship.setReferenceForPK(false);
            this.relationship.setReferredCompoundUniqueKey(null);
            this.relationship.setReferredSimpleUniqueColumn(sourceColumn);
        }
    }
}

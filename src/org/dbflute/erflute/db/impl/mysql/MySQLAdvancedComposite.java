package org.dbflute.erflute.db.impl.mysql;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.view.dialog.table.tab.AdvancedComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MySQLAdvancedComposite extends AdvancedComposite {

    private Combo engineCombo;

    private Combo characterSetCombo;

    private Combo collationCombo;

    private Text primaryKeyLengthOfText;

    public MySQLAdvancedComposite(Composite parent) {
        super(parent);
    }

    @Override
    protected void initComposite() {
        super.initComposite();

        this.engineCombo = createEngineCombo(this, this.dialog);

        this.characterSetCombo = CompositeFactory.createCombo(dialog, this, "label.character.set", 1);
        this.characterSetCombo.setVisibleItemCount(20);

        this.collationCombo = CompositeFactory.createCombo(this.dialog, this, "label.collation", 1);
        this.collationCombo.setVisibleItemCount(20);

        this.primaryKeyLengthOfText = CompositeFactory.createNumText(this.dialog, this, "label.primary.key.length.of.text", 30);
    }

    public static Combo createEngineCombo(Composite parent, AbstractDialog dialog) {
        Combo combo = CompositeFactory.createCombo(dialog, parent, "label.storage.engine", 1);
        combo.setVisibleItemCount(20);

        initEngineCombo(combo);

        return combo;
    }

    private static void initEngineCombo(Combo combo) {
        combo.add("");
        combo.add("MyISAM");
        combo.add("InnoDB");
        combo.add("Memory");
        combo.add("Merge");
        combo.add("Archive");
        combo.add("Federated");
        combo.add("NDB");
        combo.add("CSV");
        combo.add("Blackhole");
        combo.add("CSV");
    }

    private void initCharacterSetCombo() {
        this.characterSetCombo.add("");

        for (String characterSet : MySQLDBManager.getCharacterSetList()) {
            this.characterSetCombo.add(characterSet);
        }
    }

    @Override
    protected void setData() {
        super.setData();

        this.initCharacterSetCombo();

        this.engineCombo.setText(Format.toString(((MySQLTableProperties) this.tableProperties).getStorageEngine()));

        String characterSet = ((MySQLTableProperties) this.tableProperties).getCharacterSet();

        this.characterSetCombo.setText(Format.toString(characterSet));

        this.collationCombo.add("");

        for (String collation : MySQLDBManager.getCollationList(Format.toString(characterSet))) {
            this.collationCombo.add(collation);
        }

        this.collationCombo.setText(Format.toString(((MySQLTableProperties) this.tableProperties).getCollation()));

        this.primaryKeyLengthOfText.setText(Format.toString(((MySQLTableProperties) this.tableProperties).getPrimaryKeyLengthOfText()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() throws InputException {
        super.validate();

        String engine = this.engineCombo.getText();
        ((MySQLTableProperties) this.tableProperties).setStorageEngine(engine);

        String characterSet = this.characterSetCombo.getText();
        ((MySQLTableProperties) this.tableProperties).setCharacterSet(characterSet);

        String collation = this.collationCombo.getText();
        ((MySQLTableProperties) this.tableProperties).setCollation(collation);

        String str = this.primaryKeyLengthOfText.getText();
        Integer length = null;

        try {
            if (!Check.isEmptyTrim(str)) {
                length = Integer.valueOf(str);
            }
        } catch (Exception e) {
            throw new InputException("error.column.length.degit");
        }

        ((MySQLTableProperties) this.tableProperties).setPrimaryKeyLengthOfText(length);

        if (this.table != null) {
            for (NormalColumn primaryKey : this.table.getPrimaryKeys()) {
                SqlType type = primaryKey.getType();

                if (type != null && type.isFullTextIndexable() && !type.isNeedLength(this.diagram.getDatabase())) {
                    if (length == null || length == 0) {
                        throw new InputException("error.primary.key.length.empty");
                    }
                }
            }
        }
    }

    @Override
    protected void addListener() {
        this.characterSetCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String selectedCollation = collationCombo.getText();

                collationCombo.removeAll();
                collationCombo.add("");

                for (String collation : MySQLDBManager.getCollationList(characterSetCombo.getText())) {
                    collationCombo.add(collation);
                }

                int index = collationCombo.indexOf(selectedCollation);

                collationCombo.select(index);
            }
        });
    }
}

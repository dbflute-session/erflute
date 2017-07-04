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

        this.engineCombo = createEngineCombo(this, dialog);

        this.characterSetCombo = CompositeFactory.createCombo(dialog, this, "label.character.set", 1);
        characterSetCombo.setVisibleItemCount(20);

        this.collationCombo = CompositeFactory.createCombo(dialog, this, "label.collation", 1);
        collationCombo.setVisibleItemCount(20);

        this.primaryKeyLengthOfText = CompositeFactory.createNumText(dialog, this, "label.primary.key.length.of.text", 30);
    }

    public static Combo createEngineCombo(Composite parent, AbstractDialog dialog) {
        final Combo combo = CompositeFactory.createCombo(dialog, parent, "label.storage.engine", 1);
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
        characterSetCombo.add("");

        for (final String characterSet : MySQLDBManager.getCharacterSetList()) {
            characterSetCombo.add(characterSet);
        }
    }

    @Override
    protected void setData() {
        super.setData();

        initCharacterSetCombo();

        engineCombo.setText(Format.toString(((MySQLTableProperties) tableProperties).getStorageEngine()));

        final String characterSet = ((MySQLTableProperties) tableProperties).getCharacterSet();

        characterSetCombo.setText(Format.toString(characterSet));

        collationCombo.add("");

        for (final String collation : MySQLDBManager.getCollationList(Format.toString(characterSet))) {
            collationCombo.add(collation);
        }

        collationCombo.setText(Format.toString(((MySQLTableProperties) tableProperties).getCollation()));

        primaryKeyLengthOfText.setText(Format.toString(((MySQLTableProperties) tableProperties).getPrimaryKeyLengthOfText()));
    }

    @Override
    public void validate() throws InputException {
        super.validate();

        final String engine = engineCombo.getText();
        ((MySQLTableProperties) tableProperties).setStorageEngine(engine);

        final String characterSet = characterSetCombo.getText();
        ((MySQLTableProperties) tableProperties).setCharacterSet(characterSet);

        final String collation = collationCombo.getText();
        ((MySQLTableProperties) tableProperties).setCollation(collation);

        final String str = primaryKeyLengthOfText.getText();
        Integer length = null;

        try {
            if (!Check.isEmptyTrim(str)) {
                length = Integer.valueOf(str);
            }
        } catch (final Exception e) {
            throw new InputException("error.column.length.degit");
        }

        ((MySQLTableProperties) tableProperties).setPrimaryKeyLengthOfText(length);

        if (table != null) {
            for (final NormalColumn primaryKey : table.getPrimaryKeys()) {
                final SqlType type = primaryKey.getType();

                if (type != null && type.isFullTextIndexable() && !type.isNeedLength(diagram.getDatabase())) {
                    if (length == null || length == 0) {
                        throw new InputException("error.primary.key.length.empty");
                    }
                }
            }
        }
    }

    @Override
    protected void addListener() {
        characterSetCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final String selectedCollation = collationCombo.getText();

                collationCombo.removeAll();
                collationCombo.add("");

                for (final String collation : MySQLDBManager.getCollationList(characterSetCombo.getText())) {
                    collationCombo.add(collation);
                }

                final int index = collationCombo.indexOf(selectedCollation);

                collationCombo.select(index);
            }
        });
    }
}

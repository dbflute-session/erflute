package org.dbflute.erflute.editor.model.dbexport.ddl.validator;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.Rule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.all.DuplicatedPhysicalNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.all.ReservedNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column.impl.NoColumnNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column.impl.NoColumnTypeRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.column.impl.ReservedWordColumnNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl.DuplicatedColumnNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl.FullTextIndexRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl.NoColumnRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl.NoTableNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.table.impl.ReservedWordTableNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.tablespace.impl.UninputTablespaceRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.view.impl.NoViewNameRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.view.impl.NoViewSqlRule;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.view.impl.ReservedWordViewNameRule;

public class Validator {

    private static final List<Rule> RULE_LIST = new ArrayList<>();

    static {
        // 全体に対するルール
        new DuplicatedPhysicalNameRule();
        new ReservedNameRule();

        // テーブルに対するルール
        new NoTableNameRule();
        new NoColumnRule();
        new DuplicatedColumnNameRule();
        new ReservedWordTableNameRule();
        new FullTextIndexRule();

        // ビューに対するルール
        new NoViewNameRule();
        new ReservedWordViewNameRule();
        new NoViewSqlRule();

        // 列に対するルール
        new NoColumnNameRule();
        new NoColumnTypeRule();
        new ReservedWordColumnNameRule();
        new UninputTablespaceRule();
    }

    public static void addRule(Rule rule) {
        RULE_LIST.add(rule);
    }

    public List<ValidateResult> validate(ERDiagram diagram) {
        final List<ValidateResult> errorList = new ArrayList<>();

        for (final Rule rule : RULE_LIST) {
            final boolean ret = rule.validate(diagram);

            errorList.addAll(rule.getErrorList());
            rule.clear();

            if (!ret) {
                break;
            }
        }
        return errorList;
    }
}

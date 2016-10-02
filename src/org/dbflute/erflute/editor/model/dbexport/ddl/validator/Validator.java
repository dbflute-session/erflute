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

    private static final List<Rule> RULE_LIST = new ArrayList<Rule>();

    static {
        // �S�̂ɑ΂��郋�[��
        new DuplicatedPhysicalNameRule();
        new ReservedNameRule();

        // �e�[�u���ɑ΂��郋�[��
        new NoTableNameRule();
        new NoColumnRule();
        new DuplicatedColumnNameRule();
        new ReservedWordTableNameRule();
        new FullTextIndexRule();

        // �r���[�ɑ΂��郋�[��
        new NoViewNameRule();
        new ReservedWordViewNameRule();
        new NoViewSqlRule();

        // ��ɑ΂��郋�[��
        new NoColumnNameRule();
        new NoColumnTypeRule();
        new ReservedWordColumnNameRule();
        new UninputTablespaceRule();
    }

    public static void addRule(Rule rule) {
        RULE_LIST.add(rule);
    }

    public List<ValidateResult> validate(ERDiagram diagram) {
        List<ValidateResult> errorList = new ArrayList<ValidateResult>();

        for (Rule rule : RULE_LIST) {
            boolean ret = rule.validate(diagram);

            errorList.addAll(rule.getErrorList());
            rule.clear();

            if (!ret) {
                break;
            }
        }

        return errorList;
    }

}

package org.insightech.er.editor.model.dbexport.ddl.validator;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.Rule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.all.DuplicatedPhysicalNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.all.ReservedNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl.NoColumnNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl.NoColumnTypeRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl.ReservedWordColumnNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.DuplicatedColumnNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.FullTextIndexRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.NoColumnRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.NoTableNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.ReservedWordTableNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.tablespace.impl.UninputTablespaceRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl.NoViewNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl.NoViewSqlRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl.ReservedWordViewNameRule;

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

package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.all;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.DBManager;
import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.eclipse.core.resources.IMarker;

public class ReservedNameRule extends BaseRule {

    public boolean validate(ERDiagram diagram) {
        DBManager dbManager = DBManagerFactory.getDBManager(diagram);

        for (ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {

            for (ERIndex index : table.getIndexes()) {
                String indexName = index.getName().toLowerCase();

                if (dbManager.isReservedWord(indexName)) {
                    ValidateResult validateResult = new ValidateResult();
                    validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [INDEX] " + indexName
                            + " (" + table.getLogicalName() + ")");
                    validateResult.setLocation(indexName);
                    validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                    validateResult.setObject(index);

                    this.addError(validateResult);
                }
            }
        }

        for (Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            String name = sequence.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [SEQUENCE] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(sequence);

                this.addError(validateResult);
            }
        }

        for (ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            String name = view.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [VIEW] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(view);

                this.addError(validateResult);
            }
        }

        for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            String name = trigger.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [TRIGGER] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(trigger);

                this.addError(validateResult);
            }
        }

        return true;
    }
}

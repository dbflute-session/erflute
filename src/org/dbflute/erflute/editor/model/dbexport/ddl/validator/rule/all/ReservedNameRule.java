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

    @Override
    public boolean validate(ERDiagram diagram) {
        final DBManager dbManager = DBManagerFactory.getDBManager(diagram);

        for (final ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {
            for (final ERIndex index : table.getIndexes()) {
                final String indexName = index.getName().toLowerCase();

                if (dbManager.isReservedWord(indexName)) {
                    final ValidateResult validateResult = new ValidateResult();
                    validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [INDEX] " + indexName
                            + " (" + table.getLogicalName() + ")");
                    validateResult.setLocation(indexName);
                    validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                    validateResult.setObject(index);

                    addError(validateResult);
                }
            }
        }

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            final String name = sequence.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [SEQUENCE] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(sequence);

                addError(validateResult);
            }
        }

        for (final ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            final String name = view.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [VIEW] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(view);

                addError(validateResult);
            }
        }

        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            final String name = trigger.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.reserved.name") + " [TRIGGER] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(trigger);

                addError(validateResult);
            }
        }

        return true;
    }
}

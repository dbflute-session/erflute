package org.dbflute.erflute.db.impl.hsqldb;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class HSQLDBDDLCreator extends DDLCreator {

    public HSQLDBDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    protected String doBuildCreateTablespace(Tablespace tablespace) {
        return null;
    }

    @Override
    public String doBuildCreateSequence(Sequence sequence) {
        StringBuilder ddl = new StringBuilder();

        String description = sequence.getDescription();
        if (this.semicolon && !Check.isEmpty(description) && this.ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(description.replaceAll("\n", "\n-- "));
            ddl.append("\r\n");
        }

        ddl.append("CREATE ");
        ddl.append("SEQUENCE ");
        ddl.append(filter(this.getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (!Check.isEmpty(sequence.getDataType())) {
            ddl.append(" AS ");
            String dataType = sequence.getDataType();
            ddl.append(dataType);
        }
        if (sequence.getIncrement() != null) {
            ddl.append(" INCREMENT BY ");
            ddl.append(sequence.getIncrement());
        }
        if (sequence.getMinValue() != null) {
            ddl.append(" MINVALUE ");
            ddl.append(sequence.getMinValue());
        }
        if (sequence.getMaxValue() != null) {
            ddl.append(" MAXVALUE ");
            ddl.append(sequence.getMaxValue());
        }
        if (sequence.getStart() != null) {
            ddl.append(" START WITH ");
            ddl.append(sequence.getStart());
        }
        if (sequence.isCycle()) {
            ddl.append(" CYCLE");
        }
        if (this.semicolon) {
            ddl.append(";");
        }

        return ddl.toString();

    }
}

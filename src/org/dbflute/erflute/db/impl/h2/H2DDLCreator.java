package org.dbflute.erflute.db.impl.h2;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class H2DDLCreator extends DDLCreator {

    public H2DDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    protected String doBuildCreateTablespace(Tablespace object) {
        return null;
    }

    @Override
    protected String doBuildCreateSequence(Sequence sequence) {
        final StringBuilder ddl = new StringBuilder();

        final String description = sequence.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(description.replaceAll("\n", "\n-- "));
            ddl.append("\r\n");
        }

        ddl.append("CREATE ");
        ddl.append("SEQUENCE IF NOT EXISTS ");
        ddl.append(filter(getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (sequence.getStart() != null) {
            ddl.append(" START WITH ");
            ddl.append(sequence.getStart());
        }
        if (sequence.getIncrement() != null) {
            ddl.append(" INCREMENT BY ");
            ddl.append(sequence.getIncrement());
        }
        if (sequence.getCache() != null) {
            ddl.append(" CACHE ");
            ddl.append(sequence.getCache());
        }
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }
}

package org.dbflute.erflute.db.impl.standard_sql;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class StandardSQLDDLCreator extends DDLCreator {

    public StandardSQLDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    protected String getDDL(Tablespace tablespace) {
        return null;
    }
}

package org.dbflute.erflute.db.impl.access;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.DDLCreator;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class AccessDDLCreator extends DDLCreator {

    public AccessDDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    public String getDropDDL(ERIndex index, ERTable table) {
        StringBuilder ddl = new StringBuilder();

        ddl.append("DROP INDEX ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(index.getName()));
        ddl.append(" ON ");
        ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase())));

        if (this.semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
    protected String getDDL(Tablespace object) {
        return null;
    }

}

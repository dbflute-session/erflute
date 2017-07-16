package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CopyCompoundUniqueKey extends CompoundUniqueKey {

    private static final long serialVersionUID = 1L;

    private CompoundUniqueKey originalComplexUniqueKey;

    public CopyCompoundUniqueKey(CompoundUniqueKey original, List<ERColumn> copyColumns) {
        super(original.getUniqueKeyName());

        this.originalComplexUniqueKey = original;

        for (final NormalColumn originalColumn : original.getColumnList()) {
            for (final ERColumn column : copyColumns) {
                if (column instanceof CopyColumn) {
                    final CopyColumn copyColumn = (CopyColumn) column;

                    if (copyColumn.getOriginalColumn().equals(originalColumn)) {
                        addColumn(copyColumn);
                        break;
                    }
                }
            }
        }
    }

    public CompoundUniqueKey restructure() {
        if (originalComplexUniqueKey == null) {
            originalComplexUniqueKey = new CompoundUniqueKey(getUniqueKeyName());
        }

        final List<NormalColumn> normalColumns = new ArrayList<>();

        for (NormalColumn column : getColumnList()) {
            final CopyColumn copyColumn = (CopyColumn) column;
            column = copyColumn.getOriginalColumn();
            normalColumns.add(column);
        }

        originalComplexUniqueKey.setColumnList(normalColumns);
        originalComplexUniqueKey.setUniqueKeyName(getUniqueKeyName());

        return originalComplexUniqueKey;
    }

    public CompoundUniqueKey getOriginal() {
        return originalComplexUniqueKey;
    }
}

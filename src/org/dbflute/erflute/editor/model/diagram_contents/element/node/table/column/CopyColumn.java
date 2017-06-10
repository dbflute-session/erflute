package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column;

import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;

public class CopyColumn extends NormalColumn {

    private static final long serialVersionUID = 1L;

    private NormalColumn originalColumn;

    public CopyColumn(NormalColumn originalColumn) {
        super(originalColumn);

        if (originalColumn == null) {
            throw new IllegalArgumentException("originalColumn is null.");
        }

        this.originalColumn = originalColumn;
    }

    public NormalColumn getRestructuredColumn() {
        CopyWord copyWord = this.getWord();
        if (copyWord != null) {
            if (!(this.originalColumn instanceof CopyColumn)) {
                this.originalColumn.setWord(copyWord.getOriginal());
            }
        }

        copyData(this, this.originalColumn);

        return this.originalColumn;
    }

    @Override
    public boolean isForeignKey() {
        return this.originalColumn.isForeignKey();
    }

    @Override
    public boolean isRefered() {
        return this.originalColumn.isRefered();
    }

    public NormalColumn getOriginalColumn() {
        return originalColumn;
    }

    public Word getOriginalWord() {
        if (this.getWord() != null) {
            return this.getWord().getOriginal();
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        NormalColumn originalColumn = this.getOriginalColumn();

        if (obj instanceof CopyColumn) {
            CopyColumn copy = (CopyColumn) obj;
            obj = copy.getOriginalColumn();
        }

        return originalColumn.equals(obj);
    }

    @Override
    public CopyWord getWord() {
        return (CopyWord) super.getWord();
    }
}

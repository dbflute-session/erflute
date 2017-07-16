package org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class Dictionary extends AbstractModel {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_CHANGE_DICTIONARY = "dictionary";

    private final Map<Word, List<NormalColumn>> wordMap;

    public Dictionary() {
        this.wordMap = new HashMap<>();
    }

    public void add(NormalColumn column) {
        final Word word = column.getWord();
        if (word == null) {
            return;
        }

        List<NormalColumn> useColumns = wordMap.get(word);
        if (useColumns == null) {
            useColumns = new ArrayList<>();
            wordMap.put(word, useColumns);
        }

        if (!useColumns.contains(column)) {
            useColumns.add(column);
        }

        firePropertyChange(PROPERTY_CHANGE_DICTIONARY, null, null);
    }

    public void remove(NormalColumn column) {
        final Word word = column.getWord();
        if (word == null) {
            return;
        }

        final List<NormalColumn> useColumns = wordMap.get(word);
        if (useColumns != null) {
            useColumns.remove(column);
            if (useColumns.isEmpty()) {
                wordMap.remove(word);
            }
        }

        firePropertyChange(PROPERTY_CHANGE_DICTIONARY, null, null);
    }

    public void remove(TableView tableView) {
        if (tableView instanceof ERVirtualTable) {
            return; // 仮想テーブルを消すときはワードは消さない
        }
        for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
            remove(normalColumn);
        }
    }

    public void clear() {
        wordMap.clear();
    }

    public List<Word> getWordList() {
        final List<Word> list = new ArrayList<>(wordMap.keySet());
        Collections.sort(list);

        return list;
    }

    public List<NormalColumn> getColumnList(Word word) {
        return wordMap.get(word);
    }

    public void copyTo(Word from, Word to) {
        from.copyTo(to);
    }
}

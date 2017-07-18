package org.dbflute.erflute.editor.model.search;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class SearchManager {

    private static final int COLUMN_TYPE_NORMAL = 1;
    private static final int COLUMN_TYPE_GROUP = 2;

    private final ERDiagram diagram;

    // 単語
    private boolean physicalWordNameCheckBox;
    private boolean logicalWordNameCheckBox;
    private boolean wordTypeCheckBox;
    private boolean wordLengthCheckBox;
    private boolean wordDecimalCheckBox;
    private boolean wordDescriptionCheckBox;

    // テーブル
    private boolean physicalTableNameCheckBox;
    private boolean logicalTableNameCheckBox;
    private boolean physicalColumnNameCheckBox;
    private boolean logicalColumnNameCheckBox;
    private boolean columnTypeCheckBox;
    private boolean columnLengthCheckBox;
    private boolean columnDecimalCheckBox;
    private boolean columnDefaultValueCheckBox;
    private boolean columnGroupNameCheckBox;

    // グループ
    private boolean groupNameCheckBox;
    private boolean physicalGroupColumnNameCheckBox;
    private boolean logicalGroupColumnNameCheckBox;
    private boolean groupColumnDefaultValueCheckBox;

    // その他
    private boolean indexCheckBox;
    private boolean noteCheckBox;
    private boolean modelPropertiesCheckBox;
    private boolean relationCheckBox;
    private Object currentTarget;
    private String currentKeyword;
    private boolean all;
    private static final List<String> keywordList = new ArrayList<>();

    public SearchManager(ERDiagram diagram) {
        this.diagram = diagram;
    }

    public SearchResult search(String keyword, boolean all, boolean physicalWordNameCheckBox, boolean logicalWordNameCheckBox,
            boolean wordTypeCheckBox, boolean wordLengthCheckBox, boolean wordDecimalCheckBox, boolean wordDescriptionCheckBox,
            boolean physicalTableNameCheckBox, boolean logicalTableNameCheckBox, boolean physicalColumnNameCheckBox,
            boolean logicalColumnNameCheckBox, boolean columnTypeCheckBox, boolean columnLengthCheckBox, boolean columnDecimalCheckBox,
            boolean columnDefaultValueCheckBox, boolean columnDescriptionCheckBox, boolean columnGroupNameCheckBox, boolean indexCheckBox,
            boolean noteCheckBox, boolean modelPropertiesCheckBox, boolean relationCheckBox, boolean groupNameCheckBox,
            boolean physicalGroupColumnNameCheckBox, boolean logicalGroupColumnNameCheckBox, boolean groupColumnTypeCheckBox,
            boolean groupColumnLengthCheckBox, boolean groupColumnDecimalCheckBox, boolean groupColumnDefaultValueCheckBox,
            boolean groupColumnDescriptionCheckBox) {

        // 単語
        this.physicalWordNameCheckBox = physicalWordNameCheckBox;
        this.logicalWordNameCheckBox = logicalWordNameCheckBox;
        this.wordTypeCheckBox = wordTypeCheckBox;
        this.wordLengthCheckBox = wordLengthCheckBox;
        this.wordDecimalCheckBox = wordDecimalCheckBox;
        this.wordDescriptionCheckBox = wordDescriptionCheckBox;
        // テーブル
        this.physicalTableNameCheckBox = physicalTableNameCheckBox;
        this.logicalTableNameCheckBox = logicalTableNameCheckBox;
        this.physicalColumnNameCheckBox = physicalColumnNameCheckBox;
        this.logicalColumnNameCheckBox = logicalColumnNameCheckBox;
        this.columnTypeCheckBox = columnTypeCheckBox;
        this.columnLengthCheckBox = columnLengthCheckBox;
        this.columnDecimalCheckBox = columnDecimalCheckBox;
        this.columnDefaultValueCheckBox = columnDefaultValueCheckBox;
        this.columnGroupNameCheckBox = columnGroupNameCheckBox;
        // その他
        this.indexCheckBox = indexCheckBox;
        this.noteCheckBox = noteCheckBox;
        this.modelPropertiesCheckBox = modelPropertiesCheckBox;
        this.relationCheckBox = relationCheckBox;
        // グループ
        this.groupNameCheckBox = groupNameCheckBox;
        this.physicalGroupColumnNameCheckBox = physicalGroupColumnNameCheckBox;
        this.logicalGroupColumnNameCheckBox = logicalGroupColumnNameCheckBox;
        this.groupColumnDefaultValueCheckBox = groupColumnDefaultValueCheckBox;

        // すべて検索（置換）
        this.all = all;

        if (keyword.equals("")) {
            return null;
        }

        addKeyword(keyword);
        this.currentKeyword = keyword.toUpperCase();

        SearchResult result = null;
        final List<SearchResultRow> rows = new ArrayList<>();

        // 現在の検索候補が設定されている場合は、その検索候補まで、検索をスキップします
        boolean skip = false;
        if (currentTarget != null) {
            skip = true;
        }

        boolean loop = true;

        while (loop) {
            for (final Word word : diagram.getDiagramContents().getDictionary().getWordList()) {
                if (skip) {
                    // スキップ中の場合
                    if (word != currentTarget) {
                        continue;
                    } else {
                        skip = false;
                        continue;
                    }
                } else {
                    // 次の検索候補を探し中
                    if (word == currentTarget) {
                        // 現在の検索候補まで戻ってきてしまった場合
                        loop = false;
                    }
                }

                rows.addAll(search(word, currentKeyword, DisplayMessages.getMessage("label.dictionary")));
                if (!rows.isEmpty() && !all) {
                    // 検索候補が見つかって、すべて検索ではない場合
                    // 検索結果を作成して終了
                    result = new SearchResult(word, rows);
                    loop = false;
                }

                if (!loop) {
                    break;
                }
            }

            if (!loop) {
                break;
            }

            for (final DiagramWalker nodeElement : diagram.getDiagramContents().getDiagramWalkers()) {
                if (skip) {
                    if (nodeElement != currentTarget) {
                        continue;
                    } else {
                        skip = false;
                        continue;
                    }
                } else {
                    if (nodeElement == currentTarget) {
                        loop = false;
                    }
                }

                if (nodeElement instanceof ERTable) {
                    rows.addAll(search((ERTable) nodeElement, currentKeyword));
                } else if (nodeElement instanceof WalkerNote) {
                    rows.addAll(search((WalkerNote) nodeElement, currentKeyword));
                } else if (nodeElement instanceof ModelProperties) {
                    rows.addAll(search((ModelProperties) nodeElement, currentKeyword));
                }

                if (!rows.isEmpty() && !all) {
                    result = new SearchResult(nodeElement, rows);
                    loop = false;
                }

                if (!loop) {
                    break;
                }
            }

            if (!loop) {
                break;
            }

            if (this.relationCheckBox) {
                for (final DiagramWalker nodeElement : diagram.getDiagramContents().getDiagramWalkers()) {
                    if (nodeElement instanceof ERTable) {
                        final ERTable table = (ERTable) nodeElement;

                        for (final Relationship relation : table.getIncomingRelationshipList()) {
                            if (skip) {
                                if (relation != currentTarget) {
                                    continue;
                                } else {
                                    skip = false;
                                    continue;
                                }
                            } else {
                                if (relation == currentTarget) {
                                    loop = false;
                                }
                            }

                            rows.addAll(search(relation, keyword));
                            if (!rows.isEmpty() && !all) {
                                result = new SearchResult(relation, rows);
                                loop = false;
                            }

                            if (!loop) {
                                break;
                            }
                        }
                    }

                    if (!loop) {
                        break;
                    }
                }
            }

            if (!loop) {
                break;
            }

            for (final ColumnGroup columnGroup : diagram.getDiagramContents().getColumnGroupSet()) {
                if (skip) {
                    if (columnGroup != currentTarget) {
                        continue;
                    } else {
                        skip = false;
                        continue;
                    }
                } else {
                    if (columnGroup == currentTarget) {
                        loop = false;
                    }
                }

                rows.addAll(search(columnGroup, keyword));
                if (!rows.isEmpty() && !all) {
                    result = new SearchResult(columnGroup, rows);
                    loop = false;
                }

                if (!loop) {
                    break;
                }
            }

            if (skip || currentTarget == null) {
                // 前回の検索対象がなくなってしまった場合
                // または、最初の検索が１件もヒットしなかった場合
                loop = false;
            }
        }

        if (result != null) {
            this.currentTarget = result.getResultObject();
        } else if (!rows.isEmpty()) {
            result = new SearchResult(null, rows);
        }

        return result;
    }

    public SearchResult research() {
        SearchResult result = null;
        final List<SearchResultRow> rows = new ArrayList<>();

        boolean skip = false;
        if (currentTarget != null) {
            skip = true;
        }

        boolean loop = true;
        while (loop) {
            for (final Word word : diagram.getDiagramContents().getDictionary().getWordList()) {
                if (skip) {
                    // スキップ中の場合
                    if (word != currentTarget) {
                        continue;
                    } else {
                        skip = false;
                    }
                } else {
                    // 次の検索候補を探し中
                    if (word == currentTarget) {
                        // 現在の検索候補まで戻ってきてしまった場合
                        loop = false;
                        break;
                    }
                }

                rows.addAll(search(word, currentKeyword, DisplayMessages.getMessage("label.dictionary")));
                if (!rows.isEmpty() && !all) {
                    // 検索候補が見つかって、すべて検索ではない場合
                    // 検索結果を作成して終了
                    result = new SearchResult(word, rows);
                    loop = false;
                }

                if (!loop) {
                    break;
                }
            }

            if (!loop) {
                break;
            }

            for (final DiagramWalker nodeElement : diagram.getDiagramContents().getDiagramWalkers()) {
                if (skip) {
                    if (nodeElement != currentTarget) {
                        continue;
                    } else {
                        skip = false;
                    }
                } else {
                    if (nodeElement == currentTarget) {
                        loop = false;
                        break;
                    }
                }

                if (nodeElement instanceof ERTable) {
                    rows.addAll(search((ERTable) nodeElement, currentKeyword));
                } else if (nodeElement instanceof WalkerNote) {
                    rows.addAll(search((WalkerNote) nodeElement, currentKeyword));
                } else if (nodeElement instanceof ModelProperties) {
                    rows.addAll(search((ModelProperties) nodeElement, currentKeyword));
                }

                if (!rows.isEmpty() && !all) {
                    result = new SearchResult(nodeElement, rows);
                    loop = false;
                }

                if (!loop) {
                    break;
                }
            }

            if (!loop) {
                break;
            }

            if (relationCheckBox) {
                for (final DiagramWalker nodeElement : diagram.getDiagramContents().getDiagramWalkers()) {
                    if (nodeElement instanceof ERTable) {
                        final ERTable table = (ERTable) nodeElement;

                        for (final Relationship relation : table.getIncomingRelationshipList()) {
                            if (skip) {
                                if (relation != currentTarget) {
                                    continue;

                                } else {
                                    skip = false;
                                }
                            } else {
                                if (relation == currentTarget) {
                                    loop = false;
                                    break;
                                }
                            }

                            rows.addAll(search(relation, currentKeyword));
                            if (!rows.isEmpty() && !all) {
                                result = new SearchResult(relation, rows);
                                loop = false;
                            }

                            if (!loop) {
                                break;
                            }
                        }
                    }

                    if (!loop) {
                        break;
                    }
                }
            }

            if (!loop) {
                break;
            }

            for (final ColumnGroup columnGroup : diagram.getDiagramContents().getColumnGroupSet()) {
                if (skip) {
                    if (columnGroup != currentTarget) {
                        continue;
                    } else {
                        skip = false;
                    }
                } else {
                    if (columnGroup == currentTarget) {
                        loop = false;
                        break;
                    }
                }

                rows.addAll(search(columnGroup, currentKeyword));
                if (!rows.isEmpty() && !all) {
                    result = new SearchResult(columnGroup, rows);
                    loop = false;
                }

                if (!loop) {
                    break;
                }
            }

            if (skip || currentTarget == null) {
                loop = false;
            }
        }

        if (result != null) {
            currentTarget = result.getResultObject();
        } else if (!rows.isEmpty()) {
            result = new SearchResult(null, rows);
        }

        return result;
    }

    private List<SearchResultRow> search(ERTable table, String keyword) {
        final List<SearchResultRow> rows = new ArrayList<>();
        final String path = table.getLogicalName();

        if (physicalTableNameCheckBox) {
            if (search(table.getPhysicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_TABLE_PHYSICAL_NAME, table.getPhysicalName(), path, table, table));
            }
        }

        if (logicalTableNameCheckBox) {
            if (search(table.getLogicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_TABLE_LOGICAL_NAME, table.getLogicalName(), path, table, table));
            }
        }

        if (physicalColumnNameCheckBox || logicalColumnNameCheckBox || columnTypeCheckBox || columnLengthCheckBox
                || columnDecimalCheckBox || columnDefaultValueCheckBox || columnGroupNameCheckBox) {

            for (final ERColumn column : table.getColumns()) {
                if (column instanceof NormalColumn) {
                    final NormalColumn normalColumn = (NormalColumn) column;
                    rows.addAll(search(table, normalColumn, keyword, COLUMN_TYPE_NORMAL, path));
                } else if (column instanceof ColumnGroup) {
                    if (columnGroupNameCheckBox) {
                        if (search(column.getName(), keyword)) {
                            final String childPath = path + column.getName();
                            rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_GROUP_NAME,
                                    column.getName(), childPath, column, table));
                        }
                    }
                }
            }
        }

        if (indexCheckBox) {
            for (final ERIndex index : table.getIndexes()) {
                rows.addAll(search(table, index, keyword, path));
            }
        }

        return rows;
    }

    private List<SearchResultRow> search(WalkerNote note, String keyword) {
        final List<SearchResultRow> rows = new ArrayList<>();

        if (noteCheckBox) {
            final String path = null;
            if (search(note.getNoteText(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_NOTE, note.getNoteText(), path, note, note));
            }
        }

        return rows;
    }

    private List<SearchResultRow> search(ModelProperties modelProperties, String keyword) {
        final List<SearchResultRow> rows = new ArrayList<>();

        if (modelPropertiesCheckBox) {
            final String path = null;
            for (final NameValue property : modelProperties.getProperties()) {
                if (search(property.getName(), keyword)) {
                    rows.add(new SearchResultRow(SearchResultRow.TYPE_MODEL_PROPERTY_NAME,
                            property.getName(), path, property, modelProperties));
                }

                if (search(property.getValue(), keyword)) {
                    rows.add(new SearchResultRow(SearchResultRow.TYPE_MODEL_PROPERTY_VALUE,
                            property.getValue(), path, property, modelProperties));
                }
            }
        }

        return rows;
    }

    private List<SearchResultRow> search(ERTable table, NormalColumn normalColumn, String keyword, int type, String parentPath) {
        final List<SearchResultRow> rows = new ArrayList<>();

        final String path = parentPath + "/" + normalColumn.getLogicalName();

        if (type == COLUMN_TYPE_GROUP && physicalGroupColumnNameCheckBox) {
            if (search(normalColumn.getForeignKeyPhysicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME,
                        normalColumn.getForeignKeyPhysicalName(), path, normalColumn, table));
            }
        } else if (physicalColumnNameCheckBox) {
            if (search(normalColumn.getForeignKeyPhysicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME,
                        normalColumn.getForeignKeyPhysicalName(), path, normalColumn, table));
            }
        }
        if (type == COLUMN_TYPE_GROUP && logicalGroupColumnNameCheckBox) {
            if (search(normalColumn.getForeignKeyLogicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME,
                        normalColumn.getForeignKeyLogicalName(), path, normalColumn, table));
            }
        } else if (logicalColumnNameCheckBox) {
            if (search(normalColumn.getForeignKeyLogicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_LOGICAL_NAME,
                        normalColumn.getForeignKeyLogicalName(), path, normalColumn, table));
            }
        }

        if (type == COLUMN_TYPE_GROUP && groupColumnDefaultValueCheckBox) {
            if (search(normalColumn.getDefaultValue(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DEFAULT_VALUE,
                        normalColumn.getDefaultValue(), path, normalColumn, table));
            }
        } else if (columnDefaultValueCheckBox) {
            if (search(normalColumn.getDefaultValue(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_DEFAULT_VALUE,
                        normalColumn.getDefaultValue(), path, normalColumn, table));
            }
        }

        return rows;
    }

    private List<SearchResultRow> search(Word word, String keyword, String parentPath) {
        final List<SearchResultRow> rows = new ArrayList<>();
        final String path = parentPath + "/" + word.getLogicalName();

        if (physicalWordNameCheckBox) {
            if (search(word.getPhysicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_PHYSICAL_NAME, word.getPhysicalName(), path, word, null));
            }
        }
        if (logicalWordNameCheckBox) {
            if (search(word.getLogicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_LOGICAL_NAME, word.getLogicalName(), path, word, null));
            }
        }
        if (word.getType() != null && word.getType().getAlias(diagram.getDatabase()) != null) {
            if (wordTypeCheckBox) {
                if (search(word.getType().getAlias(diagram.getDatabase()), keyword)) {
                    rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_TYPE,
                            word.getType().getAlias(diagram.getDatabase()), path, word, null));
                }
            }
        }

        if (wordLengthCheckBox) {
            if (search(word.getTypeData().getLength(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_LENGTH,
                        String.valueOf(word.getTypeData().getLength()), path, word, null));
            }
        }

        if (wordDecimalCheckBox) {
            if (search(word.getTypeData().getDecimal(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_DECIMAL,
                        String.valueOf(word.getTypeData().getDecimal()), path, word, null));
            }
        }

        if (wordDescriptionCheckBox) {
            if (search(word.getDescription(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_COMMENT, word.getDescription(), path, word, null));
            }
        }

        return rows;
    }

    private List<SearchResultRow> search(ERTable table, ERIndex index, String keyword, String parentPath) {
        final List<SearchResultRow> rows = new ArrayList<>();
        final String path = parentPath + "/" + index.getName();

        if (search(index.getName(), keyword)) {
            rows.add(new SearchResultRow(SearchResultRow.TYPE_INDEX_NAME, index.getName(), path, index, table));
        }
        for (final NormalColumn normalColumn : index.getColumns()) {
            if (search(normalColumn.getPhysicalName(), keyword)) {
                rows.add(new SearchResultRow(SearchResultRow.TYPE_INDEX_COLUMN_NAME,
                        normalColumn.getPhysicalName(), path, normalColumn, table));
            }
        }

        return rows;
    }

    private List<SearchResultRow> search(Relationship relation, String keyword) {
        final List<SearchResultRow> rows = new ArrayList<>();

        if (search(relation.getForeignKeyName(), keyword)) {
            final String path = relation.getForeignKeyName();
            rows.add(new SearchResultRow(SearchResultRow.TYPE_RELATION_NAME, relation.getForeignKeyName(), path, relation, relation));
        }

        return rows;
    }

    private List<SearchResultRow> search(ColumnGroup columnGroup, String keyword) {
        final List<SearchResultRow> rows = new ArrayList<>();
        final String path = columnGroup.getGroupName();

        if (groupNameCheckBox && search(columnGroup.getName(), keyword)) {
            rows.add(new SearchResultRow(SearchResultRow.TYPE_COLUMN_GROUP_NAME, columnGroup.getName(), path, columnGroup, columnGroup));
        }

        for (final NormalColumn normalColumn : columnGroup.getColumns()) {
            rows.addAll(search(null, normalColumn, keyword, COLUMN_TYPE_GROUP, path));
        }

        return rows;
    }

    private boolean search(String str, String keyword) {
        if (str == null) {
            return false;
        }

        if (str.toUpperCase().indexOf(keyword) != -1) {
            return true;
        }

        return false;
    }

    private boolean search(Integer num, String keyword) {
        if (num == null) {
            return false;
        }

        return search(String.valueOf(num), keyword);
    }

    private static void addKeyword(String keyword) {
        if (!keywordList.contains(keyword)) {
            keywordList.add(0, keyword);
        }

        if (keywordList.size() > 20) {
            keywordList.remove(keywordList.size() - 1);
        }
    }

    public static List<String> getKeywordList() {
        return keywordList;
    }
}

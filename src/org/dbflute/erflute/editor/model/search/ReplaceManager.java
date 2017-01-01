package org.dbflute.erflute.editor.model.search;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.note.WalkerNote;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class ReplaceManager {

    private static final int[] ALPHABET_TYPES = new int[] { SearchResultRow.TYPE_RELATION_NAME, SearchResultRow.TYPE_INDEX_NAME,
            SearchResultRow.TYPE_TABLE_PHYSICAL_NAME, SearchResultRow.TYPE_WORD_PHYSICAL_NAME, SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME,
            SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME };

    private static final int[] DEGIT_TYPES = new int[] { SearchResultRow.TYPE_WORD_LENGTH, SearchResultRow.TYPE_WORD_DECIMAL,
            SearchResultRow.TYPE_COLUMN_LENGTH, SearchResultRow.TYPE_COLUMN_DECIMAL, SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LENGTH,
            SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DECIMAL };

    private static final int[] REQUIRED_TYPES = new int[] { SearchResultRow.TYPE_INDEX_NAME, SearchResultRow.TYPE_TABLE_LOGICAL_NAME,
            SearchResultRow.TYPE_WORD_LOGICAL_NAME, SearchResultRow.TYPE_COLUMN_LOGICAL_NAME, SearchResultRow.TYPE_COLUMN_GROUP_NAME,
            SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME };

    private static final int[] EXCLUDE_TYPES = new int[] { SearchResultRow.TYPE_INDEX_COLUMN_NAME, SearchResultRow.TYPE_WORD_TYPE,
            SearchResultRow.TYPE_COLUMN_TYPE, SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_TYPE };

    private static final List<String> replaceWordList = new ArrayList<String>();

    public static ReplaceResult replace(int type, Object object, String keyword, String replaceWord, String database) {

        addReplaceWord(replaceWord);

        for (final int excludeType : EXCLUDE_TYPES) {
            if (type == excludeType) {
                return null;
            }
        }

        checkAlphabet(type, replaceWord);
        checkDegit(type, replaceWord);

        if (type == SearchResultRow.TYPE_RELATION_NAME) {
            final Relationship relation = (Relationship) object;
            final String original = relation.getForeignKeyName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            relation.setForeignKeyName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_INDEX_NAME) {
            final ERIndex index = (ERIndex) object;
            final String original = index.getName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            index.setName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_INDEX_COLUMN_NAME) {

            return null;

        } else if (type == SearchResultRow.TYPE_NOTE) {
            final WalkerNote note = (WalkerNote) object;
            final String original = note.getNoteText();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            note.setNoteText(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_NAME) {
            final NameValue property = (NameValue) object;
            final String original = property.getName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            property.setName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_VALUE) {
            final NameValue property = (NameValue) object;
            final String original = property.getValue();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            property.setValue(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_TABLE_PHYSICAL_NAME) {
            final ERTable table = (ERTable) object;
            final String original = table.getPhysicalName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            table.setPhysicalName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_TABLE_LOGICAL_NAME) {
            final ERTable table = (ERTable) object;
            final String original = table.getLogicalName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            table.setLogicalName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME) {
            final NormalColumn column = (NormalColumn) object;
            final String original = column.getForeignKeyPhysicalName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            column.setForeignKeyPhysicalName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_COLUMN_LOGICAL_NAME || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME) {
            final NormalColumn column = (NormalColumn) object;
            final String original = column.getForeignKeyLogicalName();

            final String str = replace(original, keyword, replaceWord);

            checkRequired(type, str);

            column.setForeignKeyLogicalName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_COLUMN_DEFAULT_VALUE || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DEFAULT_VALUE) {

            final NormalColumn column = (NormalColumn) object;
            final String original = column.getDefaultValue();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            column.setDefaultValue(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_COLUMN_COMMENT) {
            final NormalColumn column = (NormalColumn) object;
            final String original = column.getForeignKeyDescription();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            column.setForeignKeyDescription(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_COLUMN_GROUP_NAME || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_COMMENT) {
            final ColumnGroup group = (ColumnGroup) object;
            final String original = group.getGroupName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            group.setGroupName(str);

            return new ReplaceResult(original);
        } else if (type == SearchResultRow.TYPE_WORD_PHYSICAL_NAME) {
            final Word word = (Word) object;
            final String original = word.getPhysicalName();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            word.setPhysicalName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_WORD_LOGICAL_NAME) {
            final Word word = (Word) object;
            final String original = word.getLogicalName();

            final String str = replace(original, keyword, replaceWord);

            checkRequired(type, str);

            word.setLogicalName(str);

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_WORD_LENGTH) {
            final Word word = (Word) object;
            final String original = String.valueOf(word.getTypeData().getLength());

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            if (!str.equals("")) {
                final TypeData oldTypeData = word.getTypeData();
                final TypeData newTypeData =
                        new TypeData(Integer.parseInt(str), oldTypeData.getDecimal(), oldTypeData.isArray(),
                                oldTypeData.getArrayDimension(), oldTypeData.isUnsigned(), oldTypeData.getArgs(),
                                oldTypeData.isCharSemantics());

                word.setType(word.getType(), newTypeData, database);
            }

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_WORD_DECIMAL) {
            final Word word = (Word) object;
            final String original = String.valueOf(word.getTypeData().getDecimal());

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            if (!str.equals("")) {
                final TypeData oldTypeData = word.getTypeData();
                final TypeData newTypeData =
                        new TypeData(oldTypeData.getLength(), Integer.parseInt(str), oldTypeData.isArray(),
                                oldTypeData.getArrayDimension(), oldTypeData.isUnsigned(), oldTypeData.getArgs(),
                                oldTypeData.isCharSemantics());

                word.setType(word.getType(), newTypeData, database);
            }

            return new ReplaceResult(original);

        } else if (type == SearchResultRow.TYPE_WORD_COMMENT) {
            final Word word = (Word) object;
            final String original = word.getDescription();

            final String str = replace(original, keyword, replaceWord);

            if (!checkRequired(type, str)) {
                return null;
            }

            word.setDescription(str);

            return new ReplaceResult(original);
        }

        return null;
    }

    private static boolean checkAlphabet(int type, String str) {
        if (str == null || str.equals("")) {
            return true;
        }

        for (final int alphabetType : ALPHABET_TYPES) {
            if (type == alphabetType) {
                if (!Check.isAlphabet(str)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean checkDegit(int type, String str) {
        if (str == null || str.equals("")) {
            return true;
        }

        for (final int degitType : DEGIT_TYPES) {
            if (type == degitType) {
                try {
                    final int len = Integer.parseInt(str);
                    if (len < 0) {
                        return false;
                    }

                } catch (final NumberFormatException e) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean checkRequired(int type, String str) {
        for (final int requiredType : REQUIRED_TYPES) {
            if (type == requiredType) {
                if (str == null || str.trim().equals("")) {
                    return false;
                }
            }
        }

        return true;
    }

    private static String replace(String str, String keyword, String replaceWord) {
        return str.replaceAll(keyword, replaceWord);
    }

    private static void addReplaceWord(String replaceWord) {
        if (!replaceWordList.contains(replaceWord)) {
            replaceWordList.add(0, replaceWord);
        }

        if (replaceWordList.size() > 20) {
            replaceWordList.remove(replaceWordList.size() - 1);
        }
    }

    public static List<String> getReplaceWordList() {
        return replaceWordList;
    }

    public static void undo(int type, Object object, String str) {

        if (type == SearchResultRow.TYPE_RELATION_NAME) {
            final Relationship relation = (Relationship) object;

            relation.setForeignKeyName(str);

        } else if (type == SearchResultRow.TYPE_INDEX_NAME) {
            final ERIndex index = (ERIndex) object;

            index.setName(str);

        } else if (type == SearchResultRow.TYPE_INDEX_COLUMN_NAME) {

        } else if (type == SearchResultRow.TYPE_NOTE) {
            final WalkerNote note = (WalkerNote) object;

            note.setNoteText(str);

        } else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_NAME) {
            final NameValue property = (NameValue) object;

            property.setName(str);

        } else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_VALUE) {
            final NameValue property = (NameValue) object;

            property.setValue(str);

        } else if (type == SearchResultRow.TYPE_TABLE_PHYSICAL_NAME) {
            final ERTable table = (ERTable) object;

            table.setPhysicalName(str);

        } else if (type == SearchResultRow.TYPE_TABLE_LOGICAL_NAME) {
            final ERTable table = (ERTable) object;

            table.setLogicalName(str);

        } else if (type == SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME) {
            final NormalColumn column = (NormalColumn) object;

            column.setForeignKeyPhysicalName(str);

        } else if (type == SearchResultRow.TYPE_COLUMN_LOGICAL_NAME || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME) {
            final NormalColumn column = (NormalColumn) object;

            column.setForeignKeyLogicalName(str);

        } else if (type == SearchResultRow.TYPE_COLUMN_DEFAULT_VALUE || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DEFAULT_VALUE) {
            final NormalColumn column = (NormalColumn) object;

            column.setDefaultValue(str);

        } else if (type == SearchResultRow.TYPE_COLUMN_COMMENT) {
            final NormalColumn column = (NormalColumn) object;

            column.setForeignKeyDescription(str);

        } else if (type == SearchResultRow.TYPE_COLUMN_GROUP_NAME || type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_COMMENT) {
            final ColumnGroup group = (ColumnGroup) object;

            group.setGroupName(str);

        }
    }
}

package org.dbflute.erflute.editor.persistent.xml.reader;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadColumnLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadSequenceLoader sequenceLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadColumnLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadSequenceLoader sequenceLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.sequenceLoader = sequenceLoader;
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    public List<ERColumn> loadColumns(Element parent, LoadContext context, String database) {
        final List<ERColumn> columns = new ArrayList<ERColumn>();
        final Element element = getElement(parent, "columns");
        final NodeList groupList = element.getChildNodes();
        for (int i = 0; i < groupList.getLength(); i++) {
            if (groupList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element columnElement = (Element) groupList.item(i);
            if ("column_group".equals(columnElement.getTagName())) {
                final ColumnGroup column = doLoadColumnGroup(columnElement, context);
                columns.add(column);
            } else if ("normal_column".equals(columnElement.getTagName())) {
                final NormalColumn column = doLoadNormalColumn(columnElement, context, database);
                columns.add(column);
            }
        }
        return columns;
    }

    private ColumnGroup doLoadColumnGroup(Element element, LoadContext context) {
        final String key = element.getFirstChild().getNodeValue();
        return context.columnGroupMap.get(key);
    }

    private NormalColumn doLoadNormalColumn(Element element, LoadContext context, String database) {
        final Word word = prepareWord(element, context, database);
        final NormalColumn normalColumn = createNormalColumn(element, word);
        final String id = getStringValue(element, "id");
        final boolean isForeignKey = setupRelationship(element, context, normalColumn);
        setupAutoIncrement(element, normalColumn);
        if (!isForeignKey) {
            context.dictionary.add(normalColumn);
        }
        context.columnMap.put(id, normalColumn);
        return normalColumn;
    }

    private Word prepareWord(Element element, LoadContext context, String database) {
        final String wordId = getStringValue(element, "word_id"); // needs for migration from ERMaster-b
        Word word = context.wordMap.get(wordId);
        if (word == null) { // always true after ERFlute
            word = createWord(element, context, database);
        }
        return word;
    }

    private Word createWord(Element element, LoadContext context, String database) {
        final String physicalName = nullToEmpty(getStringValue(element, "physical_name"));
        final String logicalName = nullToEmpty(getStringValue(element, "logical_name"));
        final SqlType sqlType = SqlType.valueOfId(getStringValue(element, "type"));
        final TypeData typeData = createTypeData(element);
        final String description = getStringValue(element, "description");
        Word word = new Word(physicalName, logicalName, sqlType, typeData, description, database);
        final UniqueWord uniqueWord = new UniqueWord(word); // #willanalyze needed? by jflute
        if (context.uniqueWordMap.containsKey(uniqueWord)) {
            word = context.uniqueWordMap.get(uniqueWord);
        } else {
            context.uniqueWordMap.put(uniqueWord, word);
        }
        return word;
    }

    private TypeData createTypeData(Element element) { // #for_erflute
        final Integer length = getIntegerValue(element, "length");
        final Integer decimal = getIntegerValue(element, "decimal");
        final boolean array = getBooleanValue(element, "array");
        final Integer arrayDimension = getIntegerValue(element, "arrayDimension");
        final boolean unsigned = getBooleanValue(element, "unsigned");
        final String args = getStringValue(element, "args");
        return new TypeData(length, decimal, array, arrayDimension, unsigned, args);
    }

    private NormalColumn createNormalColumn(Element element, final Word word) {
        final boolean notNull = getBooleanValue(element, "not_null");
        final boolean primaryKey = getBooleanValue(element, "primary_key");
        final boolean uniqueKey = getBooleanValue(element, "unique_key");
        final boolean autoIncrement = getBooleanValue(element, "auto_increment");
        final String defaultValue = getStringValue(element, "default_value");
        final String constraint = getStringValue(element, "constraint");
        final String uniqueKeyName = getStringValue(element, "unique_key_name");
        final String characterSet = getStringValue(element, "character_set");
        final String collation = getStringValue(element, "collation");
        return new NormalColumn(word, notNull, primaryKey, uniqueKey, autoIncrement, defaultValue, constraint, uniqueKeyName, characterSet,
                collation);
    }

    private boolean setupRelationship(Element element, LoadContext context, final NormalColumn normalColumn) {
        final String[] relationIds = getTagValues(element, "relation");
        if (relationIds != null) {
            context.columnRelationMap.put(normalColumn, relationIds);
        }
        String[] referencedColumnIds = getTagValues(element, "referenced_column");
        final List<String> temp = new ArrayList<String>();
        for (final String str : referencedColumnIds) {
            try {
                if (str != null) {
                    Integer.parseInt(str);
                    temp.add(str);
                }
            } catch (final NumberFormatException e) {}
        }
        referencedColumnIds = temp.toArray(new String[temp.size()]);
        boolean isForeignKey = false;
        if (referencedColumnIds.length != 0) {
            context.columnReferencedColumnMap.put(normalColumn, referencedColumnIds);
            isForeignKey = true;
        }
        return isForeignKey;
    }

    private void setupAutoIncrement(Element element, final NormalColumn normalColumn) {
        final Element autoIncrementSettingElement = getElement(element, "sequence");
        if (autoIncrementSettingElement != null) {
            final Sequence autoIncrementSetting = sequenceLoader.loadSequence(autoIncrementSettingElement);
            normalColumn.setAutoIncrementSetting(autoIncrementSetting);
        }
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private boolean getBooleanValue(Element element, String tagname) {
        return assistLogic.getBooleanValue(element, tagname);
    }

    private Integer getIntegerValue(Element element, String tagname) {
        return assistLogic.getIntegerValue(element, tagname);
    }

    private String[] getTagValues(Element element, String tagname) {
        return assistLogic.getTagValues(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }

    private String nullToEmpty(String str) {
        return str != null ? str : "";
    }
}
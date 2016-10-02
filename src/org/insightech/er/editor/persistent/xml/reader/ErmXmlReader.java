package org.insightech.er.editor.persistent.xml.reader;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ERView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ErmXmlReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final DateFormat DATE_FORMAT = PersistentXml.DATE_FORMAT;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDatabaseLoader databaseLoader;
    protected final ReadTablePropertiesLoader tablePropertiesLoader;
    protected final ReadViewPropertiesLoader viewPropertiesLoader;
    protected final ReadNodeElementLoader nodeElementLoader;
    protected final ReadSequenceLoader sequenceLoader;
    protected final ReadTriggerLoader triggerLoader;
    protected final ReadSettingLoader settingLoader;
    protected final ReadGroupLoader groupLoader;
    protected final ReadTablespaceLoader tablespaceLoader;
    protected final ReadDictionaryLoader dictionaryLoader;
    protected final ReadIndexLoader indexLoader;
    protected final ReadComplexUniqueKeyLoader complexUniqueKeyLoader;
    protected final ReadNoteLoader noteLoader;
    protected final ReadImageLoader imageLoader;

    // state
    protected ERDiagram diagram;
    protected String database;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ErmXmlReader(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
        this.assistLogic = new ReadAssistLogic(persistentXml);
        this.databaseLoader = new ReadDatabaseLoader(persistentXml, assistLogic);
        this.tablePropertiesLoader = new ReadTablePropertiesLoader(persistentXml, assistLogic);
        this.viewPropertiesLoader = new ReadViewPropertiesLoader(persistentXml, assistLogic);
        this.nodeElementLoader = new ReadNodeElementLoader(persistentXml, assistLogic);
        this.sequenceLoader = new ReadSequenceLoader(persistentXml, assistLogic);
        this.triggerLoader = new ReadTriggerLoader(persistentXml, assistLogic);
        this.settingLoader = new ReadSettingLoader(persistentXml, assistLogic, databaseLoader, tablePropertiesLoader, nodeElementLoader);
        this.groupLoader = new ReadGroupLoader(persistentXml, assistLogic, nodeElementLoader);
        this.tablespaceLoader = new ReadTablespaceLoader(persistentXml, assistLogic);
        this.dictionaryLoader = new ReadDictionaryLoader(persistentXml, assistLogic);
        this.indexLoader = new ReadIndexLoader(persistentXml, assistLogic);
        this.complexUniqueKeyLoader = new ReadComplexUniqueKeyLoader(persistentXml, assistLogic);
        this.noteLoader = new ReadNoteLoader(persistentXml, assistLogic, nodeElementLoader);
        this.imageLoader = new ReadImageLoader(persistentXml, assistLogic, nodeElementLoader);
    }

    // ===================================================================================
    //                                                                               Read
    //                                                                              ======
    public ERDiagram read(InputStream ins) throws Exception {
        final DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document document = parser.parse(ins);
        Node root = document.getFirstChild();
        while (root.getNodeType() == Node.COMMENT_NODE) {
            document.removeChild(root);
            root = document.getFirstChild();
        }
        load((Element) root);
        return this.diagram;
    }

    // ===================================================================================
    //                                                                               Root
    //                                                                              ======
    private void load(Element root) {
        final Element settings = getElement(root, "settings");
        this.database = databaseLoader.loadDatabase(settings);
        this.diagram = new ERDiagram(this.database);
        settingLoader.loadDBSetting(this.diagram, root);
        settingLoader.loadPageSetting(this.diagram, root);
        assistLogic.loadColor(this.diagram, root);
        assistLogic.loadDefaultColor(this.diagram, root);
        assistLogic.loadFont(this.diagram, root);
        final DiagramContents diagramContents = this.diagram.getDiagramContents();
        loadDiagramContents(diagramContents, root);
        this.diagram.setCurrentCategory(null, this.getIntValue(root, "category_index"));
        this.diagram.setCurrentErmodel(null, this.getStringValue(root, "current_ermodel"));
        final double zoom = this.getDoubleValue(root, "zoom");
        this.diagram.setZoom(zoom);
        final int x = this.getIntValue(root, "x");
        final int y = this.getIntValue(root, "y");
        this.diagram.setLocation(x, y);
    }

    // ===================================================================================
    //                                                                    Diagram Contents
    //                                                                    ================
    private void loadDiagramContents(DiagramContents diagramContents, Element parent) {
        final Dictionary dictionary = diagramContents.getDictionary();
        final LoadContext context = new LoadContext(dictionary);
        dictionaryLoader.loadDictionary(dictionary, parent, context, database);
        final Settings settings = diagramContents.getSettings();
        settingLoader.loadEnvironmentSetting(settings.getEnvironmentSetting(), parent, context);
        tablespaceLoader.loadTablespaceSet(diagramContents.getTablespaceSet(), parent, context, database);
        final GroupSet columnGroups = diagramContents.getGroups();
        columnGroups.clear();
        loadColumnGroups(columnGroups, parent, context);
        loadContents(diagramContents.getContents(), parent, context);
        diagramContents.getModelSet().addModels(loadErmodels(parent, context));
        sequenceLoader.loadSequenceSet(diagramContents.getSequenceSet(), parent);
        triggerLoader.loadTriggerSet(diagramContents.getTriggerSet(), parent);
        settingLoader.loadSettings(settings, parent, context);
        context.resolve();
    }

    // ===================================================================================
    //                                                                       Column Groups
    //                                                                       =============
    private void loadColumnGroups(GroupSet columnGroups, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "column_groups");
        final NodeList nodeList = element.getElementsByTagName("column_group");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element columnGroupElement = (Element) nodeList.item(i);
            final ColumnGroup columnGroup = new ColumnGroup();
            columnGroup.setGroupName(getStringValue(columnGroupElement, "group_name"));
            final List<ERColumn> columns = loadColumns(columnGroupElement, context);
            for (final ERColumn column : columns) {
                columnGroup.addColumn((NormalColumn) column);
            }
            columnGroups.add(columnGroup);
            final String id = getStringValue(columnGroupElement, "id");
            context.columnGroupMap.put(id, columnGroup);
        }
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    private List<ERColumn> loadColumns(Element parent, LoadContext context) {
        final List<ERColumn> columns = new ArrayList<ERColumn>();
        final Element element = this.getElement(parent, "columns");
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
                final NormalColumn column = doLoadNormalColumn(columnElement, context);
                columns.add(column);
            }
        }
        return columns;
    }

    private ColumnGroup doLoadColumnGroup(Element element, LoadContext context) {
        final String key = element.getFirstChild().getNodeValue();
        return context.columnGroupMap.get(key);
    }

    private NormalColumn doLoadNormalColumn(Element element, LoadContext context) {
        final String id = this.getStringValue(element, "id");
        final String type = this.getStringValue(element, "type");
        final String wordId = this.getStringValue(element, "word_id");
        Word word = context.wordMap.get(wordId);
        NormalColumn normalColumn = null;
        if (word == null) {
            final String physicalName = getStringValue(element, "physical_name");
            final String logicalName = getStringValue(element, "logical_name");
            final String description = getStringValue(element, "description");
            final TypeData typeData = new TypeData(null, null, false, null, false, null);
            final SqlType sqlType = SqlType.valueOfId(type);
            word = new Word(physicalName, logicalName, sqlType, typeData, description, database);
            final UniqueWord uniqueWord = new UniqueWord(word);
            if (context.uniqueWordMap.containsKey(uniqueWord)) {
                word = context.uniqueWordMap.get(uniqueWord);
            } else {
                context.uniqueWordMap.put(uniqueWord, word);
            }
        }
        final boolean notNull = getBooleanValue(element, "not_null");
        final boolean primaryKey = getBooleanValue(element, "primary_key");
        final String defaultValue = getStringValue(element, "default_value");
        final String constraint = getStringValue(element, "constraint");
        final boolean uniqueKey = getBooleanValue(element, "unique_key");
        final String uniqueKeyName = getStringValue(element, "unique_key_name");
        final String characterSet = getStringValue(element, "character_set");
        final String collation = getStringValue(element, "collation");
        final boolean autoIncrement = getBooleanValue(element, "auto_increment");
        normalColumn =
                new NormalColumn(word, notNull, primaryKey, uniqueKey, autoIncrement, defaultValue, constraint, uniqueKeyName,
                        characterSet, collation);
        final Element autoIncrementSettingElement = getElement(element, "sequence");
        if (autoIncrementSettingElement != null) {
            final Sequence autoIncrementSetting = sequenceLoader.loadSequence(autoIncrementSettingElement);
            normalColumn.setAutoIncrementSetting(autoIncrementSetting);
        }
        boolean isForeignKey = false;
        final String[] relationIds = this.getTagValues(element, "relation");
        if (relationIds != null) {
            context.columnRelationMap.put(normalColumn, relationIds);
        }
        String[] referencedColumnIds = this.getTagValues(element, "referenced_column");
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
        if (referencedColumnIds.length != 0) {
            context.columnReferencedColumnMap.put(normalColumn, referencedColumnIds);
            isForeignKey = true;
        }
        if (!isForeignKey) {
            context.dictionary.add(normalColumn);
        }
        context.columnMap.put(id, normalColumn);
        return normalColumn;
    }

    // ===================================================================================
    //                                                                            Contents
    //                                                                            ========
    private void loadContents(NodeSet contents, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "contents");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Node node = nodeList.item(i);
            if ("table".equals(node.getNodeName())) {
                final ERTable table = loadTable((Element) node, context);
                contents.addNodeElement(table);
            } else if ("view".equals(node.getNodeName())) {
                final ERView view = loadView((Element) node, context);
                contents.addNodeElement(view);
                //			} else if ("note".equals(node.getNodeName())) {
                //				Note note = this.loadNote((Element) node, context);
                //				contents.addNodeElement(note);
            } else if ("image".equals(node.getNodeName())) {
                final InsertedImage insertedImage = imageLoader.loadInsertedImage((Element) node, context);
                contents.addNodeElement(insertedImage);
            } else if ("ermodel".equals(node.getNodeName())) {
                final ERModel ermodel = loadErmodel((Element) node, context);
                contents.addNodeElement(ermodel);
                //			} else if ("group".equals(node.getNodeName())) {
                //				VGroup group = this.loadGroup((Element) node, context);
                //				contents.addNodeElement(group);
            } else {
                throw new RuntimeException("not support " + node);
            }
        }
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    private ERTable loadTable(Element element, LoadContext context) {
        final ERTable table = new ERTable();
        table.setDiagram(this.diagram);
        this.loadNodeElement(table, element, context);
        table.setPhysicalName(this.getStringValue(element, "physical_name"));
        table.setLogicalName(this.getStringValue(element, "logical_name"));
        table.setDescription(this.getStringValue(element, "description"));
        table.setConstraint(this.getStringValue(element, "constraint"));
        table.setPrimaryKeyName(this.getStringValue(element, "primary_key_name"));
        table.setOption(this.getStringValue(element, "option"));
        final List<ERColumn> columns = loadColumns(element, context);
        table.setColumns(columns);
        final List<ERIndex> indexes = indexLoader.loadIndexes(element, table, context);
        table.setIndexes(indexes);
        final List<ComplexUniqueKey> complexUniqueKeyList = complexUniqueKeyLoader.loadComplexUniqueKeyList(element, table, context);
        table.setComplexUniqueKeyList(complexUniqueKeyList);
        tablePropertiesLoader.loadTableProperties((TableProperties) table.getTableViewProperties(), element, context);
        return table;
    }

    private ERVirtualTable loadVirtualTable(ERModel model, Element element, LoadContext context) {
        final String tableId = getStringValue(element, "id");
        final ERTable rawTable = (ERTable) context.nodeElementMap.get(tableId);
        final ERVirtualTable vtable = new ERVirtualTable(model, rawTable);
        assistLogic.loadLocation(vtable, element);
        assistLogic.loadFont(vtable, element);
        return vtable;
    }

    // ===================================================================================
    //                                                                               View
    //                                                                              ======
    private ERView loadView(Element element, LoadContext context) {
        final ERView view = new ERView();
        view.setDiagram(this.diagram);
        loadNodeElement(view, element, context);
        view.setPhysicalName(getStringValue(element, "physical_name"));
        view.setLogicalName(getStringValue(element, "logical_name"));
        view.setDescription(getStringValue(element, "description"));
        view.setSql(getStringValue(element, "sql"));
        final List<ERColumn> columns = loadColumns(element, context);
        view.setColumns(columns);
        viewPropertiesLoader.loadViewProperties((ViewProperties) view.getTableViewProperties(), element, context);
        return view;
    }

    // ===================================================================================
    //                                                                             ERModel
    //                                                                             =======
    private ERModel loadErmodel(Element parent, LoadContext context) {
        final ERModel model = new ERModel(diagram);
        model.setName(getStringValue(parent, "name"));
        final List<ERVirtualTable> tables = new ArrayList<ERVirtualTable>();
        final Element vtables = getElement(parent, "vtables");
        if (vtables != null) {
            final NodeList tableEls = vtables.getElementsByTagName("vtable");
            for (int k = 0; k < tableEls.getLength(); k++) {
                final Element tableElement = (Element) tableEls.item(k);
                tables.add(loadVirtualTable(model, tableElement, context));
            }
        }
        model.setTables(tables);
        final String id = getStringValue(parent, "id");
        context.ermodelMap.put(id, model);
        return model;
    }

    // ===================================================================================
    //                                                                            ERModels
    //                                                                            ========
    private List<ERModel> loadErmodels(Element parent, LoadContext context) {
        final List<ERModel> results = new ArrayList<ERModel>();
        final Element element = getElement(parent, "ermodels");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("ermodel");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element modelElement = (Element) nodeList.item(i);
                final ERModel model = new ERModel(diagram);
                model.setName(getStringValue(modelElement, "name"));
                assistLogic.loadColor(model, modelElement);
                final List<ERVirtualTable> tables = new ArrayList<ERVirtualTable>();
                final Element vtables = getElement(modelElement, "vtables");
                if (vtables != null) {
                    final NodeList tableEls = vtables.getElementsByTagName("vtable");
                    for (int k = 0; k < tableEls.getLength(); k++) {
                        final Element tableElement = (Element) tableEls.item(k);
                        tables.add(loadVirtualTable(model, tableElement, context));
                    }
                }
                model.setTables(tables);
                loadElementNotes(context, modelElement, model);
                loadElementGroups(context, modelElement, model);
                final String id = getStringValue(modelElement, "id");
                context.ermodelMap.put(id, model);
                results.add(model);
            }
        }
        return results;
    }

    private void loadElementNotes(LoadContext context, Element modelElement, ERModel model) {
        final List<Note> notes = new ArrayList<Note>();
        final Element elNotes = getElement(modelElement, "notes");
        if (elNotes != null) {
            final NodeList noteEls = elNotes.getElementsByTagName("note");
            for (int k = 0; k < noteEls.getLength(); k++) {
                final Element noteElement = (Element) noteEls.item(k);
                final Note note = noteLoader.loadNote(model, noteElement, context);
                final String id = this.getStringValue(noteElement, "id");
                context.nodeElementMap.put(id, note);
                notes.add(note);
                diagram.getDiagramContents().getContents().addNodeElement(note);
            }
        }
        model.setNotes(notes);
    }

    private void loadElementGroups(LoadContext context, Element modelElement, ERModel model) {
        final List<VGroup> groups = new ArrayList<VGroup>();
        final Element elGroups = getElement(modelElement, "groups");
        if (elGroups != null) {
            final NodeList groupEls = elGroups.getElementsByTagName("group");
            for (int k = 0; k < groupEls.getLength(); k++) {
                final Element groupElement = (Element) groupEls.item(k);
                final VGroup group = groupLoader.loadGroup(model, groupElement, context);
                final String id = getStringValue(groupElement, "id");
                context.nodeElementMap.put(id, group);
                groups.add(group);
            }
        }
        model.setGroups(groups);
    }

    // ===================================================================================
    //                                                                        Node Element
    //                                                                        ============
    private void loadNodeElement(NodeElement nodeElement, Element element, LoadContext context) {
        nodeElementLoader.loadNodeElement(nodeElement, element, context);
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

    private int getIntValue(Element element, String tagname) {
        return assistLogic.getIntValue(element, tagname);
    }

    private double getDoubleValue(Element element, String tagname) {
        return assistLogic.getDoubleValue(element, tagname);
    }

    private String[] getTagValues(Element element, String tagname) {
        return assistLogic.getTagValues(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}

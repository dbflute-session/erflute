package org.insightech.er.editor.persistent.xml.reader;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.core.util.Format;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
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
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.EnvironmentSetting;
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
    protected final ReadDatabaseLogic databaseLogic;
    protected final ReadTablePropertiesLogic tablePropertiesLogic;
    protected final ReadNodeElementLogic nodeElementLogic;
    protected final ReadSettingLogic settingLogic;

    // state
    protected ERDiagram diagram;
    protected String database;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ErmXmlReader(PersistentXml persistentXml) {
        this.persistentXml = persistentXml;
        this.assistLogic = new ReadAssistLogic(persistentXml);
        this.databaseLogic = new ReadDatabaseLogic(persistentXml, assistLogic);
        this.tablePropertiesLogic = new ReadTablePropertiesLogic(persistentXml, assistLogic);
        this.nodeElementLogic = new ReadNodeElementLogic(persistentXml, assistLogic);
        this.settingLogic = new ReadSettingLogic(persistentXml, assistLogic, databaseLogic, tablePropertiesLogic, nodeElementLogic);
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

    private void load(Element root) {
        final Element settings = getElement(root, "settings");
        this.database = loadDatabase(settings);
        this.diagram = new ERDiagram(this.database);
        loadDBSetting(this.diagram, root);
        loadPageSetting(this.diagram, root);
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
    //                                                                            Database
    //                                                                            ========
    private String loadDatabase(Element settingsElement) {
        return databaseLogic.loadDatabase(settingsElement);
    }

    // ===================================================================================
    //                                                                          DB Setting
    //                                                                          ==========
    private void loadDBSetting(ERDiagram diagram, Element element) {
        settingLogic.loadDBSetting(diagram, element);
    }

    // ===================================================================================
    //                                                                        Page Setting
    //                                                                        ============
    private void loadPageSetting(ERDiagram diagram, Element element) {
        settingLogic.loadPageSetting(diagram, element);
    }

    // ===================================================================================
    //                                                                    Diagram Contents
    //                                                                    ================
    private void loadDiagramContents(DiagramContents diagramContents, Element parent) {
        final Dictionary dictionary = diagramContents.getDictionary();
        final LoadContext context = new LoadContext(dictionary);
        this.loadDictionary(dictionary, parent, context);
        final Settings settings = diagramContents.getSettings();
        this.loadEnvironmentSetting(settings.getEnvironmentSetting(), parent, context);
        this.loadTablespaceSet(diagramContents.getTablespaceSet(), parent, context);
        final GroupSet columnGroups = diagramContents.getGroups();
        columnGroups.clear();
        this.loadColumnGroups(columnGroups, parent, context);
        this.loadContents(diagramContents.getContents(), parent, context);
        diagramContents.getModelSet().addModels(this.loadErmodels(parent, context));
        this.loadSequenceSet(diagramContents.getSequenceSet(), parent);
        this.loadTriggerSet(diagramContents.getTriggerSet(), parent);
        this.loadSettings(settings, parent, context);
        context.resolve();
    }

    private List<ERModel> loadErmodels(Element parent, LoadContext context) {
        final List<ERModel> results = new ArrayList<ERModel>();
        final Element element = this.getElement(parent, "ermodels");
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
                final List<Note> notes = new ArrayList<Note>();
                final Element elNotes = getElement(modelElement, "notes");
                if (elNotes != null) {
                    final NodeList noteEls = elNotes.getElementsByTagName("note");
                    for (int k = 0; k < noteEls.getLength(); k++) {
                        final Element noteElement = (Element) noteEls.item(k);
                        final Note note = loadNote(model, noteElement, context);
                        final String id = this.getStringValue(noteElement, "id");
                        context.nodeElementMap.put(id, note);
                        notes.add(note);
                        diagram.getDiagramContents().getContents().addNodeElement(note);
                    }
                }
                model.setNotes(notes);
                final List<VGroup> groups = new ArrayList<VGroup>();
                final Element elGroups = getElement(modelElement, "groups");
                if (elGroups != null) {
                    final NodeList groupEls = elGroups.getElementsByTagName("group");
                    for (int k = 0; k < groupEls.getLength(); k++) {
                        final Element groupElement = (Element) groupEls.item(k);
                        final VGroup group = loadGroup(model, groupElement, context);
                        final String id = this.getStringValue(groupElement, "id");
                        context.nodeElementMap.put(id, group);
                        groups.add(group);
                    }
                }
                model.setGroups(groups);
                final String id = this.getStringValue(modelElement, "id");
                context.ermodelMap.put(id, model);
                results.add(model);
            }
        }
        return results;
    }

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

        final String id = this.getStringValue(parent, "id");
        context.ermodelMap.put(id, model);
        return model;
    }

    private void loadSequenceSet(SequenceSet sequenceSet, Element parent) {
        final Element element = this.getElement(parent, "sequence_set");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("sequence");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element sequenceElemnt = (Element) nodeList.item(i);
                final Sequence sequence = this.loadSequence(sequenceElemnt);
                sequenceSet.addSequence(sequence);
            }
        }
    }

    private Sequence loadSequence(Element element) {
        final Sequence sequence = new Sequence();
        sequence.setName(this.getStringValue(element, "name"));
        sequence.setSchema(this.getStringValue(element, "schema"));
        sequence.setIncrement(this.getIntegerValue(element, "increment"));
        sequence.setMinValue(this.getLongValue(element, "min_value"));
        sequence.setMaxValue(this.getBigDecimalValue(element, "max_value"));
        sequence.setStart(this.getLongValue(element, "start"));
        sequence.setCache(this.getIntegerValue(element, "cache"));
        sequence.setCycle(this.getBooleanValue(element, "cycle"));
        sequence.setOrder(this.getBooleanValue(element, "order"));
        sequence.setDescription(this.getStringValue(element, "description"));
        sequence.setDataType(this.getStringValue(element, "data_type"));
        sequence.setDecimalSize(this.getIntValue(element, "decimal_size"));
        return sequence;
    }

    private void loadTriggerSet(TriggerSet triggerSet, Element parent) {
        final Element element = this.getElement(parent, "trigger_set");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("trigger");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element triggerElemnt = (Element) nodeList.item(i);
                final Trigger trigger = this.loadTrigger(triggerElemnt);
                triggerSet.addTrigger(trigger);
            }
        }
    }

    private Trigger loadTrigger(Element element) {
        final Trigger trigger = new Trigger();
        trigger.setName(this.getStringValue(element, "name"));
        trigger.setSchema(this.getStringValue(element, "schema"));
        trigger.setSql(this.getStringValue(element, "sql"));
        trigger.setDescription(this.getStringValue(element, "description"));
        return trigger;
    }

    private void loadTablespaceSet(TablespaceSet tablespaceSet, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "tablespace_set");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("tablespace");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element tablespaceElemnt = (Element) nodeList.item(i);
                final Tablespace tablespace = this.loadTablespace(tablespaceElemnt, context);
                if (tablespace != null) {
                    tablespaceSet.addTablespace(tablespace);
                }
            }
        }
    }

    private Tablespace loadTablespace(Element element, LoadContext context) {
        final String id = this.getStringValue(element, "id");
        final Tablespace tablespace = new Tablespace();
        tablespace.setName(this.getStringValue(element, "name"));
        final NodeList nodeList = element.getElementsByTagName("properties");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element propertiesElemnt = (Element) nodeList.item(i);
            final String environmentId = this.getStringValue(propertiesElemnt, "environment_id");
            final Environment environment = context.environmentMap.get(environmentId);
            TablespaceProperties tablespaceProperties = null;
            if (DB2DBManager.ID.equals(this.database)) {
                tablespaceProperties = this.loadTablespacePropertiesDB2(propertiesElemnt);
            } else if (MySQLDBManager.ID.equals(this.database)) {
                tablespaceProperties = this.loadTablespacePropertiesMySQL(propertiesElemnt);
            } else if (OracleDBManager.ID.equals(this.database)) {
                tablespaceProperties = this.loadTablespacePropertiesOracle(propertiesElemnt);
            } else if (PostgresDBManager.ID.equals(this.database)) {
                tablespaceProperties = this.loadTablespacePropertiesPostgres(propertiesElemnt);
            }
            tablespace.putProperties(environment, tablespaceProperties);
        }
        if (id != null) {
            context.tablespaceMap.put(id, tablespace);
        }
        return tablespace;
    }

    private TablespaceProperties loadTablespacePropertiesDB2(Element element) {
        final DB2TablespaceProperties properties = new DB2TablespaceProperties();
        properties.setBufferPoolName(this.getStringValue(element, "buffer_pool_name"));
        properties.setContainer(this.getStringValue(element, "container"));
        // properties.setContainerDevicePath(this.getStringValue(element,
        // "container_device_path"));
        // properties.setContainerDirectoryPath(this.getStringValue(element,
        // "container_directory_path"));
        // properties.setContainerFilePath(this.getStringValue(element,
        // "container_file_path"));
        // properties.setContainerPageNum(this.getStringValue(element,
        // "container_page_num"));
        properties.setExtentSize(this.getStringValue(element, "extent_size"));
        properties.setManagedBy(this.getStringValue(element, "managed_by"));
        properties.setPageSize(this.getStringValue(element, "page_size"));
        properties.setPrefetchSize(this.getStringValue(element, "prefetch_size"));
        properties.setType(this.getStringValue(element, "type"));
        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesMySQL(Element element) {
        final MySQLTablespaceProperties properties = new MySQLTablespaceProperties();
        properties.setDataFile(this.getStringValue(element, "data_file"));
        properties.setEngine(this.getStringValue(element, "engine"));
        properties.setExtentSize(this.getStringValue(element, "extent_size"));
        properties.setInitialSize(this.getStringValue(element, "initial_size"));
        properties.setLogFileGroup(this.getStringValue(element, "log_file_group"));
        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesOracle(Element element) {
        final OracleTablespaceProperties properties = new OracleTablespaceProperties();
        properties.setAutoExtend(this.getBooleanValue(element, "auto_extend"));
        properties.setAutoExtendMaxSize(this.getStringValue(element, "auto_extend_max_size"));
        properties.setAutoExtendSize(this.getStringValue(element, "auto_extend_size"));
        properties.setAutoSegmentSpaceManagement(this.getBooleanValue(element, "auto_segment_space_management"));
        properties.setDataFile(this.getStringValue(element, "data_file"));
        properties.setFileSize(this.getStringValue(element, "file_size"));
        properties.setInitial(this.getStringValue(element, "initial"));
        properties.setLogging(this.getBooleanValue(element, "logging"));
        properties.setMaxExtents(this.getStringValue(element, "max_extents"));
        properties.setMinExtents(this.getStringValue(element, "min_extents"));
        properties.setMinimumExtentSize(this.getStringValue(element, "minimum_extent_size"));
        properties.setNext(this.getStringValue(element, "next"));
        properties.setOffline(this.getBooleanValue(element, "offline"));
        properties.setPctIncrease(this.getStringValue(element, "pct_increase"));
        properties.setTemporary(this.getBooleanValue(element, "temporary"));
        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesPostgres(Element element) {
        final PostgresTablespaceProperties properties = new PostgresTablespaceProperties();
        properties.setLocation(this.getStringValue(element, "location"));
        properties.setOwner(this.getStringValue(element, "owner"));
        return properties;
    }

    private void loadColumnGroups(GroupSet columnGroups, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "column_groups");
        final NodeList nodeList = element.getElementsByTagName("column_group");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element columnGroupElement = (Element) nodeList.item(i);
            final ColumnGroup columnGroup = new ColumnGroup();
            columnGroup.setGroupName(this.getStringValue(columnGroupElement, "group_name"));
            final List<ERColumn> columns = this.loadColumns(columnGroupElement, context);
            for (final ERColumn column : columns) {
                columnGroup.addColumn((NormalColumn) column);
            }
            columnGroups.add(columnGroup);
            final String id = this.getStringValue(columnGroupElement, "id");
            context.columnGroupMap.put(id, columnGroup);
        }
    }

    private void loadDictionary(Dictionary dictionary, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "dictionary");
        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("word");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element wordElement = (Element) nodeList.item(i);
                this.loadWord(wordElement, context);
            }
        }
    }

    private Word loadWord(Element element, LoadContext context) {
        final String id = this.getStringValue(element, "id");
        final String type = this.getStringValue(element, "type");
        final TypeData typeData =
                new TypeData(this.getIntegerValue(element, "length"), this.getIntegerValue(element, "decimal"), this.getBooleanValue(
                        element, "array"), this.getIntegerValue(element, "array_dimension"), this.getBooleanValue(element, "unsigned"),
                        this.getStringValue(element, "args"));
        final Word word =
                new Word(Format.null2blank(this.getStringValue(element, "physical_name")), Format.null2blank(this.getStringValue(element,
                        "logical_name")), SqlType.valueOfId(type), typeData,
                        Format.null2blank(this.getStringValue(element, "description")), this.database);
        context.wordMap.put(id, word);
        return word;
    }

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
                final ColumnGroup column = this.loadColumnGroup(columnElement, context);
                columns.add(column);
            } else if ("normal_column".equals(columnElement.getTagName())) {
                final NormalColumn column = this.loadNormalColumn(columnElement, context);
                columns.add(column);
            }
        }
        return columns;
    }

    private ColumnGroup loadColumnGroup(Element element, LoadContext context) {
        final String key = element.getFirstChild().getNodeValue();
        return context.columnGroupMap.get(key);
    }

    private NormalColumn loadNormalColumn(Element element, LoadContext context) {
        final String id = this.getStringValue(element, "id");
        final String type = this.getStringValue(element, "type");
        final String wordId = this.getStringValue(element, "word_id");
        Word word = context.wordMap.get(wordId);
        NormalColumn normalColumn = null;
        if (word == null) {
            word =
                    new Word(this.getStringValue(element, "physical_name"), this.getStringValue(element, "logical_name"),
                            SqlType.valueOfId(type), new TypeData(null, null, false, null, false, null), this.getStringValue(element,
                                    "description"), database);
            final UniqueWord uniqueWord = new UniqueWord(word);
            if (context.uniqueWordMap.containsKey(uniqueWord)) {
                word = context.uniqueWordMap.get(uniqueWord);
            } else {
                context.uniqueWordMap.put(uniqueWord, word);
            }
        }
        normalColumn =
                new NormalColumn(word, this.getBooleanValue(element, "not_null"), this.getBooleanValue(element, "primary_key"),
                        this.getBooleanValue(element, "unique_key"), this.getBooleanValue(element, "auto_increment"), this.getStringValue(
                                element, "default_value"), this.getStringValue(element, "constraint"), this.getStringValue(element,
                                "unique_key_name"), this.getStringValue(element, "character_set"),
                        this.getStringValue(element, "collation"));
        final Element autoIncrementSettingElement = this.getElement(element, "sequence");
        if (autoIncrementSettingElement != null) {
            final Sequence autoIncrementSetting = this.loadSequence(autoIncrementSettingElement);
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

    private void loadSettings(Settings settings, Element parent, LoadContext context) {
        settingLogic.loadSettings(settings, parent, context);
    }

    private VGroup loadGroup(ERModel model, Element node, LoadContext context) {
        final VGroup group = new VGroup();
        this.loadNodeElement(group, node, context);
        group.setName(getStringValue(node, "name"));

        final List<ERVirtualTable> vtables = model.getTables();
        final String[] keys = this.getTagValues(node, "node_element");
        final List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        for (final String key : keys) {
            final NodeElement nodeElement = context.nodeElementMap.get(key);
            if (nodeElement != null) {
                for (final ERVirtualTable vtable : vtables) {
                    if (vtable.getRawTable().equals(nodeElement)) {
                        nodeElementList.add(vtable);
                        break;
                    }
                }
            }
        }
        group.setContents(nodeElementList);

        return group;
    }

    private void loadEnvironmentSetting(EnvironmentSetting environmentSetting, Element parent, LoadContext context) {
        final Element settingElement = this.getElement(parent, "settings");
        final Element element = this.getElement(settingElement, "environment_setting");

        final List<Environment> environmentList = new ArrayList<Environment>();

        if (element != null) {
            final NodeList nodeList = element.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                final Element environmentElement = (Element) nodeList.item(i);

                final String id = this.getStringValue(environmentElement, "id");
                final String name = this.getStringValue(environmentElement, "name");
                final Environment environment = new Environment(name);

                environmentList.add(environment);
                context.environmentMap.put(id, environment);
            }
        }
        if (environmentList.isEmpty()) {
            final Environment environment = new Environment(DisplayMessages.getMessage("label.default"));
            environmentList.add(environment);
            context.environmentMap.put("", environment);
        }
        environmentSetting.setEnvironments(environmentList);
    }

    private void loadContents(NodeSet contents, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "contents");

        final NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Node node = nodeList.item(i);

            if ("table".equals(node.getNodeName())) {
                final ERTable table = this.loadTable((Element) node, context);
                contents.addNodeElement(table);

            } else if ("view".equals(node.getNodeName())) {
                final ERView view = this.loadView((Element) node, context);
                contents.addNodeElement(view);

                //			} else if ("note".equals(node.getNodeName())) {
                //				Note note = this.loadNote((Element) node, context);
                //				contents.addNodeElement(note);

            } else if ("image".equals(node.getNodeName())) {
                final InsertedImage insertedImage = this.loadInsertedImage((Element) node, context);
                contents.addNodeElement(insertedImage);

            } else if ("ermodel".equals(node.getNodeName())) {
                final ERModel ermodel = this.loadErmodel((Element) node, context);
                contents.addNodeElement(ermodel);

                //			} else if ("group".equals(node.getNodeName())) {
                //				VGroup group = this.loadGroup((Element) node, context);
                //				contents.addNodeElement(group);

            } else {
                throw new RuntimeException("not support " + node);
            }
        }
    }

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
        final List<ERColumn> columns = this.loadColumns(element, context);
        table.setColumns(columns);
        final List<ERIndex> indexes = this.loadIndexes(element, table, context);
        table.setIndexes(indexes);
        final List<ComplexUniqueKey> complexUniqueKeyList = this.loadComplexUniqueKeyList(element, table, context);
        table.setComplexUniqueKeyList(complexUniqueKeyList);
        loadTableProperties((TableProperties) table.getTableViewProperties(), element, context);
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

    private ERView loadView(Element element, LoadContext context) {
        final ERView view = new ERView();
        view.setDiagram(this.diagram);
        this.loadNodeElement(view, element, context);
        view.setPhysicalName(this.getStringValue(element, "physical_name"));
        view.setLogicalName(this.getStringValue(element, "logical_name"));
        view.setDescription(this.getStringValue(element, "description"));
        view.setSql(this.getStringValue(element, "sql"));
        final List<ERColumn> columns = this.loadColumns(element, context);
        view.setColumns(columns);
        this.loadViewProperties((ViewProperties) view.getTableViewProperties(), element, context);
        return view;
    }

    private List<ERIndex> loadIndexes(Element parent, ERTable table, LoadContext context) {
        final List<ERIndex> indexes = new ArrayList<ERIndex>();
        final Element element = this.getElement(parent, "indexes");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element indexElement = (Element) nodeList.item(i);
            String type = this.getStringValue(indexElement, "type");
            if ("null".equals(type)) {
                type = null;
            }
            final ERIndex index =
                    new ERIndex(table, this.getStringValue(indexElement, "name"), this.getBooleanValue(indexElement, "non_unique"), type,
                            this.getStringValue(indexElement, "description"));
            index.setFullText(this.getBooleanValue(indexElement, "full_text"));
            this.loadIndexColumns(index, indexElement, context);
            indexes.add(index);
        }
        return indexes;
    }

    private void loadIndexColumns(ERIndex index, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "columns");
        final NodeList nodeList = element.getChildNodes();
        final List<Boolean> descs = new ArrayList<Boolean>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element columnElement = (Element) nodeList.item(i);
            final String id = this.getStringValue(columnElement, "id");
            final NormalColumn column = context.columnMap.get(id);
            final Boolean desc = new Boolean(this.getBooleanValue(columnElement, "desc"));
            index.addColumn(column);
            descs.add(desc);
        }
        index.setDescs(descs);
    }

    private List<ComplexUniqueKey> loadComplexUniqueKeyList(Element parent, ERTable table, LoadContext context) {
        final List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();
        final Element element = this.getElement(parent, "complex_unique_key_list");
        if (element == null) {
            return complexUniqueKeyList;
        }
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element complexUniqueKeyElement = (Element) nodeList.item(i);
            final String id = this.getStringValue(complexUniqueKeyElement, "id");
            final String name = this.getStringValue(complexUniqueKeyElement, "name");
            final ComplexUniqueKey complexUniqueKey = new ComplexUniqueKey(name);
            this.loadComplexUniqueKeyColumns(complexUniqueKey, complexUniqueKeyElement, context);
            complexUniqueKeyList.add(complexUniqueKey);
            context.complexUniqueKeyMap.put(id, complexUniqueKey);
        }
        return complexUniqueKeyList;
    }

    private void loadComplexUniqueKeyColumns(ComplexUniqueKey complexUniqueKey, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "columns");
        final NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element columnElement = (Element) nodeList.item(i);
            final String id = this.getStringValue(columnElement, "id");
            final NormalColumn column = context.columnMap.get(id);
            complexUniqueKey.addColumn(column);
        }
    }

    private void loadTableProperties(TableProperties tableProperties, Element parent, LoadContext context) {
        tablePropertiesLogic.loadTableProperties(tableProperties, parent, context);
    }

    private void loadViewProperties(ViewProperties viewProperties, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "view_properties");
        if (element != null) {
            final String tablespaceId = this.getStringValue(element, "tablespace_id");
            final Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
            viewProperties.setTableSpace(tablespace);
            viewProperties.setSchema(this.getStringValue(element, "schema"));
        }
    }

    private Note loadNote(ERModel model, Element element, LoadContext context) {
        final Note note = new Note();
        note.setModel(model);
        note.setText(this.getStringValue(element, "text"));
        loadNodeElement(note, element, context);
        return note;
    }

    private InsertedImage loadInsertedImage(Element element, LoadContext context) {
        final InsertedImage insertedImage = new InsertedImage();
        insertedImage.setBase64EncodedData(this.getStringValue(element, "data"));
        insertedImage.setHue(this.getIntValue(element, "hue"));
        insertedImage.setSaturation(this.getIntValue(element, "saturation"));
        insertedImage.setBrightness(this.getIntValue(element, "brightness"));
        insertedImage.setAlpha(this.getIntValue(element, "alpha", 255));
        insertedImage.setFixAspectRatio(this.getBooleanValue(element, "fix_aspect_ratio"));
        loadNodeElement(insertedImage, element, context);
        return insertedImage;
    }

    private void loadNodeElement(NodeElement nodeElement, Element element, LoadContext context) {
        nodeElementLogic.loadNodeElement(nodeElement, element, context);
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

    private int getIntValue(Element element, String tagname, int defaultValue) {
        return assistLogic.getIntValue(element, tagname, defaultValue);
    }

    private Integer getIntegerValue(Element element, String tagname) {
        return assistLogic.getIntegerValue(element, tagname);
    }

    private Long getLongValue(Element element, String tagname) {
        return assistLogic.getLongValue(element, tagname);
    }

    private BigDecimal getBigDecimalValue(Element element, String tagname) {
        return assistLogic.getBigDecimalValue(element, tagname);
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

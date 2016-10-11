package org.dbflute.erflute.editor.persistent.xml.reader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.NameValue;
import org.dbflute.erflute.db.impl.standard_sql.StandardSQLDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.settings.CategorySetting;
import org.dbflute.erflute.editor.model.settings.DBSetting;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.model.settings.EnvironmentSetting;
import org.dbflute.erflute.editor.model.settings.ExportSetting;
import org.dbflute.erflute.editor.model.settings.PageSetting;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.view.dialog.dbexport.ExportToDDLDialog;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadSettingLoader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Map<String, String> defaultModelPropertyMigrationMap; // #for_erflute
    static {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("\u4f5c\u6210\u8005", "author");
        map.put("\u4f1a\u793e\u540d", "company name");
        map.put("\u30e2\u30c7\u30eb\u540d", "model name");
        map.put("\u30d7\u30ed\u30b8\u30a7\u30af\u30c8\u540d", "project name");
        defaultModelPropertyMigrationMap = map;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDatabaseLoader databaseLoader;
    protected final ReadTablePropertiesLoader tablePropertiesLoader;
    protected final ReadDiagramWalkerLoader nodeElementLoader;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadSettingLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadDatabaseLoader databaseLoader,
            ReadTablePropertiesLoader tablePropertiesLoader, ReadDiagramWalkerLoader nodeElementLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.databaseLoader = databaseLoader;
        this.tablePropertiesLoader = tablePropertiesLoader;
        this.nodeElementLoader = nodeElementLoader;
    }

    // ===================================================================================
    //                                                                          DB Setting
    //                                                                          ==========
    public void loadDBSetting(ERDiagram diagram, Element element) {
        final Element dbSettingElement = getElement(element, "dbsetting");
        if (dbSettingElement != null) {
            final String dbsystem = getStringValue(element, "dbsystem");
            final String server = getStringValue(element, "server");
            final int port = getIntValue(element, "port");
            final String database = getStringValue(element, "database");
            final String user = getStringValue(element, "user");
            final String password = getStringValue(element, "password");
            boolean useDefaultDriver = getBooleanValue(element, "use_default_driver", true);
            if (StandardSQLDBManager.ID.equals(dbsystem)) {
                useDefaultDriver = false;
            }
            final String url = getStringValue(element, "url");
            final String driverClassName = getStringValue(element, "driver_class_name");
            final DBSetting dbSetting =
                    new DBSetting(dbsystem, server, port, database, user, password, useDefaultDriver, url, driverClassName);
            diagram.setDbSetting(dbSetting);
        }
    }

    // ===================================================================================
    //                                                                        Page Setting
    //                                                                        ============
    public void loadPageSetting(ERDiagram diagram, Element element) {
        final Element dbSettingElement = this.getElement(element, "page_setting");
        if (dbSettingElement != null) {
            final boolean directionHorizontal = this.getBooleanValue(element, "direction_horizontal");
            final int scale = this.getIntValue(element, "scale");
            final String paperSize = this.getStringValue(element, "paper_size");
            final int topMargin = this.getIntValue(element, "top_margin");
            final int leftMargin = this.getIntValue(element, "left_margin");
            final int bottomMargin = this.getIntValue(element, "bottom_margin");
            final int rightMargin = this.getIntValue(element, "right_margin");
            final PageSetting pageSetting =
                    new PageSetting(directionHorizontal, scale, paperSize, topMargin, rightMargin, bottomMargin, leftMargin);
            diagram.setPageSetting(pageSetting);
        }
    }

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    public void loadSettings(Settings settings, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "settings");

        if (element != null) {
            settings.setDatabase(databaseLoader.loadDatabase(element));
            settings.setCapital(this.getBooleanValue(element, "capital"));
            settings.setTableStyle(Format.null2blank(this.getStringValue(element, "table_style")));

            settings.setNotation(this.getStringValue(element, "notation"));
            settings.setNotationLevel(this.getIntValue(element, "notation_level"));
            settings.setNotationExpandGroup(this.getBooleanValue(element, "notation_expand_group"));

            settings.setViewMode(this.getIntValue(element, "view_mode"));
            settings.setOutlineViewMode(this.getIntValue(element, "outline_view_mode"));
            settings.setViewOrderBy(this.getIntValue(element, "view_order_by"));

            settings.setAutoImeChange(this.getBooleanValue(element, "auto_ime_change"));
            settings.setValidatePhysicalName(this.getBooleanValue(element, "validate_physical_name", true));
            settings.setUseBezierCurve(this.getBooleanValue(element, "use_bezier_curve"));
            settings.setSuspendValidator(this.getBooleanValue(element, "suspend_validator"));
            if (this.getStringValue(element, "titleFontEm") != null) {
                settings.setTitleFontEm(new BigDecimal(this.getStringValue(element, "titleFontEm")));
            }
            if (this.getStringValue(element, "masterDataBasePath") != null) {
                settings.setMasterDataBasePath(this.getStringValue(element, "masterDataBasePath"));
            }

            final ExportSetting exportSetting = settings.getExportSetting();
            this.loadExportSetting(exportSetting, element, context);

            final CategorySetting categorySetting = settings.getCategorySetting();
            this.loadCategorySetting(categorySetting, element, context);

            final ModelProperties modelProperties = settings.getModelProperties();
            this.loadModelProperties(modelProperties, element);
            tablePropertiesLoader.loadTableProperties((TableProperties) settings.getTableViewProperties(), element, context);
        }
    }

    private void loadExportSetting(ExportSetting exportSetting, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "export_setting");

        if (element != null) {
            String categoryNameToExport = getStringValue(element, "category_name_to_export");
            if ("\u5168\u4f53".equals(categoryNameToExport)) { // Japanese "all" (zentai) as KANJI
                categoryNameToExport = ExportToDDLDialog.DEFAULT_CATEGORY;
            }
            exportSetting.setCategoryNameToExport(categoryNameToExport);
            exportSetting.setDdlOutput(this.getStringValue(element, "ddl_output"));
            exportSetting.setExcelOutput(this.getStringValue(element, "excel_output"));
            exportSetting.setExcelTemplate(this.getStringValue(element, "excel_template"));
            exportSetting.setImageOutput(this.getStringValue(element, "image_output"));
            exportSetting.setPutERDiagramOnExcel(this.getBooleanValue(element, "put_diagram_on_excel"));
            exportSetting.setUseLogicalNameAsSheet(this.getBooleanValue(element, "use_logical_name_as_sheet"));
            exportSetting.setOpenAfterSaved(this.getBooleanValue(element, "open_after_saved"));

            exportSetting.getDdlTarget().createComment = this.getBooleanValue(element, "create_comment");
            exportSetting.getDdlTarget().createForeignKey = this.getBooleanValue(element, "create_foreignKey");
            exportSetting.getDdlTarget().createIndex = this.getBooleanValue(element, "create_index");
            exportSetting.getDdlTarget().createSequence = this.getBooleanValue(element, "create_sequence");
            exportSetting.getDdlTarget().createTable = this.getBooleanValue(element, "create_table");
            exportSetting.getDdlTarget().createTablespace = this.getBooleanValue(element, "create_tablespace");
            exportSetting.getDdlTarget().createTrigger = this.getBooleanValue(element, "create_trigger");
            exportSetting.getDdlTarget().createView = this.getBooleanValue(element, "create_view");

            exportSetting.getDdlTarget().dropIndex = this.getBooleanValue(element, "drop_index");
            exportSetting.getDdlTarget().dropSequence = this.getBooleanValue(element, "drop_sequence");
            exportSetting.getDdlTarget().dropTable = this.getBooleanValue(element, "drop_table");
            exportSetting.getDdlTarget().dropTablespace = this.getBooleanValue(element, "drop_tablespace");
            exportSetting.getDdlTarget().dropTrigger = this.getBooleanValue(element, "drop_trigger");
            exportSetting.getDdlTarget().dropView = this.getBooleanValue(element, "drop_view");

            exportSetting.getDdlTarget().inlineColumnComment = this.getBooleanValue(element, "inline_column_comment");
            exportSetting.getDdlTarget().inlineTableComment = this.getBooleanValue(element, "inline_table_comment");

            exportSetting.getDdlTarget().commentValueDescription = this.getBooleanValue(element, "comment_value_description");
            exportSetting.getDdlTarget().commentValueLogicalName = this.getBooleanValue(element, "comment_value_logical_name");
            exportSetting.getDdlTarget().commentValueLogicalNameDescription =
                    this.getBooleanValue(element, "comment_value_logical_name_description");
            exportSetting.getDdlTarget().commentReplaceLineFeed = this.getBooleanValue(element, "comment_replace_line_feed");
            exportSetting.getDdlTarget().commentReplaceString = this.getStringValue(element, "comment_replace_string");

            // #deleted
            //this.loadExportJavaSetting(exportSetting.getExportJavaSetting(), element, context);
            //this.loadExportTestDataSetting(exportSetting.getExportTestDataSetting(), element, context);
        }
    }

    private void loadCategorySetting(CategorySetting categorySetting, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "category_settings");
        categorySetting.setFreeLayout(this.getBooleanValue(element, "free_layout"));
        categorySetting.setShowReferredTables(this.getBooleanValue(element, "show_referred_tables"));

        final Element categoriesElement = this.getElement(element, "categories");
        final NodeList nodeList = categoriesElement.getChildNodes();
        final List<Category> selectedCategories = new ArrayList<Category>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element categoryElement = (Element) nodeList.item(i);
            final Category category = new Category();
            nodeElementLoader.loadNodeElement(category, categoryElement, context);
            category.setName(this.getStringValue(categoryElement, "name"));
            final boolean isSelected = this.getBooleanValue(categoryElement, "selected");
            final String[] keys = this.getTagValues(categoryElement, "node_element");
            final List<DiagramWalker> nodeElementList = new ArrayList<DiagramWalker>();
            for (final String key : keys) {
                final DiagramWalker nodeElement = context.walkerMap.get(key);
                if (nodeElement != null) {
                    nodeElementList.add(nodeElement);
                }
            }
            category.setContents(nodeElementList);
            categorySetting.addCategory(category);
            if (isSelected) {
                selectedCategories.add(category);
            }
        }
        categorySetting.setSelectedCategories(selectedCategories);
    }

    // ===================================================================================
    //                                                                    Model Properties
    //                                                                    ================
    private void loadModelProperties(ModelProperties modelProperties, Element parent) {
        final Element element = getElement(parent, "model_properties");
        assistLogic.loadLocation(modelProperties, element);
        assistLogic.loadColor(modelProperties, element);
        modelProperties.setDisplay(getBooleanValue(element, "display"));
        final NodeList nodeList = element.getElementsByTagName("model_property");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element propertyElement = (Element) nodeList.item(i);
            final String name = getStringValue(propertyElement, "name");
            final String migratedName = defaultModelPropertyMigrationMap.get(name);
            final String realName = migratedName != null ? migratedName : name;
            final String value = getStringValue(propertyElement, "value");
            final NameValue nameValue = new NameValue(realName, value);
            modelProperties.addProperty(nameValue);
        }
    }

    // ===================================================================================
    //                                                              Tablespace Environment
    //                                                              ======================
    public void loadEnvironmentSetting(EnvironmentSetting environmentSetting, Element parent, LoadContext context) {
        final Element settingElement = this.getElement(parent, "settings");
        final Element element = this.getElement(settingElement, "environment_setting");
        final List<Environment> environmentList = new ArrayList<Environment>();
        final String defaultExpression = "Default";
        if (element != null) {
            final NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                final Element environmentElement = (Element) nodeList.item(i);
                final String id = getStringValue(environmentElement, "id");
                String name = getStringValue(environmentElement, "name");
                if ("\u30c7\u30d5\u30a9\u30eb\u30c8".equals(name)) { // Japanese "default" as Katakana
                    name = defaultExpression; // #for_erflute use English only
                }
                final Environment environment = new Environment(name);
                environmentList.add(environment);
                context.environmentMap.put(id, environment);
            }
        }
        if (environmentList.isEmpty()) {
            final Environment environment = new Environment(defaultExpression); // #for_erflute use English only
            environmentList.add(environment);
            context.environmentMap.put("", environment);
        }
        environmentSetting.setEnvironments(environmentList);
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

    private boolean getBooleanValue(Element element, String tagname, boolean defaultValue) {
        return assistLogic.getBooleanValue(element, tagname, defaultValue);
    }

    private int getIntValue(Element element, String tagname) {
        return assistLogic.getIntValue(element, tagname);
    }

    private String[] getTagValues(Element element, String tagname) {
        return assistLogic.getTagValues(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
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
import org.dbflute.erflute.editor.model.settings.CategorySettings;
import org.dbflute.erflute.editor.model.settings.DBSettings;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.model.settings.EnvironmentSettings;
import org.dbflute.erflute.editor.model.settings.ExportSettings;
import org.dbflute.erflute.editor.model.settings.PageSettings;
import org.dbflute.erflute.editor.model.settings.design.ConstraintSettings;
import org.dbflute.erflute.editor.model.settings.design.DesignSettings;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.dbflute.erflute.editor.view.dialog.dbexport.ExportToDDLDialog;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadSettingsLoader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Map<String, String> defaultModelPropertyMigrationMap; // #for_erflute
    static {
        final Map<String, String> map = new HashMap<>();
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
    public ReadSettingsLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadDatabaseLoader databaseLoader,
            ReadTablePropertiesLoader tablePropertiesLoader, ReadDiagramWalkerLoader nodeElementLoader) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.databaseLoader = databaseLoader;
        this.tablePropertiesLoader = tablePropertiesLoader;
        this.nodeElementLoader = nodeElementLoader;
    }

    // ===================================================================================
    //                                                                         DB Settings
    //                                                                         ===========
    public void loadDBSettings(ERDiagram diagram, Element element) {
        Element dbSettingElement = getElement(element, "dbsetting"); // migration from ERMaster
        if (dbSettingElement == null) {
            dbSettingElement = getElement(element, "db_settings"); // #for_erflute rename
        }
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
            final DBSettings dbSettings =
                    new DBSettings(dbsystem, server, port, database, user, password, useDefaultDriver, url, driverClassName);
            diagram.setDbSettings(dbSettings);
        }
    }

    // ===================================================================================
    //                                                                       Page Settings
    //                                                                       =============
    public void loadPageSetting(ERDiagram diagram, Element element) {
        Element pageSettingsElement = getElement(element, "page_setting"); // migration from ERMaster
        if (pageSettingsElement == null) {
            pageSettingsElement = getElement(element, "page_settings"); // #for_erflute rename
        }
        if (pageSettingsElement != null) {
            final boolean directionHorizontal = this.getBooleanValue(element, "direction_horizontal");
            final int scale = this.getIntValue(element, "scale");
            final String paperSize = this.getStringValue(element, "paper_size");
            final int topMargin = this.getIntValue(element, "top_margin");
            final int leftMargin = this.getIntValue(element, "left_margin");
            final int bottomMargin = this.getIntValue(element, "bottom_margin");
            final int rightMargin = this.getIntValue(element, "right_margin");
            final PageSettings pageSetting =
                    new PageSettings(directionHorizontal, scale, paperSize, topMargin, rightMargin, bottomMargin, leftMargin);
            diagram.setPageSetting(pageSetting);
        }
    }

    // ===================================================================================
    //                                                                    Diagram Settings
    //                                                                    ================
    public void loadDiagramSettings(DiagramSettings settings, Element parent, LoadContext context, String database) {
        final Element settingsElement = extractDiagramSettingsElement(parent);
        if (settingsElement != null) {
            settings.setDatabase(database);
            settings.setCapital(this.getBooleanValue(settingsElement, "capital"));
            settings.setTableStyle(Format.null2blank(this.getStringValue(settingsElement, "table_style")));

            settings.setNotation(this.getStringValue(settingsElement, "notation"));
            settings.setNotationLevel(this.getIntValue(settingsElement, "notation_level"));
            settings.setNotationExpandGroup(this.getBooleanValue(settingsElement, "notation_expand_group"));

            settings.setViewMode(this.getIntValue(settingsElement, "view_mode"));
            settings.setOutlineViewMode(this.getIntValue(settingsElement, "outline_view_mode"));
            settings.setViewOrderBy(this.getIntValue(settingsElement, "view_order_by"));

            settings.setAutoImeChange(this.getBooleanValue(settingsElement, "auto_ime_change"));
            settings.setValidatePhysicalName(this.getBooleanValue(settingsElement, "validate_physical_name", true));
            settings.setUseBezierCurve(this.getBooleanValue(settingsElement, "use_bezier_curve"));
            settings.setSuspendValidator(this.getBooleanValue(settingsElement, "suspend_validator"));
            if (getStringValue(settingsElement, "titleFontEm") != null) {
                settings.setTitleFontEm(new BigDecimal(getStringValue(settingsElement, "titleFontEm")));
            }
            if (getStringValue(settingsElement, "masterDataBasePath") != null) {
                settings.setMasterDataBasePath(getStringValue(settingsElement, "masterDataBasePath"));
            }
            settings.setUseViewObject(getBooleanValue(settingsElement, "use_view_object"));

            final ExportSettings exportSetting = settings.getExportSettings();
            loadExportSettings(exportSetting, settingsElement, context);

            final CategorySettings categorySetting = settings.getCategorySetting();
            loadCategorySettings(categorySetting, settingsElement, context);

            final ModelProperties modelProperties = settings.getModelProperties();
            loadModelProperties(modelProperties, settingsElement);
            tablePropertiesLoader.loadTableProperties((TableProperties) settings.getTableViewProperties(), settingsElement, context);
        }
    }

    private Element extractDiagramSettingsElement(Element parent) {
        Element settingsElement = getElement(parent, "settings"); // migration from ERMaster
        if (settingsElement == null) {
            settingsElement = getElement(parent, "diagram_settings"); // #for_erflute rename
        }
        return settingsElement;
    }

    private void loadExportSettings(ExportSettings settings, Element parent, LoadContext context) {
        Element settingsElement = getElement(parent, "export_setting"); // migration from ERMaster
        if (settingsElement == null) {
            settingsElement = getElement(parent, "export_settings"); // #for_erflute rename
        }
        if (settingsElement != null) {
            String categoryNameToExport = getStringValue(settingsElement, "category_name_to_export");
            if ("\u5168\u4f53".equals(categoryNameToExport)) { // Japanese "all" (zentai) as KANJI
                categoryNameToExport = ExportToDDLDialog.DEFAULT_CATEGORY;
            }
            settings.setCategoryNameToExport(categoryNameToExport);
            settings.setDdlOutput(this.getStringValue(settingsElement, "ddl_output"));
            settings.setExcelOutput(this.getStringValue(settingsElement, "excel_output"));
            settings.setExcelTemplate(this.getStringValue(settingsElement, "excel_template"));
            settings.setImageOutput(this.getStringValue(settingsElement, "image_output"));
            settings.setPutERDiagramOnExcel(this.getBooleanValue(settingsElement, "put_diagram_on_excel"));
            settings.setUseLogicalNameAsSheet(this.getBooleanValue(settingsElement, "use_logical_name_as_sheet"));
            settings.setOpenAfterSaved(this.getBooleanValue(settingsElement, "open_after_saved"));

            settings.getDdlTarget().createComment = this.getBooleanValue(settingsElement, "create_comment");
            settings.getDdlTarget().createForeignKey = this.getBooleanValue(settingsElement, "create_foreignKey");
            settings.getDdlTarget().createIndex = this.getBooleanValue(settingsElement, "create_index");
            settings.getDdlTarget().createSequence = this.getBooleanValue(settingsElement, "create_sequence");
            settings.getDdlTarget().createTable = this.getBooleanValue(settingsElement, "create_table");
            settings.getDdlTarget().createTablespace = this.getBooleanValue(settingsElement, "create_tablespace");
            settings.getDdlTarget().createTrigger = this.getBooleanValue(settingsElement, "create_trigger");
            settings.getDdlTarget().createView = this.getBooleanValue(settingsElement, "create_view");

            settings.getDdlTarget().dropIndex = this.getBooleanValue(settingsElement, "drop_index");
            settings.getDdlTarget().dropSequence = this.getBooleanValue(settingsElement, "drop_sequence");
            settings.getDdlTarget().dropTable = this.getBooleanValue(settingsElement, "drop_table");
            settings.getDdlTarget().dropTablespace = this.getBooleanValue(settingsElement, "drop_tablespace");
            settings.getDdlTarget().dropTrigger = this.getBooleanValue(settingsElement, "drop_trigger");
            settings.getDdlTarget().dropView = this.getBooleanValue(settingsElement, "drop_view");

            settings.getDdlTarget().inlineColumnComment = this.getBooleanValue(settingsElement, "inline_column_comment");
            settings.getDdlTarget().inlineTableComment = this.getBooleanValue(settingsElement, "inline_table_comment");

            settings.getDdlTarget().commentValueDescription = this.getBooleanValue(settingsElement, "comment_value_description");
            settings.getDdlTarget().commentValueLogicalName = this.getBooleanValue(settingsElement, "comment_value_logical_name");
            settings.getDdlTarget().commentValueLogicalNameDescription =
                    this.getBooleanValue(settingsElement, "comment_value_logical_name_description");
            settings.getDdlTarget().commentReplaceLineFeed = this.getBooleanValue(settingsElement, "comment_replace_line_feed");
            settings.getDdlTarget().commentReplaceString = this.getStringValue(settingsElement, "comment_replace_string");

            // #deleted
            //this.loadExportJavaSetting(exportSetting.getExportJavaSetting(), element, context);
            //this.loadExportTestDataSetting(exportSetting.getExportTestDataSetting(), element, context);
        }
    }

    private void loadCategorySettings(CategorySettings settings, Element parent, LoadContext context) {
        final Element settingsElement = this.getElement(parent, "category_settings");
        settings.setFreeLayout(this.getBooleanValue(settingsElement, "free_layout"));
        settings.setShowReferredTables(this.getBooleanValue(settingsElement, "show_referred_tables"));

        final Element categoriesElement = getElement(settingsElement, "categories");
        final NodeList nodeList = categoriesElement.getChildNodes();
        final List<Category> selectedCategories = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element categoryElement = (Element) nodeList.item(i);
            final Category category = new Category();
            nodeElementLoader.loadWalker(category, categoryElement, context);
            category.setName(this.getStringValue(categoryElement, "name"));
            final boolean isSelected = this.getBooleanValue(categoryElement, "selected");
            final String[] keys = this.getTagValues(categoryElement, "node_element");
            final List<DiagramWalker> walkerList = new ArrayList<>();
            for (final String key : keys) {
                final DiagramWalker walker = context.walkerMap.get(key);
                if (walker != null) {
                    walkerList.add(walker);
                }
            }
            category.setContents(walkerList);
            settings.addCategory(category);
            if (isSelected) {
                selectedCategories.add(category);
            }
        }
        settings.setSelectedCategories(selectedCategories);
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
    //                                                                Environment Settings
    //                                                                ====================
    public void loadEnvironmentSettings(EnvironmentSettings settings, Element parent, LoadContext context) {
        final Element settingsElement = extractDiagramSettingsElement(parent);
        Element element = getElement(settingsElement, "environment_setting"); // migration from ERMaster
        if (element == null) {
            element = getElement(settingsElement, "environment_settings"); // #for_erflute rename
        }
        final List<Environment> environmentList = new ArrayList<>();
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
        settings.setEnvironments(environmentList);
    }

    // ===================================================================================
    //                                                                     Design Settings
    //                                                                     ===============
    public void loadDesignSettings(DesignSettings settings, Element parent, LoadContext context) {
        final Element diagramElement = extractDiagramSettingsElement(parent);
        final Element designElement = getElement(diagramElement, "design_settings"); // migration from ERMaster
        if (designElement != null) {
            final ConstraintSettings constraintSettings = settings.getConstraintSettings();
            {
                final Element currentElement = getElement(designElement, "foreign_key");
                if (currentElement != null) {
                    final String prefix = getStringValue(currentElement, "default_prefix"); // null allowed
                    constraintSettings.setDefaultPrefixOfForeignKey(prefix);
                }
            }
            {
                final Element currentElement = getElement(designElement, "unique");
                if (currentElement != null) {
                    final String prefix = getStringValue(currentElement, "default_prefix"); // null allowed
                    constraintSettings.setDefaultPrefixOfUnique(prefix);
                }
            }
            {
                final Element currentElement = getElement(designElement, "index");
                if (currentElement != null) {
                    final String prefix = getStringValue(currentElement, "default_prefix"); // null allowed
                    constraintSettings.setDefaultPrefixOfIndex(prefix);
                }
            }
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

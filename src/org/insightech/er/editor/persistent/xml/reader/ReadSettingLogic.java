package org.insightech.er.editor.persistent.xml.reader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.NameValue;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadSettingLogic {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;
    protected final ReadDatabaseLogic databaseLogic;
    protected final ReadTablePropertiesLogic tablePropertiesLogic;
    protected final ReadNodeElementLogic nodeElementLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadSettingLogic(PersistentXml persistentXml, ReadAssistLogic assistLogic, ReadDatabaseLogic databaseLogic,
            ReadTablePropertiesLogic tablePropertiesLogic, ReadNodeElementLogic nodeElementLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
        this.databaseLogic = databaseLogic;
        this.tablePropertiesLogic = tablePropertiesLogic;
        this.nodeElementLogic = nodeElementLogic;
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
            settings.setDatabase(databaseLogic.loadDatabase(element));
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
            tablePropertiesLogic.loadTableProperties((TableProperties) settings.getTableViewProperties(), element, context);
        }
    }

    private void loadExportSetting(ExportSetting exportSetting, Element parent, LoadContext context) {
        final Element element = this.getElement(parent, "export_setting");

        if (element != null) {
            exportSetting.setCategoryNameToExport(this.getStringValue(element, "category_name_to_export"));
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
            nodeElementLogic.loadNodeElement(category, categoryElement, context);
            category.setName(this.getStringValue(categoryElement, "name"));
            final boolean isSelected = this.getBooleanValue(categoryElement, "selected");
            final String[] keys = this.getTagValues(categoryElement, "node_element");
            final List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
            for (final String key : keys) {
                final NodeElement nodeElement = context.nodeElementMap.get(key);
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
        final Element element = this.getElement(parent, "model_properties");
        assistLogic.loadLocation(modelProperties, element);
        assistLogic.loadColor(modelProperties, element);
        modelProperties.setDisplay(this.getBooleanValue(element, "display"));
        modelProperties.setCreationDate(this.getDateValue(element, "creation_date"));
        modelProperties.setUpdatedDate(this.getDateValue(element, "updated_date"));
        final NodeList nodeList = element.getElementsByTagName("model_property");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element propertyElement = (Element) nodeList.item(i);
            final NameValue nameValue =
                    new NameValue(this.getStringValue(propertyElement, "name"), this.getStringValue(propertyElement, "value"));
            modelProperties.addProperty(nameValue);
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

    private Date getDateValue(Element element, String tagname) {
        return assistLogic.getDateValue(element, tagname);
    }

    private String[] getTagValues(Element element, String tagname) {
        return assistLogic.getTagValues(element, tagname);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}
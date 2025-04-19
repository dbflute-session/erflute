package org.dbflute.erflute.editor.model.settings;

import java.io.Serializable;
import java.math.BigDecimal;

import org.dbflute.erflute.db.DBManagerFactory;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TablePropertiesHolder;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.settings.design.DesignSettings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class DiagramSettings implements Serializable, Cloneable, TablePropertiesHolder {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;

    public static final int VIEW_MODE_LOGICAL = 0;
    public static final int VIEW_MODE_PHYSICAL = 1;
    public static final int VIEW_MODE_BOTH = 2;
    public static final int NOTATION_LEVLE_DETAIL = 0;
    public static final int NOTATION_LEVLE_TITLE = 1;
    public static final int NOTATION_LEVLE_COLUMN = 2;
    public static final int NOTATION_LEVLE_KEY = 3;
    public static final int NOTATION_LEVLE_EXCLUDE_TYPE = 4;
    public static final int NOTATION_LEVLE_NAME_AND_KEY = 5;
    public static final String NOTATION_IE = "IE";
    public static final String NOTATION_IDEF1X = "IDEF1X";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private boolean capital;
    private boolean notationExpandGroup;
    private String tableStyle;

    private ExportSettings exportSettings;
    private CategorySettings categorySetting;
    private ModelProperties modelProperties;
    private TableProperties tableProperties;
    private EnvironmentSettings environmentSettings;
    private DesignSettings designSettings; // #for_erflute for e.g. prefix of constraint name

    private String database;
    private String notation;
    private int notationLevel;
    private int viewMode;
    private int viewOrderBy;
    private int outlineViewMode;
    private BigDecimal titleFontEm;
    private boolean autoImeChange;
    private boolean validatePhysicalName;
    private boolean useBezierCurve;
    private boolean suspendValidator;
    private boolean useViewObject; // #for_erflute view is option
    private String masterDataBasePath;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DiagramSettings() {
        this.capital = true;
        this.notationExpandGroup = true;

        this.tableStyle = null;
        this.viewMode = VIEW_MODE_PHYSICAL;
        this.outlineViewMode = VIEW_MODE_PHYSICAL;
        this.viewOrderBy = VIEW_MODE_PHYSICAL;

        this.exportSettings = new ExportSettings();
        this.modelProperties = new ModelProperties();
        this.categorySetting = new CategorySettings();
        this.environmentSettings = new EnvironmentSettings();
        this.designSettings = new DesignSettings();

        this.autoImeChange = false;
        this.validatePhysicalName = true;
        this.useBezierCurve = false;
        this.suspendValidator = false;
        this.useViewObject = false; // as default
        this.masterDataBasePath = "";
        this.titleFontEm = new BigDecimal("1.5");
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public Object clone() {
        DiagramSettings clone = null;
        try {
            clone = (DiagramSettings) super.clone();
            clone.exportSettings = exportSettings.clone();
            clone.modelProperties = modelProperties.clone();
            clone.categorySetting = (CategorySettings) categorySetting.clone();
            if (database != null) {
                clone.tableProperties = (TableProperties) getTableViewProperties().clone();
            }
            clone.environmentSettings = (EnvironmentSettings) environmentSettings.clone();
            clone.designSettings = designSettings.clone();
        } catch (final CloneNotSupportedException e) {}
        return clone;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + database + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isCapital() {
        return capital;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public boolean isNotationExpandGroup() {
        return notationExpandGroup;
    }

    public void setNotationExpandGroup(boolean notationExpandGroup) {
        this.notationExpandGroup = notationExpandGroup;
    }

    public int getNotationLevel() {
        return notationLevel;
    }

    public void setNotationLevel(int notationLevel) {
        this.notationLevel = notationLevel;
    }

    public String getTableStyle() {
        return tableStyle;
    }

    public void setTableStyle(String tableStyle) {
        this.tableStyle = tableStyle;
    }

    public ExportSettings getExportSettings() {
        return exportSettings;
    }

    public void setExportSettings(ExportSettings exportSettings) {
        this.exportSettings = exportSettings;
    }

    public CategorySettings getCategorySetting() {
        return categorySetting;
    }

    public ModelProperties getModelProperties() {
        return modelProperties;
    }

    public void setModelProperties(ModelProperties modelProperties) {
        this.modelProperties = modelProperties;
    }

    @Override
    public TableViewProperties getTableViewProperties() { // lazy loaded
        this.tableProperties = DBManagerFactory.getDBManager(database).createTableProperties(tableProperties);
        return tableProperties;
    }

    public EnvironmentSettings getEnvironmentSettings() {
        return environmentSettings;
    }

    public DesignSettings getDesignSettings() {
        return designSettings;
    }

    public void setDesignSettings(DesignSettings designSettings) {
        this.designSettings = designSettings;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public int getViewMode() {
        return this.viewMode;
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
    }

    public int getOutlineViewMode() {
        return outlineViewMode;
    }

    public void setOutlineViewMode(int outlineViewMode) {
        this.outlineViewMode = outlineViewMode;
    }

    public int getViewOrderBy() {
        return viewOrderBy;
    }

    public void setViewOrderBy(int viewOrderBy) {
        this.viewOrderBy = viewOrderBy;
    }

    public BigDecimal getTitleFontEm() {
        return titleFontEm;
    }

    public void setTitleFontEm(BigDecimal titleFontEm) {
        this.titleFontEm = titleFontEm;
    }

    public boolean isAutoImeChange() {
        return autoImeChange;
    }

    public void setAutoImeChange(boolean autoImeChange) {
        this.autoImeChange = autoImeChange;
    }

    public boolean isValidatePhysicalName() {
        return validatePhysicalName;
    }

    public void setValidatePhysicalName(boolean validatePhysicalName) {
        this.validatePhysicalName = validatePhysicalName;
    }

    public boolean isUseBezierCurve() {
        return useBezierCurve;
    }

    public void setUseBezierCurve(boolean useBezierCurve) {
        this.useBezierCurve = useBezierCurve;
    }

    public boolean isSuspendValidator() {
        return suspendValidator;
    }

    public void setSuspendValidator(boolean suspendValidator) {
        this.suspendValidator = suspendValidator;
    }

    public boolean isUseViewObject() {
        return useViewObject;
    }

    public void setUseViewObject(boolean useViewObject) {
        this.useViewObject = useViewObject;
    }

    public String getMasterDataBasePath() {
        return masterDataBasePath;
    }

    public void setMasterDataBasePath(String masterDataBasePath) {
        this.masterDataBasePath = masterDataBasePath;
    }
}

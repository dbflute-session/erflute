package org.dbflute.erflute.editor.model.settings;

import java.io.Serializable;

import org.dbflute.erflute.editor.model.dbexport.ddl.DDLTarget;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ExportSettings implements Serializable, Cloneable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 3669486436464233526L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String excelTemplate;
    private String excelOutput;
    private String imageOutput;
    private String ddlOutput;
    private boolean useLogicalNameAsSheet;
    private boolean putERDiagramOnExcel;
    private boolean openAfterSaved;
    private String categoryNameToExport;
    private DDLTarget ddlTarget = new DDLTarget();

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ExportSettings other = (ExportSettings) obj;
        if (categoryNameToExport == null) {
            if (other.categoryNameToExport != null)
                return false;
        } else if (!categoryNameToExport.equals(other.categoryNameToExport))
            return false;
        if (ddlOutput == null) {
            if (other.ddlOutput != null)
                return false;
        } else if (!ddlOutput.equals(other.ddlOutput))
            return false;
        if (ddlTarget == null) {
            if (other.ddlTarget != null)
                return false;
        } else if (!ddlTarget.equals(other.ddlTarget))
            return false;
        if (excelOutput == null) {
            if (other.excelOutput != null)
                return false;
        } else if (!excelOutput.equals(other.excelOutput))
            return false;
        if (excelTemplate == null) {
            if (other.excelTemplate != null)
                return false;
        } else if (!excelTemplate.equals(other.excelTemplate)) {
            return false;
        }
        if (imageOutput == null) {
            if (other.imageOutput != null) {
                return false;
            }
        } else if (!imageOutput.equals(other.imageOutput))
            return false;
        if (openAfterSaved != other.openAfterSaved)
            return false;
        if (putERDiagramOnExcel != other.putERDiagramOnExcel)
            return false;
        if (useLogicalNameAsSheet != other.useLogicalNameAsSheet)
            return false;
        return true;
    }

    @Override
    public ExportSettings clone() {
        try {
            final ExportSettings setting = (ExportSettings) super.clone();
            setting.setDdlTarget(this.ddlTarget.clone());
            return setting;
        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DDLTarget getDdlTarget() {
        return ddlTarget;
    }

    public void setDdlTarget(DDLTarget ddlTarget) {
        this.ddlTarget = ddlTarget;
    }

    public String getExcelOutput() {
        return excelOutput;
    }

    public void setExcelOutput(String excelOutput) {
        this.excelOutput = excelOutput;
    }

    public String getImageOutput() {
        return imageOutput;
    }

    public void setImageOutput(String imageOutput) {
        this.imageOutput = imageOutput;
    }

    public String getExcelTemplate() {
        return excelTemplate;
    }

    public void setExcelTemplate(String excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    public boolean isUseLogicalNameAsSheet() {
        return useLogicalNameAsSheet;
    }

    public void setUseLogicalNameAsSheet(boolean useLogicalNameAsSheet) {
        this.useLogicalNameAsSheet = useLogicalNameAsSheet;
    }

    public boolean isPutERDiagramOnExcel() {
        return putERDiagramOnExcel;
    }

    public void setPutERDiagramOnExcel(boolean putERDiagramOnExcel) {
        this.putERDiagramOnExcel = putERDiagramOnExcel;
    }

    public boolean isOpenAfterSaved() {
        return openAfterSaved;
    }

    public void setOpenAfterSaved(boolean openAfterSaved) {
        this.openAfterSaved = openAfterSaved;
    }

    public String getCategoryNameToExport() {
        return categoryNameToExport;
    }

    public void setCategoryNameToExport(String categoryNameToExport) {
        this.categoryNameToExport = categoryNameToExport;
    }

    public String getDdlOutput() {
        return ddlOutput;
    }

    public void setDdlOutput(String ddlOutput) {
        this.ddlOutput = ddlOutput;
    }

    // #deleted
    //public ExportJavaSetting getExportJavaSetting() {
    //    return exportJavaSetting;
    //}
    //
    //public void setExportJavaSetting(ExportJavaSetting exportJavaSetting) {
    //    this.exportJavaSetting = exportJavaSetting;
    //}
    //public ExportTestDataSetting getExportTestDataSetting() {
    //    return exportTestDataSetting;
    //}
    //
    //public void setExportTestDataSetting(ExportTestDataSetting exportTestDataSetting) {
    //    this.exportTestDataSetting = exportTestDataSetting;
    //}
}

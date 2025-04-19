package org.dbflute.erflute.editor.model.settings.design;

import java.io.Serializable;

/**
 * @author jflute
 * @since 0.5.8 (2018/08/30 however released at 2020)
 */
public class ConstraintSettings implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String defaultPrefixOfForeignKey; // null allowed
    private String defaultPrefixOfUnique; // null allowed
    private String defaultPrefixOfIndex; // null allowed

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ConstraintSettings() {
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isEmpty() {
        return defaultPrefixOfForeignKey == null //
                && defaultPrefixOfUnique == null //
                && defaultPrefixOfIndex == null;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public ConstraintSettings clone() {
        try {
            final ConstraintSettings settings = (ConstraintSettings) super.clone();
            settings.defaultPrefixOfForeignKey = defaultPrefixOfForeignKey;
            settings.defaultPrefixOfUnique = defaultPrefixOfUnique;
            settings.defaultPrefixOfIndex = defaultPrefixOfIndex;
            return settings;
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException("Unsupported clone()", e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getDefaultPrefixOfForeignKey() {
        return defaultPrefixOfForeignKey;
    }

    public void setDefaultPrefixOfForeignKey(String defaultPrefixOfForeignKey) {
        this.defaultPrefixOfForeignKey = defaultPrefixOfForeignKey;
    }

    public String getDefaultPrefixOfUnique() {
        return defaultPrefixOfUnique;
    }

    public void setDefaultPrefixOfUnique(String defaultPrefixOfUnique) {
        this.defaultPrefixOfUnique = defaultPrefixOfUnique;
    }

    public String getDefaultPrefixOfIndex() {
        return defaultPrefixOfIndex;
    }

    public void setDefaultPrefixOfIndex(String defaultPrefixOfIndex) {
        this.defaultPrefixOfIndex = defaultPrefixOfIndex;
    }
}

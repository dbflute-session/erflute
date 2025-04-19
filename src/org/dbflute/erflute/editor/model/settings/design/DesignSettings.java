package org.dbflute.erflute.editor.model.settings.design;

import java.io.Serializable;

/**
 * @author jflute
 * @since 0.5.8 (2018/08/30 however released at 2020)
 */
public class DesignSettings implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ConstraintSettings constraintSettings; // not null (not final for clone())

    // #for_now jflute these settings can be set by only XML-direct way (2020/05/14)
    // <design_settings>
    //     <foreign_key>
    //         <default_prefix>SEA_</default_prefix>
    //     </foreign_key>
    //     <unique>
    //         <default_prefix>LAND_</default_prefix>
    //     </unique>
    //     <index>
    //         <default_prefix>PIARI_</default_prefix>
    //     </index>
    // </design_settings>

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DesignSettings() {
        constraintSettings = new ConstraintSettings();
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isEmpty() {
        return constraintSettings.isEmpty();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public DesignSettings clone() {
        try {
            final DesignSettings setting = (DesignSettings) super.clone();
            setting.constraintSettings = constraintSettings.clone();
            return setting;
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException("Unsupported clone()", e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ConstraintSettings getConstraintSettings() {
        return constraintSettings;
    }
}

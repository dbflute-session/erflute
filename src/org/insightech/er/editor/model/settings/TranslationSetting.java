package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.DisplayMessages;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationSetting implements Serializable, Cloneable {

    private static final long serialVersionUID = -7691417386790834828L;

    private List<String> selectedTranslations;

    private boolean use;

    public TranslationSetting() {
        this.selectedTranslations = new ArrayList<String>();
    }

    /**
     * allTranslations ���擾���܂�.
     * 
     * @return allTranslations
     */
    public List<String> getAllTranslations() {
        List<String> list = PreferenceInitializer.getAllUserTranslations();

        list.add(DisplayMessages.getMessage("label.translation.default"));

        return list;
    }

    /**
     * selectedTranslations ���擾���܂�.
     * 
     * @return selectedTranslations
     */
    public List<String> getSelectedTranslations() {
        return selectedTranslations;
    }

    /**
     * selectedTranslations ��ݒ肵�܂�.
     * 
     * @param selectedTranslations
     *            selectedTranslations
     */
    public void setSelectedTranslations(List<String> selectedTranslations) {
        this.selectedTranslations = selectedTranslations;
    }

    /**
     * selectedTranslations ��ݒ肵�܂�.
     * 
     * @param selectedTranslations
     *            selectedTranslations
     */
    public void selectDefault() {
        this.selectedTranslations.add(DisplayMessages.getMessage("label.translation.default"));
    }

    /**
     * use ���擾���܂�.
     * 
     * @return use
     */
    public boolean isUse() {
        return use;
    }

    /**
     * use ��ݒ肵�܂�.
     * 
     * @param use
     *            use
     */
    public void setUse(boolean use) {
        this.use = use;
    }

    public boolean isSelected(String translationName) {
        for (String translation : this.selectedTranslations) {
            if (translation.equals(translationName)) {
                return true;
            }
        }

        return false;
    }

    public void addTranslationAsSelected(String translation) {
        this.selectedTranslations.add(translation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() {
        try {
            TranslationSetting settings = (TranslationSetting) super.clone();
            settings.selectedTranslations = new ArrayList<String>();

            for (String selectedTranslation : this.selectedTranslations) {
                settings.selectedTranslations.add(selectedTranslation);
            }

            return settings;

        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void load() {
    }

    protected void parseString(String stringList) {
    }
}

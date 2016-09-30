package org.insightech.er.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.Activator;
import org.insightech.er.DisplayMessages;
import org.insightech.er.editor.model.settings.TranslationSetting;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationResources { // #willdelete

    private final Map<String, String> translationMap;

    public TranslationResources(TranslationSetting translationSettings) {
        this.translationMap = new TreeMap<String, String>(new TranslationResourcesComparator());

        final String defaultFileName = DisplayMessages.getMessage("label.translation.default");

        if (translationSettings.isUse()) {
            for (final String translation : PreferenceInitializer.getAllUserTranslations()) {
                if (translationSettings.isSelected(translation)) {
                    final File file = new File(PreferenceInitializer.getTranslationPath(translation));

                    if (file.exists()) {
                        FileInputStream in = null;

                        try {
                            in = new FileInputStream(file);
                            load(in);

                        } catch (final IOException e) {
                            Activator.showExceptionDialog(e);

                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (final IOException e) {
                                    Activator.showExceptionDialog(e);
                                }
                            }
                        }
                    }

                }
            }

            if (translationSettings.isSelected(defaultFileName)) {
                final InputStream in = this.getClass().getResourceAsStream("/translation.txt");
                try {
                    load(in);

                } catch (final IOException e) {
                    Activator.showExceptionDialog(e);

                } finally {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        Activator.showExceptionDialog(e);
                    }
                }

            }
        }
    }

    private void load(InputStream in) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        String line = null;

        while ((line = reader.readLine()) != null) {
            final int index = line.indexOf(",");
            if (index == -1 || index == line.length() - 1) {
                continue;
            }

            String key = line.substring(0, index).trim();
            if ("".equals(key)) {
                continue;
            }

            final String value = line.substring(index + 1).trim();
            this.translationMap.put(key, value);

            key = key.replaceAll("[aiueo]", "");
            if (key.length() > 1) {
                this.translationMap.put(key, value);
            }
        }
    }

    /**
     * ERDiagram.properties �̎w�肳�ꂽ�L�[�ɑΉ�����l��Ԃ��܂�
     * 
     * @param key
     *            ERDiagram.properties �Œ�`���ꂽ�L�[
     * @return ERDiagram.properties �̎w�肳�ꂽ�L�[�ɑΉ�����l
     */
    public String translate(String str) {
        for (final Entry<String, String> entry : translationMap.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();

            final Pattern p = Pattern.compile("_*" + Pattern.quote(key) + "_*", Pattern.CASE_INSENSITIVE);
            final Matcher m = p.matcher(str);
            str = m.replaceAll(value);
        }

        return str;
    }

    public boolean contains(String key) {
        return this.translationMap.containsKey(key);
    }

    /**
     * �������ɕ��ׂ�B���������Ȃ玫�����B������ [A-Z] ��� [_] ��D�悷��B
     */
    private class TranslationResourcesComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            final int diff = o2.length() - o1.length();
            if (diff != 0) {
                return diff;
            } else {
                return o1.replace('_', ' ').compareTo(o2.replace('_', ' '));
            }
        }
    }
}

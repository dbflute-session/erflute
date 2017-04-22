package org.dbflute.erflute.editor.model.settings;

import java.io.Serializable;

public class Environment implements Serializable, Cloneable {

    private static final long serialVersionUID = 2894497911334351672L;

    private String name;

    public Environment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Environment clone() {
        try {
            final Environment environment = (Environment) super.clone();

            return environment;

        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }
}

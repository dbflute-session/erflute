package org.dbflute.erflute.editor.model;

public class StringObjectModel implements ObjectModel {

    private final String name;

    public StringObjectModel(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getObjectType() {
        return "other";
    }
}

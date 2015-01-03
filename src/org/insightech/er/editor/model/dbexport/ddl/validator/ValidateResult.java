package org.insightech.er.editor.model.dbexport.ddl.validator;

public class ValidateResult {

    private String message;

    private String location;

    private int severity;

    private Object object;

    /**
     * object ���擾���܂�.
     *
     * @return object
     */
    public Object getObject() {
        return object;
    }

    /**
     * object ��ݒ肵�܂�.
     *
     * @param object object
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * message ���擾���܂�.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * message ��ݒ肵�܂�.
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * location ���擾���܂�.
     *
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * location ��ݒ肵�܂�.
     *
     * @param location location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * severity ���擾���܂�.
     *
     * @return severity
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * severity ��ݒ肵�܂�.
     *
     * @param severity severity
     */
    public void setSeverity(int severity) {
        this.severity = severity;
    }

}

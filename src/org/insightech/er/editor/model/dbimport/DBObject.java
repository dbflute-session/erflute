package org.insightech.er.editor.model.dbimport;

import org.insightech.er.editor.model.AbstractModel;

public class DBObject {

	public static final String TYPE_TABLE = "table";

	public static final String TYPE_SEQUENCE = "sequence";

	public static final String TYPE_VIEW = "view";

	public static final String TYPE_TRIGGER = "trigger";

	public static final String TYPE_TABLESPACE = "tablespace";

	public static final String TYPE_NOTE = "note";

	public static final String TYPE_GROUP = "group";

	public static final String[] ALL_TYPES = { TYPE_TABLE, TYPE_VIEW,
			TYPE_SEQUENCE, TYPE_TRIGGER };

	private String schema;
	private String name;
	private String type;
	private String logicalName;

	private AbstractModel model;

	public DBObject(String schema, String name, String type) {
		this.schema = schema;
		this.name = name;
		this.type = type;
	}

	public void setModel(AbstractModel model) {
		this.model = model;
	}

	/**
	 * logicalName���擾���܂��B
	 * @return logicalName
	 */
	public String getLogicalName() {
	    return logicalName;
	}

	/**
	 * logicalName��ݒ肵�܂��B
	 * @param logicalName logicalName
	 */
	public void setLogicalName(String logicalName) {
	    this.logicalName = logicalName;
	}

	public AbstractModel getModel() {
		return model;
	}

	/**
	 * schema ���擾���܂�.
	 * 
	 * @return schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * schema ��ݒ肵�܂�.
	 * 
	 * @param schema
	 *            schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * name ���擾���܂�.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * name ��ݒ肵�܂�.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * type ���擾���܂�.
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * type ��ݒ肵�܂�.
	 * 
	 * @param type
	 *            type
	 */
	public void setType(String type) {
		this.type = type;
	}
}

package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Check;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.db.sqltype.SqlType;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class NormalColumn extends ERColumn {

    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Word word;
    private String foreignKeyPhysicalName; // #willanalyze unused? by jflute
    private String foreignKeyLogicalName;
    private String foreignKeyDescription;
    private boolean notNull;
    private boolean primaryKey;
    private boolean uniqueKey;
    private boolean autoIncrement;
    private String defaultValue;
    private String constraint;
    private String uniqueKeyName;
    private String characterSet;
    private String collation;
    private List<NormalColumn> referredColumnList = new ArrayList<>();
    private List<Relationship> relationshipList = new ArrayList<>();
    private Sequence autoIncrementSetting; // same as sequence

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public NormalColumn(Word word, boolean notNull, boolean primaryKey, boolean uniqueKey, boolean autoIncrement,
            String defaultValue, String constraint, String uniqueKeyName, String characterSet, String collation) {
        this.word = word;
        init(notNull, primaryKey, uniqueKey, autoIncrement, defaultValue, constraint, uniqueKeyName, characterSet, collation);
        this.autoIncrementSetting = new Sequence();
    }

    protected NormalColumn(NormalColumn from) {
        referredColumnList.addAll(from.referredColumnList);
        relationshipList.addAll(from.relationshipList);
        this.foreignKeyPhysicalName = from.foreignKeyPhysicalName;
        this.foreignKeyLogicalName = from.foreignKeyLogicalName;
        this.foreignKeyDescription = from.foreignKeyDescription;
        init(from.notNull, from.primaryKey, from.uniqueKey, from.autoIncrement, from.defaultValue, from.constraint, from.uniqueKeyName,
                from.characterSet, from.collation);
        this.word = from.word;
        this.autoIncrementSetting = (Sequence) from.autoIncrementSetting.clone();
    }

    public NormalColumn(NormalColumn from, NormalColumn referredColumn, Relationship relationship, boolean primaryKey) {
        this.word = null;
        referredColumnList.add(referredColumn);
        relationshipList.add(relationship);
        copyData(from, this);
        this.primaryKey = primaryKey;
        this.autoIncrement = false;
        this.autoIncrementSetting = new Sequence();
    }

    public NormalColumn(NormalColumn referredColumn, Relationship relationship, boolean referenceForPK) {
        this(referredColumn, referredColumn, relationship, referenceForPK);
        this.word = (Word) referredColumn.getWord().clone();
        this.primaryKey = false;
        this.uniqueKey = false;
    }

    protected void init(boolean notNull, boolean primaryKey, boolean uniqueKey, boolean autoIncrement,
            String defaultValue, String constraint, String uniqueKeyName, String characterSet, String collation) {
        this.notNull = notNull;
        this.primaryKey = primaryKey;
        this.uniqueKey = uniqueKey;
        this.autoIncrement = autoIncrement;
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.uniqueKeyName = uniqueKeyName;
        this.characterSet = characterSet;
        this.collation = collation;
    }

    // ===================================================================================
    //                                                                        Relationship
    //                                                                        ============
    public NormalColumn getReferredColumn(Relationship relationship) {
        for (final NormalColumn referredColumn : referredColumnList) {
            if (referredColumn.getColumnHolder() == relationship.getSourceTableView()) {
                return referredColumn;
            }
        }
        return null;
    }

    public NormalColumn getFirstReferredColumn() {
        return !referredColumnList.isEmpty() ? referredColumnList.get(0) : null;
    }

    public NormalColumn getFirstRootReferredColumn() { // e.g. FK to FK to FK to ...
        NormalColumn root = getFirstReferredColumn();
        if (root != null) {
            while (root.getFirstReferredColumn() != null) {
                root = root.getFirstReferredColumn();
            }
        }
        return root;
    }

    public List<Relationship> getOutgoingRelationList() {
        final List<Relationship> outgoingRelationList = new ArrayList<>();
        final ColumnHolder columnHolder = getColumnHolder();
        if (columnHolder instanceof ERTable) {
            final ERTable table = (ERTable) columnHolder;
            for (final Relationship relation : table.getOutgoingRelationshipList()) {
                if (relation.isReferenceForPK()) {
                    if (isPrimaryKey()) {
                        outgoingRelationList.add(relation);
                    }
                } else {
                    if (this == relation.getReferredSimpleUniqueColumn()) {
                        outgoingRelationList.add(relation);
                    }
                }
            }
        }
        return outgoingRelationList;
    }

    public List<NormalColumn> getForeignKeyList() {
        final List<NormalColumn> foreignKeyList = new ArrayList<>();
        final ColumnHolder columnHolder = getColumnHolder();
        if (columnHolder instanceof ERTable) {
            final ERTable table = (ERTable) columnHolder;
            for (final Relationship relation : table.getOutgoingRelationshipList()) {
                boolean found = false;
                for (final NormalColumn column : relation.getTargetTableView().getNormalColumns()) {
                    if (column.isForeignKey()) {
                        for (final NormalColumn referencedColumn : column.referredColumnList) {
                            if (referencedColumn == this) {
                                foreignKeyList.add(column);
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                }
            }
        }
        return foreignKeyList;
    }

    public List<Relationship> getRelationshipList() {
        return relationshipList;
    }

    public void addReference(NormalColumn referredColumn, Relationship relationship) {
        // 参照列とリレーションが重複して追加されることがあるため、ここでガードする。
        if (referredColumnList.contains(referredColumn) || relationshipList.contains(relationship)) {
            return;
        }

        this.foreignKeyDescription = getDescription();
        this.foreignKeyLogicalName = getLogicalName();
        this.foreignKeyPhysicalName = getPhysicalName();
        referredColumnList.add(referredColumn);
        relationshipList.add(relationship);
        copyData(this, this);
        this.word = null;
    }

    public void renewRelationList() {
        final List<Relationship> newRelationList = new ArrayList<>();
        newRelationList.addAll(relationshipList);
        this.relationshipList = newRelationList;
    }

    public void removeReference(Relationship relation) {
        relationshipList.remove(relation);
        if (relationshipList.isEmpty()) {
            NormalColumn temp = getFirstReferredColumn();
            while (temp.isForeignKey()) {
                temp = temp.getFirstReferredColumn();
            }
            this.word = temp.getWord();
            if (getPhysicalName() != word.getPhysicalName()
                    || getLogicalName() != word.getLogicalName()
                    || getDescription() != word.getDescription()) {
                this.word = new Word(word);
                word.setPhysicalName(getPhysicalName());
                word.setLogicalName(getLogicalName());
                word.setDescription(getDescription());
            }
            this.foreignKeyDescription = null;
            this.foreignKeyLogicalName = null;
            this.foreignKeyPhysicalName = null;
            referredColumnList.clear();
            copyData(this, this);
        } else {
            for (final NormalColumn referencedColumn : referredColumnList) {
                if (referencedColumn.getColumnHolder() == relation.getSourceTableView()) {
                    referredColumnList.remove(referencedColumn);
                    break;
                }
            }
        }
    }

    public boolean isForeignKey() {
        if (!relationshipList.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isRefered() {
        if (!(getColumnHolder() instanceof ERTable)) {
            return false;
        }
        boolean isRefered = false;
        final ERTable table = (ERTable) getColumnHolder();
        for (final Relationship relation : table.getOutgoingRelationshipList()) {
            if (!relation.isReferenceForPK()) {
                for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
                    for (final NormalColumn referencedColumn : foreignKeyColumn.referredColumnList) {
                        if (referencedColumn == this) {
                            isRefered = true;
                            break;
                        }
                    }
                    if (isRefered) {
                        break;
                    }
                }
                if (isRefered) {
                    break;
                }
            }
        }
        return isRefered;
    }

    public boolean isReferedStrictly() {
        if (!(getColumnHolder() instanceof ERTable)) {
            return false;
        }
        boolean isRefered = false;
        final ERTable table = (ERTable) getColumnHolder();
        for (final Relationship relation : table.getOutgoingRelationshipList()) {
            if (!relation.isReferenceForPK()) {
                for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
                    for (final NormalColumn referencedColumn : foreignKeyColumn.referredColumnList) {
                        if (referencedColumn == this) {
                            isRefered = true;
                            break;
                        }
                    }
                    if (isRefered) {
                        break;
                    }
                }
                if (isRefered) {
                    break;
                }
            } else {
                if (isPrimaryKey()) {
                    isRefered = true;
                    break;
                }
            }
        }
        return isRefered;
    }

    public Word getWord() {
        if (word == null) {
            return getFirstRootReferredColumn().word;
        }

        return word;
    }

    public boolean isFullTextIndexable() {
        return getType().isFullTextIndexable();
    }

    public static void copyData(NormalColumn from, NormalColumn to) {
        to.init(from.isNotNull(), from.isPrimaryKey(), from.isUniqueKey(), from.isAutoIncrement(), from.getDefaultValue(),
                from.getConstraint(), from.uniqueKeyName, from.characterSet, from.collation);
        to.autoIncrementSetting = (Sequence) from.autoIncrementSetting.clone();
        if (to.isForeignKey()) {
            final NormalColumn firstReferencedColumn = to.getFirstReferredColumn();
            if (firstReferencedColumn.getPhysicalName() == null) {
                to.foreignKeyPhysicalName = from.getPhysicalName();
            } else {
                if (from.foreignKeyPhysicalName != null && !firstReferencedColumn.getPhysicalName().equals(from.foreignKeyPhysicalName)) {
                    to.foreignKeyPhysicalName = from.foreignKeyPhysicalName;
                } else if (!firstReferencedColumn.getPhysicalName().equals(from.getPhysicalName())) {
                    to.foreignKeyPhysicalName = from.getPhysicalName();
                } else {
                    to.foreignKeyPhysicalName = null;
                }
            }
            if (firstReferencedColumn.getLogicalName() == null) {
                to.foreignKeyLogicalName = from.getLogicalName();
            } else {
                if (from.foreignKeyLogicalName != null && !firstReferencedColumn.getLogicalName().equals(from.foreignKeyLogicalName)) {
                    to.foreignKeyLogicalName = from.foreignKeyLogicalName;
                } else if (!firstReferencedColumn.getLogicalName().equals(from.getLogicalName())) {
                    to.foreignKeyLogicalName = from.getLogicalName();

                } else {
                    to.foreignKeyLogicalName = null;
                }
            }
            if (firstReferencedColumn.getDescription() == null) {
                to.foreignKeyDescription = from.getDescription();
            } else {
                if (from.foreignKeyDescription != null && !firstReferencedColumn.getDescription().equals(from.foreignKeyDescription)) {
                    to.foreignKeyDescription = from.foreignKeyDescription;
                } else if (!firstReferencedColumn.getDescription().equals(from.getDescription())) {
                    to.foreignKeyDescription = from.getDescription();
                } else {
                    to.foreignKeyDescription = null;
                }
            }
        } else {
            from.word.copyTo(to.word);
        }
        to.setColumnHolder(from.getColumnHolder());
    }

    // ===================================================================================
    //                                                                           Column ID
    //                                                                           =========
    public String buildColumnId(TableView table) {
        return table.buildTableViewId() + "." + getResolvedPhysicalName();
    }

    public String buildColumnIdAsGroup(ColumnGroup group) {
        return "columnGroup." + group.getGroupName() + "." + getResolvedPhysicalName();
    }

    public String buildSimpleUniqueColumnId(TableView table) {
        return buildColumnId(table); // should be same as normal column because of mapping when loading
    }

    private String getResolvedPhysicalName() {
        final String physicalName = getPhysicalName();
        if (Srl.is_NotNull_and_NotEmpty(physicalName)) {
            return physicalName;
        } else {
            final NormalColumn firstReferencedColumn = getFirstReferredColumn();
            if (firstReferencedColumn != null) {
                return firstReferencedColumn.getPhysicalName();
            } else { // no way? by jflute
                return "Unknown";
            }
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public NormalColumn clone() {
        final NormalColumn clone = (NormalColumn) super.clone();
        clone.relationshipList = new ArrayList<>(relationshipList);
        clone.referredColumnList = new ArrayList<>(referredColumnList);
        return clone;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(":{");
        sb.append("physicalName=" + getPhysicalName());
        sb.append(", logicalName=" + getLogicalName());
        sb.append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    @Override
    public String getName() {
        return getPhysicalName(); // #for_erflute change logical to physical for fixed sort
    }

    public String getPhysicalName() {
        if (getFirstReferredColumn() != null) {
            if (!Check.isEmpty(foreignKeyPhysicalName)) {
                return foreignKeyPhysicalName;
            } else {
                return getFirstReferredColumn().getPhysicalName();
            }
        }
        return word.getPhysicalName();
    }

    public String getLogicalName() {
        if (getFirstReferredColumn() != null) {
            if (!Check.isEmpty(foreignKeyLogicalName)) {
                return foreignKeyLogicalName;
            } else {
                return getFirstReferredColumn().getLogicalName();
            }
        }
        return word.getLogicalName();
    }

    public String getDescription() {
        if (getFirstReferredColumn() != null) {
            if (!Check.isEmpty(foreignKeyDescription)) {
                return foreignKeyDescription;
            } else {
                return getFirstReferredColumn().getDescription();
            }
        }
        return word.getDescription();
    }

    public String getForeignKeyLogicalName() {
        return foreignKeyLogicalName;
    }

    public String getForeignKeyPhysicalName() {
        return foreignKeyPhysicalName;
    }

    public String getForeignKeyDescription() {
        return foreignKeyDescription;
    }

    public SqlType getType() {
        if (getFirstReferredColumn() != null) {
            final SqlType type = getFirstReferredColumn().getType();
            if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(type)) {
                return SqlType.valueOfId(SqlType.SQL_TYPE_ID_INTEGER);
            } else if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL).equals(type)) {
                return SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_INT);
            }
            return type;
        }
        return word.getType();
    }

    public TypeData getTypeData() {
        if (getFirstReferredColumn() != null) {
            return getFirstReferredColumn().getTypeData();
        }
        return word.getTypeData();
    }

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isUniqueKey() {
        return uniqueKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getConstraint() {
        return constraint;
    }

    public String getUniqueKeyName() {
        return uniqueKeyName;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public void setForeignKeyPhysicalName(String physicalName) {
        this.foreignKeyPhysicalName = physicalName;
    }

    public void setForeignKeyLogicalName(String logicalName) {
        this.foreignKeyLogicalName = logicalName;
    }

    public void setForeignKeyDescription(String description) {
        this.foreignKeyDescription = description;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public void setUniqueKeyName(String uniqueKeyName) {
        this.uniqueKeyName = uniqueKeyName;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public void setUniqueKey(boolean uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public void copyForeikeyData(NormalColumn to) {
        to.setConstraint(getConstraint());
        to.setForeignKeyDescription(getForeignKeyDescription());
        to.setForeignKeyLogicalName(getForeignKeyLogicalName());
        to.setForeignKeyPhysicalName(getForeignKeyPhysicalName());
        to.setNotNull(isNotNull());
        to.setUniqueKey(isUniqueKey());
        to.setPrimaryKey(isPrimaryKey());
        to.setAutoIncrement(isAutoIncrement());
        to.setCharacterSet(getCharacterSet());
        to.setCollation(getCollation());
    }

    public List<NormalColumn> getReferencedColumnList() {
        return referredColumnList;
    }

    public void clearRelations() {
        relationshipList = new ArrayList<>();
    }

    public Sequence getAutoIncrementSetting() {
        return autoIncrementSetting;
    }

    public void setAutoIncrementSetting(Sequence autoIncrementSetting) {
        this.autoIncrementSetting = autoIncrementSetting;
    }
}

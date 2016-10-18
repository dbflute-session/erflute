package org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.AbstractModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CompoundUniqueKey extends AbstractModel {

    private static final long serialVersionUID = -3970737521746421701L;

    private String uniqueKeyName;
    private List<NormalColumn> columnList;

    public CompoundUniqueKey(String uniqueKeyName) {
        this.uniqueKeyName = uniqueKeyName;
        this.columnList = new ArrayList<NormalColumn>();
    }

    public boolean isRemoved(List<NormalColumn> tableColumnList) {
        for (final NormalColumn normalColumn : this.columnList) {
            if (!tableColumnList.contains(normalColumn)) {
                return true;
            }
        }
        return false;
    }

    public String getLabel() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Format.null2blank(this.uniqueKeyName));
        sb.append(" (");
        boolean first = true;
        for (final NormalColumn normalColumn : this.getColumnList()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(normalColumn.getName());
        }
        sb.append(")");
        return sb.toString();
    }

    public boolean isReferred(ERTable table) {
        boolean isReferred = false;
        CompoundUniqueKey target = this;
        if (target instanceof CopyCompoundUniqueKey) {
            target = ((CopyCompoundUniqueKey) target).getOriginal();
        }
        for (final Relationship relationship : table.getOutgoingRelationshipList()) {
            if (relationship.getReferredCompoundUniqueKey() == target) {
                isReferred = true;
                break;
            }
        }
        return isReferred;
    }

    public String buildUniqueKeyId(TableView table) {
        if (Srl.is_NotNull_and_NotTrimmedEmpty(uniqueKeyName)) {
            return getUniqueKeyName();
        } else {
            final StringBuilder sb = new StringBuilder();
            for (final NormalColumn normalColumn : columnList) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append(normalColumn.getPhysicalName());
            }
            return "compoundUniqueKey." + table.buildTableViewId() + ".[" + sb.toString() + "]";
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + uniqueKeyName + ", columns=" + columnList.size() + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getUniqueKeyName() {
        return uniqueKeyName;
    }

    public List<NormalColumn> getColumnList() {
        return columnList;
    }

    public void addColumn(NormalColumn column) {
        this.columnList.add(column);
    }

    public void setColumnList(List<NormalColumn> columnList) {
        this.columnList = columnList;
    }

    public void setUniqueKeyName(String uniqueKeyName) {
        this.uniqueKeyName = uniqueKeyName;
    }
}

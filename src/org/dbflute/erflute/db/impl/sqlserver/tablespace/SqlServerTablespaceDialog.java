package org.dbflute.erflute.db.impl.sqlserver.tablespace;

import org.dbflute.erflute.core.util.Format;
import org.dbflute.erflute.core.widgets.CompositeFactory;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.dbflute.erflute.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SqlServerTablespaceDialog extends TablespaceDialog {

    // (REGULAR/LARGI/SYSTEM TEMPORARY/USER TEMPORARY)
    private Text type;
    private Text pageSize;
    private Text managedBy;
    private Text container;
    private Text extentSize;
    private Text prefetchSize;
    private Text bufferPoolName;

    @Override
    protected void initComponent(Composite composite) {
        super.initComponent(composite);

        this.type = CompositeFactory.createText(this, composite, "label.tablespace.type", false);
        this.pageSize = CompositeFactory.createText(this, composite, "label.tablespace.page.size", false);
        this.managedBy = CompositeFactory.createText(this, composite, "label.tablespace.managed.by", false);
        this.container = CompositeFactory.createText(this, composite, "label.tablespace.container", false);
        this.extentSize = CompositeFactory.createText(this, composite, "label.tablespace.extent.size", false);
        this.prefetchSize = CompositeFactory.createText(this, composite, "label.tablespace.prefetch.size", false);
        this.bufferPoolName = CompositeFactory.createText(this, composite, "label.tablespace.buffer.pool.name", false);
    }

    @Override
    protected TablespaceProperties setTablespaceProperties() {
        final SqlServerTablespaceProperties tablespaceProperties = new SqlServerTablespaceProperties();

        tablespaceProperties.setType(type.getText().trim());
        tablespaceProperties.setPageSize(pageSize.getText().trim());
        tablespaceProperties.setManagedBy(managedBy.getText().trim());
        tablespaceProperties.setContainer(container.getText().trim());
        tablespaceProperties.setExtentSize(extentSize.getText().trim());
        tablespaceProperties.setPrefetchSize(prefetchSize.getText().trim());
        tablespaceProperties.setBufferPoolName(bufferPoolName.getText().trim());

        return tablespaceProperties;
    }

    @Override
    protected void setData(TablespaceProperties tablespaceProperties) {
        if (tablespaceProperties instanceof SqlServerTablespaceProperties) {
            final SqlServerTablespaceProperties properties = (SqlServerTablespaceProperties) tablespaceProperties;

            type.setText(Format.toString(properties.getType()));
            pageSize.setText(Format.toString(properties.getPageSize()));
            managedBy.setText(Format.toString(properties.getManagedBy()));
            container.setText(Format.toString(properties.getContainer()));
            extentSize.setText(Format.toString(properties.getExtentSize()));
            prefetchSize.setText(Format.toString(properties.getPrefetchSize()));
            bufferPoolName.setText(Format.toString(properties.getBufferPoolName()));
        }
    }
}

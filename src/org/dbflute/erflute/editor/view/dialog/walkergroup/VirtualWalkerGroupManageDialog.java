package org.dbflute.erflute.editor.view.dialog.walkergroup;

import java.util.List;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.WalkerGroup;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.eclipse.swt.widgets.Shell;

/**
 * @author jflute
 */
public class VirtualWalkerGroupManageDialog extends MainWalkerGroupManageDialog {

    private final ERVirtualDiagram vdiagram;

    public VirtualWalkerGroupManageDialog(Shell parentShell, ERVirtualDiagram vdiagram) {
        super(parentShell, vdiagram.getDiagram()); // super's diagram is unused (dummy)
        this.vdiagram = vdiagram;
    }

    // ===================================================================================
    //                                                                   Diagram Resources
    //                                                                   =================
    @Override
    protected List<WalkerGroup> getWalkerGroups() {
        return vdiagram.getWalkerGroups();
    }

    @Override
    protected List<ERVirtualTable> getTableWalkers() {
        return vdiagram.getVirtualTables();
    }

    @Override
    protected int[] getDefaultColor() {
        return vdiagram.getDiagram().getDefaultColor();
    }
}

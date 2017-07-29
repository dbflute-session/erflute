package org.dbflute.erflute.editor.controller.command.ermodel;

import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagram;

public class ChangeVirtualDiagramNameCommand extends AbstractCommand {

    private final ERVirtualDiagram virtualDiagram;
    private final String oldName;
    private final String newName;

    public ChangeVirtualDiagramNameCommand(ERVirtualDiagram virtualDiagram, String newName) {
        this.virtualDiagram = virtualDiagram;
        this.oldName = virtualDiagram.getName();
        this.newName = newName;
    }

    private ERDiagram getDiagram() {
        return virtualDiagram.getDiagram();
    }

    @Override
    protected void doExecute() {
        virtualDiagram.setName(newName);
        getDiagram().getDiagramContents().getVirtualDiagramSet().changeVdiagram(virtualDiagram);

        // virtualDiagram.setName内に下記呼び出しを書くことは可能だったがやめた。
        // modelがviewであるERFluteMultiPageEditorを直接変更するのおかしい。
        // そもそもvirtualDiagramがERFluteMultiPageEditorの参照を直接持つのは良いのか？
        // オブザーバーパターンとか使ったほうが良いのでは？
        virtualDiagram.getEditor().setPageText(virtualDiagram, newName);
    }

    @Override
    protected void doUndo() {
        virtualDiagram.setName(oldName);
        getDiagram().getDiagramContents().getVirtualDiagramSet().changeVdiagram(virtualDiagram);

        virtualDiagram.getEditor().setPageText(virtualDiagram, oldName);
    }
}

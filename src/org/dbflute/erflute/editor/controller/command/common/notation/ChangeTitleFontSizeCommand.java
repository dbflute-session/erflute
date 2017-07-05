package org.dbflute.erflute.editor.controller.command.common.notation;

import java.math.BigDecimal;

import org.dbflute.erflute.Activator;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class ChangeTitleFontSizeCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final boolean oldCapital;

    private final boolean newCapital;

    private final DiagramSettings settings;

    public ChangeTitleFontSizeCommand(ERDiagram diagram, boolean isCapital) {
        this.diagram = diagram;
        this.settings = diagram.getDiagramContents().getSettings();
        this.newCapital = isCapital;
        this.oldCapital = settings.isCapital();
    }

    @Override
    protected void doExecute() {
        settings.setTitleFontEm(newCapital ? new BigDecimal("1.5") : new BigDecimal("1"));

        if (!ERModelUtil.refreshDiagram(diagram)) {
            Activator.showMessageDialog("テーブルタイトルのフォントサイズを変更しました。\nリアルタイムで変更が反映されないので、ermファイルを保存してもう一度開き直してください。");
        }
    }

    @Override
    protected void doUndo() {
        settings.setTitleFontEm(oldCapital ? new BigDecimal("1.5") : new BigDecimal("1"));
        diagram.changeAll();
    }
}

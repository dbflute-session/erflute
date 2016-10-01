package org.insightech.er.editor.model.diagram_contents;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.Settings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class DiagramContents {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Settings settings;
    private NodeSet contents;
    private GroupSet groups;
    private Dictionary dictionary;
    private SequenceSet sequenceSet;
    private TriggerSet triggerSet;
    private IndexSet indexSet;
    private TablespaceSet tablespaceSet;
    private ERModelSet modelSet;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DiagramContents() {
        this.settings = new Settings();
        this.contents = new NodeSet();
        this.groups = new GroupSet();
        this.dictionary = new Dictionary();
        this.sequenceSet = new SequenceSet();
        this.triggerSet = new TriggerSet();
        this.indexSet = new IndexSet();
        this.tablespaceSet = new TablespaceSet();
        this.modelSet = new ERModelSet();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + settings + ", " + contents + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public NodeSet getContents() {
        return this.contents;
    }

    public void setContents(NodeSet contents) {
        this.contents = contents;
    }

    public GroupSet getGroups() {
        return this.groups;
    }

    public void setColumnGroups(GroupSet groups) {
        this.groups = groups;
    }

    public Dictionary getDictionary() {
        return this.dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public SequenceSet getSequenceSet() {
        return sequenceSet;
    }

    public void setSequenceSet(SequenceSet sequenceSet) {
        this.sequenceSet = sequenceSet;
    }

    public TriggerSet getTriggerSet() {
        return triggerSet;
    }

    public void setTriggerSet(TriggerSet triggerSet) {
        this.triggerSet = triggerSet;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public IndexSet getIndexSet() {
        return indexSet;
    }

    public void setIndexSet(IndexSet indexSet) {
        this.indexSet = indexSet;
    }

    public TablespaceSet getTablespaceSet() {
        return tablespaceSet;
    }

    public void setTablespaceSet(TablespaceSet tablespaceSet) {
        this.tablespaceSet = tablespaceSet;
    }

    public ERModelSet getModelSet() {
        return modelSet;
    }

    public void setModelSet(ERModelSet modelSet) {
        this.modelSet = modelSet;
    }
}

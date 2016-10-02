package org.insightech.er.editor.persistent.xml;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.editor.persistent.xml.reader.ErmXmlReader;
import org.insightech.er.editor.persistent.xml.writer.ErmXmlWriter;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class PersistentXml extends Persistent {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public class PersistentContext {
        public final Map<ColumnGroup, Integer> columnGroupMap = new HashMap<ColumnGroup, Integer>();
        public final Map<ConnectionElement, Integer> connectionMap = new HashMap<ConnectionElement, Integer>();
        public final Map<ERColumn, Integer> columnMap = new HashMap<ERColumn, Integer>();
        public final Map<ComplexUniqueKey, Integer> complexUniqueKeyMap = new HashMap<ComplexUniqueKey, Integer>();
        public final Map<NodeElement, Integer> nodeElementMap = new HashMap<NodeElement, Integer>();
        public final Map<ERModel, Integer> ermodelMap = new HashMap<ERModel, Integer>();
        public final Map<Word, Integer> wordMap = new HashMap<Word, Integer>();
        public final Map<Tablespace, Integer> tablespaceMap = new HashMap<Tablespace, Integer>();
        public final Map<Environment, Integer> environmentMap = new HashMap<Environment, Integer>();
    }

    public PersistentContext getContext(DiagramContents diagramContents) {
        final PersistentContext context = new PersistentContext();
        int columnGroupCount = 0;
        int columnCount = 0;
        for (final ColumnGroup columnGroup : diagramContents.getGroups()) {
            context.columnGroupMap.put(columnGroup, new Integer(columnGroupCount));
            columnGroupCount++;
            for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                context.columnMap.put(normalColumn, new Integer(columnCount));
                columnCount++;
            }
        }

        int nodeElementCount = 0;
        int connectionCount = 0;
        int complexUniqueKeyCount = 0;
        for (final NodeElement content : diagramContents.getContents()) {
            context.nodeElementMap.put(content, new Integer(nodeElementCount));
            nodeElementCount++;
            final List<ConnectionElement> connections = content.getIncomings();
            for (final ConnectionElement connection : connections) {
                context.connectionMap.put(connection, new Integer(connectionCount));
                connectionCount++;
            }
            if (content instanceof ERTable) {
                final ERTable table = (ERTable) content;
                final List<ERColumn> columns = table.getColumns();
                for (final ERColumn column : columns) {
                    if (column instanceof NormalColumn) {
                        context.columnMap.put(column, new Integer(columnCount));
                        columnCount++;
                    }
                }
                for (final ComplexUniqueKey complexUniqueKey : table.getComplexUniqueKeyList()) {
                    context.complexUniqueKeyMap.put(complexUniqueKey, new Integer(complexUniqueKeyCount));
                    complexUniqueKeyCount++;
                }
            }
        }

        int wordCount = 0;
        for (final Word word : diagramContents.getDictionary().getWordList()) {
            context.wordMap.put(word, new Integer(wordCount));
            wordCount++;
        }

        int tablespaceCount = 0;
        for (final Tablespace tablespace : diagramContents.getTablespaceSet()) {
            context.tablespaceMap.put(tablespace, new Integer(tablespaceCount));
            tablespaceCount++;
        }

        int environmentCount = 0;
        for (final Environment environment : diagramContents.getSettings().getEnvironmentSetting().getEnvironments()) {
            context.environmentMap.put(environment, new Integer(environmentCount));
            environmentCount++;
        }

        int virtualModelCount = 0;
        for (final ERModel model : diagramContents.getModelSet()) {
            context.ermodelMap.put(model, new Integer(virtualModelCount));
            virtualModelCount++;
        }

        return context;
    }

    public PersistentContext getCurrentContext(ERDiagram diagram) {
        return this.getContext(diagram.getDiagramContents());
    }

    // ===================================================================================
    //                                                                               Read
    //                                                                              ======
    @Override
    public ERDiagram read(InputStream ins) throws Exception {
        return new ErmXmlReader(this).read(ins);
    }

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====
    @Override
    public InputStream write(ERDiagram diagram) throws IOException {
        return new ErmXmlWriter(this).write(diagram);
    }
}
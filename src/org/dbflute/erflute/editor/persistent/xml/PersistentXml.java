package org.dbflute.erflute.editor.persistent.xml;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.settings.Environment;
import org.dbflute.erflute.editor.persistent.Persistent;
import org.dbflute.erflute.editor.persistent.xml.reader.ErmXmlReader;
import org.dbflute.erflute.editor.persistent.xml.writer.ErmXmlWriter;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class PersistentXml extends Persistent {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public class PersistentContext {
        public final Map<NodeElement, Integer> nodeElementMap = new HashMap<NodeElement, Integer>();
        public final Map<ConnectionElement, Integer> connectionMap = new HashMap<ConnectionElement, Integer>();
        public final Map<ColumnGroup, Integer> columnGroupMap = new HashMap<ColumnGroup, Integer>();
        public final Map<ERColumn, Integer> columnMap = new HashMap<ERColumn, Integer>(); // column = ID
        public final Map<ComplexUniqueKey, Integer> complexUniqueKeyMap = new HashMap<ComplexUniqueKey, Integer>();
        public final Map<Word, Integer> wordMap = new HashMap<Word, Integer>();
        public final Map<Tablespace, Integer> tablespaceMap = new HashMap<Tablespace, Integer>();
        public final Map<Environment, Integer> environmentMap = new HashMap<Environment, Integer>();
        public final Map<ERModel, Integer> ermodelMap = new HashMap<ERModel, Integer>();
    }

    public PersistentContext getCurrentContext(ERDiagram diagram) { // called by writer
        return createContext(diagram.getDiagramContents());
    }

    private PersistentContext createContext(DiagramContents diagramContents) {
        final PersistentContext context = new PersistentContext();
        final int columnNo = setupColumnGroup(diagramContents, context);
        setupNodeElement(diagramContents, context, columnNo); // contains table, column
        setupWord(diagramContents, context);
        setupTablespace(diagramContents, context);
        setupEnvironment(diagramContents, context);
        setupVirtualModel(diagramContents, context);
        return context;
    }

    private int setupColumnGroup(DiagramContents diagramContents, final PersistentContext context) {
        int columnGroupNo = 1;
        int columnNo = 1;
        for (final ColumnGroup columnGroup : diagramContents.getGroups()) {
            context.columnGroupMap.put(columnGroup, columnGroupNo);
            columnGroupNo++;
            for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                context.columnMap.put(normalColumn, columnNo);
                columnNo++;
            }
        }
        return columnNo;
    }

    private void setupNodeElement(DiagramContents diagramContents, final PersistentContext context, int columnNo) {
        int nodeElementNo = 1;
        int connectionNo = 1;
        int complexUniqueKeyNo = 1;
        for (final NodeElement content : diagramContents.getContents()) {
            context.nodeElementMap.put(content, nodeElementNo);
            nodeElementNo++;
            final List<ConnectionElement> connections = content.getIncomings();
            for (final ConnectionElement connection : connections) {
                context.connectionMap.put(connection, connectionNo);
                connectionNo++;
            }
            if (content instanceof ERTable) {
                final ERTable table = (ERTable) content;
                final List<ERColumn> columns = table.getColumns();
                for (final ERColumn column : columns) {
                    if (column instanceof NormalColumn) {
                        context.columnMap.put(column, columnNo);
                        columnNo++;
                    }
                }
                for (final ComplexUniqueKey complexUniqueKey : table.getComplexUniqueKeyList()) {
                    context.complexUniqueKeyMap.put(complexUniqueKey, complexUniqueKeyNo);
                    complexUniqueKeyNo++;
                }
            }
        }
    }

    private void setupWord(DiagramContents diagramContents, final PersistentContext context) {
        int wordNo = 1;
        for (final Word word : diagramContents.getDictionary().getWordList()) {
            context.wordMap.put(word, wordNo);
            wordNo++;
        }
    }

    private void setupTablespace(DiagramContents diagramContents, final PersistentContext context) {
        int tablespaceNo = 1;
        for (final Tablespace tablespace : diagramContents.getTablespaceSet()) {
            context.tablespaceMap.put(tablespace, tablespaceNo);
            tablespaceNo++;
        }
    }

    private void setupEnvironment(DiagramContents diagramContents, final PersistentContext context) {
        int environmentNo = 1;
        for (final Environment environment : diagramContents.getSettings().getEnvironmentSetting().getEnvironments()) {
            context.environmentMap.put(environment, environmentNo);
            environmentNo++;
        }
    }

    private void setupVirtualModel(DiagramContents diagramContents, final PersistentContext context) {
        int virtualModelNo = 1;
        for (final ERModel model : diagramContents.getModelSet()) {
            context.ermodelMap.put(model, virtualModelNo);
            virtualModelNo++;
        }
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
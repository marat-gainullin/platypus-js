/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query;

import com.eas.client.ClientConstants;
import com.eas.client.MetadataCache;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.StoredQueryFactory;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.ModelEditingListener;
import com.eas.client.model.QueryDocument;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.client.model.Relation;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.store.QueryDocument2XmlDom;
import com.eas.client.model.store.QueryModel2XmlDom;
import com.eas.client.model.store.XmlDom2QueryModel;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.query.editing.DocumentTextCompiler;
import com.eas.designer.application.query.lexer.SqlErrorAnnotation;
import com.eas.designer.application.query.lexer.SqlLanguageHierarchy;
import com.eas.designer.application.query.nodes.QueryNodeChildren;
import com.eas.designer.application.query.nodes.QueryRootNode;
import com.eas.designer.application.query.nodes.QueryRootNodePropertiesUndoRecorder;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import com.eas.script.JsDoc;
import com.eas.util.CollectionListener;
import com.eas.util.IdGenerator;
import com.eas.util.ListenerRegistration;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import javax.sql.DataSource;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.TablesFinder;
import net.sf.jsqlparser.TablesFinder.TO_CASE;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.Annotation;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;

public class PlatypusQueryDataObject extends PlatypusDataObject {

    public void setQueryFlags(boolean aPublicQuery, boolean aProcedure, boolean aReadonly) {
        publicQuery = aPublicQuery;
        procedure = aProcedure;
        readonly = aReadonly;
        publicChanged(!publicQuery, publicQuery);
        procedureChanged(!procedure, procedure);
        readonlyChanged(!readonly, readonly);
    }

    protected class QueryModelModifiedObserver implements ModelEditingListener<QueryEntity>, PropertyChangeListener, CollectionListener<Fields, Field> {

        @Override
        public void entityAdded(QueryEntity e) {
            setModelModified(true);
            e.getChangeSupport().addPropertyChangeListener(this);
        }

        @Override
        public void entityRemoved(QueryEntity e) {
            setModelModified(true);
            e.getChangeSupport().removePropertyChangeListener(this);
        }

        @Override
        public void relationAdded(Relation<QueryEntity> rel) {
            setModelModified(true);
            rel.getChangeSupport().addPropertyChangeListener(this);
        }

        @Override
        public void relationRemoved(Relation<QueryEntity> rel) {
            setModelModified(true);
            rel.getChangeSupport().removePropertyChangeListener(this);
        }

        @Override
        public void entityIndexesChanged(QueryEntity e) {
            setModelModified(true);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            setModelModified(true);
        }

        @Override
        public void added(Fields c, Field v) {
            setModelModified(true);
            v.getChangeSupport().addPropertyChangeListener(this);
        }

        @Override
        public void added(Fields c, Collection<Field> clctn) {
            setModelModified(true);
            clctn.stream().forEach((v) -> {
                v.getChangeSupport().addPropertyChangeListener(this);
            });
        }

        @Override
        public void removed(Fields c, Field v) {
            setModelModified(true);
            v.getChangeSupport().removePropertyChangeListener(this);
        }

        @Override
        public void removed(Fields c, Collection<Field> clctn) {
            setModelModified(true);
            clctn.stream().forEach((v) -> {
                v.getChangeSupport().removePropertyChangeListener(this);
            });
        }

        @Override
        public void reodered(Fields c) {
            setModelModified(true);
        }

        @Override
        public void cleared(Fields c) {
            setModelModified(true);
        }

    }

    // reflectioned properties
    public static final String PUBLIC_PROP_NAME = "public";
    public static final String PROCEDURE_PROP_NAME = "procedure";
    public static final String READONLY_PROP_NAME = "readonly";
    public static final String CONN_PROP_NAME = "datasourceName";
    //
    public static final String DATAOBJECT_DOC_PROPERTY = "dataObject";
    public static final String OUTPUT_FIELDS = "outputFields";
    public static final String STATEMENT_PROP_NAME = "statement";
    public static final String STATEMENT_ERROR_PROP_NAME = "statementError";
    protected transient Entry modelEntry;
    protected transient Entry outEntry;
    protected transient Entry dialectEntry;
    // Read / Write data
    protected String sqlText;
    protected String dialectText;
    protected String datasourceName;
    protected boolean publicQuery;
    protected boolean procedure;
    protected boolean readonly;
    protected QueryModel model;
    protected List<StoredFieldMetadata> outputFieldsHints;
    // Generated data
    protected transient ModelNode<QueryEntity, QueryModel> modelNode;
    protected transient Statement statement;
    protected transient Statement commitedStatement;
    protected transient ParseException statementError;
    protected transient Annotation statementAnnotation;
    protected transient Fields outputFields;
    protected transient NbEditorDocument sqlTextDocument;
    protected transient NbEditorDocument sqlFullTextDocument;
    // runtime 
    protected transient QueryModelModifiedObserver modelChangesObserver = new QueryModelModifiedObserver();
    protected transient boolean modelModified;
    protected transient boolean sqlModified;
    protected transient boolean fullSqlModified;
    protected transient boolean outputFieldsHintsModified;
    protected transient ListenerRegistration queriesReg;
    protected transient PlatypusProject.QueriesChangeListener modelValidator = () -> {
        setModelValid(false);
        startModelValidating();
    };

    public PlatypusQueryDataObject(FileObject aSqlFile, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(aSqlFile, loader);
        FileObject aDialectFile = FileUtil.findBrother(aSqlFile, PlatypusFiles.DIALECT_EXTENSION);
        FileObject aModelFile = FileUtil.findBrother(aSqlFile, PlatypusFiles.MODEL_EXTENSION);
        FileObject anOutFile = FileUtil.findBrother(aSqlFile, PlatypusFiles.OUT_EXTENSION);
        modelEntry = registerEntry(aModelFile);
        dialectEntry = registerEntry(aDialectFile);
        outEntry = registerEntry(anOutFile);
        CookieSet cookies = getCookieSet();
        cookies.add(new PlatypusQuerySupport(this));
        // dirty hack
        // NetBeans marks only primary file with appropriate script engine.
        // Though, parameters aren't inserted into secondary files in data objects.
        aModelFile.setAttribute(javax.script.ScriptEngine.class.getName(), "freemarker");
        // end of dirty hack
        PlatypusProject project = getProject();
        if (project != null) {
            queriesReg = project.addQueriesChangeListener(modelValidator);
        }
    }

    @Override
    protected Node createNodeDelegate() {
        Node node = super.createNodeDelegate();
        if (node instanceof AbstractNode) {
            ((AbstractNode) node).setIconBaseWithExtension(PlatypusQueryDataObject.class.getPackage().getName().replace('.', '/') + "/query.png");
        }
        return node;
    }

    @Override
    protected void dispose() {
        getLookup().lookup(PlatypusQuerySupport.class).closeAllViews();
        super.dispose();
    }

    protected void readQuery() throws Exception {
        sqlText = getPrimaryFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        if (dialectEntry.getFile() != getPrimaryFile()) {
            dialectText = dialectEntry.getFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        }
        Document modelDoc = Source2XmlDom.transform(modelEntry.getFile().asText(PlatypusUtils.COMMON_ENCODING_NAME));
        model = XmlDom2QueryModel.transform(getBasesProxy(), getProject().getQueries(), modelDoc);

        model.addEditingListener(modelChangesObserver);
        model.getParametersEntity().getChangeSupport().addPropertyChangeListener(modelChangesObserver);
        model.getParametersEntity().getFields().getCollectionSupport().addListener(modelChangesObserver);
        model.getParametersEntity().getFields().toCollection().stream().forEach((param) -> {
            param.getChangeSupport().addPropertyChangeListener(modelChangesObserver);
        });
        model.getEntities().values().stream().forEach((entity) -> {
            entity.getChangeSupport().addPropertyChangeListener(modelChangesObserver);
        });
        model.getRelations().stream().forEach((rel) -> {
            rel.getChangeSupport().addPropertyChangeListener(modelChangesObserver);
        });

        datasourceName = model.getDatasourceName();
        publicQuery = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.PUBLIC_TAG) != null;
        procedure = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.PROCEDURE_TAG) != null;
        readonly = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.READONLY_TAG) != null;

        //TODO set output fields in query document
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            statement = parserManager.parse(new StringReader(sqlText));
            statementError = null;
            commitedStatement = statement;
        } catch (Exception ex) {
            statement = commitedStatement = null;
            if ((ex instanceof JSQLParserException) && ((JSQLParserException) ex).getCause() instanceof ParseException) {
                statementError = (ParseException) ex.getCause();
            }
        }
        UndoRedo.Manager undoReciever = getLookup().lookup(PlatypusQuerySupport.class).getUndo();
        EditorKit editorKit = CloneableEditorSupport.getEditorKit(SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);

        sqlTextDocument = (NbEditorDocument) editorKit.createDefaultDocument();
        sqlTextDocument.putProperty(NbEditorDocument.MIME_TYPE_PROP, SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);
        sqlTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, this);
        sqlTextDocument.insertString(0, sqlText, null);
        sqlTextDocument.addUndoableEditListener(undoReciever);
        sqlTextDocument.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                sqlModified = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sqlModified = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sqlModified = true;
            }
        });

        sqlFullTextDocument = (NbEditorDocument) editorKit.createDefaultDocument();
        sqlFullTextDocument.putProperty(NbEditorDocument.MIME_TYPE_PROP, SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);
        sqlFullTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, this);
        sqlFullTextDocument.insertString(0, dialectText, null);
        sqlFullTextDocument.addUndoableEditListener(undoReciever);
        sqlFullTextDocument.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                fullSqlModified = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fullSqlModified = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fullSqlModified = true;
            }
        });

        sqlTextDocument.addDocumentListener(new DocumentTextCompiler(this));

        String hintsContent = outEntry.getFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        if (hintsContent != null && !hintsContent.isEmpty()) {
            Document fieldsHintsDoc = Source2XmlDom.transform(hintsContent);
            if (fieldsHintsDoc != null) {
                outputFieldsHints = QueryDocument.parseFieldsHintsTag(fieldsHintsDoc.getDocumentElement());
            } else {
                outputFieldsHints = new ArrayList<>();
            }
        } else {
            outputFieldsHints = new ArrayList<>();
        }
        refreshOutputFields();
        modelNode = new QueryRootNode(new QueryNodeChildren(this, model, undoReciever, getLookup()), this);
        modelNode.addPropertyChangeListener(new QueryRootNodePropertiesUndoRecorder(undoReciever));
    }

    protected void checkQueryRead() throws Exception {
        if (model == null || sqlText == null) {
            readQuery();
        }
    }

    public QueryModel getModel() throws Exception {
        checkQueryRead();
        return model;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String aValue) throws Exception {
        String oldValue = datasourceName;
        if (aValue != null && aValue.isEmpty()) {
            aValue = null;
        }
        datasourceName = aValue;
        if (model != null) {
            model.setDatasourceName(aValue);
        }
        setModelModified(true);
        firePropertyChange(CONN_PROP_NAME, oldValue, aValue);
    }

    public boolean isPublic() {
        return publicQuery;
    }

    public void setPublic(boolean aValue) {
        boolean oldValue = publicQuery;
        publicQuery = aValue;
        if (oldValue != publicQuery) {
            publicChanged(oldValue, aValue);
            try {
                String content = sqlTextDocument.getText(0, sqlTextDocument.getLength());
                String newContent = PlatypusFilesSupport.replaceAnnotationValue(content, JsDoc.Tag.PUBLIC_TAG, publicQuery ? "" : null);
                sqlTextDocument.replace(0, sqlTextDocument.getLength(), newContent, null);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public void publicChanged(boolean oldValue, boolean aValue) {
        firePropertyChange(PUBLIC_PROP_NAME, oldValue, aValue);
    }

    public boolean isProcedure() {
        return procedure;
    }

    public void setProcedure(boolean aValue) {
        boolean oldValue = procedure;
        procedure = aValue;
        if (oldValue != procedure) {
            procedureChanged(oldValue, aValue);
            try {
                String content = sqlTextDocument.getText(0, sqlTextDocument.getLength());
                String newContent = PlatypusFilesSupport.replaceAnnotationValue(content, JsDoc.Tag.PROCEDURE_TAG, procedure ? "" : null);
                sqlTextDocument.replace(0, sqlTextDocument.getLength(), newContent, null);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public void procedureChanged(boolean aOldValue, boolean aNewValue) {
        firePropertyChange(PROCEDURE_PROP_NAME, aOldValue, aNewValue);
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean aValue) {
        boolean oldValue = readonly;
        readonly = aValue;
        if (oldValue != readonly) {
            readonlyChanged(oldValue, aValue);
            try {
                String content = sqlTextDocument.getText(0, sqlTextDocument.getLength());
                String newContent = PlatypusFilesSupport.replaceAnnotationValue(content, JsDoc.Tag.READONLY_TAG, readonly ? "" : null);
                sqlTextDocument.replace(0, sqlTextDocument.getLength(), newContent, null);
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    public void readonlyChanged(boolean aOldValue, boolean aNewValue) {
        firePropertyChange(READONLY_PROP_NAME, aOldValue, aNewValue);
    }

    public boolean getOutputFieldsHintsModified() {
        return outputFieldsHintsModified;
    }

    public void setOutputFieldsHintsModified(boolean aValue) {
        outputFieldsHintsModified = aValue;
    }

    public boolean isModelModified() {
        return modelModified;
    }

    public void setModelModified(boolean aValue) {
        modelModified = aValue;
        // Undoable edits will mark whole dataobject as modified.
        // Only specific modified status is setted here.
        // Model's modified staus is the case.
    }

    public ModelNode<QueryEntity, QueryModel> getModelNode() throws Exception {
        checkQueryRead();
        return modelNode;
    }

    public Statement getStatement() throws Exception {
        checkQueryRead();
        return statement;
    }

    public void setStatement(Statement aValue) {
        Statement oldValue = statement;
        statement = aValue;
        firePropertyChange(STATEMENT_PROP_NAME, oldValue, statement);
    }

    public ParseException getStatementError() {
        return statementError;
    }

    public void setStatementError(ParseException aValue) throws BadLocationException {
        if (statementError != aValue) {
            ParseException oldValue = statementError;
            statementError = aValue;
            firePropertyChange(STATEMENT_ERROR_PROP_NAME, oldValue, statementError);
            setStatementErrorAnnotation(statementError);
        }
    }

    protected void setStatementErrorAnnotation(ParseException aValue) throws BadLocationException {
        if (statementAnnotation != null) {
            try {
                sqlTextDocument.removeAnnotation(statementAnnotation);
            } catch (IllegalStateException ex) {
                // no op
                ex = null;
            }
            statementAnnotation = null;
        }
        if (aValue != null && sqlTextDocument.getLength() != 0) {
            statementAnnotation = new SqlErrorAnnotation(aValue);
            int start = 0;
            int end = 0;
            if (aValue.currentToken != null) {
                start = NbDocument.findLineOffset(sqlTextDocument, aValue.currentToken.beginLine > 0 ? aValue.currentToken.beginLine - 1 : 0) + aValue.currentToken.beginColumn > 0 ? aValue.currentToken.beginColumn - 1 : 0;
                end = NbDocument.findLineOffset(sqlTextDocument, aValue.currentToken.endLine > 0 ? aValue.currentToken.endLine - 1 : 0) + aValue.currentToken.endColumn > 0 ? aValue.currentToken.endColumn : 0;
            }
            sqlTextDocument.addAnnotation(sqlTextDocument.createPosition(start), end - start, statementAnnotation);
        }
    }

    public Fields getOutputFields() throws Exception {
        checkQueryRead();
        return outputFields;
    }

    public List<StoredFieldMetadata> getOutputFieldsHints() throws Exception {
        checkQueryRead();
        return outputFieldsHints;
    }

    public javax.swing.text.Document getSqlTextDocument() throws Exception {
        checkQueryRead();
        return sqlTextDocument;
    }

    public javax.swing.text.Document getSqlFullTextDocument() throws Exception {
        checkQueryRead();
        return sqlFullTextDocument;
    }

    @Override
    protected void validateModel() throws Exception {
        if (getModel() != null) {
            getModel().validate();
        }
    }

    public void shrink() throws IOException {
        if (modelNode != null) {
            modelNode.destroy();
        }
        modelNode = null;
        statement = null;
        commitedStatement = null;
        if (model != null) {
            model.getParametersEntity().getFields().toCollection().stream().forEach((param) -> {
                param.getChangeSupport().removePropertyChangeListener(modelChangesObserver);
            });
        }
        model = null;
        modelModified = false;
        if (sqlTextDocument != null) {
            sqlTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, null);
        }
        sqlTextDocument = null;
        sqlModified = false;
        if (sqlFullTextDocument != null) {
            sqlFullTextDocument.putProperty(DATAOBJECT_DOC_PROPERTY, null);
        }
        sqlFullTextDocument = null;
        fullSqlModified = false;
        outputFields = null;
        outputFieldsHintsModified = false;
    }

    public void saveQuery() throws Exception {
        boolean contentModified = sqlModified || fullSqlModified || modelModified || outputFieldsHintsModified;
        if (sqlModified) {
            sqlText = sqlTextDocument.getText(0, sqlTextDocument.getLength());
            write2File(getPrimaryFile(), sqlText);
            sqlModified = false;
        }
        if (fullSqlModified) {
            FileObject dialectFileO = dialectEntry.getFile();
            if (dialectFileO == getPrimaryFile()) {
                String path = getPrimaryFile().getPath();
                File dialectFile = new File(path.substring(0, path.length() - getPrimaryFile().getExt().length()) + PlatypusFiles.DIALECT_EXTENSION);
                if (dialectFile.createNewFile()) {
                    dialectFileO = FileUtil.toFileObject(dialectFile);
                    dialectEntry = registerEntry(dialectFileO);
                }
            }
            if (getPrimaryFile() != dialectFileO) {
                dialectText = sqlFullTextDocument.getText(0, sqlFullTextDocument.getLength());
                write2File(dialectFileO, dialectText);
            }
            fullSqlModified = false;
        }
        if (modelModified) {
            Document modelDocument = QueryModel2XmlDom.transform(model);
            write2File(modelEntry.getFile(), XmlDom2String.transform(modelDocument));
            modelModified = false;
        }
        if (outputFieldsHintsModified) {
            Document outHintsDocument = QueryDocument2XmlDom.transformOutHints(outputFieldsHints, outputFields);
            write2File(outEntry.getFile(), XmlDom2String.transform(outHintsDocument));
            outputFieldsHintsModified = false;
        }
        if (contentModified) {
            PlatypusProject project = getProject();
            String queryName = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.NAME_TAG);
            if (project != null && project.getQueries().getCachedQuery(queryName) != null) {
                if (queriesReg != null) {
                    queriesReg.remove();
                    queriesReg = null;
                }
                project.fireQueriesChanged();
                queriesReg = project.addQueriesChangeListener(modelValidator);
            }
        }
    }

    private void write2File(FileObject aFile, String aContent) throws Exception {
        try (OutputStream out = aFile.getOutputStream()) {
            byte[] data = aContent.getBytes(PlatypusUtils.COMMON_ENCODING_NAME);
            out.write(data);
            out.flush();
        }
    }

    @Override
    protected void handleDelete() throws IOException {
        if (queriesReg != null) {
            queriesReg.remove();
            queriesReg = null;
        }
        if (sqlText == null) {
            sqlText = getPrimaryFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        }
        String queryName = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.NAME_TAG);
        PlatypusProject project = getProject();
        super.handleDelete();
        if (project != null && project.getQueries().getCachedQuery(queryName) != null) {
            project.fireQueriesChanged();
        }
    }

    public Statement getCommitedStatement() {
        return commitedStatement;
    }

    public void commitStatement() throws Exception {
        validateStatement();
        commitedStatement = statement;
        sqlText = sqlTextDocument.getText(0, sqlTextDocument.getLength());
        boolean oldPublicQuery = publicQuery;
        publicQuery = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.PUBLIC_TAG) != null;
        if (oldPublicQuery != publicQuery) {
            firePropertyChange(PUBLIC_PROP_NAME, oldPublicQuery, publicQuery);
        }
        boolean oldProcedure = procedure;
        procedure = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.PROCEDURE_TAG) != null;
        if (oldProcedure != procedure) {
            firePropertyChange(PROCEDURE_PROP_NAME, oldProcedure, procedure);
        }
        boolean oldReadonly = readonly;
        readonly = PlatypusFilesSupport.getAnnotationValue(sqlText, JsDoc.Tag.READONLY_TAG) != null;
        if (oldReadonly != readonly) {
            firePropertyChange(READONLY_PROP_NAME, oldReadonly, readonly);
        }
        refreshOutputFields();
    }

    public Set<String> achieveSchemas() throws Exception {
        Set<String> schemas = new HashSet<>();
        DatabasesClient basesProxy = getBasesProxy();
        if (basesProxy != null) {
            DataSource ds = basesProxy.obtainDataSource(datasourceName);
            try (Connection conn = ds.getConnection()) {
                try (ResultSet r = conn.getMetaData().getSchemas()) {
                    ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                    int schemaColIndex = idxs.find(ClientConstants.JDBCCOLS_TABLE_SCHEM);
                    while (r.next()) {
                        String schemaName = r.getString(schemaColIndex);
                        schemas.add(schemaName);
                    }
                }
            }
        }
        return schemas;
    }

    /*
    public Map<String, Fields> achieveTables(final String aSchema) throws Exception {
        final Map<String, Fields> tables = new HashMap<>();
        DatabasesClient basesProxy = getBasesProxy();
        if (basesProxy != null) {
            MetadataCache mdCache = basesProxy.getMetadataCache(datasourceName);
            final String schema = aSchema != null && aSchema.equalsIgnoreCase(mdCache.getDatasourceSchema()) ? null : aSchema;
            if (schema != null) {
                mdCache.fillTablesCacheBySchema(schema, true);
            }
            SqlDriver driver = mdCache.getDatasourceSqlDriver();
            String sql4Tables = driver.getSql4TablesEnumeration(schema != null ? schema : mdCache.getDatasourceSchema());
            SqlCompiledQuery tablesQuery = new SqlCompiledQuery(basesProxy, datasourceName, sql4Tables);
            tablesQuery.executeQuery((ResultSet r) -> {
                ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                int tableColIndex = idxs.find(ClientConstants.JDBCCOLS_TABLE_NAME);
                while (r.next()) {
                    String cachedTableName = (schema != null ? schema + "." : "") + r.getString(tableColIndex);
                    Fields fields = mdCache.getTableMetadata(cachedTableName);
                    //tables.put(cachedTableName.toLowerCase(), fields);
                    tables.put(cachedTableName, fields);
                }
                return null;
            }, null, null, null);
        }
        return tables;
    }
     */
    public MetadataCache getMetadataCache() throws Exception {
        DatabasesClient basesProxy = getBasesProxy();
        return basesProxy != null ? basesProxy.getMetadataCache(datasourceName) : null;
    }

    private void validateStatement() throws Exception {
        MetadataCache mdCache = getMetadataCache();
        if (mdCache != null) {
            Map<String, Table> tables = TablesFinder.getTablesMap(TO_CASE.LOWER, statement, true);
            for (Table table : tables.values()) {
                String schema = table.getSchemaName();
                if (schema != null && schema.equalsIgnoreCase(mdCache.getDatasourceSchema())) {
                    schema = null;
                }
                String cachedTablyName = (schema != null ? schema + "." : "") + table.getName();
                if (!validateTablyName(cachedTablyName)) {
                    throw new AbsentTableParseException(NbBundle.getMessage(PlatypusQueryDataObject.class, "absentTable", cachedTablyName));
                }
            }
        }
    }

    private boolean validateTablyName(String aTablyName) throws Exception {
        if (aTablyName.startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)) {
            return existsAppQuery(aTablyName.substring(ClientConstants.STORED_QUERY_REF_PREFIX.length()));
        } else {
            return validateTableName(aTablyName) || existsAppQuery(aTablyName);
        }
    }

    /**
     * Refreshs output fields from sql text document.
     */
    public void refreshOutputFields() {
        Fields oldValue = outputFields;
        DatabasesClient basesProxy = getBasesProxy();
        String s = null;
        try {
            s = sqlTextDocument.getText(0, sqlTextDocument.getLength());
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().log(ex.getMessage());
        }
        if (statementError == null && basesProxy != null && s != null && !s.isEmpty()) {
            try {
                StoredQueryFactory factory = new StoredQueryFactory(basesProxy, getProject().getQueries(), getProject().getIndexer());
                factory.setAliasesToTableNames(true);
                SqlQuery outQuery = new SqlQuery(basesProxy, datasourceName, s);
                outQuery.setEntityName(IdGenerator.genStringId());
                factory.putTableFieldsMetadata(outQuery);
                outputFields = outQuery.getFields();
            } catch (Exception ex) {
                ErrorManager.getDefault().log(ex.getMessage());
                StatusDisplayer.getDefault().setStatusText(ex.getMessage()); // NOI18N                    
                outputFields = null;
            }
        } else {
            outputFields = null;
        }
        if ((oldValue == null && outputFields != null) || (oldValue != null && !oldValue.equals(outputFields))) {
            outputFieldsChanged(oldValue, outputFields);
        }
    }

    public void outputFieldsChanged(Fields aOldValue, Fields aNewValue) {
        firePropertyChange(OUTPUT_FIELDS, aOldValue, aNewValue);
    }

    public boolean existsAppQuery(String aStoredQueryName) {
        PlatypusProject project = getProject();
        if (project != null) {
            try {
                FileObject sqlFile = IndexerQuery.appElementId2File(project, aStoredQueryName);
                return sqlFile != null && sqlFile.isValid();
            } catch (Exception ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected DataObject handleCopy(DataFolder df) throws IOException {
        DataObject copied = super.handleCopy(df);
        Project projectOfThis = FileOwnerQuery.getOwner(getPrimaryFile());
        Project projectOfCopied = FileOwnerQuery.getOwner(copied.getPrimaryFile());
        if (projectOfThis == projectOfCopied) {
            String content = copied.getPrimaryFile().asText(PlatypusFiles.DEFAULT_ENCODING);
            String oldPlatypusId = PlatypusFilesSupport.getAnnotationValue(content, JsDoc.Tag.NAME_TAG);
            String newPlatypusId = NewApplicationElementWizardIterator.getNewValidAppElementName(getProject(), oldPlatypusId);
            content = PlatypusFilesSupport.replaceAnnotationValue(content, JsDoc.Tag.NAME_TAG, newPlatypusId);
            try (OutputStream os = copied.getPrimaryFile().getOutputStream()) {
                os.write(content.getBytes(PlatypusFiles.DEFAULT_ENCODING));
                os.flush();
            }
        }
        return copied;
    }

    public boolean validateTableName(String aTablyName) throws Exception {
        MetadataCache mdCache = getMetadataCache();
        if (mdCache != null) {
            boolean containsTableMetadata;
            try {
                containsTableMetadata = mdCache.getTableMetadata(aTablyName) != null;
            } catch (Exception ex) {
                containsTableMetadata = false;
            }
            return containsTableMetadata;
        } else {
            return false;
        }
    }
}

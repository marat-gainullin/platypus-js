/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.client.ClientConstants;
import com.eas.client.MetadataCache;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.designer.application.indexer.AppElementInfo;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.lexer.LexSqlTokenId;
import com.eas.designer.application.query.lexer.SqlLanguageHierarchy;
import com.eas.designer.application.query.lexer.SqlTokenId;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import net.sf.jsqlparser.TablesFinder;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class SqlCompletionProvider implements CompletionProvider {

    public static final int UNKNOWN_ZONE = 0; //
    public static final int SELECT_ZONE = 1; // aliases, table names without aliases and column with dot
    public static final int FROM_ZONE = 3; // tables names
    public static final int WHERE_ZONE = 5; //  aliases, tables names
    public static final int HAVING_ZONE = 7; // aliases, table names without aliases
    public static final int GROUP_ZONE = 8;
    public static final int INSERT_INTO_ZONE = 9;
    public static final int INSERT_FIELDS_ZONE = 10;
    public static final int INSERT_VALUES_ZONE = 11;
    public static final int INSERT_VALUES_LIST_ZONE = 12;
    public static final int UPDATE_ZONE = 13;
    public static final int SET_ZONE = 14;
    public static final int KEYWORD_ZONE = 15;// keywords names
    protected int addedCompletionItems = 0;

    public void addCompletionItem(CompletionPoint point, SqlCompletionItem item, CompletionResultSet resultSet) {
        if (point.filter == null || item.getText().toLowerCase().startsWith(point.filter.toLowerCase())) {
            resultSet.addItem(item);
            addedCompletionItems++;
        }
    }

    public void fillCompletionByStoredQueries(PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet) throws DataObjectNotFoundException {
        Collection<AppElementInfo> appElements = IndexerQuery.appElementsByPrefix(dataObject.getProject(), "");
        if (appElements != null) {
            for (AppElementInfo appInfo : appElements) {
                if (appInfo != null && appInfo.primaryFileObject != null) {
                    DataObject fdo = DataObject.find(appInfo.primaryFileObject);
                    if (fdo instanceof PlatypusQueryDataObject && fdo != dataObject) {
                        SqlCompletionItem item = new StoredQuerySqlCompletionItem(appInfo.appElementId, dataObject, (PlatypusQueryDataObject) fdo, point.startOffset, point.endOffset);
                        addCompletionItem(point, item, resultSet);
                    }
                }
            }
        }
    }

    public void fillCompletionByTablesBySchema(String aSchema, PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        final String schema = aSchema != null && aSchema.equalsIgnoreCase(dataObject.getMetadataCache().getDatasourceSchema()) ? null : aSchema;
        Map<String, Fields> tablesFields = dataObject.getMetadataCache().lookupFieldsCache(schema);
        fillCompletionByTables(tablesFields, dataObject, point, resultSet);
    }

    public void fillCompletionByTables(Map<String, Fields> aTables, PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        if (aTables != null) {
            aTables.values().stream().map((aTableFields) -> new TableSqlCompletionItem(dataObject, aTableFields, point.startOffset, point.endOffset)).forEach((item) -> {
                addCompletionItem(point, item, resultSet);
            });
        }
    }

    public void fillCompletionByFields(Fields aFields, PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        if (aFields != null) {
            if (!aFields.isEmpty()) {
                aFields.toCollection().stream().map((field) -> new FieldSqlCompletionItem(field, point.startOffset, point.endOffset)).forEach((item) -> {
                    addCompletionItem(point, item, resultSet);
                });
            } else {
                SqlCompletionItem item = new EmptySqlCompletionItem(NbBundle.getMessage(SqlCompletionProvider.class, "NoFields"));
                addCompletionItem(point, item, resultSet);
            }
        }
    }

    public void fillCompletionByParameters(Parameters aParams, PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        if (aParams != null) {
            if (!aParams.isEmpty()) {
                for (Field field : aParams.toCollection()) {
                    SqlCompletionItem item = new ParameterSqlCompletionItem((Parameter) field, point.startOffset, point.endOffset);
                    addCompletionItem(point, item, resultSet);
                }
            } else {
                SqlCompletionItem item = new EmptySqlCompletionItem(NbBundle.getMessage(SqlCompletionProvider.class, "NoParameters"));
                addCompletionItem(point, item, resultSet);
            }
        }
    }

    public void fillCompletionInsertFieldsZone(CompletionPoint point, PlatypusQueryDataObject dataObject, CompletionResultSet resultSet) throws Exception {
        if (dataObject.getStatement() instanceof Insert) {
            DatabasesClient basesProxy = dataObject.getBasesProxy();
            if (basesProxy != null) {
                Insert iStatement = (Insert) dataObject.getStatement();
                MetadataCache mdCache = basesProxy.getMetadataCache(dataObject.getDatasourceName());
                String schema = iStatement.getTable().getSchemaName();
                String defaultSchema = mdCache.getDatasourceSchema();
                Fields fields = mdCache.getTableMetadata(defaultSchema.equalsIgnoreCase(schema) ? iStatement.getTable().getName() : iStatement.getTable().getWholeTableName());
                fillCompletionByFields(fields, dataObject, point, resultSet);
            }
        }
    }

    public void fillCompletionUpdateFieldsZone(CompletionPoint point, PlatypusQueryDataObject dataObject, CompletionResultSet resultSet) throws Exception {
        if (dataObject.getStatement() instanceof Update) {
            DatabasesClient basesProxy = dataObject.getBasesProxy();
            if (basesProxy != null) {
                Update uStatement = (Update) dataObject.getStatement();
                MetadataCache mdCache = basesProxy.getMetadataCache(dataObject.getDatasourceName());
                String schema = uStatement.getTable().getSchemaName();
                String defaultSchema = mdCache.getDatasourceSchema();
                Fields fields = mdCache.getTableMetadata(defaultSchema.equalsIgnoreCase(schema) ? uStatement.getTable().getName() : uStatement.getTable().getWholeTableName());
                fillCompletionByFields(fields, dataObject, point, resultSet);
            }
        }
    }

    public void fillCompletionParametersZone(CompletionPoint point, PlatypusQueryDataObject dataObject, CompletionResultSet resultSet) throws Exception {
        fillCompletionByParameters(dataObject.getModel().getParameters(), dataObject, point, resultSet);
    }

    public void fillCompletionFromZone(CompletionPoint point, PlatypusQueryDataObject dataObject, CompletionResultSet resultSet) throws Exception {
        if (point.atDot) {
            if (point.prevContext != null) {
                if (point.prevPrevContext == null) {
                    Set<String> schemas = dataObject.achieveSchemas();
                    String schema = findName(point.prevContext, schemas);
                    if (schema != null) {
                        fillCompletionByTablesBySchema(schema, dataObject, point, resultSet);
                    }
                }
            }
        } else if (point.filter != null && point.filter.startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)) {
            fillCompletionByStoredQueries(dataObject, point, resultSet);
        } else {
            fillCompletionByTablesBySchema(null, dataObject, point, resultSet);
        }
    }

    public void fillCompletionSelectZone(PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        if (dataObject.getStatement() instanceof Select) {
            DatabasesClient basesProxy = dataObject.getBasesProxy();
            if (basesProxy != null) {
                MetadataCache mdCache = basesProxy.getMetadataCache(dataObject.getDatasourceName());
                if (point.atDot) {
                    if (point.prevContext != null) {
                        Set<String> schemas = dataObject.achieveSchemas();
                        Map<String, Table> tables = TablesFinder.getTablesMap(null, dataObject.getStatement(), true);
                        if (point.prevPrevContext != null) {
                            // Two times qualified
                            String schema = findName(point.prevPrevContext, schemas);
                            if (schema != null) {
                                String defaultSchema = mdCache.getDatasourceSchema();
                                Fields fields = mdCache.getTableMetadata(defaultSchema.equalsIgnoreCase(schema) ? point.prevContext : schema + "." + point.prevContext);
                                fillCompletionByFields(fields, dataObject, point, resultSet);
                            }
                        } else {
                            // Once qualified
                            String schema = findName(point.prevContext, schemas);
                            if (schema == null) {
                                String aliasOrTable = findName(point.prevContext, tables.keySet());
                                if (aliasOrTable != null) {
                                    // [Alias's] tables's fields
                                    Table table = tables.get(aliasOrTable);

                                    String parserTableName = table.getWholeTableName();
                                    Fields fields = null;
                                    if (parserTableName.startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)) {
                                        SqlQuery q = basesProxy.getQueries().getQuery(parserTableName.substring(1), null, null, null);
                                        if (q != null) {
                                            fields = q.getFields();
                                        }
                                    } else {
                                        fields = mdCache.getTableMetadata(table.getWholeTableName());
                                    }
                                    if (fields != null) {
                                        fillCompletionByFields(fields, dataObject, point, resultSet);
                                    }
                                } else {
                                    // fallback to default schema's tables
                                    Fields fields = mdCache.getTableMetadata(point.prevContext);
                                    fillCompletionByFields(fields, dataObject, point, resultSet);
                                }
                            } else {
                                // Tables list (not aliased)
                                fillCompletionByTablesBySchema(schema, dataObject, point, resultSet);
                            }
                        }
                    }
                } else {
                    Map<String, Table> tables = TablesFinder.getTablesMap(null, dataObject.getStatement(), true);
                    for (String alias : tables.keySet()) {
                        String parserTableName = tables.get(alias).getWholeTableName();
                        if (parserTableName.startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)) {
                            FileObject subjectFO = IndexerQuery.appElementId2File(dataObject.getProject(), parserTableName.substring(1));
                            if (subjectFO != null) {
                                DataObject subjectDO = DataObject.find(subjectFO);
                                if (subjectDO instanceof PlatypusQueryDataObject) {
                                    SqlCompletionItem item = new StoredQuerySqlCompletionItem(dataObject, (PlatypusQueryDataObject) subjectDO, alias, point.startOffset, point.endOffset);
                                    addCompletionItem(point, item, resultSet);
                                }
                            }
                        } else {
                            Fields fields = mdCache.getTableMetadata(parserTableName);
                            SqlCompletionItem item = new TableSqlCompletionItem(dataObject, alias, fields, point.startOffset, point.endOffset);
                            addCompletionItem(point, item, resultSet);
                        }
                    }
                }
            }
        }
    }

    public void fillCompletionWhereZone(PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        DatabasesClient basesProxy = dataObject.getBasesProxy();
        if (basesProxy != null) {
            MetadataCache mdCache = basesProxy.getMetadataCache(dataObject.getDatasourceName());
            if (point.atDot) {
                if (point.prevContext != null) {
                    Set<String> schemas = dataObject.achieveSchemas();
                    Map<String, Table> tables = TablesFinder.getTablesMap(null, dataObject.getStatement(), true);
                    if (point.prevPrevContext != null) {
                        // Two times qualified
                        String schema = findName(point.prevPrevContext, schemas);
                        if (schema != null) {
                            String defaultSchema = mdCache.getDatasourceSchema();
                            Fields fields = mdCache.getTableMetadata(defaultSchema.equalsIgnoreCase(schema) ? point.prevContext : schema + "." + point.prevContext);
                            fillCompletionByFields(fields, dataObject, point, resultSet);
                        }
                    } else {
                        // Once qualified
                        String schema = findName(point.prevContext, schemas);
                        if (schema == null) {
                            String aliasOrTable = findName(point.prevContext, tables.keySet());
                            if (aliasOrTable != null) {
                                // [Alias's] tables's fields
                                Table table = tables.get(aliasOrTable);
                                String parserTableName = table.getWholeTableName();
                                Fields fields = null;
                                if (parserTableName.startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)) {
                                    SqlQuery q = basesProxy.getQueries().getQuery(parserTableName.substring(1), null, null, null);
                                    if (q != null) {
                                        fields = q.getFields();
                                    }
                                } else {
                                    fields = mdCache.getTableMetadata(table.getWholeTableName());
                                }
                                if (fields != null) {
                                    fillCompletionByFields(fields, dataObject, point, resultSet);
                                }
                            } else {
                                // fallback to default schema's tables
                                Fields fields = mdCache.getTableMetadata(point.prevContext);
                                fillCompletionByFields(fields, dataObject, point, resultSet);
                            }
                        } else {
                            // Tables list (not aliased)
                            fillCompletionByTablesBySchema(schema, dataObject, point, resultSet);
                        }
                    }
                }
            } else {
                Map<String, Table> tables = TablesFinder.getTablesMap(null, dataObject.getStatement(), true);
                for (String alias : tables.keySet()) {
                    String parserTableName = tables.get(alias).getWholeTableName();
                    if (parserTableName.startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)) {
                        FileObject subjectFO = IndexerQuery.appElementId2File(dataObject.getProject(), parserTableName.substring(1));
                        if (subjectFO != null) {
                            DataObject subjectDO = DataObject.find(subjectFO);
                            if (subjectDO instanceof PlatypusQueryDataObject) {
                                SqlCompletionItem item = new StoredQuerySqlCompletionItem(dataObject, (PlatypusQueryDataObject) subjectDO, alias, point.startOffset, point.endOffset);
                                addCompletionItem(point, item, resultSet);
                            }
                        }
                    } else {
                        Fields fields = mdCache.getTableMetadata(parserTableName);
                        SqlCompletionItem item = new TableSqlCompletionItem(dataObject, alias, fields, point.startOffset, point.endOffset);
                        addCompletionItem(point, item, resultSet);
                    }
                }
            }
        }
    }

    public void fillCompletionWithKeywords(CompletionPoint point, CompletionResultSet resultSet) {
        Collection<SqlTokenId> tokens = SqlLanguageHierarchy.checkTokens();
        for (SqlTokenId token : tokens) {
            if (SqlLanguageHierarchy.KEYWORD_CATEGORY_NAME.equals(token.primaryCategory())) {
                if (token.name().startsWith("K_")) {
                    SqlCompletionItem item = new KeywordSqlCompletionItem(token, point.startOffset, point.endOffset);
                    addCompletionItem(point, item, resultSet);
                }
            }
        }
    }

    public void fillCompletionWithBaseKeywords(CompletionPoint point, CompletionResultSet resultSet) {
        Collection<SqlTokenId> tokens = SqlLanguageHierarchy.checkTokens();
        for (SqlTokenId token : tokens) {
            if (SqlLanguageHierarchy.KEYWORD_CATEGORY_NAME.equals(token.primaryCategory())) {
                if (token.name().equalsIgnoreCase("K_SELECT")
                        || token.name().equalsIgnoreCase("K_INSERT")
                        || token.name().equalsIgnoreCase("K_UPDATE")
                        || token.name().equalsIgnoreCase("K_DELETE")) {
                    SqlCompletionItem item = new KeywordSqlCompletionItem(token, point.startOffset, point.endOffset);
                    addCompletionItem(point, item, resultSet);
                }
            }
        }
    }

    private void fillCompletionException(Exception ex, CompletionResultSet resultSet) {
        resultSet.addItem(new ExceptionSqlCompletionItem(ex));
    }

    protected class CompletionPoint {

        public int zone = UNKNOWN_ZONE;
        public String prevPrevContext = null;
        public String prevContext = null;
        public String filter = null;
        public boolean atDot;
        public int startOffset = -1;
        public int endOffset = -1;
    }

    public SqlCompletionProvider() {
        super();
    }

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType == CompletionProvider.COMPLETION_QUERY_TYPE
                || queryType == CompletionProvider.TOOLTIP_QUERY_TYPE
                || queryType == CompletionProvider.DOCUMENTATION_QUERY_TYPE) {
            return createCompletionTask(component);
        } else {
            return null;
        }
    }

    public CompletionTask createCompletionTask(JTextComponent component) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                try {
                    PlatypusQueryDataObject dataObject = (PlatypusQueryDataObject) doc.getProperty(PlatypusQueryDataObject.DATAOBJECT_DOC_PROPERTY);
                    if (dataObject == null || dataObject.getSqlFullTextDocument() == doc) {
                        resultSet.addItem(new DummySqlCompletionItem());
                    } else if (doc instanceof NbEditorDocument) {
                        CompletionPoint completionPoint = calcCompletionPoint((NbEditorDocument) doc, caretOffset);
                        fillCompletionPoint(dataObject, completionPoint, resultSet, doc, caretOffset);
                    }
                    resultSet.finish();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }, component);
    }

    @Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return CompletionProvider.COMPLETION_QUERY_TYPE;
    }

    public CompletionPoint calcCompletionPoint(NbEditorDocument doc, int caretOffset) throws Exception {
        CompletionPoint point = new CompletionPoint();
        point.zone = UNKNOWN_ZONE;
        point.filter = null;
        point.atDot = false;
        if (caretOffset > 0) {
            final StyledDocument styledDoc = (StyledDocument) doc;
            // Calc start and end offset
            point.startOffset = getStartWordOffset(doc, caretOffset);
            point.endOffset = getEndWordOffset(doc, caretOffset);
            // Calc filter fragment
            if (caretOffset - point.startOffset > 0) {
                point.filter = styledDoc.getText(point.startOffset, caretOffset - point.startOffset);
            }
            // calc dots contexts
            if (point.startOffset > 0) {
                point.atDot = ".".equals(doc.getText(point.startOffset - 1, 1));
                if (point.atDot) {
                    int startOffset = getStartWordOffset(doc, point.startOffset - 2);
                    if (startOffset > -1 && point.startOffset - 1 - startOffset > 0) {
                        point.prevContext = doc.getText(startOffset, point.startOffset - 1 - startOffset);
                        if (startOffset > 0) {
                            boolean atDot = ".".equals(doc.getText(startOffset - 1, 1));
                            if (atDot) {
                                int preStartOffset = getStartWordOffset(doc, startOffset - 2);
                                if (preStartOffset > -1 && startOffset - 1 - preStartOffset > 0) {
                                    point.prevPrevContext = doc.getText(preStartOffset, startOffset - 1 - preStartOffset);
                                }
                            }
                        }
                    }
                }
            }
            doc.readLock();
            try {
                TokenHierarchy<?> hierarchy = TokenHierarchy.get(doc);
                TokenSequence<LexSqlTokenId> ts = hierarchy.tokenSequence(LexSqlTokenId.language());
                while (ts.moveNext()) {
                    Token<LexSqlTokenId> t = ts.token();
                    int tokenOffset = ts.offset();
                    int tokenLength = t.length();
                    int counter = 0;
                    if (tokenLength != t.text().length()) {
                        while (counter < t.text().length() && t.text().charAt(counter++) == ' ') {
                            tokenOffset++;
                            tokenLength--;
                        }
                        counter = t.text().length() - 1;
                        while (counter >= 0 && t.text().charAt(counter--) == ' ') {
                            tokenLength--;
                        }
                    }
                    if (caretOffset <= tokenOffset) {
                        break;
                    }
                    if (caretOffset > tokenOffset && caretOffset <= tokenOffset + tokenLength) {
                        if (SqlLanguageHierarchy.KEYWORD_CATEGORY_NAME.equals(t.id().primaryCategory())) {
                            point.zone = KEYWORD_ZONE;
                        }
                    }
                    if ("select".equalsIgnoreCase(t.text().toString())) {
                        point.zone = SELECT_ZONE;
                    } else if ("from".equalsIgnoreCase(t.text().toString())) {
                        point.zone = FROM_ZONE;
                    } else if ("where".equalsIgnoreCase(t.text().toString())) {
                        point.zone = WHERE_ZONE;
                    } else if ("having".equalsIgnoreCase(t.text().toString())) {
                        point.zone = HAVING_ZONE;
                    } else if ("group".equalsIgnoreCase(t.text().toString())) {
                        point.zone = GROUP_ZONE;
                        ts.moveNext(); // BY
                    } else if ("insert".equalsIgnoreCase(t.text().toString())) {
                        point.zone = INSERT_INTO_ZONE;
                        ts.moveNext(); // INTO
                    } else if ("values".equalsIgnoreCase(t.text().toString())) {
                        point.zone = INSERT_VALUES_ZONE;
                    } else if (point.zone == INSERT_INTO_ZONE && "(".equalsIgnoreCase(t.text().toString())) {
                        point.zone = INSERT_FIELDS_ZONE;
                    } else if (point.zone == INSERT_VALUES_ZONE && "(".equalsIgnoreCase(t.text().toString())) {
                        point.zone = INSERT_VALUES_LIST_ZONE;
                    } else if ("update".equalsIgnoreCase(t.text().toString())) {
                        point.zone = UPDATE_ZONE;
                    } else if ("set".equalsIgnoreCase(t.text().toString())) {
                        point.zone = SET_ZONE;
                    }
                }
            } finally {
                doc.readUnlock();
            }
        } else {
            point.zone = UNKNOWN_ZONE;
            point.startOffset = 0;
            point.endOffset = 0;
        }
        return point;
    }

    private synchronized void fillCompletionPoint(PlatypusQueryDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet, Document doc, int caretOffset) throws Exception {
        try {
            addedCompletionItems = 0;
            if (point.zone == SELECT_ZONE) {
                fillCompletionSelectZone(dataObject, point, resultSet);
            } else if (point.zone == FROM_ZONE) {
                fillCompletionFromZone(point, dataObject, resultSet);
            } else if (point.zone == WHERE_ZONE) {
                fillCompletionWhereZone(dataObject, point, resultSet);
                if (!point.atDot) {
                    fillCompletionParametersZone(point, dataObject, resultSet);
                }
            } else if (point.zone == GROUP_ZONE) {
                fillCompletionWhereZone(dataObject, point, resultSet);
            } else if (point.zone == HAVING_ZONE) {
                fillCompletionSelectZone(dataObject, point, resultSet);
            } else if (point.zone == INSERT_INTO_ZONE) {
                fillCompletionFromZone(point, dataObject, resultSet);
            } else if (point.zone == INSERT_FIELDS_ZONE) {
                fillCompletionInsertFieldsZone(point, dataObject, resultSet);
            } else if (point.zone == INSERT_VALUES_LIST_ZONE) {
                fillCompletionParametersZone(point, dataObject, resultSet);
            } else if (point.zone == UPDATE_ZONE) {
                fillCompletionFromZone(point, dataObject, resultSet);
            } else if (point.zone == SET_ZONE) {
                fillCompletionUpdateFieldsZone(point, dataObject, resultSet);
                fillCompletionParametersZone(point, dataObject, resultSet);
            } else if (point.zone == KEYWORD_ZONE) {
                fillCompletionWithKeywords(point, resultSet);
            } else if (addedCompletionItems == 0) {
                if (point.filter != null && !point.filter.isEmpty()) {
                    fillCompletionWithKeywords(point, resultSet);
                } else {
                    fillCompletionWithBaseKeywords(point, resultSet);
                }
            }
        } catch (Exception ex) {
            fillCompletionException(ex, resultSet);
        }
    }

    static int getRowFirstNonWhite(StyledDocument doc, int offset)
            throws BadLocationException {
        Element lineElement = doc.getParagraphElement(offset);
        int start = lineElement.getStartOffset();
        while (start + 1 < lineElement.getEndOffset()) {
            try {
                if (doc.getText(start, 1).charAt(0) != ' ') {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1)
                        + ") on doc of length: " + doc.getLength(), start).initCause(ex);
            }
            start++;
        }
        return start;
    }

    protected int getStartWordOffset(NbEditorDocument aDoc, int caretOffset) throws Exception {
        while (caretOffset > 0 && aDoc.getLength() > 0
                && (Character.isJavaIdentifierPart(aDoc.getText(caretOffset - 1, 1).toCharArray()[0])
                || aDoc.getText(caretOffset - 1, 1).startsWith(":")/*Parameters case*/
                || aDoc.getText(caretOffset - 1, 1).startsWith(ClientConstants.STORED_QUERY_REF_PREFIX)/*Sub-queries strong reference case*/)) {
            caretOffset--;
        }
        return caretOffset;
    }

    public int getEndWordOffset(NbEditorDocument aDoc, int caretOffset) throws BadLocationException {
        while (caretOffset < aDoc.getLength() && aDoc.getLength() > 0
                && Character.isJavaIdentifierPart(aDoc.getText(caretOffset, 1).toCharArray()[0])) {
            caretOffset++;
        }
        return caretOffset;
    }

    protected String findName(String aPattern, Collection<String> aNames) {
        for (String name : aNames) {
            if (name.equalsIgnoreCase(aPattern)) {
                return name;
            }
        }
        return null;
    }
}

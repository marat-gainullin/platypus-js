/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.result;

import com.eas.client.DatabasesClient;
import com.eas.client.SQLUtils;
import com.eas.client.SqlQuery;
import com.eas.client.StoredQueryFactory;
import com.eas.client.changes.Change;
import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.Delete;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
import com.eas.client.dataflow.FlowProvider;
import com.eas.client.dataflow.FlowProviderNotPagedException;
import com.eas.client.forms.components.model.ModelCheckBox;
import com.eas.client.forms.components.model.ModelDate;
import com.eas.client.forms.components.model.ModelFormattedField;
import com.eas.client.forms.components.model.ModelSpin;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.model.grid.header.ModelGridColumn;
import com.eas.client.forms.components.model.grid.header.ServiceGridColumn;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.scripts.JSObjectFacade;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.editing.SqlTextEditsComplementor;
import com.eas.designer.explorer.project.PlatypusProjectImpl;
import com.eas.script.Scripts;
import com.eas.util.IdGenerator;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.NamedParameter;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author vv
 */
public class QueryResultsView extends javax.swing.JPanel {

    private QuerySetupView querySetupView;
    private ModelGrid grid;
    private final DatabasesClient basesProxy;
    private String queryText;
    private Parameters parameters;
    private SqlQuery query;
    private String parsableQueryText;
    private FlowProvider flow;
    private List<Change> changeLog;
    private Insert lastInsert;
    private JSObject lastInserted;
    private static int queryIndex;
    private static CCJSqlParserManager parserManager = new CCJSqlParserManager();
    private static final String DEFAULT_TEXT_COLOR_KEY = "textText"; //NOI18N
    private static final String VALUE_PREF_KEY = "value"; //NOI18N
    private static final Logger logger = Logger.getLogger(QueryResultsView.class.getName());
    private static final int[] pageSizes = {100, 200, 500, 1000};
    private PageSizeItem[] pageSizeItems;
    private int pageSize;
    private String datasourceName;
    private String queryName;

    public QueryResultsView(DatabasesClient aBasesProxy, String aDatasourceName, String aSchemaName, String aTableName) throws Exception {
        this(aBasesProxy, aDatasourceName, String.format(SQLUtils.TABLE_NAME_2_SQL, getTableName(aSchemaName, aTableName)));
        setName(aTableName);
    }

    public QueryResultsView(PlatypusQueryDataObject aQueryDataObject) throws Exception {
        this(aQueryDataObject.getBasesProxy(), aQueryDataObject.getDatasourceName(), extractText(aQueryDataObject));
        if (parameters == null) {
            parsableQueryText = aQueryDataObject.getSqlTextDocument().getText(0, aQueryDataObject.getSqlTextDocument().getLength());
            tryToParseParameters(parsableQueryText);
        }
        for (Field sourceParam : aQueryDataObject.getModel().getParameters().toCollection()) {
            Parameter p = parameters.get(sourceParam.getName());
            if (p != null) {
                p.setType(sourceParam.getType());
                p.setMode(((Parameter) sourceParam).getMode());
            }
        }
        setName(aQueryDataObject.getName());
        queryName = IndexerQuery.file2AppElementId(aQueryDataObject.getPrimaryFile());
        if (queryName != null && !queryName.isEmpty()) {
            loadParametersValues();
        }
    }

    public QueryResultsView(DatabasesClient aBasesProxy, String aDatasourceName, String aQueryText) throws Exception {
        initComponents();
        initPageSizes();
        initCopyMessage();
        basesProxy = aBasesProxy;
        datasourceName = aDatasourceName;
        queryText = aQueryText;
        parseParameters();
        setName(getGeneratedTitle());
    }

    protected static String extractText(PlatypusQueryDataObject aQueryDataObject) throws Exception {
        StoredQueryFactory factory = new StoredQueryFactory(aQueryDataObject.getBasesProxy(), aQueryDataObject.getProject().getQueries(), aQueryDataObject.getProject().getIndexer());
        String queryText = aQueryDataObject.getSqlTextDocument().getText(0, aQueryDataObject.getSqlTextDocument().getLength());
        String dialectQueryText = aQueryDataObject.getSqlFullTextDocument().getText(0, aQueryDataObject.getSqlFullTextDocument().getLength());
        if (dialectQueryText != null && !dialectQueryText.isEmpty() && !dialectQueryText.replaceAll("\\s", "").isEmpty()) {
            queryText = dialectQueryText;
        }
        return factory.compileSubqueries(queryText, aQueryDataObject.getModel());
    }

    public void setPageSize(int aValue) {
        pageSize = aValue;
    }

    public PageSizeItem[] getPageSizeItems() {
        return pageSizeItems;
    }

    private static String getTableName(String aTableSchemaName, String aTableName) {
        if (aTableName == null || aTableName.isEmpty()) {
            throw new IllegalArgumentException("Table name is null or empty."); //NOI18N
        }
        return aTableSchemaName != null && !aTableSchemaName.isEmpty() ? String.format("%s.%s", aTableSchemaName, aTableName) : aTableName; //NOI18N
    }

    /**
     *
     * @return True if underlying query is a select, false if it a insert,
     * delete, update or stored procedure call
     * @throws Exception
     */
    private void initModel() throws Exception {
        query = new SqlQuery(basesProxy, queryText);
        query.setDatasourceName(datasourceName);
        query.setPageSize(pageSize);
        parameters.toCollection().stream().forEach((p) -> {
            query.getParameters().add(p.copy());
        });
        try {
            StoredQueryFactory factory = new StoredQueryFactory(basesProxy, null, null);
            try {
                query.setCommand(!factory.putTableFieldsMetadata(query));
            } catch (JSQLParserException ex) {
                if (parsableQueryText != null) {
                    query.setSqlText(parsableQueryText);
                    query.setCommand(!factory.putTableFieldsMetadata(query));
                    query.setSqlText(queryText);
                } else {
                    throw ex;
                }
            }
            flow = query.compile().getFlowProvider();
            changeLog = new ArrayList<>();
            commitButton.setEnabled(!query.isCommand());
            nextPageButton.setEnabled(!query.isCommand());
            addButton.setEnabled(!query.isCommand());
            deleteButton.setEnabled(!query.isCommand());
            commitButton.setToolTipText(NbBundle.getMessage(QueryResultsView.class, "HINT_Commit"));
        } catch (Exception ex) {
            query.setFields(null);// We have to accept database's fields here.
            commitButton.setEnabled(false);
            nextPageButton.setEnabled(false);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            commitButton.setToolTipText(NbBundle.getMessage(QueryResultsView.class, "HINT_Uncommitable"));
        }
        refreshButton.setEnabled(true);
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String aQueryText) {
        queryText = aQueryText;
    }

    private String getGeneratedTitle() {
        return String.format("%s %d", NbBundle.getMessage(QuerySetupView.class, "QueryResultsView.queryName"), ++queryIndex); //NOI18N
    }

    private void resetMessage() {
        showInfo(""); // NOI18N
    }

    private void showInfo(final String aText) {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setForeground(UIManager.getColor(DEFAULT_TEXT_COLOR_KEY));
            messageLabel.setText(aText);
            messageLabel.setCaretPosition(0);
        });
    }

    private void showWarning(final String aText) {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setForeground(Color.RED.darker());
            messageLabel.setText(aText);
            messageLabel.setCaretPosition(0);
        });
    }

    private void showQueryResultsMessage() {
        if (query.getFields() != null && grid.getData() != null) {
            String message = String.format(NbBundle.getMessage(QuerySetupView.class, "QueryResultsView.resultMessage"), JSType.toInteger(grid.getData().getMember("length")));
            List<Field> pks = query.getFields().getPrimaryKeys();
            if (pks == null || pks.isEmpty()) {
                message += "\n " + String.format(NbBundle.getMessage(QuerySetupView.class, "QueryResultsView.noKeysMessage"), query.getEntityName());
            }
            showInfo(message);
        }
    }

    public void logParameters() throws Exception {
        if (logger.isLoggable(Level.FINEST)) {
            for (int i = 1; i <= parameters.getParametersCount(); i++) {
                Parameter p = parameters.get(i);
                logger.log(Level.FINEST, "Parameter {0} of type {1} is assigned with value: {2}", new Object[]{p.getName(), p.getType(), p.getValue()});
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        refreshButton = new javax.swing.JButton();
        nextPageButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        commitButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        verticalFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        resultsPanel = new javax.swing.JPanel();
        gridPanel = new javax.swing.JPanel();
        footerPanel = new javax.swing.JPanel();
        messageLabel = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);
        toolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.setRollover(true);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/designer/application/query/result/refresh-records-btn.png"))); // NOI18N
        refreshButton.setText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "QueryResultsView.refreshButton.text")); // NOI18N
        refreshButton.setToolTipText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "refreshButton.Tooltip")); // NOI18N
        refreshButton.setFocusable(false);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        toolBar.add(refreshButton);

        nextPageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/designer/application/query/result/next.png"))); // NOI18N
        nextPageButton.setText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "QueryResultsView.nextPageButton.text")); // NOI18N
        nextPageButton.setToolTipText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "nextPageButton.Tooltip")); // NOI18N
        nextPageButton.setFocusable(false);
        nextPageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextPageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextPageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextPageButtonActionPerformed(evt);
            }
        });
        toolBar.add(nextPageButton);

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/designer/application/query/result/new.png"))); // NOI18N
        addButton.setToolTipText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "QueryResultsView.addButton.toolTipText")); // NOI18N
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        toolBar.add(addButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/designer/application/query/result/delete.png"))); // NOI18N
        deleteButton.setToolTipText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "QueryResultsView.deleteButton.toolTipText")); // NOI18N
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        toolBar.add(deleteButton);

        commitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/designer/application/query/result/commit-record-btn.png"))); // NOI18N
        commitButton.setText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "QueryResultsView.commitButton.text")); // NOI18N
        commitButton.setFocusable(false);
        commitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        commitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        commitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commitButtonActionPerformed(evt);
            }
        });
        toolBar.add(commitButton);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/designer/application/query/result/runsql.png"))); // NOI18N
        runButton.setText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "QueryResultsView.runButton.text")); // NOI18N
        runButton.setToolTipText(org.openide.util.NbBundle.getMessage(QueryResultsView.class, "runButton.Tooltip")); // NOI18N
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        toolBar.add(runButton);
        toolBar.add(verticalFiller);

        add(toolBar, java.awt.BorderLayout.WEST);

        resultsPanel.setLayout(new java.awt.BorderLayout());

        gridPanel.setLayout(new java.awt.BorderLayout());
        resultsPanel.add(gridPanel, java.awt.BorderLayout.CENTER);

        footerPanel.setPreferredSize(new java.awt.Dimension(10, 22));
        footerPanel.setLayout(new java.awt.BorderLayout());

        messageLabel.setEditable(false);
        messageLabel.setBorder(null);
        messageLabel.setOpaque(false);
        footerPanel.add(messageLabel, java.awt.BorderLayout.CENTER);

        resultsPanel.add(footerPanel, java.awt.BorderLayout.SOUTH);

        add(resultsPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        try {
            showQuerySetupDialog();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private Runnable disableButtons() {
        boolean rEnabled = refreshButton.isEnabled();
        boolean nEnabled = nextPageButton.isEnabled();
        boolean aEnabled = addButton.isEnabled();
        boolean dEnabled = deleteButton.isEnabled();
        boolean cEnabled = commitButton.isEnabled();
        boolean rrEnabled = runButton.isEnabled();
        refreshButton.setEnabled(false);
        nextPageButton.setEnabled(false);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        commitButton.setEnabled(false);
        runButton.setEnabled(false);
        boolean gInsertable = grid != null ? grid.isInsertable() : false;
        boolean gEditable = grid != null ? grid.isEditable() : false;
        boolean gDeletable = grid != null ? grid.isDeletable() : false;
        if (grid != null) {
            grid.setInsertable(false);
            grid.setEditable(false);
            grid.setDeletable(false);
        }
        return () -> {
            refreshButton.setEnabled(rEnabled);
            nextPageButton.setEnabled(nEnabled);
            addButton.setEnabled(aEnabled);
            deleteButton.setEnabled(dEnabled);
            commitButton.setEnabled(cEnabled);
            runButton.setEnabled(rrEnabled);
            if (grid != null) {
                grid.setInsertable(gInsertable);
                grid.setEditable(gEditable);
                grid.setDeletable(gDeletable);
            }
        };
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        if (query != null) {
            final Runnable reEnableButtons = disableButtons();
            RequestProcessor.getDefault().execute(() -> {
                final ProgressHandle ph = ProgressHandleFactory.createHandle(getName());
                ph.start();
                try {
                    if (query.isCommand()) {
                        int rowsAffected = basesProxy.executeUpdate(query.compile(), null, null);
                        showInfo(NbBundle.getMessage(QuerySetupView.class, "QueryResultsView.affectedRowsMessage", rowsAffected));
                        EventQueue.invokeLater(() -> {
                            reEnableButtons.run();
                            gridPanel.revalidate();
                            gridPanel.repaint();
                        });
                    } else {
                        changeLog = new ArrayList<>();
                        if (flow != null) {
                            Collection<Map<String, Object>> fetched = flow.refresh(query.compile().getParameters(), null, null);
                            EventQueue.invokeLater(() -> {
                                Scripts.Space space = PlatypusProjectImpl.getJsSpace();
                                JSObject jsFetched = space.readJsArray(fetched);
                                JSObject processed = processData(jsFetched, space);
                                grid.setData(processed);
                                showQueryResultsMessage();
                                EventQueue.invokeLater(() -> {
                                    reEnableButtons.run();
                                    nextPageButton.setEnabled(true);
                                    gridPanel.revalidate();
                                    gridPanel.repaint();
                                });
                            });
                        }
                    }
                } catch (Exception ex) {
                    showWarning(ex.getMessage() != null ? ex.getMessage() : ex.toString()); //NO1I18N
                    runButton.setEnabled(true);
                } finally {
                    ph.finish();
                }
            });
        }
    }//GEN-LAST:event_refreshButtonActionPerformed

    protected void generateChangeLogKeys(List<ChangeValue> aKeys, JSObject aSubject, String propName, Object oldValue) {
        if (query != null) {
            Fields fields = query.getFields();
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                Field field = fields.get(i);
                if (field.isPk()) {
                    String fieldName = field.getName();
                    Object value = aSubject.getMember(fieldName);
                    // Some tricky processing of primary keys modification case ...
                    if (fieldName.equalsIgnoreCase(propName)) {
                        value = oldValue;
                    }
                    aKeys.add(new ChangeValue(fieldName, value));
                }
            }
        }
    }

    protected void generateChangeLogData(List<ChangeValue> aData, JSObject aSubject) {
        if (query != null) {
            Fields fields = query.getFields();
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                Field field = fields.get(i);
                String fieldName = field.getName();
                Object value = aSubject.getMember(fieldName);
                if (JSType.nullOrUndefined(value) && field.isPk() && !field.isFk()) {
                    value = field.generateValue();
                    aSubject.setMember(fieldName, value);
                }
                if (!JSType.nullOrUndefined(value)) {
                    aData.add(new ChangeValue(fieldName, value));
                }
            }
        }
    }

    protected class JSWrapper extends JSObjectFacade {

        public JSWrapper(JSObject aDelegate) {
            super(aDelegate);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof JSObjectFacade) {
                obj = ((JSObjectFacade) obj).getDelegate();
            }
            return getDelegate().equals(obj);
        }

        @Override
        public int hashCode() {
            return getDelegate().hashCode();
        }

        @Override
        public void setMember(String name, Object value) {
            Field field = query.getFields().get(name);
            if (field != null) {
                Object oldValue = super.getMember(name);
                if (!Objects.equals(oldValue, value)) {
                    super.setMember(name, value);
                    boolean complemented = false;
                    if (!field.isNullable() && lastInsert != null && lastInserted.equals(this)) {
                        boolean met = false;
                        for (int d = 0; d < lastInsert.getData().size(); d++) {
                            ChangeValue chv = lastInsert.getData().get(d);
                            if (chv.getName().equalsIgnoreCase(name)) {
                                met = true;
                                break;
                            }
                        }
                        if (!met) {
                            lastInsert.getData().add(new ChangeValue(name, value));
                            complemented = true;
                        }
                    }
                    if (!complemented) {
                        Update update = new Update("");
                        generateChangeLogKeys(update.getKeys(), getDelegate(), name, oldValue);
                        update.getData().add(new ChangeValue(name, value));
                        changeLog.add(update);
                    }
                }
            }
        }

    }

    protected JSObject processData(JSObject aSubject, Scripts.Space aSpace) {
        JSObject processed = new JSObjectFacade(aSubject) {

            @Override
            public Object getMember(String name) {
                if ("splice".equals(name)) {
                    JSObject jsSplice = (JSObject) super.getMember(name);
                    return new JSObjectFacade(jsSplice) {

                        @Override
                        public Object call(Object thiz, Object... args) {
                            Object res = super.call(thiz instanceof JSObjectFacade ? ((JSObjectFacade) thiz).getDelegate() : thiz, args);
                            if (res instanceof JSObject) {
                                JSObject jsDeleted = (JSObject) res;
                                int deletedLength = JSType.toInteger(jsDeleted.getMember("length"));
                                for (int i = 0; i < deletedLength; i++) {
                                    JSObject jsDeletedItem = (JSObject) jsDeleted.getSlot(i);
                                    Delete delete = new Delete("");
                                    generateChangeLogKeys(delete.getKeys(), jsDeletedItem, null, null);
                                    changeLog.add(delete);
                                }
                                grid.removed((JSObject) res);
                            }
                            JSObject added = aSpace.makeArray();
                            JSObject push = (JSObject)added.getMember("push");
                            for (int i = 2; i < args.length; i++) {
                                Insert insert = new Insert("");
                                JSObject jsSubject = (JSObject) args[i];
                                if (jsSubject instanceof JSWrapper) {
                                    jsSubject = ((JSWrapper) jsSubject).getDelegate();
                                }
                                generateChangeLogData(insert.getData(), jsSubject);
                                changeLog.add(insert);
                                lastInsert = insert;
                                lastInserted = (JSObject) args[i];
                                push.call(added, args[i]);
                            }
                            grid.added(added);
                            return res;
                        }

                    };
                } else {
                    return super.getMember(name);
                }
            }

            @Override
            public Object getSlot(int index) {
                Object slot = super.getSlot(index);
                return JSType.nullOrUndefined(slot) || slot instanceof JSObjectFacade ? slot : new JSWrapper((JSObject) slot);
            }

        };
        int length = JSType.toInteger(processed.getMember("length"));
        processed.setMember(CURSOR_PROP_NAME, length > 0 ? processed.getSlot(0) : null);
        processed.setMember("elementClass", new AbstractJSObject() {

            @Override
            public boolean isFunction() {
                return true;
            }

            @Override
            public Object newObject(Object... args) {
                return new JSWrapper(aSpace.makeObj());
            }

        });
        return processed;
    }

    private void commitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commitButtonActionPerformed
        try {
            if (query != null && !changeLog.isEmpty()) {
                final Runnable reEnableButtons = disableButtons();
                final String entityName = IdGenerator.genStringId();
                query.setEntityName(entityName);
                changeLog.forEach((Change aChange) -> {
                    aChange.entityName = entityName;
                });
                ((LocalQueriesProxy) basesProxy.getQueries()).putCachedQuery(entityName, query);
                RequestProcessor.getDefault().execute(() -> {
                    final ProgressHandle ph = ProgressHandleFactory.createHandle(getName());
                    ph.start();
                    try {
                        int rowsAffected = basesProxy.commit(Collections.singletonMap(query.getDatasourceName(), changeLog), null, null);
                        showInfo(NbBundle.getMessage(QueryResultsView.class, "DataSaved") + ". " + NbBundle.getMessage(QueryResultsView.class, "QueryResultsView.affectedRowsMessage", rowsAffected));
                    } catch (Exception ex) {
                        showInfo(ex.getMessage()); //NO1I18N
                    } finally {
                        ph.finish();
                        EventQueue.invokeLater(() -> {
                            ((LocalQueriesProxy) basesProxy.getQueries()).clearCachedQuery(entityName);
                            changeLog = new ArrayList<>();
                            reEnableButtons.run();
                        });
                    }
                });
            } else {
                resetMessage();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_commitButtonActionPerformed

    private void nextPageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPageButtonActionPerformed
        if (flow != null) {
            final Runnable reEnableButtons = disableButtons();
            RequestProcessor.getDefault().execute(() -> {
                try {
                    Collection<Map<String, Object>> fetched = flow.nextPage(null, null);
                    EventQueue.invokeLater(() -> {
                        Scripts.Space space = PlatypusProjectImpl.getJsSpace();
                        JSObject jsFetched = space.readJsArray(fetched);
                        int length = JSType.toInteger(jsFetched.getMember("length"));
                        if (length > 0) {
                            JSObject processed = processData(jsFetched, space);
                            grid.setData(processed);
                            showQueryResultsMessage();
                        }
                        reEnableButtons.run();
                        gridPanel.revalidate();
                        gridPanel.repaint();
                    });
                } catch (Exception ex) {
                    EventQueue.invokeLater(() -> {
                        reEnableButtons.run();
                        nextPageButton.setEnabled(false);
                        gridPanel.revalidate();
                        gridPanel.repaint();
                    });
                    if (!(ex instanceof FlowProviderNotPagedException)) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
        }
    }//GEN-LAST:event_nextPageButtonActionPerformed
    protected static final String CURSOR_PROP_NAME = "cursor";

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        grid.insertElementAtCursor();
    }//GEN-LAST:event_addButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        grid.deleteSelectedElements();
    }//GEN-LAST:event_deleteButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton commitButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JTextField messageLabel;
    private javax.swing.JButton nextPageButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel resultsPanel;
    private javax.swing.JButton runButton;
    private javax.swing.JToolBar toolBar;
    private javax.swing.Box.Filler verticalFiller;
    // End of variables declaration//GEN-END:variables

    public void runQuery() throws Exception {
        assert parameters != null : "Parameters must be initialized."; //NOI18N
        if (!parameters.isEmpty()) {
            showQuerySetupDialog();
        } else {
            requestExecuteQuery();
        }
    }

    private void showQuerySetupDialog() {
        try {
            if (querySetupView == null) {
                querySetupView = new QuerySetupView(this);
            }
            DialogDescriptor nd = new DialogDescriptor(querySetupView, getName());
            Dialog dlg = DialogDisplayer.getDefault().createDialog(nd);
            dlg.setModal(true);
            querySetupView.setDialog(dlg, nd);
            dlg.setVisible(true);
            if (DialogDescriptor.OK_OPTION.equals(nd.getValue())) {
                queryText = querySetupView.getSqlText();
                parameters = querySetupView.acceptParametersValues();
                logParameters();
                if (queryName != null && !queryName.isEmpty() && querySetupView.isSaveParamsValuesEnabled()) {
                    saveParametersValues();
                }
                requestExecuteQuery();
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void requestExecuteQuery() throws Exception {
        resetMessage();
        refresh();
    }

    /**
     *
     * @return True if results grid is initialized and false if dml query has
     * been executed
     * @throws Exception
     */
    private void refresh() throws Exception {
        initModel();
        gridPanel.removeAll();
        if (!query.isCommand()) {
            initModelGrid();
        }
        refreshButtonActionPerformed(null);
    }

    private void initModelGrid() throws Exception {
        grid = new ModelGrid();
        grid.setRowsHeight(20);
        grid.setAutoRefreshHeader(false);
        gridPanel.add(grid);
        Fields fields = query.getFields();
        if (fields != null) {
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                Field columnField = fields.get(i);
                ModelGridColumn columnNode = new ModelGridColumn();
                grid.addColumnNode(columnNode);
                ModelColumn column = (ModelColumn) columnNode.getTableColumn();
                int lwidth = 80;
                if (lwidth >= columnNode.getWidth()) {
                    columnNode.setWidth(lwidth);
                }
                String description = columnField.getDescription();
                if (description != null && !description.isEmpty()) {
                    columnNode.setTitle(description);
                } else {
                    columnNode.setTitle(columnField.getName());
                }
                columnNode.setField(columnField.getName());
                if (columnField.getType() != null) {
                    switch (columnField.getType()) {
                        // Numbers
                        case Scripts.NUMBER_TYPE_NAME: {
                            ModelSpin editor = new ModelSpin();
                            editor.setMin(-Double.MAX_VALUE);
                            editor.setMax(Double.MAX_VALUE);
                            ModelSpin view = new ModelSpin();
                            view.setMin(-Double.MAX_VALUE);
                            view.setMax(Double.MAX_VALUE);
                            column.setEditor(editor);
                            column.setView(view);
                            break;
                        }
                        // Logical
                        case Scripts.BOOLEAN_TYPE_NAME:
                            column.setEditor(new ModelCheckBox());
                            column.setView(new ModelCheckBox());
                            break;
                        // Date and time
                        case Scripts.DATE_TYPE_NAME: {
                            ModelDate editor = new ModelDate();
                            editor.setFormat("dd.MM.yyyy HH:mm:ss.SSS");
                            ModelDate view = new ModelDate();
                            view.setFormat("dd.MM.yyyy HH:mm:ss.SSS");
                            column.setEditor(editor);
                            column.setView(view);
                            break;
                        }
                        default:
                            column.setEditor(new ModelFormattedField());
                            column.setView(new ModelFormattedField());
                            break;
                    }
                } else {
                    column.setEditor(new ModelFormattedField());
                    column.setView(new ModelFormattedField());
                }
                column.getEditor().setNullable(columnField.isNullable());
            }
            List<Field> pks = query.getFields().getPrimaryKeys();
            grid.setEditable(pks != null && !pks.isEmpty());
            grid.setDeletable(pks != null && !pks.isEmpty());
            deleteButton.setEnabled(pks != null && !pks.isEmpty());
            if (!deleteButton.isEnabled()) {
                showInfo(String.format(NbBundle.getMessage(QuerySetupView.class, "QueryResultsView.noKeysMessage"), query.getEntityName()));
            }
        }
        grid.setAutoRefreshHeader(true);
        grid.insertColumnNode(0, new ServiceGridColumn());
    }

    public Parameters getParameters() {
        return parameters;
    }

    private void parseParameters() {
        try {
            tryToParseParameters(queryText);
        } catch (JSQLParserException ex) {
            Logger.getLogger(QueryResultsView.class.getName()).log(Level.WARNING, ex.toString());
        }
    }

    private void tryToParseParameters(String aQueryText) throws JSQLParserException {
        Statement statement = parserManager.parse(new StringReader(aQueryText));
        parameters = new Parameters();
        Set<NamedParameter> parsedParameters = SqlTextEditsComplementor.extractParameters(statement);
        parsedParameters.stream().forEach((NamedParameter parsedParameter) -> {
            Parameter newParameter = new Parameter(parsedParameter.getName());
            newParameter.setMode(1);
            newParameter.setType(Scripts.STRING_TYPE_NAME);
            newParameter.setValue(null);
            parameters.add(newParameter);
        });
    }

    private void initPageSizes() {
        pageSizeItems = new PageSizeItem[pageSizes.length];
        for (int i = 0; i < pageSizeItems.length; i++) {
            pageSizeItems[i] = new PageSizeItem(pageSizes[i]);
        }
        pageSize = pageSizes[0];
    }

    private void initCopyMessage() {
        final JPopupMenu menu = new JPopupMenu();
        final JMenuItem copyItem = new JMenuItem(NbBundle.getMessage(QuerySetupView.class, "QueryResultsView.copyMessage")); //NOI18N
        copyItem.addActionListener((ActionEvent e) -> {
            StringSelection stringSelection = new StringSelection(messageLabel.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        menu.add(copyItem);
        messageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void loadParametersValues() throws BackingStoreException {
        Preferences modulePreferences = NbPreferences.forModule(QueryResultsView.class);
        Preferences paramsPreferences = modulePreferences.node(queryName);
        for (String paramPrefNodeName : paramsPreferences.childrenNames()) {
            Parameter parameter = parameters.get(paramPrefNodeName);
            if (parameter != null) {
                Preferences paramNode = paramsPreferences.node(parameter.getName());
                try {
                    String paramType = parameter.getType();
                    if (null != paramType) {
                        switch (paramType) {
                            case Scripts.DATE_TYPE_NAME:
                                long lValue = paramNode.getLong(VALUE_PREF_KEY, -1);
                                if (lValue != -1) {
                                    parameter.setValue(new Date(lValue));
                                } else {
                                    parameter.setValue(null);
                                }
                                break;
                            case Scripts.BOOLEAN_TYPE_NAME: {
                                Object val = paramNode.getBoolean(VALUE_PREF_KEY, false);
                                parameter.setValue(val);
                                break;
                            }
                            case Scripts.NUMBER_TYPE_NAME: {
                                Object val = paramNode.getDouble(VALUE_PREF_KEY, 0d);
                                parameter.setValue(val);
                                break;
                            }
                            default: {
                                Object val = paramNode.get(VALUE_PREF_KEY, ""); //NOI18N
                                parameter.setValue(val);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    //no-op
                }
            }
        }
    }

    private void saveParametersValues() throws BackingStoreException {
        Preferences modulePreferences = NbPreferences.forModule(QueryResultsView.class);
        Preferences paramsPreferences = modulePreferences.node(queryName);
        parameters.toCollection().stream().forEach((pField) -> {
            try {
                Parameter parameter = (Parameter) pField;
                Preferences paramNode = paramsPreferences.node(parameter.getName());
                if (parameter.getValue() != null) {
                    if (parameter.getValue() instanceof Date) {
                        paramNode.putLong(VALUE_PREF_KEY, ((Date) parameter.getValue()).getTime());
                    } else if (Scripts.NUMBER_TYPE_NAME.equals(parameter.getType())) {
                        paramNode.putDouble(VALUE_PREF_KEY, ((Number) parameter.getValue()).doubleValue());
                    } else if (Scripts.BOOLEAN_TYPE_NAME.equals(parameter.getType())) {
                        paramNode.putBoolean(VALUE_PREF_KEY, (Boolean) parameter.getValue());
                    } else if (parameter.getValue() instanceof String) {
                        String sVal = (String) parameter.getValue();
                        paramNode.put(VALUE_PREF_KEY, sVal);
                    } else {
                        paramNode.remove(VALUE_PREF_KEY);
                    }
                } else {
                    paramNode.removeNode();
                }
            } catch (Exception ex) {
                //no-op
            }
        });
        paramsPreferences.flush();
    }

    public void close() throws Exception {
        if (flow != null) {
            flow.close();
            flow = null;
        }
    }

    public static class PageSizeItem {

        private final Integer pageSize;

        public PageSizeItem(int aPageSize) {
            pageSize = aPageSize;
        }

        public int getValue() {
            return pageSize;
        }

        @Override
        public String toString() {
            return String.format(NbBundle.getMessage(QuerySetupView.class, "QueryResultsView.pageSizeStr"), pageSize); //NOI18N
        }
    }
}

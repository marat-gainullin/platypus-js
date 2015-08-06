/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.designer.application.query.AbsentTableParseException;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.script.JsDoc;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
public class DocumentTextCompiler implements DocumentListener {

    public static final String INSERT_COMPILEABLE = "insert into %s values('') "; // Whitespace at the end is needed
    public static final String INSERT_TEMPLATE = "insert\\s+into\\s+([a-zA-z_][a-zA-z0-9_\\.]*)\\s*(\\s+as\\s+)*(\\s+[a-zA-z_][a-zA-z0-9_\\.]*\\s*)*\\(.*";
    public static final String UPDATE_COMPILEABLE = "update %s set f1=:p1 "; // Whitespace at the end is needed
    public static final String UPDATE_AS_COMPILEABLE = "update %s as %s set f1=:p1 "; // Whitespace at the end is needed
    public static final String UPDATE_ALIAS_COMPILEABLE = "update %s %s set f1=:p1 "; // Whitespace at the end is needed
    public static final String UPDATE_TEMPLATE = "update\\s+([a-zA-z_][a-zA-z0-9_\\.]*\\s+)(as\\s+)?([a-zA-z_][a-zA-z0-9_\\.]*\\s+)?.*";
    protected PlatypusQueryDataObject dataObject;

    public DocumentTextCompiler(PlatypusQueryDataObject aDataObject) throws Exception {
        super();
        dataObject = aDataObject;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            documentChanged();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            documentChanged();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        try {
            documentChanged();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    private void documentChanged() throws Exception {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        String docText = dataObject.getSqlTextDocument().getText(0, dataObject.getSqlTextDocument().getLength());
        try {
            if (docText.trim().isEmpty()) {
                dataObject.setStatement(null);
                dataObject.setStatementError(null);
                dataObject.commitStatement();
            } else {
                Statement statement = parserManager.parse(new StringReader(docText));
                dataObject.setStatement(statement);
                dataObject.setStatementError(null);
                dataObject.commitStatement();
            }
        } catch (Exception ex) {
            if (!(ex instanceof AbsentTableParseException)) {
                if (ex instanceof JSQLParserException && ((JSQLParserException) ex).getCause() instanceof ParseException) {
                    dataObject.setStatementError((ParseException) ex.getCause());
                }
                Matcher insertMatcher = Pattern.compile(INSERT_TEMPLATE, Pattern.CASE_INSENSITIVE).matcher(docText);
                if (insertMatcher.matches()) {
                    String tableName = insertMatcher.group(1);
                    docText = String.format(INSERT_COMPILEABLE, tableName.trim());
                }
                Matcher updateMatcher = Pattern.compile(UPDATE_TEMPLATE, Pattern.CASE_INSENSITIVE).matcher(docText);
                if (updateMatcher.matches()) {
                    String tableName = updateMatcher.group(1);
                    if (tableName != null) {
                        tableName = tableName.trim();
                    }
                    String asImage = updateMatcher.group(2);
                    if (asImage != null) {
                        asImage = asImage.trim().toLowerCase();
                    }
                    String aliasImage = updateMatcher.group(3);
                    if (aliasImage != null) {
                        aliasImage = aliasImage.trim().toLowerCase();
                    }

                    if (asImage == null && (aliasImage == null || "set".equals(aliasImage))) {
                        docText = String.format(UPDATE_COMPILEABLE, tableName);
                    } else if (asImage != null && aliasImage != null) {
                        docText = String.format(UPDATE_AS_COMPILEABLE, tableName, aliasImage);
                    } else if (asImage == null && aliasImage != null) {
                        docText = String.format(UPDATE_ALIAS_COMPILEABLE, tableName, aliasImage);
                    }
                }
                tryToCompilePartially(docText, parserManager);
            } else {
                dataObject.setStatementError((AbsentTableParseException) ex);
            }
        }
    }

    public void tryToCompilePartially(String docText, CCJSqlParserManager parserManager) {
        String[] parts = docText.split("[\\s]+");
        if (parts != null && parts.length > 0) {
            int partsLength = parts.length;
            boolean continueTries = true;
            while (continueTries && partsLength > 0) {
                try {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < partsLength; i++) {
                        sb.append(parts[i]).append(" ");
                    }
                    partsLength--;
                    Statement statement = parserManager.parse(new StringReader(sb.toString()));
                    dataObject.setStatement(statement);
                    continueTries = false;
                } catch (JSQLParserException ex) {
                    continueTries = true;
                }
            }
        }
        boolean publicQuery = PlatypusFilesSupport.getAnnotationValue(docText, JsDoc.Tag.PUBLIC_TAG) != null;
        boolean procedure = PlatypusFilesSupport.getAnnotationValue(docText, JsDoc.Tag.PROCEDURE_TAG) != null;
        boolean readonly = PlatypusFilesSupport.getAnnotationValue(docText, JsDoc.Tag.READONLY_TAG) != null;
        dataObject.setQueryFlags(publicQuery, procedure, readonly);
    }
}

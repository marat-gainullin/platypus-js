/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.lexer;

import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import net.sf.jsqlparser.parser.CCJSqlParser;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 * @author mg
 */
public class SqlParser extends Parser {

    private Snapshot snapshot;
    private CCJSqlParser sqlParser;

    @Override
    public void parse(Snapshot aSnapshot, Task aTask, SourceModificationEvent aEvent) {
        snapshot = aSnapshot;
        Reader reader = new StringReader(aSnapshot.getText().toString());
        sqlParser = new CCJSqlParser(reader);
        try {
            sqlParser.parse();
        } catch (Exception ex) {
            Logger.getLogger(SqlParser.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    @Override
    public Result getResult(Task task) {
        return new SqlParserResult(snapshot, sqlParser);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }

    public static class SqlParserResult extends Result {

        private CCJSqlParser sqlParser;
        private boolean valid = true;

        SqlParserResult(Snapshot snapshot, CCJSqlParser aSqlParser) {
            super(snapshot);
            sqlParser = aSqlParser;
        }

        public CCJSqlParser getSqlParser() throws org.netbeans.modules.parsing.spi.ParseException {
            if (!valid) {
                throw new org.netbeans.modules.parsing.spi.ParseException();
            }
            return sqlParser;
        }

        @Override
        protected void invalidate() {
            valid = false;
        }
    }
}

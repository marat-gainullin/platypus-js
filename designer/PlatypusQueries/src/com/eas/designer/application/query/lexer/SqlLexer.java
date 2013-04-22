/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.lexer;

import net.sf.jsqlparser.parser.CCJSqlParserTokenManager;
import net.sf.jsqlparser.parser.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author mg
 */
public class SqlLexer implements Lexer<SqlTokenId> {

    private LexerRestartInfo<SqlTokenId> info;
    private CCJSqlParserTokenManager javaParserTokenManager;

    public SqlLexer(LexerRestartInfo<SqlTokenId> aInfo) {
        info = aInfo;
        SqlCharStream stream = new SqlCharStream(aInfo.input());
        javaParserTokenManager = new CCJSqlParserTokenManager(stream);
    }

    @Override
    public org.netbeans.api.lexer.Token<SqlTokenId> nextToken() {
        Token token = javaParserTokenManager.getNextToken();
        if (info.input().readLength() < 1) {
            return null;
        }
        return info.tokenFactory().createToken(SqlLanguageHierarchy.getToken(token.kind));
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }
}

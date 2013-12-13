/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.lexer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jsqlparser.parser.CCJSqlParserConstants;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author mg
 */
public class SqlLanguageHierarchy extends LanguageHierarchy<SqlTokenId> {

    public static final String PLATYPUS_SQL_MIME_TYPE_NAME = "text/x-platypus-sql";
    //
    public static final String DIGIT_CATEGORY_NAME = "number";
    public static final String IDENTIFIER_CATEGORY_NAME = "identifier";
    public static final String KEYWORD_CATEGORY_NAME = "keyword";
    public static final String LETTER_CATEGORY_NAME = "string";
    public static final String LINE_COMMENT_CATEGORY_NAME = "linecomment";
    public static final String MULTI_LINE_COMMENT_CATEGORY_NAME = "multilinecomment";
    public static final String SPECIAL_CATEGORY_NAME = "special";
    public static final String OPERATOR_CATEGORY_NAME = "operator";
    public static final String WHITESPACE_CATEGORY_NAME = "whitespace";
    private static List<SqlTokenId> tokens;
    private static Map<Integer, SqlTokenId> idToToken;

    private static void init() {
        tokens = Arrays.<SqlTokenId>asList(new SqlTokenId[]{
                    /** End of File. */
                    new SqlTokenId("EOF", WHITESPACE_CATEGORY_NAME, CCJSqlParserConstants.EOF),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_AS", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_AS),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_START", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_START),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_CONNECT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_CONNECT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_NOCYCLE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_NOCYCLE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_PRIOR", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_PRIOR),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_BY", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_BY),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_DO", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_DO),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_IS", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_IS),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_IN", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_IN),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_OR", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_OR),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_ON", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_ON),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_ALL", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_ALL),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_AND", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_AND),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_ANY", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_ANY),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_KEY", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_KEY),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_NOT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_NOT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_SET", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_SET),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_ASC", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_ASC),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_TOP", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_TOP),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_END", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_END),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_DESC", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_DESC),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_INTO", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_INTO),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_NULL", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_NULL),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_LIKE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_LIKE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_DROP", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_DROP),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_JOIN", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_JOIN),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_LEFT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_LEFT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_FROM", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_FROM),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_OPEN", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_OPEN),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_CASE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_CASE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_WHEN", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_WHEN),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_THEN", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_THEN),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_ELSE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_ELSE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_SOME", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_SOME),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_FULL", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_FULL),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_WITH", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_WITH),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_TABLE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_TABLE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_WHERE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_WHERE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_USING", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_USING),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_UNION", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_UNION),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_GROUP", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_GROUP),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_BEGIN", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_BEGIN),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_INDEX", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_INDEX),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_INNER", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_INNER),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_LIMIT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_LIMIT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_OUTER", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_OUTER),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_ORDER", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_ORDER),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_RIGHT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_RIGHT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_DELETE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_DELETE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_CREATE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_CREATE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_SELECT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_SELECT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_OFFSET", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_OFFSET),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_EXISTS", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_EXISTS),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_HAVING", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_HAVING),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_INSERT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_INSERT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_UPDATE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_UPDATE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_VALUES", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_VALUES),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_ESCAPE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_ESCAPE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_PRIMARY", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_PRIMARY),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_NATURAL", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_NATURAL),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_REPLACE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_REPLACE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_BETWEEN", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_BETWEEN),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_TRUNCATE", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_TRUNCATE),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_DISTINCT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_DISTINCT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_INTERSECT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_INTERSECT),
                    /** RegularExpression Id. */
                    new SqlTokenId("K_EXCEPT", KEYWORD_CATEGORY_NAME, CCJSqlParserConstants.K_EXCEPT),
                    /** RegularExpression Id. */
                    new SqlTokenId("DOUBLE", DIGIT_CATEGORY_NAME, CCJSqlParserConstants.S_DOUBLE),
                    /** RegularExpression Id. */
                    new SqlTokenId("INTEGER", DIGIT_CATEGORY_NAME, CCJSqlParserConstants.S_INTEGER),
                    /** RegularExpression Id. */
                    new SqlTokenId("DIGIT", DIGIT_CATEGORY_NAME, CCJSqlParserConstants.DIGIT),
                    /** RegularExpression Id. */
                    new SqlTokenId("LINE_COMMENT", LINE_COMMENT_CATEGORY_NAME, CCJSqlParserConstants.LINE_COMMENT),
                    /** RegularExpression Id. */
                    new SqlTokenId("MULTI_LINE_COMMENT", MULTI_LINE_COMMENT_CATEGORY_NAME, CCJSqlParserConstants.MULTI_LINE_COMMENT),
                    /** RegularExpression Id. */
                    new SqlTokenId("S_IDENTIFIER", IDENTIFIER_CATEGORY_NAME, CCJSqlParserConstants.S_IDENTIFIER),
                    /** RegularExpression Id. */
                    new SqlTokenId("LETTER", LETTER_CATEGORY_NAME, CCJSqlParserConstants.LETTER),
                    /** RegularExpression Id. */
                    new SqlTokenId("SPECIAL_CHARS", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.SPECIAL_CHARS),
                    /** RegularExpression Id. */
                    new SqlTokenId("S_CHAR_LITERAL", LETTER_CATEGORY_NAME, CCJSqlParserConstants.S_CHAR_LITERAL),
                    /** RegularExpression Id. */
                    new SqlTokenId("S_QUOTED_IDENTIFIER", IDENTIFIER_CATEGORY_NAME, CCJSqlParserConstants.S_QUOTED_IDENTIFIER),
                    /** RegularExpression Id. */
                    new SqlTokenId("SEMICOLON", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.SEMICOLON),
                    /** RegularExpression Id. */
                    new SqlTokenId("EQUALS", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.EQUALS),
                    /** RegularExpression Id. */
                    new SqlTokenId("COMMA", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.COMMA),
                    /** RegularExpression Id. */
                    new SqlTokenId("LPAREN", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.LPAREN),
                    /** RegularExpression Id. */
                    new SqlTokenId("RPAREN", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.RPAREN),
                    /** RegularExpression Id. */
                    new SqlTokenId("PERCENT", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.PERCENT),
                    new SqlTokenId("LSQPAREN", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.LSQPAREN),
                    new SqlTokenId("RSQPAREN", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.RSQPAREN),
                    new SqlTokenId("TILDA", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.TILDA),
                    new SqlTokenId("EXCLAMATION", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.EXCLAMATION),
                    new SqlTokenId("AT", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.AT),
                    new SqlTokenId("DOLLAR", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.DOLLAR),
                    new SqlTokenId("SINGLEQUOTE", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.SINGLEQUOTE),
                    new SqlTokenId("DOUBLEQUOTE", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.DOUBLEQUOTE),
                    new SqlTokenId("BACKQUOTE", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.BACKQUOTE),
                    new SqlTokenId("BACKSLASH", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.BACKSLASH),
                    ///* excluded because of strong subquery reference symbol */new SqlTokenId("CROSS", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.CROSS),
                    /** RegularExpression Id. */
                    new SqlTokenId("DOT", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.DOT),
                    /** RegularExpression Id. */
                    new SqlTokenId("MULTIPLY", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.MULTIPLY),
                    /** RegularExpression Id. */
                    new SqlTokenId("HOOK", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.HOOK),
                    /** RegularExpression Id. */
                    new SqlTokenId("GT", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.GT),
                    /** RegularExpression Id. */
                    new SqlTokenId("LT", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.LT),
                    /** RegularExpression Id. */
                    new SqlTokenId("GE", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.GE),
                    /** RegularExpression Id. */
                    new SqlTokenId("LE", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.LE),
                    /** RegularExpression Id. */
                    new SqlTokenId("NOTEQUAL1", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.NOTEQUAL1),
                    /** RegularExpression Id. */
                    new SqlTokenId("NOTEQUAL2", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.NOTEQUAL2),
                    /** RegularExpression Id. */
                    new SqlTokenId("ATAT", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.ATAT),
                    /** RegularExpression Id. */
                    new SqlTokenId("OR", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.OR),
                    /** RegularExpression Id. */
                    new SqlTokenId("BITWISEOR", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.BITWISEOR),
                    /** RegularExpression Id. */
                    new SqlTokenId("BITWISEAND", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.BITWISEAND),
                    /** RegularExpression Id. */
                    new SqlTokenId("ADD", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.ADD),
                    /** RegularExpression Id. */
                    new SqlTokenId("MINUS", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.MINUS),
                    /** RegularExpression Id. */
                    new SqlTokenId("DIVIDE", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.DIVIDE),
                    /** RegularExpression Id. */
                    new SqlTokenId("XOR", OPERATOR_CATEGORY_NAME, CCJSqlParserConstants.XOR),
                    /** RegularExpression Id. */
                    new SqlTokenId("COLON", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.COLON),
                    /** RegularExpression Id. */
                    new SqlTokenId("DATELITERAL", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.DATELITERAL),
                    /** RegularExpression Id. */
                    new SqlTokenId("TIMELITERAL", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.TIMELITERAL),
                    /** RegularExpression Id. */
                    new SqlTokenId("TIMESTAMPLITERAL", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.TIMESTAMPLITERAL),
                    /** RegularExpression Id. */
                    new SqlTokenId("ESCAPEDLITEARL", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.ESCAPEDLITEARL),
                    /** RegularExpression Id. */
                    new SqlTokenId("LITERALEND", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.LITERALEND),
                    /** RegularExpression Id. */
                    new SqlTokenId("LITERALBEGIN", SPECIAL_CATEGORY_NAME, CCJSqlParserConstants.LITERALBEGIN)
        });

        idToToken = new HashMap<>();
        for (SqlTokenId token : tokens) {
            idToToken.put(token.ordinal(), token);
        }
    }

    static synchronized SqlTokenId getToken(int id) {
        if (idToToken == null) {
            init();
        }
        return idToToken.get(id);
    }

    @Override
    protected synchronized Collection<SqlTokenId> createTokenIds() {
        return checkTokens();
    }

    public static Collection<SqlTokenId> checkTokens() {
        if (tokens == null) {
            init();
        }
        return tokens;
    }

    @Override
    protected synchronized Lexer<SqlTokenId> createLexer(LexerRestartInfo<SqlTokenId> info) {
        return new SqlLexer(info);
    }

    @Override
    protected String mimeType() {
        return PLATYPUS_SQL_MIME_TYPE_NAME;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.syntax;

import java.awt.Color;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;

/**
 *
 * @author pk
 */
public class SqlHighlighter implements Highlighting {

    protected static final String STYLES_FILE_NAME = "EasScript.styles.";
    protected static final String STYLES_FILE_EXTENSION = ".xml";
    protected static final String USER_HOME_SYSPROPERTY = "user.home";
    protected static final String DIRECTORIES_SEPARATOR_SYSPROPERTY = "file.separator";
    private static String LANG_NAME = "sql";
    protected static final Map<String, Lexeme> lexemes = new HashMap<>();
    private static SerializableStyles styles = null;
    private static SyntaxTabSet tabs = new SyntaxTabSet();

    public SqlHighlighter() {
        super();
        if (styles == null) {
            styles = loadStyles();
        }
    }

    @Override
    public TabSet getTabs() {
        return tabs;
    }

    @Override
    public List<Style> getStylesAsVector() {
        List<Style> lVector = new ArrayList<>();
        Enumeration<?> lEnum = styles.getStyleNames();
        while (lEnum.hasMoreElements()) {
            Object oName = lEnum.nextElement();
            if (oName != null && oName instanceof String) {
                String stName = (String) oName;
                if (!stName.equals(LexemeStylesNames.SYNTAX_STYLE_EMPTY)
                        && !stName.equals(LexemeStylesNames.SYNTAX_STYLE_DEFAULT)
                        && !stName.equals(LexemeStylesNames.SYNTAX_STYLE_WHITESPACE)
                        && !stName.equals(LexemeStylesNames.SYNTAX_STYLE_MATCHED)
                        && !stName.equals(LexemeStylesNames.SYNTAX_STYLE_UNMATCHED)) {
                    lVector.add(styles.getStyle(stName));
                }
            }
        }
        return lVector;
    }

    public SerializableStyles loadStyles() {
        SerializableStyles lstyles = null;
        BufferedInputStream istrm = null;
        try {
            istrm = new BufferedInputStream(new FileInputStream(getProfileStylesFileName()));
            XMLDecoder xml = new XMLDecoder(istrm);
            lstyles = (SerializableStyles) xml.readObject();
            xml = null;
            if (lstyles == null) {
                lstyles = createDefaultStyles();
            }
        } catch (Exception ex) {
            lstyles = createDefaultStyles();
        } finally {
            try {
                if (istrm != null) {
                    istrm.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (lstyles.getTabSize() < 1) {
            lstyles.setTabSize(1);
            lstyles.setTabPixelSize(12);
        }
        if (lstyles.getTabSize() > 16) {
            lstyles.setTabSize(16);
            lstyles.setTabPixelSize(162);
        }

        tabs.setTabPixelSize(lstyles.getTabPixelSize());
        return lstyles;
    }

    @Override
    public String getSyntaxElementStyleName(String firstSeparator, String secondSeparator, String token) {
        if (firstSeparator != null && isCorrespondingSeparator(firstSeparator, secondSeparator)) {
            if (isStringDelimiter(firstSeparator)) {
                return LexemeStylesNames.SYNTAX_STYLE_STRING;
            } else if (isCommentDelimiter(firstSeparator)) {
                if (isMCommentDelimiter(firstSeparator)) {
                    return LexemeStylesNames.SYNTAX_STYLE_MCOMMENT;
                } else {
                    return LexemeStylesNames.SYNTAX_STYLE_COMMENT;
                }
            }
        } else if (firstSeparator != null && secondSeparator != null) {
            if (token != null && isNumeric(token)) {
                return LexemeStylesNames.SYNTAX_STYLE_NUMBER;
            } else if (token != null && isIdentifier(token)) {
                return LexemeStylesNames.SYNTAX_STYLE_IDENTIFIER;
            }
        }
        return null;
    }

    @Override
    public boolean isOverridingNextSepsSeparator(String firstSep) {
        return (firstSep.equals("'") || firstSep.equals("\"") || firstSep.equals("--") || firstSep.equals("/*"));
    }

    public boolean isCommentDelimiter(String aSep) {
        return (aSep.equals("--") || aSep.equals("/*") || aSep.equals("*/"));
    }

    public boolean isMCommentDelimiter(String aSep) {
        return (aSep.equals("/*") || aSep.equals("*/"));
    }

    static {
        lexemes.clear();
        /*
        String[] keyword = {
        Token.T_AS, Token.T_AND, Token.T_ALL, Token.T_ANY, Token.T_AVG,
        Token.T_BY, Token.T_BETWEEN, Token.T_BOTH, Token.T_CALL,
        Token.T_CASE, Token.T_CASEWHEN, Token.T_CAST, Token.T_CONVERT,
        Token.T_COUNT, Token.T_COALESCE, Token.T_DISTINCT, Token.T_ELSE,
        Token.T_END, Token.T_EVERY, Token.T_EXISTS, Token.T_EXCEPT,
        Token.T_EXTRACT, Token.T_FOR, Token.T_FROM, Token.T_GROUP,
        Token.T_HAVING, Token.T_IF, Token.T_INTO, Token.T_IFNULL,
        Token.T_IS, Token.T_IN, Token.T_INTERSECT, Token.T_JOIN, Token.T_NULL,
        Token.T_INNER, Token.T_LEADING, Token.T_LIKE, Token.T_MAX, Token.T_OFFSET,
        Token.T_MIN, Token.T_NEXT, Token.T_NULLIF, Token.T_NOT, Token.T_LIMIT,
        Token.T_NVL, Token.T_MINUS, Token.T_ON, Token.T_ORDER, Token.T_OR,
        Token.T_OUTER, Token.T_POSITION, Token.T_PRIMARY, Token.T_SELECT,
        Token.T_SET, Token.T_SOME, Token.T_STDDEV_POP, Token.T_STDDEV_SAMP,
        Token.T_SUBSTRING, Token.T_SUM, Token.T_THEN, Token.T_TO,
        Token.T_TRAILING, Token.T_TRIM, Token.T_UNIQUE, Token.T_UNION,
        Token.T_VALUES, Token.T_VAR_POP, Token.T_VAR_SAMP, Token.T_WHEN,
        Token.T_WHERE, Token.T_CONNECT, Token.T_START, Token.T_WITH,
        Token.T_NOCYCLE, Token.T_PRIOR, Token.T_LEVEL, Token.T_RIGHT, Token.T_FULL,
        Token.T_CONNECT_BY_ROOT, Token.T_RECURSIVE, Token.T_LEFT, Token.T_CROSS
        };
        
        for (String kw : keyword) {
        lexemes.put(kw.toLowerCase(), new Lexeme(kw.toLowerCase(), "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        }
        lexemes.put("+", new Lexeme("+", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("-", new Lexeme("-", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("*", new Lexeme("*", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("/", new Lexeme("/", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("=", new Lexeme("=", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("<>", new Lexeme("==", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("<", new Lexeme("<", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put(">", new Lexeme(">", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        
        lexemes.put(" ", new Lexeme(" ", "", LexemeStylesNames.SYNTAX_STYLE_WHITESPACE, "", Lexeme.LEXEME_TYPE_KEYWORD));
        
        lexemes.put(".", new Lexeme(".", "", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_OPENINGBRACE));
        lexemes.put(",", new Lexeme(",", "", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_OPENINGBRACE));
        lexemes.put(";", new Lexeme(";", "", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_OPENINGBRACE));
        lexemes.put(":", new Lexeme(":", "", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_OPENINGBRACE));
        
        
        lexemes.put("(", new Lexeme("(", ")", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_OPENINGBRACE));
        lexemes.put("[", new Lexeme("[", "]", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_OPENINGBRACE));
        lexemes.put("{", new Lexeme("{", "}", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_OPENINGBRACE));
        
        lexemes.put(")", new Lexeme(")", "(", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_CLOSINGBRACE));
        lexemes.put("]", new Lexeme("]", "[", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_CLOSINGBRACE));
        lexemes.put("}", new Lexeme("}", "{", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_CLOSINGBRACE));
        
        lexemes.put("--", new Lexeme("--", "", LexemeStylesNames.SYNTAX_STYLE_COMMENT, "", Lexeme.LEXEME_TYPE_COMMENT));
        
        lexemes.put("/*", new Lexeme("/*", "*\/", LexemeStylesNames.SYNTAX_STYLE_MCOMMENT, "", Lexeme.LEXEME_TYPE_MCOMMENT_OPENINGBRACE));
        lexemes.put("*\/", new Lexeme("*\/", "/*", LexemeStylesNames.SYNTAX_STYLE_MCOMMENT, "", Lexeme.LEXEME_TYPE_MCOMMENT_CLOSINGBRACE));
        
        lexemes.put("'", new Lexeme("'", "'", LexemeStylesNames.SYNTAX_STYLE_STRING, "", Lexeme.LEXEME_TYPE_STRINGBRACE));
        lexemes.put("\"", new Lexeme("\"", "\"", LexemeStylesNames.SYNTAX_STYLE_STRING, "", Lexeme.LEXEME_TYPE_STRINGBRACE));
         */
    }

    public static SerializableStyles createDefaultStyles() {
        SerializableStyles lstyles = new SerializableStyles();
        Style style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_EMPTY);
        if (style == null) {
            lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_EMPTY, null);
        }
        Style oldStyle = null;
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_IDENTIFIER);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_IDENTIFIER, oldStyle);
        }
        StyleConstants.setForeground(style, Color.BLACK);
        StyleConstants.setFontFamily(style, "Courier");
        StyleConstants.setFontSize(style, 12);
        StyleConstants.setItalic(style, false);
        StyleConstants.setBold(style, false);
        StyleConstants.setUnderline(style, false);
//        StyleConstants.setLineSpacing(, );
        oldStyle = style;

        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_COMMENT);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_COMMENT, oldStyle);
        }
        StyleConstants.setForeground(style, Color.GREEN.darker());
        StyleConstants.setItalic(style, true);

        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT, oldStyle);
        }
        StyleConstants.setForeground(style, Color.GREEN.darker().darker());
        StyleConstants.setItalic(style, true);
        style.addAttribute(AbstractDocument.ElementNameAttribute, LexemeStylesNames.SYNTAX_STYLE_MCOMMENT);


        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_CHARACTER);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_CHARACTER, oldStyle);
        }
        StyleConstants.setForeground(style, Color.BLUE.brighter());
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_KEYWORD);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_KEYWORD, oldStyle);
        }
        StyleConstants.setBold(style, true);
        StyleConstants.setForeground(style, Color.BLUE.darker());

        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_NUMBER);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_NUMBER, oldStyle);
        }
        StyleConstants.setForeground(style, Color.CYAN.darker());
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_OPERATOR);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_OPERATOR, oldStyle);
        }
        //StyleConstants.setForeground(style, Color.GREEN);
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_SEPARATOR);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, oldStyle);
        }
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_STRING);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_STRING, oldStyle);
        }
        StyleConstants.setForeground(style, Color.BLUE.brighter());
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_WHITESPACE);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_WHITESPACE, oldStyle);
        }
        //StyleConstants.setForeground(style, style.);
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_MATCHED);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_MATCHED, oldStyle);
        }
        StyleConstants.setBackground(style, Color.GREEN.brighter());
        style = lstyles.getStyle(LexemeStylesNames.SYNTAX_STYLE_UNMATCHED);
        if (style == null) {
            style = lstyles.addStyle(LexemeStylesNames.SYNTAX_STYLE_UNMATCHED, oldStyle);
        }
        StyleConstants.setBackground(style, Color.RED);
        return lstyles;
    }

    @Override
    public void writeStyles() {
        try {
            BufferedOutputStream ostrm = new BufferedOutputStream(new FileOutputStream(getProfileStylesFileName()));
            try (XMLEncoder outxml = new XMLEncoder(ostrm)) {
                outxml.writeObject(styles);
            }
        } catch (Exception lex) {
            Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, lex);
        }
    }

    private String getProfileStylesFileName() {
        String userHome = System.getProperty(USER_HOME_SYSPROPERTY);
        userHome = userHome + System.getProperty(DIRECTORIES_SEPARATOR_SYSPROPERTY) + STYLES_FILE_NAME + getLangName() + STYLES_FILE_EXTENSION;
        return userHome;
    }

    public int getMaximumTokenSize() {
        return 9;
    }

    @Override
    public int getMaximumSeparatorSize() {
        return 2;
    }

    @Override
    public String getTokenStyleName(String key) {
        if (key != null) {
            Lexeme lx = null;
            if (isKeyWord(key)) {
                lx = lexemes.get(key.toLowerCase());
            } else {
                lx = lexemes.get(key);
            }
            if (lx != null) {
                return lx.getHighlightStyleName();
            } else {
                return null;
            }
        }
        return null;
    }
    /*
    public String getBadStyleName(String key)
    {
    Lexeme lx = Lexemes.get(key);
    if(lx != null)
    {
    return lx.getBadHighlightStyleName();
    }else
    return null;
    }
     */

    @Override
    public boolean isKeyWord(String aToken) {
        if (aToken != null) {
            Lexeme llexeme = lexemes.get(aToken.toLowerCase());
            if (llexeme != null) {
                return (llexeme.getType() == Lexeme.LEXEME_TYPE_KEYWORD);
            }
        }
        return false;
    }

    @Override
    public boolean isSeparartor(String aToken) {
        Lexeme llexeme = lexemes.get(aToken);
        if (llexeme != null) {
            return (llexeme.getHighlightStyleName().equals(LexemeStylesNames.SYNTAX_STYLE_OPERATOR)
                    || llexeme.getHighlightStyleName().equals(LexemeStylesNames.SYNTAX_STYLE_SEPARATOR)
                    || llexeme.getHighlightStyleName().equals(LexemeStylesNames.SYNTAX_STYLE_WHITESPACE)
                    || llexeme.getHighlightStyleName().equals(LexemeStylesNames.SYNTAX_STYLE_COMMENT)
                    || llexeme.getHighlightStyleName().equals(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT)
                    || llexeme.getHighlightStyleName().equals(LexemeStylesNames.SYNTAX_STYLE_STRING));
        }
        if (aToken.equals("\n") || aToken.equals("\r")
                || aToken.equals("\t") || aToken.equals("\r\n")
                || aToken.equals("\n\r")) {
            return true;
        }
        return false;
    }

    protected static boolean isStringDelimiter(String aSep) {
        return (aSep.equals("\"") || aSep.equals("'"));
    }

    protected static boolean isNumeric(String token) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp = "[eE]";//"[eE][+-]?"+Digits;
        final String fpRegex =
                ("[\\x00-\\x20]*" + // Optional leading &quot;whitespace&quot;
                "[+-]?(" + // Optional sign character
                "NaN|" + // "NaN" string
                "Infinity|"
                + // "Infinity" string
                // A decimal floating-point string representing a finite positive
                // number without a leading sign has at most five basic pieces:
                // Digits . Digits ExponentPart FloatTypeSuffix
                //
                // Since this method allows integer-only strings as input
                // in addition to strings of floating-point literals, the
                // two sub-patterns below are simplifications of the grammar
                // productions from the Java Language Specification, 2nd
                // edition, section 3.10.2.
                // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|"
                + // . Digits ExponentPart_opt FloatTypeSuffix_opt
                "(\\.(" + Digits + ")(" + Exp + ")?)|"
                + // Hexadecimal strings
                "(("
                + // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "(\\.)?)|"
                + // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")"
                + ")[pP][+-]?" + Digits + "))"
                + "[fFdD]?))"
                + "[\\x00-\\x20]*");// Optional trailing &quot;whitespace&quot;

        return Pattern.matches(fpRegex, token);
    }

    @Override
    public String getSingleCommentSymbol() {
        return "//";
    }

    @Override
    public String getMultilineCommentEndStyleName() {
        return LexemeStylesNames.SYNTAX_STYLE_MCOMMENT;
    }

    @Override
    public String getMultilineCommentStartSeparator() {
        return "/*";
    }

    @Override
    public String getMultilineCommentEndSeparator() {
        return "*/";
    }

    @Override
    public Style getStyle(String styleName) {
        return styles.getStyle(styleName);
    }

    @Override
    public void deleteSettingsFile() {
        File f = new File(getProfileStylesFileName());
        if (f != null) {
            f.delete();
        }
    }

    @Override
    public boolean isMultilineCommentEndSeparator(String aSep) {
        return (aSep != null && aSep.equals("*/"));
    }

    @Override
    public boolean isMultilineCommentsPresent() {
        return true;
    }

    @Override
    public boolean needHighlight1LineTail() {
        return true;
    }

    @Override
    public boolean needHighlightLastLineBegining() {
        return true;
    }

    @Override
    public boolean isSingleLineCommentsPresent() {
        return true;
    }

    @Override
    public boolean isValidMLCommentStart(String line, int inlineMLStartIndex) {
        int singleLineCommentIndex = line.indexOf("//");
        return (inlineMLStartIndex == 0 || singleLineCommentIndex == -1 || singleLineCommentIndex >= inlineMLStartIndex);
    }

    @Override
    public int getTabSize() {
        return styles.getTabSize();
    }

    public int getTabPixelSize() {
        return styles.getTabPixelSize();
    }

    @Override
    public void setTabPixelSize(int tabPixelsize) {
        styles.setTabPixelSize(tabPixelsize);
        tabs.setTabPixelSize(tabPixelsize);
    }

    @Override
    public void setTabSize(int ltabsize) {
        styles.setTabSize(ltabsize);
    }

    @Override
    public void updateStyles() {
        styles = loadStyles();
    }

    public boolean isIdentifier(String token) {
        return (!isNumeric(token.substring(0)) && lexemes.get(token) == null && !isKeyWord(token));
    }

    public String getLangName() {
        return LANG_NAME;
    }

    @Override
    public boolean isOverlappingSeparator(String aSep, String aPrevSep) {

        return ((!aSep.equals("'") || !aSep.equals(aPrevSep))
                && (!aSep.equals("\"") || !aSep.equals(aPrevSep)));
    }

    @Override
    public boolean isCorrespondingSeparator(String firstSep, String secondSep) {
        switch (firstSep) {
            case "'":
                return (secondSep.equals("'") || secondSep.startsWith("\r") || secondSep.startsWith("\n"));
            case "\"":
                return (secondSep.equals("\"") || secondSep.startsWith("\r") || secondSep.startsWith("\n"));
            case "//":
                return (secondSep.startsWith("\r") || secondSep.startsWith("\n"));
            case "/*":
                return (secondSep.startsWith("*/") || secondSep.startsWith("\r") || secondSep.startsWith("\n"));
            default:
                return false;
        }
    }

    @Override
    public boolean isSkippingSeparator(String aSep) {
        return false;
    }
}

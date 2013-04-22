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
 * @author Marat
 */
public class JsHighlighting implements Highlighting {

    protected static final String STYLES_FILE_NAME = "EasScript.styles.";
    protected static final String STYLES_FILE_EXTENSION = ".xml";
    protected static final String USER_HOME_SYSPROPERTY = "user.home";
    protected static final String DIRECTORIES_SEPARATOR_SYSPROPERTY = "file.separator";
    private static String LANG_NAME = "js";
    protected static final Map<String, Lexeme> lexemes = new HashMap<>();
    private static SerializableStyles styles = null;
    private static SyntaxTabSet tabs = new SyntaxTabSet();

    public JsHighlighting() {
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

    static {
        lexemes.put("double", new Lexeme("double", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("enum", new Lexeme("enum", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("extends", new Lexeme("extends", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("final", new Lexeme("final", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("float", new Lexeme("float", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("goto", new Lexeme("goto", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("implements", new Lexeme("implements", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("int", new Lexeme("int", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("interface", new Lexeme("interface", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("long", new Lexeme("long", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("native", new Lexeme("native", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("package", new Lexeme("package", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("private", new Lexeme("private", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("protected", new Lexeme("protected", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("public", new Lexeme("public", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("short", new Lexeme("short", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("static", new Lexeme("static", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("super", new Lexeme("super", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("synchronized", new Lexeme("synchronized", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("throws", new Lexeme("throws", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("transient", new Lexeme("transient", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("void", new Lexeme("void", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("volatile", new Lexeme("volatile", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("abstract", new Lexeme("abstract", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("boolean", new Lexeme("boolean", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("byte", new Lexeme("byte", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("char", new Lexeme("char", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("class", new Lexeme("class", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("const", new Lexeme("const", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("debugger", new Lexeme("debugger", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));


        lexemes.put("function", new Lexeme("function", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("return", new Lexeme("return", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("new", new Lexeme("new", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("in", new Lexeme("in", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("delete", new Lexeme("delete", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("instanceof", new Lexeme("instanceof", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("var", new Lexeme("var", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("try", new Lexeme("try", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("catch", new Lexeme("catch", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("finally", new Lexeme("finally", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("for", new Lexeme("for", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("while", new Lexeme("while", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("do", new Lexeme("do", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("if", new Lexeme("if", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("else", new Lexeme("else", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("null", new Lexeme("null", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("undefined", new Lexeme("undefined", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("break", new Lexeme("break", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("continue", new Lexeme("continue", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("export", new Lexeme("export", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("import", new Lexeme("import", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("switch", new Lexeme("switch", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("case", new Lexeme("case", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("default", new Lexeme("default", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("with", new Lexeme("with", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("Object", new Lexeme("Object", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("typeof", new Lexeme("typeof", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("throw", new Lexeme("throw", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("this", new Lexeme("this", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("true", new Lexeme("true", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("false", new Lexeme("false", "", LexemeStylesNames.SYNTAX_STYLE_KEYWORD, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("+", new Lexeme("+", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("-", new Lexeme("-", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("*", new Lexeme("*", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("/", new Lexeme("/", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("=", new Lexeme("=", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("+=", new Lexeme("+=", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("-=", new Lexeme("-=", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("*=", new Lexeme("*=", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("/=", new Lexeme("/=", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("++", new Lexeme("++", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("--", new Lexeme("--", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("==", new Lexeme("==", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("!=", new Lexeme("!=", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("<", new Lexeme("<", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put(">", new Lexeme(">", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("||", new Lexeme("||", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("&&", new Lexeme("&&", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("|", new Lexeme("|", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("&", new Lexeme("&", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("!", new Lexeme("!", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));
        lexemes.put("?", new Lexeme("?", "", LexemeStylesNames.SYNTAX_STYLE_OPERATOR, "", Lexeme.LEXEME_TYPE_KEYWORD));

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

        lexemes.put("\\\"", new Lexeme("\\\"", "", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_IDENTIFIER));
        lexemes.put("\\'", new Lexeme("\\'", "", LexemeStylesNames.SYNTAX_STYLE_SEPARATOR, "", Lexeme.LEXEME_TYPE_IDENTIFIER));

        lexemes.put("//", new Lexeme("//", "", LexemeStylesNames.SYNTAX_STYLE_COMMENT, "", Lexeme.LEXEME_TYPE_COMMENT));

        lexemes.put("/*", new Lexeme("/*", "*/", LexemeStylesNames.SYNTAX_STYLE_MCOMMENT, "", Lexeme.LEXEME_TYPE_MCOMMENT_OPENINGBRACE));
        lexemes.put("*/", new Lexeme("*/", "/*", LexemeStylesNames.SYNTAX_STYLE_MCOMMENT, "", Lexeme.LEXEME_TYPE_MCOMMENT_CLOSINGBRACE));

        lexemes.put("'", new Lexeme("'", "'", LexemeStylesNames.SYNTAX_STYLE_STRING, "", Lexeme.LEXEME_TYPE_STRINGBRACE));
        lexemes.put("\"", new Lexeme("\"", "\"", LexemeStylesNames.SYNTAX_STYLE_STRING, "", Lexeme.LEXEME_TYPE_STRINGBRACE));

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
        Lexeme lx = lexemes.get(key);
        if (lx != null) {
            return lx.getHighlightStyleName();
        } else {
            return null;
        }
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
        Lexeme llexeme = lexemes.get(aToken);
        if (llexeme != null) {
            return (llexeme.getType() == Lexeme.LEXEME_TYPE_KEYWORD);
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
        return (!isNumeric(token.substring(0)) && lexemes.get(token) == null);
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
    public boolean isOverridingNextSepsSeparator(String firstSep) {
        return (firstSep.equals("'") || firstSep.equals("\"") || firstSep.equals("//") || firstSep.equals("/*"));
    }

    public boolean isCommentDelimiter(String aSep) {
        return (aSep.equals("//") || aSep.equals("/*") || aSep.equals("*/"));
    }

    public boolean isMCommentDelimiter(String aSep) {
        return (aSep.equals("/*") || aSep.equals("*/"));
    }

    @Override
    public boolean isSkippingSeparator(String aSep) {
        return (aSep.equals("\\\"") || aSep.equals("\\'"));
    }
}

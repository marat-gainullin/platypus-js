/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.syntax;

/**
 *
 * @author pk
 */
public class Lexeme {
    public static final int LEXEME_TYPE_IDENTIFIER   = 1;
    public static final int LEXEME_TYPE_KEYWORD      = 2;
    public static final int LEXEME_TYPE_SEPARATOR    = 3;
    public static final int LEXEME_TYPE_OPENINGBRACE = 4;
    public static final int LEXEME_TYPE_CLOSINGBRACE = 5;
    public static final int LEXEME_TYPE_COMMENT      = 6;
    public static final int LEXEME_TYPE_MCOMMENT_OPENINGBRACE = 7;
    public static final int LEXEME_TYPE_MCOMMENT_CLOSINGBRACE = 8;
    public static final int LEXEME_TYPE_CHARACTERBRACE = 9;
    public static final int LEXEME_TYPE_STRINGBRACE = 10;

    protected int Type = LEXEME_TYPE_IDENTIFIER;
    protected String Name = null;
    protected String CorrespondingName = null;
    protected String HighlightStyleName = null;
    protected String BadHighlightStyleName = null;

    public Lexeme(String aName, String aCorrespondingName, String aHighlightStyleName,
                  String aBadHighlightStyleName, int aType)
    {
        Type = aType;
        Name = aName;
        CorrespondingName = aCorrespondingName;
        HighlightStyleName = aHighlightStyleName;
        BadHighlightStyleName = aBadHighlightStyleName;
    }

    public int getType()
    {
        return Type;
    }
    public String getName()
    {
        return Name;
    }
    public String getCorrespondingName()
    {
        return CorrespondingName;
    }

    public String getHighlightStyleName()
    {
        return HighlightStyleName;
    }
    public String getBadHighlightStyleName()
    {
        return BadHighlightStyleName;
    }
}

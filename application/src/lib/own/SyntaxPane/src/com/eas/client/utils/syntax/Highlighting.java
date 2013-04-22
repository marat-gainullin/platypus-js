/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.syntax;

import java.util.List;
import javax.swing.text.Style;
import javax.swing.text.TabSet;

/**
 *
 * @author pk
 */
public interface Highlighting {

    public void deleteSettingsFile();

    public String getSingleCommentSymbol();

    public List<Style> getStylesAsVector();

    public TabSet getTabs();

    public String getTokenStyleName(String separator);

    public boolean isMultilineCommentEndSeparator(String lSep);

    public void setTabPixelSize(int ltabsize);

    public void setTabSize(int ltabsize);

    public void updateStyles();

    public void writeStyles();

    int getMaximumSeparatorSize();

    String getMultilineCommentEndSeparator();

    String getMultilineCommentEndStyleName();

    String getMultilineCommentStartSeparator();

    Style getStyle(String styleName);

    String getSyntaxElementStyleName(String firstSeparator, String secondSeparator, String token);

    boolean isCorrespondingSeparator(String firstSep, String secondSep);

    boolean isMultilineCommentsPresent();

    boolean isOverlappingSeparator(String aSep, String aPrevSep);

    boolean isSkippingSeparator(String aSep);

    boolean isOverridingNextSepsSeparator(String firstSep);

    boolean isSeparartor(String aToken);

    boolean isValidMLCommentStart(String line, int inlineMLStartIndex);

    boolean isKeyWord(String aToken);

    boolean needHighlight1LineTail();

    boolean needHighlightLastLineBegining();

    public boolean isSingleLineCommentsPresent();

    public int getTabSize();
}

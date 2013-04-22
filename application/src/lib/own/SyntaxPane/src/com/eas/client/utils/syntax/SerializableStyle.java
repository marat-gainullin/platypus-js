package com.eas.client.utils.syntax;

import java.awt.Color;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleContext.NamedStyle;

public class SerializableStyle{
    
    private NamedStyle style = null;
    private StyleContext tmpCtx = new StyleContext();
    
    public SerializableStyle()
    {
        super();
        style = (NamedStyle) tmpCtx.addStyle("tmp", null);
    }

    public SerializableStyle(NamedStyle astyle)
    {
        super();
        style = astyle;
    }

    public Style nativeStyle()
    {
        return style;
    }
    
    public String getName() {return style.getName(); }
    public Color getBackground() {return StyleConstants.getBackground(style); }
    public Color getForeground() {return StyleConstants.getForeground(style); }
    public String getFontFamily() {return StyleConstants.getFontFamily(style); }
    public Integer getFontSize() {return StyleConstants.getFontSize(style); }
    public Boolean getItalic() {return StyleConstants.isItalic(style); }
    public Boolean getBold() {return StyleConstants.isBold(style); }
    public Boolean getUnderline() {return StyleConstants.isUnderline(style); }

    public void setName(String name) { style.setName(name); }
    public void setBackground(Color cl) { StyleConstants.setBackground(style, cl); }
    public void setForeground(Color cl) { StyleConstants.setForeground(style, cl); }
    public void setFontFamily(String family) { StyleConstants.setFontFamily(style, family); }
    public void setFontSize(Integer sz) { StyleConstants.setFontSize(style, sz); }
    public void setItalic(Boolean italic) { StyleConstants.setItalic(style, italic); }
    public void setBold(Boolean bold) { StyleConstants.setBold(style, bold); }
    public void setUnderline(Boolean und) { StyleConstants.setUnderline(style, und); }
}

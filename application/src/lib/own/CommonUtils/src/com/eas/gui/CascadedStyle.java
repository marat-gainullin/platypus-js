/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import com.eas.resources.images.IconCache;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.store.Serial;
import com.eas.store.SerialColor;
import com.eas.store.SerialFont;
import java.awt.Color;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class CascadedStyle implements HasPublished {

    private static JSObject publisher;
    //
    protected static JTextField testText = new JTextField();
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected Color background = defaultBackground();
    protected Color foreground = defaultForeground();
    protected Font font = defaultFont();
    protected Integer align = defaultAlign();
    protected Icon icon = null;
    protected Icon folderIcon = null;
    protected Icon openFolderIcon = null;
    protected Icon leafIcon = null;
    protected String iconName = null;
    protected String folderIconName = null;
    protected String openFolderIconName = null;
    protected String leafIconName = null;
    protected CascadedStyle parent = null;
    protected Object published;

    public CascadedStyle() {
        super();
    }
    
    private static final String CONSTRUCTOR_JS_DOC = "/**\n"
            + "* Style object. Can inherit propeties from the parent style object.\n"
            + "* @param parent a parent <code>Style</code> (optional)\n"
            + "*/";
    @ScriptFunction(name="Style", params = {"parent"}, jsDoc = CONSTRUCTOR_JS_DOC)
    public CascadedStyle(CascadedStyle aParent) {
        this();
        parent = aParent;
        background = null;
        foreground = null;
        font = null;
        align = null;
        icon = null;
        folderIcon = null;
        openFolderIcon = null;
        leafIcon = null;
    }

    private static final String ICON_JS_DOC = "/**\n"
            + "* An icon associated with this style.\n"
            + "*/";
    @ScriptFunction(jsDoc = ICON_JS_DOC)
    public Icon getIcon() {
        return icon != null ? icon : (parent != null ? parent.getIcon() : null);
    }
    
    public void setIcon(Icon aValue) {
        Icon oldValue = icon;
        icon = aValue;
        changeSupport.firePropertyChange("icon", oldValue, icon);
    }
    
    private static final String FOLDER_ICON_JS_DOC = "/**\n"
            + "* A forlder icon associated with this style.\n"
            + "*/";
    @ScriptFunction(jsDoc = FOLDER_ICON_JS_DOC)
    public Icon getFolderIcon() {
        return folderIcon != null ? folderIcon : (parent != null ? parent.getFolderIcon() : null);
    }

    public void setFolderIcon(Icon aValue) {
        Icon oldValue = folderIcon;
        folderIcon = aValue;
        changeSupport.firePropertyChange("folderIcon", oldValue, folderIcon);
    }
    
    private static final String OPEN_FOLDER_ICON_JS_DOC = "/**\n"
            + "* An open forlder icon associated with this style.\n"
            + "*/";
    @ScriptFunction(jsDoc = OPEN_FOLDER_ICON_JS_DOC)
    public Icon getOpenFolderIcon() {
        return openFolderIcon != null ? openFolderIcon : (parent != null ? parent.getOpenFolderIcon() : null);
    }

    public void setOpenFolderIconName(String aValue) {
        setNativeOpenFolderIconName(aValue);
    }
    
    private static final String LEAF_ICON_JS_DOC = "/**\n"
            + "* A leaf icon associated with this style.\n"
            + "*/";
    @ScriptFunction(jsDoc = LEAF_ICON_JS_DOC)
    public Icon getLeafIcon() {
        return leafIcon != null ? leafIcon : (parent != null ? parent.getLeafIcon() : null);
    }

    public void setLeafIcon(Icon aValue) {
        Icon oldValue = leafIcon;
        leafIcon = aValue;
        changeSupport.firePropertyChange("leafIcon", oldValue, leafIcon);
    }
    
    private static final String FONT_JS_DOC = "/**\n"
            + "* A font associated with this style.\n"
            + "*/";
    @ScriptFunction(jsDoc = FONT_JS_DOC)
    public Font getFont() {
        return font != null ? font : (parent != null ? parent.getFont() : defaultFont());
    }
    
    public void setFont(Font aValue) {
        Font oldValue = font;
        font = aValue;
        changeSupport.firePropertyChange("font", oldValue, font);
    }    
    
    private static final String FOREGROUND_JS_DOC = "/**\n"
            + "* A foreground color associated with this style.\n"
            + "*/";
    @ScriptFunction(jsDoc = FOREGROUND_JS_DOC)
    public Color getForeground() {
        return foreground != null ? foreground : (parent != null ? parent.getForeground() : defaultForeground());
    }
    
    public void setForeground(Color aValue) {
        Color oldValue = foreground;
        foreground = aValue;
        changeSupport.firePropertyChange("foreground", oldValue, foreground);
    }
    
    
    private static final String BACKGROUND_JS_DOC = "/**\n"
            + "* A background color associated with this style.\n"
            + "*/";
    @ScriptFunction(jsDoc = BACKGROUND_JS_DOC)
    public Color getBackground() {
        return background != null ? background : (parent != null ? parent.getBackground() : defaultBackground());
    }
    
    public void setBackground(Color aValue) {
        Color oldValue = background;
        background = aValue;
        changeSupport.firePropertyChange("background", oldValue, background);
    }
    
    
    private static final String ALIGN_JS_DOC = "/**\n"
            + "* An align constraint associated with this style:\n"
            + "* CENTER = 0; TOP = 1; LEFT = 2; BOTTOM = 3; RIGHT = 4.\n"
            + "*/";
    @ScriptFunction(jsDoc = ALIGN_JS_DOC)
    public Integer getAlign() {
        return align != null ? align : (parent != null ? parent.getAlign() : defaultAlign());
    }
    
    public void setAlign(Integer aValue) {
        Integer oldValue = align;
        align = aValue;
        changeSupport.firePropertyChange("align", oldValue, align);
    }
    
    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }
    
    protected void assign(CascadedStyle aSource) {
        if (aSource.getBackground() != null) {
            Color col = aSource.getBackground();
            background = new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
        } else {
            background = null;
        }
        if (aSource.getForeground() != null) {
            Color col = aSource.getForeground();
            foreground = new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
        } else {
            foreground = null;
        }

        font = new Font(aSource.getFont().getFamily(), aSource.getFont().getStyle(), aSource.getFont().getSize());
        align = aSource.getNativeAlign();

        if (aSource.getNativeIconName() != null) {
            setNativeIconName(new String(aSource.getNativeIconName().toCharArray()));
        } else {
            icon = null;
            iconName = null;
        }
        if (aSource.getNativeFolderIconName() != null) {
            setNativeFolderIconName(new String(aSource.getNativeFolderIconName().toCharArray()));
        } else {
            folderIcon = null;
            folderIconName = null;
        }
        if (aSource.getNativeOpenFolderIconName() != null) {
            setNativeOpenFolderIconName(new String(aSource.getNativeOpenFolderIconName().toCharArray()));
        } else {
            openFolderIcon = null;
            openFolderIconName = null;
        }
        if (aSource.getNativeLeafIconName() != null) {
            setNativeLeafIconName(new String(aSource.getNativeLeafIconName().toCharArray()));
        } else {
            leafIcon = null;
            leafIconName = null;
        }
    }

    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CascadedStyle other = (CascadedStyle) obj;
        if (this.background != other.background && (this.background == null || !this.background.equals(other.background))) {
            return false;
        }
        if (this.foreground != other.foreground && (this.foreground == null || !this.foreground.equals(other.foreground))) {
            return false;
        }
        if (this.font != other.font && (this.font == null || !this.font.isEqual(other.font))) {
            return false;
        }
        if (this.align != other.align) {
            return false;
        }
        if (this.iconName != other.iconName && (this.iconName == null || !this.iconName.equals(other.iconName))) {
            return false;
        }
        if (this.folderIconName != other.folderIconName && (this.folderIconName == null || !this.folderIconName.equals(other.folderIconName))) {
            return false;
        }
        if (this.openFolderIconName != other.openFolderIconName && (this.openFolderIconName == null || !this.openFolderIconName.equals(other.openFolderIconName))) {
            return false;
        }
        if (this.leafIconName != other.leafIconName && (this.leafIconName == null || !this.leafIconName.equals(other.leafIconName))) {
            return false;
        }
        return true;
    }
    
    protected static Font defFont;
 
    public static String encodeColor(Color aValue) {
        StringBuilder sb = new StringBuilder();
        sb.append('#');
        String hex = Integer.toHexString(0x00ffffff & aValue.getRGB());
        for (int i = 0; i < 6 - hex.length(); i++) {
            sb.append('0');
        }
        sb.append(hex);
        return sb.toString();
    }

    public static Color darkerColor(Color color, double factorColor) {
        if (color == null) {
            return null;
        }
        return new Color(Math.max((int) (color.getRed() * factorColor), 0),
                Math.max((int) (color.getGreen() * factorColor), 0),
                Math.max((int) (color.getBlue() * factorColor), 0),
                color.getAlpha());
    }

    public static Color brighterColor(Color color, double factorColor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int) (1.0 / (1.0 - factorColor));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }

        return new Color(Math.min((int) (r / factorColor), 255),
                Math.min((int) (g / factorColor), 255),
                Math.min((int) (b / factorColor), 255),
                alpha);
    }

    public static Font defaultFont() {
        if (defFont == null) {
            defFont = new Font(testText.getFont().getFamily(), nativeFontStyleToFontStyle(testText.getFont()), testText.getFont().getSize());
        }
        return defFont;
    }

    public static Color defaultBackground() {
        return testText.getBackground();
    }

    public static Color defaultForeground() {
        return testText.getForeground();
    }

    public static int defaultAlign() {
        return SwingConstants.LEFT;
    }

    public CascadedStyle getParent() {
        return parent;
    }

    public void setParent(CascadedStyle style) {
        parent = style;
    }

    @Serial
    public SerialFont getNativeFont() {
        return font != null ? new SerialFont(new java.awt.Font(font.getFamily(), fontStyleToNativeFontStyle(font.getStyle()), font.getSize())) : null;
    }

    @Serial
    public String getBackgroundColor() {
        return background != null ? encodeColor(background) : null;
    }

    @Serial
    public void setBackgroundColor(String aValue) {
        if (aValue != null) {
            background = Color.decode(aValue);
        } else {
            background = null;
        }
    }

    @Serial
    public String getForegroundColor() {
        return foreground != null ? encodeColor(foreground) : null;
    }

    @Serial
    public void setForegroundColor(String aValue) {
        if (aValue != null) {
            foreground = Color.decode(aValue);
        } else {
            foreground = null;
        }
    }

    @Serial
    public Integer getNativeAlign() {
        return align;
    }

    @Serial
    public void setNativeFont(SerialFont aFont) {
        if (aFont != null) {
            font = new Font(aFont.getDelegate().getFamily(), nativeFontStyleToFontStyle(aFont.getDelegate()), aFont.getDelegate().getSize());
        }
    }

    @Serial
    public void setNativeBackground(SerialColor aBackground) {
        if (aBackground != null) {
            background = aBackground.getDelegate();
        }
    }

    @Serial
    public void setNativeForeground(SerialColor aForeground) {
        if (aForeground != null) {
            foreground = aForeground.getDelegate();
        }
    }

    @Serial
    public void setNativeAlign(Integer align) {
        this.align = align;
    }

    @Serial
    public String getNativeIconName() {
        return iconName;
    }

    @Serial
    public String getNativeFolderIconName() {
        return folderIconName;
    }

    @Serial
    public String getNativeOpenFolderIconName() {
        return openFolderIconName;
    }

    @Serial
    public String getNativeLeafIconName() {
        return leafIconName;
    }

    @Serial
    public void setNativeIconName(String aIconName) {
        iconName = aIconName;
        icon = IconCache.getIcon(aIconName);
    }

    @Serial
    public void setNativeFolderIconName(String aIconName) {
        folderIconName = aIconName;
        folderIcon = IconCache.getIcon(aIconName);
    }

    @Serial
    public void setNativeOpenFolderIconName(String aIconName) {
        openFolderIconName = aIconName;
        openFolderIcon = IconCache.getIcon(aIconName);
    }

    @Serial
    public void setNativeLeafIconName(String aIconName) {
        leafIconName = aIconName;
        leafIcon = IconCache.getIcon(aIconName);
    }

    public String getIconName() {
        return iconName != null ? iconName : (parent != null ? parent.getIconName() : null);
    }

    public String getFolderIconName() {
        return folderIconName != null ? folderIconName : (parent != null ? parent.getFolderIconName() : null);
    }

    public String getOpenFolderIconName() {
        return openFolderIconName != null ? openFolderIconName : (parent != null ? parent.getOpenFolderIconName() : null);
    }

    public String getLeafIconName() {
        return leafIconName != null ? leafIconName : (parent != null ? parent.getLeafIconName() : null);
    }

    public void setIconName(String aValue) {
        setNativeIconName(aValue);
    }

    public void setFolderIconName(String aValue) {
        setNativeFolderIconName(aValue);
    }

    public void setLeafIconName(String aValue) {
        setNativeLeafIconName(aValue);
    }

    public void setOpenFolderIcon(Icon aValue) {
        Icon oldValue = openFolderIcon;
        openFolderIcon = aValue;
        changeSupport.firePropertyChange("openFolderIcon", oldValue, openFolderIcon);
    }

    public CascadedStyle copy() {
        CascadedStyle copiedStyle = new CascadedStyle();
        copiedStyle.assign(this);
        return copiedStyle;
    }

    public static int fontStyleToNativeFontStyle(int aFontStyle) {
        int nativeStyle = java.awt.Font.PLAIN;
        if (aFontStyle == FontStyle.ITALIC) {
            nativeStyle = java.awt.Font.ITALIC;
        } else if (aFontStyle == FontStyle.BOLD) {
            nativeStyle = java.awt.Font.BOLD;
        } else if (aFontStyle == FontStyle.BOLD_ITALIC) {
            nativeStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
        }
        return nativeStyle;
    }

    public static int nativeFontStyleToFontStyle(java.awt.Font aFont) {
        int style = FontStyle.NORMAL;
        if (aFont.isBold()) {
            style = aFont.isItalic() ? FontStyle.BOLD_ITALIC : FontStyle.BOLD;
        } else if (aFont.isItalic()) {
            style = FontStyle.ITALIC;
        }
        return style;
    }
    
    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{});
        }
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
    
}

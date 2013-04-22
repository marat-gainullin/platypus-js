package com.eas.client.utils.syntax;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.text.StyleContext;

public class SerializableStyles extends StyleContext {

    private int tabSize = 4;
    private int tabPixelSize = 44;

    public SerializableStyles() {
        super();
    }

    public SerializableStyle[] getStyles() {
        @SuppressWarnings("unchecked")
        Enumeration<String> stylesEnum = (Enumeration<String>) getStyleNames();
        List<SerializableStyle> vct = new ArrayList<>();
        while (stylesEnum.hasMoreElements()) {
            vct.add(new SerializableStyle((NamedStyle) getStyle(stylesEnum.nextElement())));
        }
        SerializableStyle[] outStyles = new SerializableStyle[vct.size()];
        for (int i = 0; i < vct.size(); i++) {
            outStyles[i] = vct.get(i);
        }
        return outStyles;
    }

    public void setStyles(SerializableStyle[] styles) {
        for (int i = 0; i < styles.length; i++) {
            NamedStyle st = (NamedStyle) addStyle(styles[i].getName(), null);
            st.addAttributes(styles[i].nativeStyle());
        }
    }

    public int getTabSize() {
        return tabSize;
    }

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    public int getTabPixelSize() {
        return tabPixelSize;
    }

    public void setTabPixelSize(int tabPixelSize) {
        this.tabPixelSize = tabPixelSize;
    }
}

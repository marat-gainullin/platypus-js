package com.eas.form;

import com.eas.core.Utils;
import com.eas.ui.DefaultUiReader;
import com.eas.ui.Widget;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Element;

/**
 *
 * @author mg
 */
public class FormReader extends DefaultUiReader {

    protected Form form;

    public FormReader(Element anElement, JavaScriptObject aModel) {
        super(anElement, aModel);
    }

    public Form getForm() {
        if (form == null) {
            form = new Form((Widget) viewWidget);
            form.setDefaultCloseOperation(Utils.getIntegerAttribute(element, "dco", "defaultCloseOperation", 2));
            String iconImage = Utils.getAttribute(element, "i", "icon", null);
            if (iconImage != null && !iconImage.isEmpty()) {
                form.setIcon(iconImage);
            }
            form.setTitle(Utils.getAttribute(element, "tl", "title", null));
            form.setClosable(Utils.getBooleanAttribute(element, "cle", "closable", Boolean.TRUE));
            form.setMaximizable(Utils.getBooleanAttribute(element, "mxe", "maximizable", Boolean.TRUE));
            form.setMinimizable(Utils.getBooleanAttribute(element, "mne", "minimizable", Boolean.TRUE));
            form.setResizable(Utils.getBooleanAttribute(element, "rs", "resizable", Boolean.TRUE));
            form.setUndecorated(Utils.getBooleanAttribute(element, "udr", "undecorated", Boolean.FALSE));
            form.setOpacity(Utils.getFloatAttribute(element, "opc", "opacity", 1.0f));
            form.setAlwaysOnTop(Utils.getBooleanAttribute(element, "aot", "alwaysOnTop", Boolean.FALSE));
            form.setLocationByPlatform(Utils.getBooleanAttribute(element, "lbp", "locationByPlatform", Boolean.TRUE));
            // form.setDesignedViewSize(viewWidget.getPreferredSize());
        }
        return form;
    }
}

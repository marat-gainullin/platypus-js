package com.eas.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.CallbackAdapter;
import com.eas.core.Utils;
import com.eas.ui.DefaultUiReader;
import com.eas.ui.PlatypusImageResource;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;

/**
 * 
 * @author mg
 */
public class FormReader extends DefaultUiReader {

	protected PlatypusWindow form;

	public FormReader(Element anElement, JavaScriptObject aModel) {
		super(anElement, aModel);
	}

	public PlatypusWindow getForm() {
                if(form == null){
                    form = new PlatypusWindow((Widget) viewWidget);
                    form.setDefaultCloseOperation(Utils.getIntegerAttribute(element, "dco", "defaultCloseOperation", 2));
                    String iconImage = Utils.getAttribute(element, "i", "icon", null);
                    if (iconImage != null && !iconImage.isEmpty()) {
                            PlatypusImageResource.load(iconImage, new CallbackAdapter<ImageResource, String>() {
                                    @Override
                                    protected void doWork(ImageResource aResult) throws Exception {
                                            form.setIcon(aResult);
                                    }

                                    @Override
                                    public void onFailure(String reason) {
                                            Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, "Factory failed to load window title icon. " + reason);
                                    }
                            });
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

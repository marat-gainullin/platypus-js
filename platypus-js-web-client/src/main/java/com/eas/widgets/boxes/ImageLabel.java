package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 * @author mg
 */
public class ImageLabel extends ImageParagraph {

    protected static Element createLabelContainer() {
        Element container = Document.get().createDivElement();
        container.setAttribute("class", "label");
        container.getStyle().setProperty("outline", "none");
        return container;
    }

    public ImageLabel(String aTitle, boolean asHtml, ImageResource aImage) {
        super(createLabelContainer(), aTitle, asHtml, aImage);
    }

    public ImageLabel(String aTitle, boolean asHtml) {
        this(aTitle, asHtml, null);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 * @author mg
 */
public class ImageLabel extends ImageParagraph {

    protected static Element createLabelContainer() {
        Element container = Document.get().createDivElement();
        container.setAttribute("class", "gwt-Label");
        return container;
    }

    public ImageLabel(String aTitle, boolean asHtml, ImageResource aImage) {
        super(createLabelContainer(), aTitle, asHtml, aImage);
    }

    public ImageLabel(String aTitle, boolean asHtml) {
        this(aTitle, asHtml, null);
    }

}

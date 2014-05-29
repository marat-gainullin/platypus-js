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
public class ImageButton extends ImageParagraph {

    protected static Element createButtonContainer() {
        Element container = Document.get().createPushButtonElement();
        container.setAttribute("class", "gwt-Button");
        return container;
    }

    public ImageButton(String aTitle, boolean asHtml, ImageResource aImage) {
        super(createButtonContainer(), aTitle, asHtml, aImage);
    }

    public ImageButton(String aTitle, boolean asHtml) {
        this(aTitle, asHtml, null);
    }

    @Override
    protected void organizeVerticalAlignment(Style contentStyle) {
        switch (verticalAlignment) {
            case TOP: {
                int widgetHeight = getElement().getClientHeight();
                int pHeight = content.getOffsetHeight();
                int topValue = (widgetHeight - pHeight) / 2;
                contentStyle.setTop(-topValue, Style.Unit.PX);
                contentStyle.clearBottom();
            }
            break;
            case BOTTOM: {
                int widgetHeight = getElement().getClientHeight();
                int pHeight = content.getOffsetHeight();
                int topValue = (widgetHeight - pHeight) / 2;
                contentStyle.setTop(topValue, Style.Unit.PX);
                contentStyle.clearBottom();
                break;
            }
            case CENTER: {
                contentStyle.clearTop();
                contentStyle.clearBottom();
                break;
            }
        }
    }

}

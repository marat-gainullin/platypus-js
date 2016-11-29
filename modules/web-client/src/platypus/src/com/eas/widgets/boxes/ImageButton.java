/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author mg
 */
public class ImageButton extends ImageParagraph {

	public ImageButton(String aTitle, boolean asHtml, ImageResource aImage) {
		this(Document.get().createPushButtonElement(), aTitle, asHtml, aImage);
	}

	public ImageButton(Element aContainer, String aTitle, boolean asHtml, ImageResource aImage) {
		super(aContainer, aTitle, asHtml, aImage);
		horizontalAlignment = ImageParagraph.CENTER;
		addStyleName("btn");
		setStylePrimaryName("btn");
		setStyleDependentName("default", true);
	}

	public ImageButton(String aTitle, boolean asHtml) {
		this(aTitle, asHtml, null);
	}
}

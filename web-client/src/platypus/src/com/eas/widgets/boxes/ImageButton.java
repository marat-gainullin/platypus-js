/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author mg
 */
public class ImageButton extends ImageParagraph {

	protected String styleSuffix = "default";

	public ImageButton(String aTitle, boolean asHtml, ImageResource aImage) {
		super(Document.get().createPushButtonElement(), aTitle, asHtml, aImage);
		horizontalAlignment = ImageParagraph.CENTER;
		addStyleName("btn");
		setStylePrimaryName("btn");
		setStyleDependentName(styleSuffix, true);
	}

	public ImageButton(String aTitle, boolean asHtml) {
		this(aTitle, asHtml, null);
	}

	public String getStyleSuffix() {
		return styleSuffix;
	}

	public void setStyleSuffix(String aValue) {
		if (styleSuffix == null ? aValue != null : !styleSuffix.equals(aValue)) {
			setStyleDependentName(styleSuffix, false);
			styleSuffix = aValue;
			setStyleDependentName(styleSuffix, true);
		}
	}
}

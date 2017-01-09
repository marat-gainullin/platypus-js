package com.eas.widgets.boxes;

import com.google.gwt.dom.client.Document;
import com.google.gwt.text.client.DoubleParser;
import com.google.gwt.text.client.DoubleRenderer;

public class ExplicitDoubleBox extends ExplicitValueBox<Double> {

	public ExplicitDoubleBox() {
		super(Document.get().createTextInputElement(), DoubleRenderer.instance(), DoubleParser.instance());
	}

}

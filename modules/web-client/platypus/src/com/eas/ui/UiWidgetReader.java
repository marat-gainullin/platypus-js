package com.eas.ui;

import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.xml.client.Element;

public interface UiWidgetReader {

	public UIObject readWidget(Element anElement, final UiReader aFactory) throws Exception;
}

package com.eas.ui;

import com.google.gwt.xml.client.Element;

public interface UiWidgetReader {

    public Widget readWidget(Element anElement, final UiReader aFactory) throws Exception;
}

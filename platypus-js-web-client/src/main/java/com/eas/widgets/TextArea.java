package com.eas.widgets;

import com.eas.ui.HasScroll;
import com.eas.ui.HorizontalScrollFiller;
import com.eas.ui.VerticalScrollFiller;
import com.google.gwt.dom.client.Document;

public class TextArea extends TextField implements HasScroll, HorizontalScrollFiller, VerticalScrollFiller {

    public TextArea() {
        super(Document.get().createTextAreaElement());
        element.setAttribute("wrap", "off");
    }
}

package com.eas.ui;

import com.eas.ui.Widget;

public interface HasLayers {

    public void toFront(Widget aWidget);

    public void toFront(Widget aWidget, int aCount);

    public void toBack(Widget aWidget);

    public void toBack(Widget aWidget, int aCount);

}

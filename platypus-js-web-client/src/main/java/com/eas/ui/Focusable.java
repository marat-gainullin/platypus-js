package com.eas.ui;

/**
 *
 * @author mgainullin TODO: check widgets againts focusable capability
 */
public interface Focusable {

    void setFocus(boolean aValue);

    int getTabIndex();

    void setTabIndex(int tabindex);
}

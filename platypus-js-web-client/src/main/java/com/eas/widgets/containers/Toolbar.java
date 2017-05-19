package com.eas.widgets.containers;

/**
 * Toolbar is horizontal only box.
 * Toolbar takes into account width, when a component is visible in
 * content area and sets it's height to 100%.
 *
 * @author mg
 */
public class Toolbar extends Box {

    // TODO: Add carousel like in tabs
    public Toolbar() {
        super();
        element.addClassName("toolbar");
        element.addClassName("btn-group");
    }

    @Override
    protected void applyOrientation(int aValue) {
        // no op since toolbar is horizontal only
    }

}

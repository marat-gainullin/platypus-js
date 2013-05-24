/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell.SpinnerFieldAppearance;
import com.sencha.gxt.core.client.dom.XElement;

public class SpinnerFieldDefaultAppearance extends TwinTriggerFieldDefaultAppearance implements SpinnerFieldAppearance {

  public interface SpinnerFieldResources extends TwinTriggerFieldResources {
    @Override
    @Source("spinnerUp.png")
    public ImageResource triggerArrow();

    @Override
    @Source("spinnerUpClick.png")
    public ImageResource triggerArrowClick();

    @Override
    @Source("spinnerUpOver.png")
    public ImageResource triggerArrowOver();

    @Override
    @Source("spinnerUpFocus.png")
    public ImageResource triggerArrowFocus();

    @Override
    @Source("spinnerUpFocusClick.png")
    public ImageResource triggerArrowFocusClick();

    @Override
    @Source("spinnerUpFocusOver.png")
    public ImageResource triggerArrowFocusOver();

    @Source("spinnerDown.png")
    public ImageResource twinTriggerArrow();

    @Source("spinnerDownClick.png")
    public ImageResource twinTriggerArrowClick();

    @Source("spinnerDownOver.png")
    public ImageResource twinTriggerArrowOver();

    @Source("spinnerDownFocus.png")
    public ImageResource twinTriggerArrowFocus();

    @Source("spinnerDownFocusClick.png")
    public ImageResource twinTriggerArrowFocusClick();

    @Source("spinnerDownFocusOver.png")
    public ImageResource twinTriggerArrowFocusOver();

    @Override
    @Source({"ValueBaseField.css", "TextField.css", "TriggerField.css", "SpinnerField.css"})
    SpinnerFieldStyle css();
  }

  public interface SpinnerFieldStyle extends TwinTriggerFieldStyle {

  }

  public SpinnerFieldDefaultAppearance() {
    this(GWT.<SpinnerFieldResources> create(SpinnerFieldResources.class));
  }

  public SpinnerFieldDefaultAppearance(TwinTriggerFieldResources res) {
    super(res);
  }

  @Override
  public void onTwinTriggerClick(XElement parent, boolean click) {

  }
}

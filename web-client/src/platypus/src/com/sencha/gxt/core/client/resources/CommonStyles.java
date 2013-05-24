/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Non-theme related common styles.
 */
public class CommonStyles {

  public interface CommonStylesAppearance {
    ImageResource shim();

    Styles styles();
  }

  public static class CommonStylesDefaultAppearance implements CommonStylesAppearance {
    
    public interface CommonDefaultResources extends ClientBundle {

      ImageResource shim();

      @Source("CommonStyles.css")
      CommonDefaultStyles styles();

    }

    public interface CommonDefaultStyles extends Styles {
      @ClassName("x-clear")
      @Override
      String clear();
    }

    private final CommonDefaultResources bundle;
    private final Styles styles;
    
    public CommonStylesDefaultAppearance() {
      bundle = GWT.create(CommonDefaultResources.class);
      styles = bundle.styles();
    }

    @Override
    public ImageResource shim() {
      return bundle.shim();
    }
    @Override
    public Styles styles() {
      return styles;
    }
  }

  public interface Styles extends CssResource {

    String clear();

    String columnResize();

    String columnRowResize();

    String disabled();

    String floatRight();

    String hideDisplay();

    String hideOffsets();

    String hideVisibility();

    String ignore();

    String inlineBlock();

    String nodrag();

    String noFocusOutline();

    String nowrap();

    String positionable();

    String repaint();

    String shim();

    String unselectable();

    String unselectableSingle();

  }

  private final CommonStylesAppearance appearance;

  private static CommonStyles instance;

  /**
   * Returns the singleton instance.
   * 
   * @return the common styles
   */
  public static Styles get() {
    if (instance == null) {
      instance = new CommonStyles();
    }
    return instance.appearance.styles();
  }

  private CommonStyles() {
    this.appearance = GWT.create(CommonStylesAppearance.class);

    StyleInjectorHelper.ensureInjected(this.appearance.styles(), true);
  }

}

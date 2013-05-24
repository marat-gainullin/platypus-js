/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form.error;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.WidgetComponent;
import com.sencha.gxt.widget.core.client.tips.Tip.TipAppearance;
import com.sencha.gxt.widget.core.client.tips.ToolTip;

public class SideErrorHandler implements ErrorHandler {
  
  /**
   * Marker interface to indicate that we want a slightly different appearance than usual,
   * to indicate that this is an error, and not help text.
   */
  public interface SideErrorTooltipAppearance extends TipAppearance {
    
  }

  public interface SideErrorResources extends ClientBundle {

    @Source("exclamation.gif")
    ImageResource errorIcon();
  }

  private class Handler implements AttachEvent.Handler {

    @Override
    public void onAttachOrDetach(AttachEvent event) {
      if (event.isAttached()) {
        doAttach();
      } else {
        doDetach();
      }
    }

  }

  protected Widget target;
  protected WidgetComponent errorIcon;
  protected final SideErrorResources resources;
  protected ToolTip tip;

  private boolean showingError;
  private boolean adjustTargetWidth = true;
  private Handler handler = new Handler();
  private int originalWidth = -1;

  public SideErrorHandler(Widget target) {
    this.target = target;

    target.addAttachHandler(handler);

    if (target.isAttached()) {
      doAttach();
    }

    resources = GWT.create(SideErrorResources.class);
  }

  @Override
  public void clearInvalid() {
    if (errorIcon != null) {
      if (adjustTargetWidth) {
        target.setWidth(originalWidth + "px");
      }
      ComponentHelper.doDetach(errorIcon);
      errorIcon.hide();
      target.getElement().setAttribute("aria-describedby", "");
      showingError = false;
    }
  }

  public boolean isAdjustTargetWidth() {
    return adjustTargetWidth;
  }
  
  @Override
  public void markInvalid(List<EditorError> errors) {
    if (errors.size() == 0) {
      clearInvalid();
      return;
    }
    
    String error = errors.get(0).getMessage();
    
    if (showingError && tip != null) {
      tip.getToolTipConfig().setBodyText(error);
      tip.update(tip.getToolTipConfig());
      return;
    }
    showingError = true;
    


    if (errorIcon == null) {
      errorIcon = new WidgetComponent(new Image(resources.errorIcon()));
      errorIcon.setHideMode(HideMode.VISIBILITY);
      errorIcon.hide();

      Element p = target.getElement().getParentElement();
      p.appendChild(errorIcon.getElement());

      errorIcon.getElement().setDisplayed(true);
      errorIcon.getElement().makePositionable(true);

    } else if (!errorIcon.getElement().isConnected()) {
      errorIcon.setHideMode(HideMode.VISIBILITY);
      errorIcon.hide();
      Element p = target.getElement().getParentElement();
      p.appendChild(errorIcon.getElement());
    }
    
    if (tip == null) {
      tip = new ToolTip(errorIcon, GWT.<SideErrorTooltipAppearance>create(SideErrorTooltipAppearance.class));
    }
    
    
    if (!errorIcon.isAttached()) {
      ComponentHelper.doAttach(errorIcon);
    }
    
    if (adjustTargetWidth) {
      int w = target.getElement().<XElement>cast().getStyleSize().getWidth();

      if (w != -1) {
        originalWidth = w;
        target.setWidth(w - 18 + "px");
      }
    }

    errorIcon.getElement().setVisibility(false);
    errorIcon.show();
    alignErrorIcon();

    // needed to prevent flickering
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        if (errorIcon.isAttached()) {
          errorIcon.show();
          alignErrorIcon();
          errorIcon.getElement().setVisibility(true);
        }
      }
    });
    


    tip.getToolTipConfig().setBodyText(error);
    tip.update(tip.getToolTipConfig());
  }

  public void setAdjustTargetWidth(boolean adjustTargetWidth) {
    this.adjustTargetWidth = adjustTargetWidth;
  }

  protected void alignErrorIcon() {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        Element input = null;//target.getElement().<XElement> cast().selectNode("input");
        if (input == null) {
          input = target.getElement();
        }
        errorIcon.getElement().alignTo(input, new AnchorAlignment(Anchor.TOP_LEFT, Anchor.TOP_RIGHT, false),
            new int[] {2, 3});
      }
    });
  }

  protected void doAttach() {
    ComponentHelper.doAttach(errorIcon);
  }

  protected void doDetach() {
    ComponentHelper.doDetach(errorIcon);
  }

}
/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.cell.core.client.form.FieldCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.tips.Tip;

public class SliderCell extends FieldCell<Integer> {

  public interface HorizontalSliderAppearance extends SliderAppearance {

  }

  public interface SliderAppearance extends FieldAppearance {

    int getClickedValue(Context context, Element parent, NativeEvent event);

    int getSliderLength(XElement parent);

    Element getThumb(Element parent);

    boolean isVertical();

    void onMouseDown(Context context, Element parent, NativeEvent event);

    void onMouseOut(Context context, Element parent, NativeEvent event);

    void onMouseOver(Context context, Element parent, NativeEvent event);

    void onMouseUp(Context context, Element parent, NativeEvent event);

    void render(double fractionalValue, int width, int height, SafeHtmlBuilder sb);

    void setThumbPosition(Element parent, int left);

  }
  public interface VerticalSliderAppearance extends SliderAppearance {
    
  }

  protected class DragPreview extends BaseEventPreview {

    private final Context context;
    private final Element parent;
    private int thumbWidth;
    private int thumbHeight;

    private final ValueUpdater<Integer> valueUpdater;

    public DragPreview(Context context, Element parent, ValueUpdater<Integer> valueUpdater, NativeEvent e) {
      super();
      this.context = context;
      this.parent = parent;
      this.valueUpdater = valueUpdater;

      XElement t = appearance.getThumb(parent).cast();
      thumbWidth = t.getOffsetWidth();
      thumbHeight = t.getOffsetHeight();

      positionTip(e);
    }

    @Override
    protected boolean onPreview(NativePreviewEvent event) {
      boolean allow = super.onPreview(event);

      switch (event.getTypeInt()) {
        case Event.ONMOUSEMOVE: {
          positionTip(event.getNativeEvent());
          break;
        }
        case Event.ONMOUSEUP:
          this.remove();
          XElement p = XElement.as(parent);
          int v = setValue(p, reverseValue(p, appearance.getClickedValue(context, p, event.getNativeEvent())));
          valueUpdater.update(v);
          appearance.onMouseUp(context, parent, event.getNativeEvent());
          appearance.onMouseOut(context, parent, event.getNativeEvent());
          tip.hide();
          break;
      }

      return allow;
    }

    private void positionTip(NativeEvent e) {
      Point thumbPosition = appearance.getThumb(parent).<XElement> cast().getPosition(false);

      int x = thumbPosition.getX();
      int y = thumbPosition.getY();

      XElement p = XElement.as(parent);
      int v = setValue(p, reverseValue(p, appearance.getClickedValue(context, p, e)));
      tip.getAppearance().getTextElement(tip.getElement()).setInnerText(onFormatValue(v));

      tip.showAt(-5000, -5000);
      int w = tip.getOffsetWidth();
      int h = tip.getOffsetHeight();

      if (!vertical) {
        boolean top = y > 35;
        if (top) {
          thumbPosition.setX(x - (w / 2) + (thumbWidth / 2));
          thumbPosition.setY(y - h - 5);

        } else {
          thumbPosition.setX(x - (w / 2) + (thumbWidth / 2));
          thumbPosition.setY(y + thumbHeight + 5);
        }
      } else {
        int vleft = Window.getClientWidth();
        boolean right = x < (vleft - 30);
        if (right) {
          thumbPosition.setX(parent.getAbsoluteLeft() + parent.getOffsetWidth() + 5);
          thumbPosition.setY(Math.max(0, y - (h / 2)));
        } else {
          thumbPosition.setX(parent.getAbsoluteLeft() - w - 5);
          thumbPosition.setY(Math.max(0, y - (h / 2)));
        }
      }

      tip.showAt(thumbPosition);
    }

  }

  private SliderAppearance appearance;
  private String message = "{0}";
  private boolean vertical = false;
  private int maxValue = 100;
  private int minValue = 0;
  private final Tip tip = new Tip() {
    protected void onAfterFirstAttach() {
      super.onAfterFirstAttach();
      constrainPosition = false;
    };
  };

  private int increment = 10;

  public SliderCell() {
    this(GWT.<SliderAppearance> create(SliderAppearance.class));
  }

  public SliderCell(SliderAppearance appearance) {
    super(appearance, "mousedown", "mouseover", "mouseout", "keydown");
    this.appearance = appearance;

    vertical = appearance.isVertical();
    tip.setMinWidth(25);

  }

  /**
   * Returns the increment.
   * 
   * @return the increment
   */
  public int getIncrement() {
    return increment;
  }

  /**
   * Returns the max value (defaults to 100).
   * 
   * @return the max value
   */
  public int getMaxValue() {
    return maxValue;
  }

  /**
   * Returns the tool tip message.
   * 
   * @return the tool tip message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the minimum value (defaults to 0).
   * 
   * @return the minimum value
   */
  public int getMinValue() {
    return minValue;
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, Integer value, NativeEvent event,
      ValueUpdater<Integer> valueUpdater) {
    Element target = event.getEventTarget().cast();
    if (!parent.isOrHasChild(target)) {
      return;
    }
    super.onBrowserEvent(context, parent, value, event, valueUpdater);

    String eventType = event.getType();
    if ("mousedown".equals(eventType)) {
      onMouseDown(context, parent, event, valueUpdater);
    } else if ("mouseover".equals(eventType)) {
      appearance.onMouseOver(context, parent, event);
    } else if ("mouseout".equals(eventType)) {
      appearance.onMouseOut(context, parent, event);
    } else if ("keydown".equals(eventType)) {
      int key = event.getKeyCode();
      if (!vertical) {
        switch (key) {
          case KeyCodes.KEY_LEFT: {
            int v = setValue(parent, value - increment);
            valueUpdater.update(v);
            break;
          }
          case KeyCodes.KEY_RIGHT: {
            int v = setValue(parent, value + increment);
            valueUpdater.update(v);
            break;
          }
        }
      } else {
        switch (key) {
          case KeyCodes.KEY_DOWN: {
            int v = setValue(parent, value - increment);
            valueUpdater.update(v);
            break;
          }
          case KeyCodes.KEY_UP: {
            int v = setValue(parent, value + increment);
            valueUpdater.update(v);
            break;
          }
        }
      }
    }
  }

  @Override
  public void onEmpty(XElement parent, boolean empty) {
    appearance.onEmpty(parent, empty);
  }

  @Override
  public boolean redrawOnResize() {
    return true;
  }

  @Override
  public void render(Context context, Integer value, SafeHtmlBuilder sb) {
    double fractionalValue;
    if (value == null) {
      fractionalValue = 0.5;
    } else {
      fractionalValue = 1.0 * (value - minValue) / (maxValue - minValue);
    }
    appearance.render(fractionalValue, getWidth(), getHeight(), sb);

  }

  /**
   * How many units to change the slider when adjusting by drag and drop. Use
   * this option to enable 'snapping' (default to 10).
   * 
   * @param increment the increment
   */
  public void setIncrement(int increment) {
    this.increment = increment;
  }

  /**
   * Sets the max value (defaults to 100).
   * 
   * @param maxValue the max value
   */
  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
  }

  /**
   * Sets the tool tip message (defaults to '{0}'). "{0} will be substituted
   * with the current slider value.
   * 
   * @param message the tool tip message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Sets the minimum value (defaults to 0).
   * 
   * @param minValue the minimum value
   */
  public void setMinValue(int minValue) {
    this.minValue = minValue;
  }

  protected int constrain(int value) {
    return Util.constrain(value, minValue, maxValue);
  }

  protected int doSnap(int v) {
    if (increment == 1) {
      return v;
    }
    int m = v % increment;
    if (m != 0) {
      v -= m;
      if (m * 2 > increment) {
        v += increment;
      } else if (m * 2 < -increment) {
        v -= increment;
      }
    }
    return v;
  }

  protected double getRatio(XElement parent) {
    int v = maxValue - minValue;
    if (vertical) {
      int h = appearance.getSliderLength(parent);
      return v == 0 ? h : ((double) h / v);
    } else {
      int w = appearance.getSliderLength(parent);
      return v == 0 ? w : ((double) w / v);
    }
  }

  protected int normalizeValue(int value) {
    value = doSnap(value);
    value = constrain(value);
    return value;
  }

  protected String onFormatValue(int value) {
    return Format.substitute(getMessage(), value);
  }

  protected void onMouseDown(final Context context, final Element parent, NativeEvent event,
      final ValueUpdater<Integer> valueUpdater) {
    Element target = Element.as(event.getEventTarget());
    if (!appearance.getThumb(parent).isOrHasChild(target)) {
      int value = appearance.getClickedValue(context, parent, event);
      value = reverseValue(parent.<XElement> cast(), value);
      value = normalizeValue(value);

      valueUpdater.update(value);

      int pos = translateValue(parent.<XElement> cast(), value);
      appearance.setThumbPosition(parent, pos);

      return;
    }

    BaseEventPreview preview = new DragPreview(context, parent, valueUpdater, event);
    appearance.onMouseDown(context, parent, event);
    preview.add();
  }

  protected int reverseValue(XElement parent, int pos) {
    double ratio = getRatio(parent);
    if (vertical) {
      int length = appearance.getSliderLength(parent);
      return (int) (((minValue * ratio) + length - pos) / ratio);
    } else {
      int halfThumb = appearance.getThumb(parent).getOffsetWidth() / 2;
      return (int) ((pos + halfThumb + (minValue * ratio)) / ratio);
    }
  }

  protected int translateValue(XElement parent, int v) {
    double ratio = getRatio(parent);
    int halfThumb = appearance.getThumb(parent).getOffsetWidth() / 2;
    return (int) ((v * ratio) - (minValue * ratio) - halfThumb);
  }

  private int setValue(Element parent, int value) {
    value = normalizeValue(value);
    int left = translateValue(parent.<XElement> cast(), value);

    appearance.setThumbPosition(parent, left);

    return value;
  }

}

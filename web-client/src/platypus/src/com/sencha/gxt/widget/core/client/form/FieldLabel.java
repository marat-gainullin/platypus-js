/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;

/**
 * Form field wrapper to add a label and validation error text.
 */
public class FieldLabel extends SimpleContainer implements HasText, HasHTML, HasSafeHtml {

  /**
   * Describes the appearance for a {@link FieldLabel} object. Different from
   * most appearance objects, as it must provide direct access to the wrapper
   * element instead of just access to set and get the properties of the UI.
   */
  public interface FieldLabelAppearance {

    void clearLabelFor(XElement parent);

    XElement getChildElementWrapper(XElement parent);

    XElement getLabelElement(XElement parent);

    String getLabelHtml(XElement parent);

    String getLabelText(XElement parent);

    void onUpdateOptions(XElement parent, FieldLabelOptions options);

    void render(SafeHtmlBuilder sb, String id, FieldLabelOptions label);

    void setLabelFor(XElement parent, String id);
  }

  /**
   * A set of configuration parameters for a FieldLabel.
   */
  public class FieldLabelOptions implements HasHTML, HasSafeHtml {

    /** The justification of a field label inside its available space */
    private LabelAlign labelAlign = LabelAlign.LEFT;

    /** The width of area available for label text */
    private int labelWidth = 100;

    /**
     * The width of the padding between the label and the control to which the
     * label applies
     */
    private int labelPad = 5;

    /**
     * The string to use as a suffix on the label to separate it from the field
     * it labels
     */
    private String labelSeparator = ":";

    /**
     * The content of the label. This string represents the text content of the
     * label or HTML to be rendered in the label space depending on the value of
     * {@link #htmlContent}.
     */
    private String content;

    /**
     * Whether the {@link #content} should be treated as HTML markup. This is
     * set depending on whether {@link #setText(String)} or
     * {@link #setHTML(String)} was last called.
     */
    private boolean htmlContent;

    /**
     * Returns the content of the label. This string represents the text content
     * of the label or HTML to be rendered in the label space depending on the
     * value of {@link #htmlContent}.
     * 
     * @return the content of the label
     */
    public String getContent() {
      return content;
    }

    @Override
    public String getHTML() {
      /*
       * Included to satisfy HasHTML contract. If content represents arbitrary
       * text, we escape it to avoid a naive consumer using the text content
       * directly as HTML when the text would represent unsafe HTML.
       */
      return htmlContent ? content : SafeHtmlUtils.htmlEscape(content);
    }

    /**
     * Returns the justification of a field label inside its available space.
     * 
     * @return the justification of a field label inside its available space
     */
    public LabelAlign getLabelAlign() {
      return labelAlign;
    }

    /**
     * Returns the width of the padding between the label and the control to
     * which the label applies.
     * 
     * @return the width of the padding between the label and the control to
     *         which the label applies
     */
    public int getLabelPad() {
      return labelPad;
    }

    /**
     * Returns the string to use as a suffix on the label to separate it from
     * the field it labels.
     * 
     * @return the string to use as a suffix on the label to separate it from
     *         the field it labels
     */
    public String getLabelSeparator() {
      return labelSeparator;
    }

    /**
     * Returns the width of area available for label text.
     * 
     * @return the width of area available for label text
     */
    public int getLabelWidth() {
      return labelWidth;
    }

    @Override
    public String getText() {
      /*
       * Included to satisfy HasHTML contract. This method will return markup if
       * content represents HTML content.
       */
      return content;
    }

    /**
     * Returns true if the {@link #content} should be treated as HTML markup.
     * This is set depending on whether {@link #setText(String)} or
     * {@link #setHTML(String)} was last called.
     * 
     * @return true if content should be treated as HTML markup.
     */
    public boolean isHtmlContent() {
      return htmlContent;
    }

    public void setHTML(SafeHtml html) {
      content = html.asString();
      htmlContent = true;
    }

    public void setHTML(String html) {
      content = html;
      htmlContent = true;
    }

    /**
     * Sets the justification of a field label inside its available space.
     * 
     * @param labelAlign the justification of a field label inside its available
     *          space
     */
    public void setLabelAlign(LabelAlign labelAlign) {
      this.labelAlign = labelAlign;
    }

    /**
     * Sets the width of the padding between the label and the control to which
     * the label applies.
     * 
     * @param labelPad the width of the padding between the label and the
     *          control to which the label applies
     */
    public void setLabelPad(int labelPad) {
      this.labelPad = labelPad;
    }

    /**
     * Sets the string to use as a suffix on the label to separate it from the
     * field it labels.
     * 
     * @param labelSeparator the string to use as a suffix on the label to
     *          separate it from the field it labels
     */
    public void setLabelSeparator(String labelSeparator) {
      this.labelSeparator = labelSeparator;
    }

    /**
     * Sets the width of area available for label text.
     * 
     * @param labelWidth the width of area available for label text
     */
    public void setLabelWidth(int labelWidth) {
      this.labelWidth = labelWidth;
    }

    public void setText(String text) {
      content = text;
      htmlContent = false;
    }

  }

  private final FieldLabelAppearance appearance;

  private FieldLabelOptions options = new FieldLabelOptions();

  /**
   * Creates a field label with the default appearance. To be useful, use
   * {@link #setWidget(Widget)} to set the widget and {@link #setText(String)}
   * to set the label.
   */
  public FieldLabel() {
    this(null);
  }

  /**
   * Creates a field label with the default appearance for the specified widget.
   * 
   * @param widget the widget to label
   */
  public FieldLabel(Widget widget) {
    this(widget, GWT.<FieldLabelAppearance> create(FieldLabelAppearance.class));
  }

  /**
   * Creates a field label with the specified the specified widget and
   * appearance.
   * 
   * @param widget the widget to label
   * @param appearance the appearance of the field label
   */
  public FieldLabel(Widget widget, FieldLabelAppearance appearance) {
    super(true);
    this.appearance = appearance;
    String id;
    if (widget == null) {
      id = null;
    } else {
      id = ComponentHelper.getWidgetId(widget);

      if (widget instanceof ValueBaseField<?>) {
        id += "-input";
      }
    }

    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb, id, options);
    setElement(XDOM.create(sb.toSafeHtml()));

    if (widget != null) {
      setWidget(widget);
    }
  }

  /**
   * Creates a field label with the default appearance and the specified widget
   * and label.
   * 
   * @param widget the widget to label
   * @param label the text to use for the label
   */
  public FieldLabel(Widget widget, String label) {
    this(widget);
    setText(label);
  }

  /**
   * Creates a field label with the specified widget, label and appearance.
   * 
   * @param widget the widget to label
   * @param label the text to use for the label
   * @param appearance the appearance of the field label
   */
  public FieldLabel(Widget widget, String label, FieldLabelAppearance appearance) {
    this(widget, appearance);
    setText(label);
  }

  @Override
  public String getHTML() {
    return appearance.getLabelElement(getElement()).getInnerHTML();
  }

  /**
   * Returns the justification of a field label inside its available space.
   * 
   * @return the justification of a field label inside its available space
   */
  public LabelAlign getLabelAlign() {
    return options.getLabelAlign();
  }

  /**
   * Returns the width of the padding between the label and the control to which
   * the label applies.
   * 
   * @return the width of the padding between the label and the control to which
   *         the label applies
   */
  public int getLabelPad() {
    return options.getLabelPad();
  }

  /**
   * Returns the label separator.
   * 
   * @return the label separator
   */
  public String getLabelSeparator() {
    return options.getLabelSeparator();
  }

  /**
   * Returns the label width.
   * 
   * @return the label width
   */
  public int getLabelWidth() {
    return options.getLabelWidth();
  }

  @Override
  public String getText() {
    return appearance.getLabelText(getElement());
  }

  @Override
  public void setHTML(SafeHtml html) {
    options.setHTML(html);
    appearance.onUpdateOptions(getElement(), options);
  }

  @Override
  public void setHTML(String html) {
    options.setHTML(html);
    appearance.onUpdateOptions(getElement(), options);
  }

  /**
   * Sets the justification of a field label inside its available space.
   * 
   * @param labelAlign the justification of a field label inside its available
   *          space
   */
  public void setLabelAlign(LabelAlign labelAlign) {
    options.setLabelAlign(labelAlign);
    appearance.onUpdateOptions(getElement(), options);
  }

  /**
   * Sets the width of the padding between the label and the control to which
   * the label applies.
   * 
   * @param labelPad the width of the padding between the label and the control
   *          to which the label applies
   */
  public void setLabelPad(int labelPad) {
    options.setLabelPad(labelPad);
    appearance.onUpdateOptions(getElement(), options);
  }

  /**
   * The standard separator to display after the text of each form label
   * (defaults to colon ':').
   * 
   * @param labelSeparator the label separator or "" for none
   */
  public void setLabelSeparator(String labelSeparator) {
    options.setLabelSeparator(labelSeparator);
    appearance.onUpdateOptions(getElement(), options);
  }

  /**
   * Sets the label width (defaults to 100).
   * 
   * @param labelWidth the label width
   */
  public void setLabelWidth(int labelWidth) {
    options.setLabelWidth(labelWidth);
    appearance.onUpdateOptions(getElement(), options);
  }

  @Override
  public void setText(String text) {
    options.setText(text);
    appearance.onUpdateOptions(getElement(), options);
  }

  @Override
  public void setWidget(Widget w) {
    super.setWidget(w);
    String id;
    if (w == null) {
      appearance.clearLabelFor(getElement());
      id = getId();
    } else {
      id = ComponentHelper.getWidgetId(widget);

      if (widget instanceof ValueBaseField<?>) {
        id += "-input";
      }

      appearance.setLabelFor(getElement(), id);
    }
  }

  @Override
  protected void doLayout() {
    if (widget != null) {
      Size size = getElement().getStyleSize();
      int width = -1;
      if (!isAutoWidth()) {

        if (options.getLabelAlign() == LabelAlign.TOP) {
          width = size.getWidth() - getLeftRightMargins(widget);
        } else {
          XElement wrapper = appearance.getChildElementWrapper(getElement());
          width = (wrapper != null ? wrapper.getWidth(true) : size.getWidth()) - getLeftRightMargins(widget);
        }

        if (GXT.isIE6()) {
          width -= 5;
        }
      }
      int height = -1;

      if (!isAutoHeight()) {
        height = size.getHeight() - getTopBottomMargins(widget);
      }

      applyLayout(widget, width, height);
    }
  }

  @Override
  protected XElement getContainerTarget() {
    return appearance.getChildElementWrapper(getElement());
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    String id;
    if (widget == null) {
      appearance.clearLabelFor(getElement());
    } else {
      id = ComponentHelper.getWidgetId(widget);
      appearance.setLabelFor(getElement(), id);
    }
  }

}

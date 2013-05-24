/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasName;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;

/**
 * A file upload field. When using this field, the containing form panel's
 * encoding must be set to MULTIPART using
 * {@link FormPanel#setEncoding(Encoding)}. In addition, the method should be
 * set to POST using {@link FormPanel#setMethod(Method)}
 * 
 * <p />
 * You must set a name for uploads to work with Firefox.
 */
public class FileUploadField extends Component implements IsField<String>, HasChangeHandlers, HasName {

  public interface FileUploadFieldAppearance {

    String buttonClass();

    String fileInputClass();

    void render(SafeHtmlBuilder sb);

    String textFieldClass();

    String wrapClass();
  }

  public interface FileUploadFieldMessages {
    String browserText();
  }

  protected class DefaultFileUploadFieldMessages implements FileUploadFieldMessages {

    @Override
    public String browserText() {
      return DefaultMessages.getMessages().uploadField_browseText();
    }

  }

  private FileUploadFieldMessages messages;
  private final FileUploadFieldAppearance appearance;
  private int buttonOffset = 3;
  private TextButton button;
  private XElement file;
  private String name;
  private TextField input;

  /**
   * Creates a new file upload field.
   */
  public FileUploadField() {
    this(GWT.<FileUploadFieldAppearance> create(FileUploadFieldAppearance.class));
  }

  /**
   * Creates a new file upload field.
   * 
   * @param appearance the appearance
   */
  public FileUploadField(FileUploadFieldAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));

    input = new TextField();
    input.setReadOnly(true);
    getElement().appendChild(input.getElement());

    sinkEvents(Event.ONCHANGE | Event.ONCLICK | Event.MOUSEEVENTS);

    createFileInput();

    button = new TextButton(getMessages().browserText());
    DivElement wrapper = Document.get().createDivElement();
    wrapper.addClassName(appearance.buttonClass());
    XElement buttonElement = button.getElement();
    if (GXT.isIE6() || GXT.isIE7() || GXT.isIE8() || GXT.isOpera()) {
      buttonElement.removeClassName(CommonStyles.get().inlineBlock());
    }
    wrapper.appendChild(buttonElement);
    getElement().appendChild(wrapper);

    ensureVisibilityOnSizing = true;
    setWidth(150);
  }

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }

  @Override
  public void clear() {
    input.reset();
    createFileInput();
  }

  @Override
  public void clearInvalid() {
    // do nothing
  }

  /**
   * Returns the file upload field messages.
   * 
   * @return the messages
   */
  public FileUploadFieldMessages getMessages() {
    if (messages == null) {
      messages = new DefaultFileUploadFieldMessages();
    }
    return messages;
  }

  @Override
  public String getName() {
    return file.getAttribute("name");
  }

  @Override
  public String getValue() {
    return getFileInput().getValue();
  }

  /**
   * Returns the field's allow blank state.
   * 
   * @return true if blank values are allowed
   */
  public boolean isAllowBlank() {
    return input.isAllowBlank();
  }

  /**
   * Returns the read only state.
   * 
   * @return true if read only, otherwise false
   */
  public boolean isReadOnly() {
    return input.isReadOnly();
  }

  /**
   * Returns whether or not the field value is currently valid.
   * 
   * @return true if the value is valid, otherwise false
   */
  public boolean isValid() {
    return input.isValid();
  }

  @Override
  public boolean isValid(boolean preventMark) {
    return input.isValid(preventMark);
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);

    if (event.getTypeInt() == Event.ONCHANGE) {
      event.stopPropagation();
      onChange(event);
    }

    if ((event.getTypeInt() != Event.ONCLICK) && event.getEventTarget().<Element> cast().isOrHasChild(file)) {
      button.onBrowserEvent(event);
    }
  }

  @Override
  public void reset() {
    clear();
  }

  /**
   * Sets whether a field is valid when its value length = 0 (default to true).
   * 
   * @param allowBlank true to allow blanks, false otherwise
   */
  public void setAllowBlank(boolean allowBlank) {
    input.setAllowBlank(allowBlank);
  }

  /**
   * Sets the file upload field messages.
   * 
   * @param messages the messages
   */
  public void setMessages(FileUploadFieldMessages messages) {
    this.messages = messages;
  }

  @Override
  public void setName(String name) {
    this.name = name;
    file.<InputElement> cast().setName(name);
  }

  /**
   * Sets the field's read only state.
   * 
   * @param readonly the read only state
   */
  public void setReadOnly(boolean readonly) {
    button.setEnabled(readonly);
    input.setReadOnly(readonly);
  }

  @Override
  public void setValue(String value) {
    throw new UnsupportedOperationException("You cannot set the value for file upload field");
  }

  @Override
  public boolean validate(boolean preventMark) {
    return input.validate(preventMark);
  }

  protected void createFileInput() {
    if (file != null) {
      file.removeFromParent();
    }

    file = Document.get().createFileInputElement().cast();
    file.addEventsSunk(Event.ONCHANGE | Event.FOCUSEVENTS);
    file.setId(XDOM.getUniqueId());
    file.addClassName(appearance.fileInputClass());
    file.setTabIndex(-1);
    ((InputElement) file.cast()).setName(name);

    getElement().insertFirst(file);

    // file.setEnabled(isEnabled());
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(input);
    ComponentHelper.doAttach(button);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(input);
    ComponentHelper.doDetach(button);
  }

  /**
   * Returns the file input element. You should not store a reference to this.
   * When resetting this field the file input will change.
   */
  protected InputElement getFileInput() {
    return (InputElement) file.cast();
  }

  @Override
  protected XElement getFocusEl() {
    return input.getElement();
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    resizeFile();
  }

  @Override
  protected void onBlur(Event event) {
    super.onBlur(event);
    Rectangle rec = button.getElement().getBounds();
    if (rec.contains(BaseEventPreview.getLastXY())) {
      event.stopPropagation();
      event.preventDefault();
      return;
    }
    super.onBlur(event);
  }

  protected void onChange(Event event) {
    if (GXT.isIE()) {
      input.setReadOnly(false);
    }
    input.setValue(getFileInput().getValue());
    if (GXT.isIE()) {
      input.setReadOnly(true);
    }
    input.focus();
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    input.setWidth(width - button.getOffsetWidth() - buttonOffset);
    resizeFile();
  }

  protected void resizeFile() {
    file.setWidth(button.getOffsetWidth());
  }

}

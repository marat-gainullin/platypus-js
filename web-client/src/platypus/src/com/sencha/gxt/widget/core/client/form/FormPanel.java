/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FormPanelImpl;
import com.google.gwt.user.client.ui.impl.FormPanelImplHost;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.HasSubmitCompleteHandlers;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.HasSubmitHandlers;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;

/**
 * A panel that wraps an HTML form. The field names and data on the form can be
 * submitted to an HTTP server.
 * <p/>
 * A form panel is a {@link SimpleContainer}, which contains only a single
 * widget. Multiple fields can be added to a form panel by adding them first to
 * another container and then adding that container to the form panel.
 * <p/>
 * If you do not need to submit a form to a server, consider using one of the
 * other containers, instead of <code>FormPanel</code>.
 * <p/>
 * Code snippet for a form panel containing a {@link FileUploadField}:
 * 
 * <pre>
  public void onModuleLoad() {

    final FormPanel fp = new FormPanel();

    FileUploadField fuf = new FileUploadField();
    fuf.setName("fileUploadField");

    VerticalLayoutContainer vlc = new VerticalLayoutContainer();
    vlc.add(new FieldLabel(fuf, "File"));
    vlc.add(new TextButton("OK", new SelectHandler() {
      public void onSelect(SelectEvent event) {
        fp.submit();
      }
    }));

    fp.setMethod(Method.POST);
    fp.setEncoding(Encoding.MULTIPART);
    fp.setAction("http://www.example.com/my_upload_url");
    fp.setWidget(vlc);
    fp.addSubmitCompleteHandler(new SubmitCompleteHandler() {
      public void onSubmitComplete(SubmitCompleteEvent event) {
        String resultHtml = event.getResults();
        Info.display("Upload Response", resultHtml);
      }
    });

    Window w = new Window();
    w.setHeadingText("Upload File");
    w.setPixelSize(300, 100);
    w.setWidget(fp);
    w.show();
  }
 * </pre>
 * 
 */
public class FormPanel extends SimpleContainer implements FormPanelImplHost, HasSubmitHandlers,
    HasSubmitCompleteHandlers {

  /**
   * Form encoding enumeration.
   */
  public enum Encoding {
    MULTIPART("multipart/form-data"), URLENCODED("application/x-www-form-urlencoded");
    private final String value;

    private Encoding(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }
  }

  /**
   * Label alignment enumeration.
   */
  public enum LabelAlign {
    LEFT, TOP;
  }

  /**
   * Form method enumeration.
   */
  public enum Method {
    GET, POST;
  }

  private static FormPanelImpl impl = GWT.create(FormPanelImpl.class);
  private int labelWidth = -1;
  private LabelAlign labelAlign;
  private Method method = Method.GET;
  private String frameName;
  private Element synthesizedFrame;
  private static int formId = 0;

  /**
   * Creates a panel that wraps an HTML form. The field names and data on the
   * form can be submitted to an HTTP server.
   */
  public FormPanel() {
    this(Document.get().createFormElement(), true);
  }

  protected FormPanel(Element element, boolean createIFrame) {
    super(true);
    setElement(element);

    FormElement.as(element);

    if (createIFrame) {
      assert ((getTarget() == null) || (getTarget().trim().length() == 0)) : "Cannot create target iframe if the form's target is already set.";

      // We use the module name as part of the unique ID to ensure that ids are
      // unique across modules.
      frameName = "FormPanel_" + GWT.getModuleName() + "_" + (++formId);
      setTarget(frameName);

      sinkEvents(Event.ONLOAD);
    }
  }

  /**
   * Adds a {@link SubmitCompleteEvent} handler.
   * 
   * @param handler the handler
   * @return the handler registration used to remove the handler
   */
  public HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler) {
    return addHandler(handler, SubmitCompleteEvent.getType());
  }

  /**
   * Adds a {@link SubmitEvent} handler.
   * 
   * @param handler the handler
   * @return the handler registration used to remove the handler
   */
  public HandlerRegistration addSubmitHandler(SubmitHandler handler) {
    return addHandler(handler, SubmitEvent.getType());
  }

  /**
   * Gets the 'action' associated with this form. This is the URL to which it
   * will be submitted.
   * 
   * @return the form's action
   */
  public String getAction() {
    return getFormElement().getAction();
  }

  /**
   * Gets the encoding used for submitting this form. This should be either
   * {@link Encoding#MULTIPART} or {@link Encoding#URLENCODED}.
   * 
   * @return the form's encoding
   */
  public String getEncoding() {
    return impl.getEncoding(getElement());
  }

  /**
   * Returns all of the panel's child field labels. Field labels in nested
   * containers are included in the returned list.
   * 
   * @return the fields
   */
  public List<FieldLabel> getFieldLabels() {
    return FormPanelHelper.getFieldLabels(this);
  }

  /**
   * Returns all of the panel's child fields. Fields in nested containers are
   * included in the returned list.
   * 
   * @return the fields
   */
  public List<IsField<?>> getFields() {
    return FormPanelHelper.getFields(this);
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
   * Returns the width of area available for label text.
   * 
   * @return the width of area available for label text
   */
  public int getLabelWidth() {
    return labelWidth;
  }

  /**
   * Returns the form's method. Only applies when using standard HTML form
   * submits.
   * 
   * @return the method the method
   */
  public Method getMethod() {
    return method;
  }

  /**
   * Gets the form's 'target'. This is the name of the {@link NamedFrame} that
   * will receive the results of submission, or <code>null</code> if none has
   * been specified.
   * 
   * @return the form's target.
   */
  public String getTarget() {
    return getFormElement().getTarget();
  }

  /**
   * Returns true if the form is invalid.
   * 
   * @return true if all fields are valid
   */
  public boolean isValid() {
    return isValid(false);
  }

  /**
   * Returns the form's valid state by querying all child fields.
   * 
   * @param preventMark true for silent validation (no invalid event and field
   *          is not marked invalid)
   * 
   * @return true if all fields are valid
   */
  public boolean isValid(boolean preventMark) {
    return FormPanelHelper.isValid(this, preventMark);
  }

  @Override
  public boolean onFormSubmit() {
    return onFormSubmitImpl();
  }

  public void onFrameLoad() {
    onFrameLoadImpl();
  }

  /**
   * Resets all field values.
   */
  public void reset() {
    FormPanelHelper.reset(this);
  }

  /**
   * Sets the 'action' associated with this form. This is the URL to which it
   * will be submitted.
   * 
   * @param url the form's action
   */
  public void setAction(SafeUri url) {
    setAction(url.asString());
  }

  /**
   * Sets the action of the form.
   * 
   * @param url the action
   */
  public void setAction(String url) {
    getFormElement().setAction(url);
  }

  /**
   * Sets the encoding to use when submitting the form.
   * 
   * @param encoding the mime encoding
   */
  public void setEncoding(Encoding encoding) {
    impl.setEncoding(getElement(), encoding.value);
  }

  /**
   * Sets the justification of a field label inside its available space.
   * 
   * @param align the justification of a field label inside its available
   *          space
   */
  public void setLabelAlign(LabelAlign align) {
    this.labelAlign = align;
    List<FieldLabel> labels = getFieldLabels();
    for (int i = 0; i < labels.size(); i++) {
      labels.get(i).setLabelAlign(align);
    }
  }

  /**
   * Sets the width of the padding between the label and the control to which
   * the label applies.
   * 
   * @param labelWidth the width of the padding between the label and the control
   *          to which the label applies
   */
  public void setLabelWidth(int labelWidth) {
    this.labelWidth = labelWidth;
    List<FieldLabel> labels = getFieldLabels();
    for (int i = 0; i < labels.size(); i++) {
      labels.get(i).setLabelWidth(labelWidth);
    }
  }

  /**
   * Specifies if the form will be submitted using an HTTP Post or Get request
   * (defaults to GET).
   * 
   * @param method the method
   */
  public void setMethod(Method method) {
    this.method = method;
    getFormElement().setMethod(method.name().toLowerCase());
  }

  /**
   * Submits the form.
   * 
   * <p>
   * The FormPanel must <em>not</em> be detached (i.e. removed from its parent
   * or otherwise disconnected from a {@link RootPanel}) until the submission is
   * complete. Otherwise, notification of submission will fail.
   * </p>
   */
  public void submit() {
    // Fire the onSubmit event, because javascript's form.submit() does not
    // fire the built-in onsubmit event.
    if (!fireSubmitEvent()) {
      return;
    }

    impl.submit(getElement(), synthesizedFrame);
  }

  @Override
  protected void onAttach() {
    if (labelAlign != null) {
      List<FieldLabel> labels = getFieldLabels();
      for (FieldLabel lbl : labels) {
        lbl.setLabelAlign(labelAlign);
      }
    }

    super.onAttach();

    if (frameName != null) {
      // Create and attach a hidden iframe to the body element.
      createFrame();
      Document.get().getBody().appendChild(synthesizedFrame);
    }
    // Hook up the underlying iframe's onLoad event when attached to the DOM.
    // Making this connection only when attached avoids memory-leak issues.
    // The FormPanel cannot use the built-in GWT event-handling mechanism
    // because there is no standard onLoad event on iframes that works across
    // browsers.
    impl.hookEvents(synthesizedFrame, getElement(), this);
  }

  @Override
  protected void onDetach() {
    super.onDetach();

    // Unhook the iframe's onLoad when detached.
    impl.unhookEvents(synthesizedFrame, getElement());

    if (synthesizedFrame != null) {
      // And remove it from the document.
      Document.get().getBody().removeChild(synthesizedFrame);
      synthesizedFrame = null;
    }
  }

  @Override
  protected void onInsert(int index, Widget child) {
    super.onInsert(index, child);

    if (child instanceof FieldLabel) {
      FieldLabel label = (FieldLabel) child;
      if (labelWidth != -1) {
        label.setLabelWidth(labelWidth);
      }
    }
  }

  private void createFrame() {
    // Attach a hidden IFrame to the form. This is the target iframe to which
    // the form will be submitted. We have to create the iframe using innerHTML,
    // because setting an iframe's 'name' property dynamically doesn't work on
    // most browsers.
    Element dummy = Document.get().createDivElement();
    dummy.setInnerHTML("<iframe src=\"javascript:''\" name='" + frameName
        + "' style='position:absolute;width:0;height:0;border:0'>");

    synthesizedFrame = dummy.getFirstChildElement();
  }

  /**
   * Fire a {@link FormPanel.SubmitEvent}.
   * 
   * @return true to continue, false if canceled
   */
  private boolean fireSubmitEvent() {
    SubmitEvent event = new SubmitEvent();
    fireEvent(event);
    return !event.isCanceled();
  }

  private FormElement getFormElement() {
    return getElement().cast();
  }

  /**
   * Returns true if the form is submitted, false if canceled.
   */
  private boolean onFormSubmitImpl() {
    return fireSubmitEvent();
  }

  private void onFrameLoadImpl() {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        fireEvent(new SubmitCompleteEvent(impl.getContents(synthesizedFrame)));
      }
    });

  }

  private void setTarget(String target) {
    getFormElement().setTarget(target);
  }

}

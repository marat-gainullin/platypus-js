/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.cell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.view.client.HasKeyProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.sencha.gxt.cell.core.client.AbstractEventCell;
import com.sencha.gxt.cell.core.client.DisableCell;
import com.sencha.gxt.cell.core.client.FocusableCell;
import com.sencha.gxt.cell.core.client.ResizableCell;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;

/**
 * A {@link Component} that wraps a {@link Cell}.
 * 
 * <p />
 * It is important to not that a single cell instance should not be used in a
 * single <code>CellComponent</code> instance.
 * 
 * <p />
 * Subclasses should be aware that {@link #redraw()} calls are deferred before
 * the component is "rendered". The component is rendered when getElement is
 * called for the first time, typically, when the component is added to a panel
 * or container. To force a redraw use {@link #redraw(boolean)}.
 * 
 * @param <C> the type that the Cell represents
 */
public class CellComponent<C> extends Component implements HasKeyProvider<C>, HasValue<C>, LeafValueEditor<C> {

  /**
   * Create the default element used to wrap the Cell. The default element is a
   * div with display set to inline-block.
   * 
   * @return the default wrapper element
   */
  private static Element createDefaultWrapperElement(boolean inlineBlock) {
    Element div = Document.get().createDivElement();
    if (inlineBlock) {
      div.setClassName(CommonStyles.get().inlineBlock());
    }
    return div;
  }

  /**
   * The cell being wrapped.
   */
  private final Cell<C> cell;

  /**
   * The key provider for the value.
   */
  private ProvidesKey<C> keyProvider;

  /**
   * The current cell value.
   */
  private C value;

  private boolean inlineBlock;

  /**
   * The {@link ValueUpdater} used to trigger value update events.
   */
  protected final ValueUpdater<C> valueUpdater = new ValueUpdater<C>() {
    public void update(C value) {
      // no need to redraw, the Cell took care of it
      setValue(value, true, false);
    }
  };

  /**
   * We defer redraw calls before the widget is "rendered". The widget is
   * rendered when getElement is called which will typically be when the widget
   * is added to a panel or container.
   */
  private boolean rendered = false;

  /**
   * We ignore first getElement call as constructor calls it.
   */
  private boolean init = false;

  /**
   * Construct a new {@link CellWidget} with the specified cell and an initial
   * value of <code>null</code>.
   * 
   * @param cell the cell to wrap
   */
  protected CellComponent(Cell<C> cell) {
    this(cell, null, null);
  }

  /**
   * Construct a new {@link CellWidget} with the specified cell and initial
   * value.
   * 
   * @param cell the cell to wrap
   * @param initialValue the initial value of the Cell
   */
  protected CellComponent(Cell<C> cell, C initialValue) {
    this(cell, initialValue, null);
  }

  /**
   * Construct a new {@link CellWidget} with the specified cell, initial value,
   * and key provider.
   * 
   * @param cell the cell to wrap
   * @param initialValue the initial value of the Cell
   * @param keyProvider the key provider used to get keys from values
   */
  protected CellComponent(Cell<C> cell, C initialValue, ProvidesKey<C> keyProvider) {
    this(cell, initialValue, keyProvider, false);
  }

  /**
   * Creates a new cell component.
   * 
   * @param cell the cell
   * @param initialValue the initial value
   * @param keyProvider the key provider
   * @param inlineBlock true for inline block
   */
  protected CellComponent(Cell<C> cell, C initialValue, ProvidesKey<C> keyProvider, boolean inlineBlock) {
    this(cell, initialValue, keyProvider, createDefaultWrapperElement(inlineBlock));
    this.inlineBlock = inlineBlock;
  }

  /**
   * Creates a {@link CellWidget} with the specified cell, initial value, key
   * provider, using the specified element as the wrapper around the cell.
   * 
   * @param cell the cell to wrap
   * @param initialValue the initial value of the Cell
   * @param keyProvider the key provider used to get keys from values
   * @param elem the browser element to use
   */
  protected CellComponent(Cell<C> cell, C initialValue, ProvidesKey<C> keyProvider, Element elem) {
    this.cell = cell;
    this.keyProvider = keyProvider;
    setElement(elem);
    CellWidgetImplHelper.sinkEvents(this, cell.getConsumedEvents());
    setValue(initialValue);
    init = true;
  }

  /**
   * Construct a new {@link CellWidget} with the specified cell and key
   * provider, and an initial value of <code>null</code>.
   * 
   * @param cell the cell to wrap
   * @param keyProvider the key provider used to get keys from values
   */
  protected CellComponent(Cell<C> cell, ProvidesKey<C> keyProvider) {
    this(cell, null, keyProvider);
  }

  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<C> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  /**
   * Get the {@link Cell} wrapped by this widget.
   * 
   * @return the {@link Cell} being wrapped
   */
  public Cell<C> getCell() {
    return cell;
  }

  @Override
  public XElement getElement() {
    if (init && !rendered) {
      rendered = true;
      redraw();
    }
    return super.getElement();
  }

  @Override
  public ProvidesKey<C> getKeyProvider() {
    return keyProvider;
  }

  @Override
  public C getValue() {
    return value;
  }

  @Override
  public void onBrowserEvent(Event event) {
    CellWidgetImplHelper.onBrowserEvent(this, event);
    super.onBrowserEvent(event);

    // Forward the event to the cell.
    String eventType = event.getType();
    if (cell.getConsumedEvents().contains(eventType)) {
      cell.onBrowserEvent(createContext(), getElement(), value, event, valueUpdater);
    }
  }

  /**
   * Redraw the widget.
   */
  public void redraw() {
    // potential issue here if caller expects redraw to occur
    redraw(false);
  }

  protected boolean redraw(boolean force) {
    if (!force && (!init || !rendered)) {
      return false;
    }
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    cell.render(createContext(), value, sb);
    getElement().setInnerHTML(sb.toSafeHtml().asString());
    onRedraw();
    return true;
  }
  
  protected void onRedraw() {
    
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method will redraw the widget if the new value does not equal the
   * existing value.
   * </p>
   */
  public void setValue(C value) {
    setValue(value, false, true);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method will redraw the widget if the new value does not equal the
   * existing value.
   * </p>
   */
  public void setValue(C value, boolean fireEvents) {
    setValue(value, fireEvents, true);
  }

  /**
   * Sets this object's value and optionally redraw the widget. Fires
   * {@link com.google.gwt.event.logical.shared.ValueChangeEvent} when
   * fireEvents is true and the new value does not equal the existing value.
   * Redraws the widget when redraw is true and the new value does not equal the
   * existing value.
   * 
   * @param value the object's new value
   * @param fireEvents fire events if true and value is new
   * @param redraw redraw the widget if true and value is new
   */
  public void setValue(C value, boolean fireEvents, boolean redraw) {
    C oldValue = getValue();
    if (value != oldValue && (oldValue == null || !oldValue.equals(value))) {
      this.value = value;
      if (redraw) {
        redraw();
      }
      if (fireEvents) {
        ValueChangeEvent.fire(this, value);
      }
    }
  }

  /**
   * Get the {@link Context} for the cell.
   */
  protected Context createContext() {
    return new DefaultHandlerManagerContext(0, 0, getKey(value), ComponentHelper.ensureHandlers(this));
  }

  @Override
  protected XElement getFocusEl() {
    if (cell instanceof FocusableCell) {
      return ((FocusableCell) cell).getFocusElement(getElement());
    }
    return super.getFocusEl();
  }

  /**
   * Get the key for the specified value.
   * 
   * @param value the value
   * @return the key
   */
  protected Object getKey(C value) {
    // since cells can't be reused with CellComponent we use a hard coded key
    // that will remain consistent
    // unlike the GWT impl which uses the actual value when key provider is
    // null. also, the GWT cell code
    // does not handle null keys
    Object key = (keyProvider == null) ? "component" : keyProvider.getKey(value);
    return key;
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    if (inlineBlock && (GXT.isIE6() || GXT.isIE7())) {
      // widgets not scrolling when in tables
      Element p = getElement().getParentElement();
      if (p != null && p.getTagName().equals("TD")) {
        getElement().getStyle().setPosition(Position.STATIC);
      }
    }
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    if (cell instanceof DisableCell) {
      ((DisableCell)cell).disable(createContext(), getElement());
    }
    if (cell instanceof AbstractEventCell<?>) {
      ((AbstractEventCell<?>) cell).setDisableEvents(true);
    }
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    if (cell instanceof DisableCell) {
      ((DisableCell)cell).enable(createContext(), getElement());
    }
    if (cell instanceof AbstractEventCell<?>) {
      ((AbstractEventCell<?>) cell).setDisableEvents(false);
    }
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    if (cell instanceof ResizableCell) {
      ResizableCell rc = (ResizableCell) cell;
      if (rc.redrawOnResize()) {
        rc.setSize(width, height);
        redraw();
      } else {
        rc.setSize(getElement(), width, height);
      }
    }
  }

  protected void setKeyProvider(ProvidesKey<C> keyProvider) {
    this.keyProvider = keyProvider;
  }
}
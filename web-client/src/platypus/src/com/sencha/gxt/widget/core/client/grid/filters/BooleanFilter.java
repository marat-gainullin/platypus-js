/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.filters;

import java.util.List;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.loader.BooleanFilterHandler;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;

/**
 * A boolean filter. See {@link Filter} for more information.
 * 
 * @param <M> the model type
 */
public class BooleanFilter<M> extends Filter<M, Boolean> {

  /**
   * The locale-sensitive messages used by this class.
   */
  public interface BooleanFilterMessages {
    String noText();

    String yesText();
  }

  /**
   * The default locale-sensitive messages used by this class.
   */
  public class DefaultBooleanFilterMessages implements BooleanFilterMessages {

    @Override
    public String noText() {
      return DefaultMessages.getMessages().booleanFilter_noText();
    }

    @Override
    public String yesText() {
      return DefaultMessages.getMessages().booleanFilter_yesText();
    }

  }

  private CheckMenuItem yesItem, noItem;
  private BooleanFilterMessages messages = new DefaultBooleanFilterMessages();
  private CheckChangeHandler<CheckMenuItem> handler = new CheckChangeHandler<CheckMenuItem>() {

    @Override
    public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
      fireUpdate();
    }
  };

  /**
   * Creates a boolean filter for the specified value provider. See
   * {@link Filter#Filter(ValueProvider)} for more information.
   * 
   * @param valueProvider the value provider
   */
  public BooleanFilter(ValueProvider<? super M, Boolean> valueProvider) {
    super(valueProvider);

    setHandler(new BooleanFilterHandler());

    yesItem = new CheckMenuItem();
    yesItem.addCheckChangeHandler(handler);
    yesItem.setGroup(XDOM.getUniqueId());

    noItem = new CheckMenuItem();
    noItem.addCheckChangeHandler(handler);
    noItem.setGroup(yesItem.getGroup());

    menu.add(yesItem);
    menu.add(noItem);

    setMessages(messages);
  }

  @Override
  public List<FilterConfig> getFilterConfig() {
    FilterConfigBean config = new FilterConfigBean();
    config.setType("boolean");
    config.setValue(getHandler().convertToString((Boolean) getValue()));
    return Util.<FilterConfig> createList(config);
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the local-sensitive messages used by this class.
   */
  public BooleanFilterMessages getMessages() {
    return messages;
  }

  @Override
  public Object getValue() {
    return Boolean.valueOf(yesItem.isChecked());
  }

  @Override
  public boolean isActive() {
    return super.isActive();
  }

  /**
   * Sets the local-sensitive messages used by this class.
   * 
   * @param messages the locale sensitive messages used by this class.
   */
  public void setMessages(BooleanFilterMessages messages) {
    this.messages = messages;
    yesItem.setText(getMessages().yesText());
    noItem.setText(getMessages().noText());
  }

  @Override
  protected Class<Boolean> getType() {
    return Boolean.class;
  }

  @Override
  protected boolean isActivatable() {
    return super.isActivatable();
  };

  @Override
  protected boolean validateModel(M model) {
    Boolean val = getValueProvider().getValue(model);
    return getValue().equals(val == null ? Boolean.FALSE : val);
  }

}

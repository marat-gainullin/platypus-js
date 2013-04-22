/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.gui.actions;

import com.jeta.forms.gui.form.GridComponent;
import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 *
 * @author Marat
 */
public class ComponentActionEvent extends ActionEvent{

  private GridComponent actionSubject = null;
  
  public ComponentActionEvent(GridComponent aActionSubject, Component ainvoker, int id, String command, long when, int modifiers)
  {
    super(ainvoker, id, command, when, modifiers);
    actionSubject = aActionSubject;
  }

  public Component getActionSubject() {
    return actionSubject;
  }
  
}

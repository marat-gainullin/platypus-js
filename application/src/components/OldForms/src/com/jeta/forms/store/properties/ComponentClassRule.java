/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.properties;

import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;
import java.awt.Component;

/**
 *
 * @author Marat
 */
public class ComponentClassRule implements JETARule{

  Class<?> clazz = null;

  public ComponentClassRule(Class cl)
  {
    clazz = cl;
  }

  public void setClass(Class<?> cl)
  {
      clazz = cl;
  }

    @Override
  public RuleResult check(Object[] params) {
    if (params != null && params.length > 0 && 
        params[0] instanceof Component)
    {
      Component lcomp = (Component)params[0];
      if (lcomp != null)
      {
          Class<?> cl = lcomp.getClass();
          if (clazz.isAssignableFrom(cl))
            return RuleResult.SUCCESS;
      }        
    }
    return RuleResult.FAIL; 
  }

}
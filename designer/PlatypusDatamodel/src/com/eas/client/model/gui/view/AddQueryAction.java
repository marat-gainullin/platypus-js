/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.DmAction;
import com.eas.client.model.gui.IconCache;
import com.eas.client.model.gui.selectors.AppElementSelectorCallback;
import com.eas.client.model.gui.view.model.ModelView;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mg
 */
public class AddQueryAction extends DmAction {

    protected ModelView<?, ?> modelView;
    protected UndoableEditSupport undoSupport;
    protected AppElementSelectorCallback appElementSelector;

    public AddQueryAction(ModelView<?, ?> aModelView, UndoableEditSupport aUndoSupport, AppElementSelectorCallback aAppElementSelector) {
        super();
        modelView = aModelView;
        undoSupport = aUndoSupport;
        appElementSelector = aAppElementSelector;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isEnabled()) {
            try {
                String appElement = appElementSelector.select(null);
                if (appElement != null) {
                    modelView.doAddQuery(appElement, 0, 0);
                }
            } catch (Exception ex) {
                Logger.getLogger(AddQueryAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getDmActionText() {
        return DatamodelDesignUtils.getLocalizedString(AddQueryAction.class.getSimpleName());
    }

    @Override
    public String getDmActionHint() {
        return DatamodelDesignUtils.getLocalizedString(AddQueryAction.class.getSimpleName() + ".hint");
    }

    @Override
    public Icon getDmActionSmallIcon() {
        return IconCache.getIcon("query-plus.png");
    }

    @Override
    public KeyStroke getDmActionAccelerator() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
    }
}

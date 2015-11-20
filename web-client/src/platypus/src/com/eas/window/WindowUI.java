/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.window;

import com.eas.window.events.HasActivateHandlers;
import com.eas.window.events.HasBeforeCloseHandlers;
import com.eas.window.events.HasClosedHandlers;
import com.eas.window.events.HasDeactivateHandlers;
import com.eas.window.events.HasMaximizeHandlers;
import com.eas.window.events.HasMinimizeHandlers;
import com.eas.window.events.HasMoveHandlers;
import com.eas.window.events.HasRestoreHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.user.client.ui.HasHTML;

/**
 *
 * @author mg
 */
public interface WindowUI extends HasOpenHandlers<WindowUI>, HasClosedHandlers<WindowUI>, HasBeforeCloseHandlers<WindowUI>,
                                  HasActivateHandlers<WindowUI>, HasDeactivateHandlers<WindowUI>,
                                  HasMaximizeHandlers<WindowUI>, HasMinimizeHandlers<WindowUI>, HasRestoreHandlers<WindowUI>,
                                  HasMoveHandlers<WindowUI>, HasResizeHandlers {
    
    public void setCaptionWidget(HasHTML aCaptionWidget);

    public HasHTML getCaptionWidget();

    public boolean isMovable();

    public void setMovable(boolean aValue);

    public boolean isMinimized();

    public boolean isMaximized();

    public boolean isMinimizable();

    public void setMinimizable(boolean minimizable);

    public boolean isMaximizable();

    public void setMaximizable(boolean maximizable);

    public boolean isClosable();

    public void setClosable(boolean maximizable);

    public void maximize();

    public void minimize();

    public void restoreSnapshot();

    public void restore();

    public boolean isActive();

    public void activate();

    public void deactivate();

    public boolean isUndecorated();

    public void setUndecorated(boolean aValue);

    public boolean isResizable();

    public void setResizable(boolean aValue);

    public void setPosition(double aLeft, double aTop);

    public void setSize(double aWidth, double aHeight);

    public void close();
}

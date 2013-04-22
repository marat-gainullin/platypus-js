/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.syntax;

import java.awt.Dimension;
import javax.swing.JViewport;

/**
 *
 * @author mg
 */
public class JSyntaxPane extends JInternalSyntaxPane {

    private Dimension preferredSize = null;
    protected Dimension minViewportSize = new Dimension(2, 2);
    protected boolean highlightingEnabled = true;

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            Dimension prefSize = getPreferredSize();
            Dimension viewportSize = ((JViewport) getParent()).getSize();
            return viewportSize.width > prefSize.width;
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            Dimension prefSize = getPreferredSize();
            Dimension viewportSize = ((JViewport) getParent()).getSize();
            return viewportSize.height > prefSize.height;
        }
        return false;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return minViewportSize;
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return preferredSize;
        }
        Dimension size = null;
        if (ui != null) {
            size = ui.getPreferredSize(this);
        }
        return (size != null) ? size : new Dimension(2, 2);
    }

    @Override
    public void setPreferredSize(Dimension aPreferredSize) {
        super.setPreferredSize(aPreferredSize);
        preferredSize = aPreferredSize;
    }

    @Override
    public boolean isPreferredSizeSet() {
        return preferredSize != null;
    }

    @Override
    public void updateHighlighting(int lStartElement, int lEndElement) {
        if (highlightingEnabled) {
            super.updateHighlighting(lStartElement, lEndElement);
        }
    }

    @Override
    public void updateHighlighting() {
        if (highlightingEnabled) {
            super.updateHighlighting();
        }
    }

    public boolean isHighlightingEnabled() {
        return highlightingEnabled;
    }

    public void setHighlightingEnabled(boolean highlightingEnabled) {
        this.highlightingEnabled = highlightingEnabled;
    }

    @Override
    public void setText(String t) {
        highlightingEnabled = false;
        try {
            super.setText(t);
        } finally {
            highlightingEnabled = true;
        }
    }
}

package com.eas.client.utils.scalableui;

import javax.swing.RepaintManager;
import java.awt.*;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ZoomRepaintManager extends RepaintManager {

    protected ZoomRepaintManager() {
        super();
    }

    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        JScalablePanel pc = getPanelContainer(c);
        if (pc != null && pc.isVisible() && c != pc) {
            if (!pc.isPainting()) {
                float scale = pc.getScale();
                float dxy = 2 / scale;
                Point lnewDirtyLocation = SwingUtilities.convertPoint(c, x, y, pc);
                int sx = Math.round(scale * (float) lnewDirtyLocation.x - dxy);
                int sy = Math.round(scale * (float) lnewDirtyLocation.y - dxy);
                int sw = Math.round(scale * (float) w + dxy);
                int sh = Math.round(scale * (float) h + dxy);
                super.addDirtyRegion(pc, sx, sy, sw, sh);
                //
                assert RepaintManager.currentManager(pc) != this;
                // Warning! this call is leagal only and only if global repaint manager is not this!!!!
                pc.repaint(sx, sy, sw, sh);
            }
        } else {
            super.addDirtyRegion(c, x, y, w, h);
        }
    }

    public static JScalablePanel getPanelContainer(Component c) {
        while (c != null && !(c instanceof JScalablePanel)) {
            c = c.getParent();
        }
        if (c != null && c.isVisible() && c instanceof JScalablePanel) {
            return (JScalablePanel) c;
        }
        return null;
    }

    public static ScalableComboPopup getScalableComboPopup(Component c) {
        while (c != null && !(c instanceof ScalableComboPopup)) {
            c = c.getParent();
        }
        if (c != null && c.isVisible() && c instanceof ScalableComboPopup) {
            return (ScalableComboPopup) c;
        }
        return null;
    }
}

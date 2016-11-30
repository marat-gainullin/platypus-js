/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.cache;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.map.MapContent;

/**
 *
 * @author mg
 */
public class SyncMapTilesCache extends MapTilesCache {

    public SyncMapTilesCache(int aCacheSize, MapContent aDisplayContext, AffineTransform aTransform) {
        super(aCacheSize, aDisplayContext, aTransform);
    }

    public SyncMapTilesCache(MapContent aDisplayContext, AffineTransform aTransform) {
        super(aDisplayContext, aTransform);
    }

    @Override
    protected Image renderTile(Point ptKey) {
        try {
            RenderingTask task = new RenderingTask(ptKey);
            task.run();
            return cache.get(ptKey);
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(SyncMapTilesCache.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void scaleChanged() {
        clear();
    }
}

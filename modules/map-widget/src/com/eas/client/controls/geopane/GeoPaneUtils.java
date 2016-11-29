/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane;

import com.eas.client.controls.geopane.cache.AsyncMapTilesCache;
import com.eas.client.controls.geopane.cache.webtiles.AsyncGoogleMapTilesCache;
import com.eas.client.controls.geopane.cache.webtiles.AsyncOSMMapTilesCache;
import com.eas.client.controls.geopane.cache.webtiles.AsyncWebMapTilesCache;
import com.eas.client.controls.geopane.cache.webtiles.AsyncYandexMapTilesCache;
import java.awt.geom.AffineTransform;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import org.geotools.map.MapContent;

/**
 *
 * @author mg
 */
public class GeoPaneUtils {

    protected static final ResourceBundle localizations = ResourceBundle.getBundle(GeoPaneUtils.class.getPackage().getName() + "/resources/localizations");

    public static String getString(String aKey) {
        if (localizations.containsKey(aKey)) {
            return localizations.getString(aKey);
        } else {
            return aKey;
        }
    }

    public static AsyncMapTilesCache createWebTilesCache(MapContent aContext, ReadWriteLock aMapContextLock, AffineTransform aViewTransform, String aUrlFormatString) {
        if (aUrlFormatString != null && !aUrlFormatString.isEmpty()) {
            if (aUrlFormatString.toLowerCase().contains("yandex")) {
                AsyncYandexMapTilesCache cache = new AsyncYandexMapTilesCache(aUrlFormatString, aContext, aMapContextLock, aViewTransform);
                if (aUrlFormatString.toLowerCase().contains("sat")) {
                    cache.setBackingLayerName(AsyncWebMapTilesCache.DEFAULT_SAT_LAYER_NAME);
                }
                return cache;
            } else if (aUrlFormatString.toLowerCase().contains("google")) {
                AsyncGoogleMapTilesCache cache = new AsyncGoogleMapTilesCache(aUrlFormatString, aContext, aMapContextLock, aViewTransform);
                if (aUrlFormatString.toLowerCase().contains("khm")) {
                    cache.setBackingLayerName(AsyncWebMapTilesCache.DEFAULT_SAT_LAYER_NAME);
                }
                return cache;
            } else if (aUrlFormatString.toLowerCase().contains("openstreetmap")) {
                AsyncOSMMapTilesCache cache = new AsyncOSMMapTilesCache(aUrlFormatString, aContext, aMapContextLock, aViewTransform);
                return cache;
            } else {
                return new AsyncMapTilesCache(aContext, aMapContextLock, aViewTransform);
            }
        }
        return new AsyncMapTilesCache(aContext, aMapContextLock, aViewTransform);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.cache.webtiles;

import java.awt.geom.AffineTransform;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import org.geotools.map.MapContent;

/**
 *
 * @author mg
 */
public class AsyncOSMMapTilesCache extends AsyncWebMapTilesCache {

    protected Random rnd = new Random(System.currentTimeMillis());
    private final String[] domains = new String[]{"a", "b", "c"};

    public AsyncOSMMapTilesCache(String aBaseUrl, MapContent aDisplayContext, ReadWriteLock aMapContextLock, AffineTransform aTransform) {
        super(aBaseUrl, aDisplayContext, aMapContextLock, aTransform);
    }

    @Override
    protected String getWebIndexName() {
        return "osm";
    }

    @Override
    protected void constraintTilesLevel() {
        if (tilesLevel < 0) {
            tilesLevel = 0;
        }
        if (tilesLevel > 18) {
            tilesLevel = 18;
        }
    }

    @Override
    public void configureVectorDomains(int aMin, int aMax) {
    }

    @Override
    public void configureSatteliteDomains(int aMin, int aMax) {
    }

    @Override
    protected String formatTileUrl(WebTileKey aTileKey) {

        return String.format(Locale.ENGLISH, tilesServerUrl, domains[rnd.nextInt(domains.length)], aTileKey.z, aTileKey.x, aTileKey.y);
    }
}

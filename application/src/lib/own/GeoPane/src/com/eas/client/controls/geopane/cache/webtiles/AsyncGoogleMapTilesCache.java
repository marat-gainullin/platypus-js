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
public class AsyncGoogleMapTilesCache extends AsyncWebMapTilesCache {

    protected Random rnd = new Random(System.currentTimeMillis());
    protected int minVec = 0;
    protected int maxVec = 3;
    protected int minSat = 0;
    protected int maxSat = 3;

    public AsyncGoogleMapTilesCache(String aBaseUrl, MapContent aDisplayContext, ReadWriteLock aMapContextLock, AffineTransform aTransform) {
        super(aBaseUrl, aDisplayContext, aMapContextLock, aTransform);
    }

    @Override
    protected String getWebIndexName() {
        return "google";
    }

    @Override
    protected void constraintTilesLevel() {
        if (tilesLevel < 0) {
            tilesLevel = 0;
        }
        if (tilesLevel > 19) {
            tilesLevel = 19;
        }
    }

    @Override
    protected String formatTileUrl(WebTileKey aTileKey) {
        int domainNumber = 2;
        if (tilesServerUrl.startsWith("http://khm")) // sattelite
        {
            domainNumber = rnd.nextInt(maxSat - minSat + 1) + minSat;
        } else { // vector
            domainNumber = rnd.nextInt(maxVec - minVec + 1) + minVec;
        }
        return String.format(Locale.ENGLISH, tilesServerUrl, domainNumber, aTileKey.x, aTileKey.y, aTileKey.z);
    }

    @Override
    public void configureVectorDomains(int aMin, int aMax) {
        minVec = aMin;
        maxVec = aMax;
    }

    @Override
    public void configureSatteliteDomains(int aMin, int aMax) {
        minSat = aMin;
        maxSat = aMax;
    }
}

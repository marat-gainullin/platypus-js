/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.cache;

import com.eas.client.controls.geopane.TileUtils;
import com.eas.client.controls.geopane.TilesBoundaries;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author mg
 */
public abstract class TilesCache {

    public static final int DEFAULT_CACHE_SIZE = 12 * 12;
    public static final int DEF_TILE_SIZE = 256; // 256x256 pixels
    // this would take 16 mb with 4-byte full colored images by default
    // i.e. 128*10 = 1280 - standard resolution for huge part of monitors
    // 10 + 2 for 1 inset over each edge of monitor.
    public static final int DEF_TILE_FACTOR = 10;
    public static final int DEF_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    protected int cacheSize = DEFAULT_CACHE_SIZE;
    protected int tileSize = DEF_TILE_SIZE;
    protected int tileHalf = DEF_TILE_SIZE / 2;
    protected int imageType = DEF_IMAGE_TYPE;
    protected Color background = new Color(0, 0, 0, 0);
    protected Map<Point, Image> cache = new HashMap<>();
    protected TilesBoundaries actualArea;
    protected Queue<Image> imagesQueue = new ConcurrentLinkedQueue<>();
    protected static int IMAGES_QUEUE_MAX_SIZE = 30;

    public TilesCache() {
        super();
    }

    public TilesCache(int aCacheSize) {
        super();
        cacheSize = aCacheSize;
        if (cacheSize <= 0) {
            cacheSize = DEFAULT_CACHE_SIZE;
        }
    }

    public void calcTiles(int aWidth, int aHeight) {
        if (aWidth > 0 && aHeight > 0) {
            int edgeSize = Math.max(aWidth, aHeight);
            tileSize = Math.round((float) edgeSize / (float) DEF_TILE_FACTOR);
            int edgeSizeInTiles = tileSize + 2;
            cacheSize = edgeSizeInTiles * edgeSizeInTiles;
        } else {
            tileSize = DEF_TILE_SIZE;
            cacheSize = DEFAULT_CACHE_SIZE;
        }
        tileHalf = tileSize / 2;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int aTileSize) {
        tileSize = aTileSize;
        if (tileSize <= 0) {
            tileSize = DEF_TILE_SIZE;
        }
        tileHalf = tileSize / 2;
    }

    public int getImageType() {
        return imageType;
    }

    /**
     * Ordinary shrinking the cache, clearing it if it had grew more than maximum size.
     */
    protected void shrink() {
        if (cache.size() >= cacheSize) {
            Object[] imgs = cache.values().toArray();
            cache.clear();
            for (Object img : imgs) {
                assert img == null || img instanceof Image;
                if (img != null && imagesQueue.size() < IMAGES_QUEUE_MAX_SIZE) {
                    assert !imagesQueue.contains((Image) img);
                    imagesQueue.add((Image) img);
                }
            }
        }
    }

    public void setImageType(int aImageType) {
        imageType = aImageType;
    }

    public Point expandPointFromCell(Point ptKey) {
        Rectangle rt = TileUtils.expandRectFromCell(ptKey.x, ptKey.y, tileSize);
        return new Point(rt.x + rt.width / 2, rt.y + rt.height / 2);
    }

    protected abstract Image renderTile(Point ptKey);

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color aBackground) {
        background = aBackground;
    }

    public abstract void scaleChanged();

    // synchronized methods
    public synchronized Image get(Point ptKey) {
        Image cached = cache.get(ptKey);
        if (cached == null) {
            cached = renderTile(ptKey);
            shrink();
            cache.put(ptKey, cached);
        }
        return cached;
    }

    public synchronized void put(Point ptKey, Image aImage) {
        shrink();
        cache.put(ptKey, aImage);
    }

    public synchronized void clear() {
        Object[] imgs = cache.values().toArray();
        cache.clear();
        for (Object img : imgs) {
            assert img == null || img instanceof Image;
            if (img != null && imagesQueue.size() < IMAGES_QUEUE_MAX_SIZE) {
                assert !imagesQueue.contains((Image) img);
                imagesQueue.add((Image) img);
            }
        }
    }

    /**
     * Special cache operation, intended to riddle cached tiles to avoid clearing
     * the cache while ordinary shrinking.
     * @param aActualArea TilesBoundaries instance, defining are to maintain im this cache
     * @see TilesBoundaries
     */
    public synchronized void setActualArea(TilesBoundaries aActualArea) {
        List<Point> toDel = new ArrayList<>();
        for (Entry<Point, Image> tileEntry : cache.entrySet()) {
            Point tileKey = tileEntry.getKey();
            if (!aActualArea.contains(tileKey)) {
                toDel.add(tileKey);
            }
        }
        for (Point toDelKey : toDel) {
            Image img = cache.remove(toDelKey);
            // toDelKey key may be invalid and no elements will be removed.
            // So we have a simple check here rather than an assert.
            if (img != null) {
                if (imagesQueue.size() < IMAGES_QUEUE_MAX_SIZE) {
                    assert !imagesQueue.contains(img);
                    imagesQueue.add(img);
                }
            }
        }
        actualArea = aActualArea;
    }

    public synchronized TilesBoundaries getActualArea() {
        return actualArea;
    }

    public synchronized int getFactSize() {
        return cache.size();
    }
}

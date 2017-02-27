/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.cache.webtiles;

import com.eas.client.controls.geopane.GeoPaneUtils;
import com.eas.client.controls.geopane.TileUtils;
import com.eas.client.controls.geopane.TilesBoundaries;
import com.eas.client.controls.geopane.cache.AsyncMapTilesCache;
import com.eas.util.BinaryUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.geotools.map.MapContent;

/**
 *
 * @author mg
 */
public abstract class AsyncWebMapTilesCache extends AsyncMapTilesCache {

    public static final String DEFAULT_VECTOR_LAYER_NAME = "vec";
    public static final String DEFAULT_SAT_LAYER_NAME = "sat";
    public static final String DEFAULT_HYBRID_LAYER_NAME = "hybrid";
    public static final String TEMP_DIR_PROP_NAME = "java.io.tmpdir";
    public static final String WEB_TILE_FILE_NAME_TEMPLATE = "x%d_y%d.png";
    protected static final double MERCATOR_WORLD_LENGTH = 4.007501668557849e+7;
    protected static final int WEB_TILES_CACHE_MAX_SIZE = 128;
    protected String tilesServerUrl;
    // level of the tiles in multi layer tiles storage of yandex or google or other maps service.
    protected int tilesLevel = 7;
    protected int tileInnerPixelSize = 256;
    protected static final Map<WebTileKey, Image> webTilesCache = new ConcurrentHashMap<>();
    protected Image urledPlaceholderImage;
    protected String backingLayerName = DEFAULT_VECTOR_LAYER_NAME;

    public String getBackingUrl() {
        return tilesServerUrl;
    }

    protected abstract String formatTileUrl(WebTileKey aTileKey);

    public abstract void configureVectorDomains(int aMin, int aMax);

    public abstract void configureSatteliteDomains(int aMin, int aMax);

    protected class AsyncThirdPartyRenderingTask extends AsyncRenderingTask {

        public AsyncThirdPartyRenderingTask(Point aTilePoint) throws NoninvertibleTransformException {
            super(aTilePoint);
        }

        @Override
        protected void prepareImageTile(BufferedImage image) {
            if (tilesServerUrl != null) {
                Graphics2D g = image.createGraphics();
                try {
                    try2RenderTileBack(g, tilesLevel);
                    // the following algorithm consumes too much resources
                    /*
                    int dLevel = 0;
                    while (tilesLevel >= dLevel && try2RenderTileBack(g, tilesLevel - dLevel) > 0) {
                        dLevel++;
                    }
                     */
                } finally {
                    g.dispose();
                }
            } else {
                super.prepareImageTile(image);
            }
        }

        protected int try2RenderTileBack(Graphics2D g, int aTilesLevel) {
            int badTiles = 0;
            Point2D.Double ptTopLeft = new Point2D.Double(tileAoi.getMinX(), tileAoi.getMinY());
            Point2D.Double ptBottomRight = new Point2D.Double(tileAoi.getMaxX(), tileAoi.getMaxY());
            Rectangle2D.Double cartesianTileAoi = new Rectangle2D.Double(ptTopLeft.x, ptTopLeft.y, ptBottomRight.x - ptTopLeft.x, ptBottomRight.y - ptTopLeft.y);
            double cart2ScreenCoef = (double) tileSize / cartesianTileAoi.width;
            cartesianTileAoi.x += MERCATOR_WORLD_LENGTH / 2;
            cartesianTileAoi.y += MERCATOR_WORLD_LENGTH / 2;
            double cartesianTileSize = MERCATOR_WORLD_LENGTH / Math.pow(2, aTilesLevel);
            TilesBoundaries tiles = calcTilesBoundaries(cartesianTileAoi, cartesianTileSize);
            Rectangle tilesTileAoi = convertCartesianTileAoi2TilesAoi(cartesianTileAoi, aTilesLevel);
            for (int tileX = tiles.minX; tileX <= tiles.maxX; tileX++) {
                for (int tileY = tiles.minY; tileY <= tiles.maxY; tileY++) {
                    Rectangle imageRect = TileUtils.calcImageRect(tileX, tileY, tilesTileAoi, tileInnerPixelSize);
                    Rectangle2D.Double controlRect2D = TileUtils.calcControlRect(tileX, tileY, cartesianTileAoi, cartesianTileSize);
                    controlRect2D.x -= cartesianTileAoi.x;
                    controlRect2D.y -= cartesianTileAoi.y;
                    Rectangle controlRect = new Rectangle(
                            Double.valueOf(Math.floor(controlRect2D.x * cart2ScreenCoef)).intValue(),
                            Double.valueOf(Math.floor(controlRect2D.y * cart2ScreenCoef)).intValue(),
                            Double.valueOf(Math.ceil(controlRect2D.width * cart2ScreenCoef)).intValue(),
                            Double.valueOf(Math.ceil(controlRect2D.height * cart2ScreenCoef)).intValue());
                    Image urledImage = achieveUrledTileImage(tileX, tileY, aTilesLevel);
                    if (urledImage != null) {
                        if (urledImage == urledPlaceholderImage) {
                            badTiles++;
                        }
                        int lx = 0, rx = 0, ty = 0, by = 0;
                        if (tileX == tiles.minX && controlRect.x > 0) {
                            lx = controlRect.x;
                        }
                        if (tileX == tiles.maxX && controlRect.x + controlRect.width < getTileSize() - 1) {
                            rx = getTileSize() - 1 - controlRect.x + controlRect.width;
                        }
                        if (tileY == tiles.minY && controlRect.y > 0) {
                            ty = controlRect.y;
                        }
                        if (tileY == tiles.maxY && controlRect.y + controlRect.height < getTileSize() - 1) {
                            by = getTileSize() - 1 - controlRect.y + controlRect.height;
                        }
                        g.drawImage(urledImage, controlRect.x - lx, controlRect.y - ty, controlRect.x + controlRect.width + lx + rx, controlRect.y + controlRect.height + ty + by,
                                imageRect.x - lx, imageRect.y - ty, imageRect.x + imageRect.width + lx + rx, imageRect.y + imageRect.height + ty + by, null);
                    }
                }
            }
            return badTiles;
        }

        protected Rectangle convertCartesianTileAoi2TilesAoi(Rectangle2D.Double aCartesianAoi, int aTilesLevel) {
            double pixelsByWorld = Math.pow(2, aTilesLevel) * (double) tileInnerPixelSize;
            double pixelByMeter = pixelsByWorld / MERCATOR_WORLD_LENGTH;
            return new Rectangle(
                    Double.valueOf(aCartesianAoi.x * pixelByMeter).intValue(),
                    Double.valueOf(aCartesianAoi.y * pixelByMeter).intValue(),
                    Double.valueOf(Math.ceil(aCartesianAoi.width * pixelByMeter)).intValue(),
                    Double.valueOf(Math.ceil(aCartesianAoi.height * pixelByMeter)).intValue());
        }

        protected TilesBoundaries calcTilesBoundaries(Rectangle2D.Double areaOfInterest, double aTileSize) {
            TilesBoundaries bounds = new TilesBoundaries();
            bounds.minX = Double.valueOf(Math.floor(areaOfInterest.x / aTileSize)).intValue();
            bounds.maxX = Double.valueOf(Math.floor((areaOfInterest.x + areaOfInterest.width) / aTileSize)).intValue();
            bounds.minY = Double.valueOf(Math.floor(areaOfInterest.y / aTileSize)).intValue();
            bounds.maxY = Double.valueOf(Math.floor((areaOfInterest.y + areaOfInterest.height) / aTileSize)).intValue();
            return bounds;
        }
    }

    protected static class WebTileKey extends Object {

        public int x;
        public int y;
        public int z;

        public WebTileKey(int aX, int aY, int aZ) {
            super();
            x = aX;
            y = aY;
            z = aZ;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final WebTileKey other = (WebTileKey) obj;
            if (this.x != other.x) {
                return false;
            }
            if (this.y != other.y) {
                return false;
            }
            return this.z == other.z;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + this.x;
            hash = 37 * hash + this.y;
            hash = 37 * hash + this.z;
            return hash;
        }

        @Override
        public String toString() {
            return "Web tile x:" + String.valueOf(x) + "; y:" + String.valueOf(y) + "; z:" + String.valueOf(z);
        }
    }

    public AsyncWebMapTilesCache(int aCacheSize, MapContent aDisplayContext, ReadWriteLock aMapContextLock, AffineTransform aTransform) {
        super(aCacheSize, aDisplayContext, aMapContextLock, aTransform);
    }

    public AsyncWebMapTilesCache(String aBaseUrl, MapContent aDisplayContext, ReadWriteLock aMapContextLock, AffineTransform aTransform) {
        super(aDisplayContext, aMapContextLock, aTransform);
        tilesServerUrl = aBaseUrl;
    }

    public String getBackingLayerName() {
        return backingLayerName;
    }

    public void setBackingLayerName(String aBackingLayerName) {
        backingLayerName = aBackingLayerName;
    }

    @Override
    public void scaleChanged() {
        super.scaleChanged();
        double scale = cartesian2ScreenTransform.getScaleX();
        double scaledWorldLength = scale * MERCATOR_WORLD_LENGTH;
        double tilesCount = scaledWorldLength / (double) tileInnerPixelSize;
        double tilesLevelOfInterest = Math.log(tilesCount) / Math.log(2);
        tilesLevel = Long.valueOf(Math.round(tilesLevelOfInterest)).intValue();
        constraintTilesLevel();
    }

    @Override
    protected Image renderTile(Point ptKey) {
        try {
            AsyncRenderingTask task = new AsyncThirdPartyRenderingTask(ptKey);
            offeredTasks.put(ptKey, task);
            executor.execute(task);
            return null;
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(AsyncMapTilesCache.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    protected Image achieveUrledTileImage(int x, int y, int z) {
        WebTileKey tKey = new WebTileKey(x, y, z);
        synchronized (webTilesCache) {
            Image cached = webTilesCache.get(tKey);
            if (cached == null) {
                cached = loadUrledTileImage(tKey);
                if (webTilesCache.size() > WEB_TILES_CACHE_MAX_SIZE) {
                    webTilesCache.clear();
                }
                webTilesCache.put(tKey, cached);
            }
            return cached;
        }
    }

    /**
     * Loads image from Web tiles index (like yandex, google, yahoo) or
     * from disk file cache.
     * @param aTileKey
     * @return Image achieved.
     * WARNING! Detalization level is NOT zoom of any kind.
     * Although Web documentation names z as zoom level, it's not true!
     * It's only detailization level and it affects only on world size in pixels of target data.
     * Furthermore, in such indexes image tile is not rendering helper or cache it is data.
     */
    protected Image loadUrledTileImage(WebTileKey aTileKey) {
        Image urledImage = null;
        try {
            String filePath = constructCachePath() + File.separator + calcTileFileName(aTileKey);
            forceCreatePath(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            File f = new File(filePath);
            if (f.exists()) {
                try (FileInputStream fi = new FileInputStream(f)) {
                    byte[] imageData = BinaryUtils.readStream(fi, -1);
                    ImageIcon icon = new ImageIcon(imageData);
                    urledImage = icon.getImage();
                }
            } else {
                String urlWithParams = formatTileUrl(aTileKey);
                Logger.getLogger(AsyncWebMapTilesCache.class.getName()).log(Level.FINE, "Formatted web tile url is: {0}", new Object[]{urlWithParams});
                URL url = new URL(urlWithParams);
                URLConnection connection = url.openConnection();
                try {
                    try (InputStream wi = connection.getInputStream()) {
                        byte[] imageData = BinaryUtils.readStream(wi, -1);
                        ImageIcon icon = new ImageIcon(imageData);
                        urledImage = icon.getImage();
                        if (!f.exists()) {
                            try (FileOutputStream fo = new FileOutputStream(f)) {
                                fo.write(imageData);
                            }
                        }
                    }
                } finally {
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AsyncWebMapTilesCache.class.getName()).log(Level.SEVERE, "{0} is unavailable. The cause is: {1}", new Object[]{aTileKey.toString(), ex.toString()});
            ceckPlaceHolderImage();
            urledImage = urledPlaceholderImage;
        }
        return urledImage;
    }

    protected String calcTileFileName(WebTileKey aTileKey) {
        String plainFileName = String.format(WEB_TILE_FILE_NAME_TEMPLATE, aTileKey.x, aTileKey.y);
        String dirPrefix = "z" + String.valueOf(aTileKey.z) + File.separator + backingLayerName;
        int mod = Double.valueOf(Math.pow(10, aTileKey.z - 5)).intValue();
        if (aTileKey.z > 5 && aTileKey.x * aTileKey.y > mod) {
            String modDirPrefix = String.valueOf(aTileKey.x * aTileKey.y % mod);
            return dirPrefix + File.separator + modDirPrefix + File.separator + plainFileName;
        } else {
            return dirPrefix + File.separator + plainFileName;
        }
    }

    protected void forceCreateDirectory(String aPath) {
        File file = new File(aPath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    protected String constructCachePath() {
        String cachedTilesPath = System.getProperty(TEMP_DIR_PROP_NAME);
        if (!cachedTilesPath.endsWith(File.separator)) {
            cachedTilesPath += File.separator;
        }
        cachedTilesPath += ".eas";
        cachedTilesPath += File.separator + "mapsTilesCache";
        cachedTilesPath += File.separator + getWebIndexName();
        return cachedTilesPath;
    }

    protected void forceCreatePath(String aPath) {
        String sep = File.separator;
        if (sep.equals("\\")) {
            sep = "\\" + sep;
        }
        String[] pathElements = aPath.split(sep);
        String forcedPath = null;
        for (String pathElement : pathElements) {
            if (forcedPath != null) {
                forcedPath += File.separator;
            }
            if (forcedPath == null) {
                forcedPath = "";
            }
            forcedPath += pathElement;
            forceCreateDirectory(forcedPath);
        }
    }

    protected void ceckPlaceHolderImage() {
        if (urledPlaceholderImage == null) {
            urledPlaceholderImage = new BufferedImage(tileInnerPixelSize, tileInnerPixelSize, BufferedImage.TYPE_INT_RGB);
            Graphics g = urledPlaceholderImage.getGraphics();
            g.setColor(background);
            g.fillRect(0, 0, tileInnerPixelSize, tileInnerPixelSize);
            g.setColor(Color.gray);
            g.drawString(GeoPaneUtils.getString("backingUnavailable"), 2, tileInnerPixelSize / 2);
        }
    }

    protected abstract String getWebIndexName();

    protected abstract void constraintTilesLevel();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane;

import com.eas.client.controls.geopane.cache.TilesCache;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReadWriteLock;
import javax.swing.JPanel;

/**
 * Component designed form fast multithreaded rendering of maps and schemes.
 * The idea is to paint series of tiles across component's area.
 * Tiles will be made by multitreaded renderer from geotools or netbeans visual libraries.
 * Tiles will be cached by this component for reuse while painting.
 * The cache will be cleared by any non-translating transformations.
 * The approach is to translate screen grid instead of translating user's point of view
 * whenever such behavour remains correct, i.e. while scale, rotate and shear transformations are absent
 * and only translating is present.
 * @author mg
 */
public class JTiledPane extends JPanel {

    public static final String PLACE_HOLDER_LABEL_TEXT = "loadingLabelText";
    public static final int WAIT_4_RENDERING_TIME = 4;
    // tiles screen grid offset
    protected Point offset = new Point(0, 0);
    // map or schema background images, rendered vector figures and other heavyweight layers group.
    protected TilesCache cache;
    protected ReadWriteLock cacheLock;
    // selection, signalig and other lightweight layers group tiles cache
    protected TilesCache lightweightCache;
    protected ReadWriteLock lightweightCacheLock;
    // image placeholder of the absent tile
    protected Image placeholderImage = null;
    protected static final int SCREEN_INSET_SIZE = 1;
    protected Rectangle selectionRectangle;

    protected JTiledPane() {
        super();
    }

    public JTiledPane(TilesCache aCache) {
        this();
        cache = aCache;
        cache.setBackground(getBackground());
    }

    public JTiledPane(TilesCache aCache, TilesCache aLightweightCache) {
        this(aCache);
        lightweightCache = aLightweightCache;
        lightweightCache.setImageType(BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (cache != null) {
            cache.setBackground(getBackground());
        }
    }

    public TilesCache getCache() {
        return cache;
    }

    public TilesCache getLightweightCache() {
        return lightweightCache;
    }

    public void clearGridTranslating() {
        offset.x = 0;
        offset.y = 0;
    }

    public void translateGrid(int x, int y) throws Exception {
        translateGrid(x, y, true);
    }

    public void translateGrid(int x, int y, boolean isLast) throws Exception {
        offset.x -= x;
        offset.y -= y;
        invalidate();
    }

    public int getGridOffsetX() {
        return offset.x;
    }

    public int getGridOffsetY() {
        return offset.y;
    }

    public Rectangle getSelectionRectangle() {
        return selectionRectangle;
    }

    public void setSelectionRectangle(Rectangle aRectangle) {
        assert aRectangle == null || aRectangle.width >= 0;
        assert aRectangle == null || aRectangle.height >= 0;
        selectionRectangle = aRectangle;
    }

    @Override
    public void paint(Graphics g) {
        validate();
        super.paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics tiledGraphics = g.create();
        try {
            if (placeholderImage == null) {
                placeholderImage = generatePlaceholder(cache);
            }
            Dimension size = getSize();
            // place the begining of coordinate system into center of the window and than
            // translate it to last grid anchor
            tiledGraphics.translate(size.width / 2 - offset.x, size.height / 2 - offset.y);

            Rectangle clip = tiledGraphics.getClipBounds();
            if (!clip.isEmpty()) {
                Rectangle screen = new Rectangle(-size.width / 2, -size.height / 2, size.width, size.height);
                screen.translate(offset.x, offset.y);
                TilesBoundaries clipTiles = calcTilesBoundaries(clip);
                TilesBoundaries screenTiles = calcTilesBoundaries(screen);
                cache.setActualArea(screenTiles.expanded(SCREEN_INSET_SIZE));
                lightweightCache.setActualArea(screenTiles.expanded(SCREEN_INSET_SIZE));
                int cX = (clipTiles.minX + clipTiles.maxX) / 2;
                int cY = (clipTiles.minY + clipTiles.maxY) / 2;
                paintTile(cX, cY, clip, tiledGraphics);
                paintTile(cX - 1, cY - 1, clip, tiledGraphics);
                paintTile(cX - 1, cY, clip, tiledGraphics);
                paintTile(cX, cY - 1, clip, tiledGraphics);
                for (int x = clipTiles.minX; x <= clipTiles.maxX; x++) {
                    for (int y = clipTiles.minY; y <= clipTiles.maxY; y++) {
                        if (x == cX && y == cY || x == cX - 1 && y == cY - 1 || x == cX - 1 && y == cY || x == cX && y == cY - 1) {
                        } else {
                            paintTile(x, y, clip, tiledGraphics);
                        }
                    }
                }
                if (tiledGraphics instanceof Graphics2D) {
                    paintScreenContext((Graphics2D) tiledGraphics);
                }
            }
        } finally {
            tiledGraphics.dispose();
        }
    }

    protected void paintScreenContext(Graphics2D g) {
        if (selectionRectangle != null) {
            g.translate(offset.x, offset.y);
            g.setColor(Color.green.darker());
            g.drawRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
        }
    }

    private void paintTile(int x, int y, Rectangle clip, Graphics g) {
        Point ptKey = new Point(x, y);
        Image image = cache.get(ptKey);
        Image lightweightImage = lightweightCache.get(ptKey);
        Rectangle controlRect = TileUtils.calcControlRect(x, y, clip, cache.getTileSize());
        Rectangle imageRect = TileUtils.calcImageRect(x, y, clip, cache.getTileSize());
        if (image == null) {
            image = placeholderImage;
        }
        if (image != null) {
            g.drawImage(image, controlRect.x, controlRect.y, controlRect.x + controlRect.width, controlRect.y + controlRect.height, imageRect.x, imageRect.y, imageRect.x + imageRect.width, imageRect.y + imageRect.height, null);
        }
        if (lightweightImage != null) {
            g.drawImage(lightweightImage, controlRect.x, controlRect.y, controlRect.x + controlRect.width, controlRect.y + controlRect.height, imageRect.x, imageRect.y, imageRect.x + imageRect.width, imageRect.y + imageRect.height, null);
        }
    }

    /**
     * Calculates tiles numbers along corresponding axes.
     * Let's assume tile size is 128 pixels.
     * screen point (95, 10) will lead to tile point (0, 0).
     * screen point (-5, -1) will lead to tile point (-1, -1).
     * screen point (129, 10) will lead to tile point (1, 0).
     * @param areaOfInterest Area, tile numbers will be calculated for.
     * @return TilesBoundaries instance with calculated tiles numbers.
     */
    protected TilesBoundaries calcTilesBoundaries(Rectangle areaOfInterest) {
        TilesBoundaries bounds = new TilesBoundaries();
        bounds.minX = Double.valueOf(Math.floor((double) areaOfInterest.x / (double) cache.getTileSize())).intValue();
        bounds.maxX = Double.valueOf(Math.floor((double) (areaOfInterest.x + areaOfInterest.width) / (double) cache.getTileSize())).intValue();
        bounds.minY = Double.valueOf(Math.floor((double) areaOfInterest.y / (double) cache.getTileSize())).intValue();
        bounds.maxY = Double.valueOf(Math.floor((double) (areaOfInterest.y + areaOfInterest.height) / (double) cache.getTileSize())).intValue();
        return bounds;
    }

    private Image generatePlaceholder(TilesCache aCache) {
        String labelText = GeoPaneUtils.getString(PLACE_HOLDER_LABEL_TEXT);
        Image img = new BufferedImage(aCache.getTileSize(), aCache.getTileSize(), aCache.getImageType());
        Graphics g = img.getGraphics();
        g.setColor(aCache.getBackground());
        g.fillRect(0, 0, img.getWidth(null), img.getHeight(null));
        g.setColor(Color.gray);
        g.drawString(labelText, 0, aCache.getTileSize() / 2 - 5);
        return img;
    }
}

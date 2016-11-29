/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.mappane.grid;

import com.eas.client.controls.mappane.GraphicTest;
import com.eas.client.controls.geopane.JTiledPane;
import com.eas.client.controls.geopane.TileUtils;
import com.eas.client.controls.geopane.cache.TilesCache;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.junit.Test;

/**
 * This test translates world transformation matrix and clears geo pane's cache.
 * It's slow approach. Dynamic tiles test will follow.
 * @author mg
 */
public class StaticTilesMachineTest extends GraphicTest {

    protected final AffineTransform transform = new AffineTransform();
    protected JTiledPane pane;

    protected class LeftAction extends AbstractAction {

        public LeftAction() {
            super();
            putValue(Action.NAME, " < ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        }

        public void actionPerformed(ActionEvent e) {
            transform.translate(2, 0);
            pane.getCache().clear();
            pane.getLightweightCache().clear();
            pane.repaint();
        }
    }

    protected class RightAction extends AbstractAction {

        public RightAction() {
            super();
            putValue(Action.NAME, " > ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        }

        public void actionPerformed(ActionEvent e) {
            transform.translate(-2, 0);
            pane.getCache().clear();
            pane.getLightweightCache().clear();
            pane.repaint();
        }
    }

    protected class UpAction extends AbstractAction {

        public UpAction() {
            super();
            putValue(Action.NAME, " < ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        }

        public void actionPerformed(ActionEvent e) {
            transform.translate(0, 2);
            pane.getCache().clear();
            pane.getLightweightCache().clear();
            pane.repaint();
        }
    }

    protected class DownAction extends AbstractAction {

        public DownAction() {
            super();
            putValue(Action.NAME, " < ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        }

        public void actionPerformed(ActionEvent e) {
            transform.translate(0, -2);
            pane.getCache().clear();
            pane.getLightweightCache().clear();
            pane.repaint();
        }
    }

    public StaticTilesMachineTest() {
        super();
    }

    @Test
    public void simpleRectangleTest() throws InterruptedException {
        // let's assume our view point is (0,0)
        JFrame fr = new JFrame();
        fr.setTitle(StaticTilesMachineTest.class.getSimpleName());
        transform.scale(10, 10);
        Container container = fr.getContentPane();
        container.setLayout(new BorderLayout());
        TilesCache cache = new TilesCache() {

            @Override
            protected Image renderTile(Point ptKey) {
                Rectangle rect = TileUtils.expandRectFromCell(ptKey, tileSize);
                Image image = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
                Graphics g = image.getGraphics();
                // let's move coordinate system to the center
                g.translate(tileSize / 2 - (rect.x + rect.width / 2), tileSize / 2 - (rect.y + rect.height / 2));
                for (Point pt : points) {
                    Point pt1 = new Point();
                    transform.transform(pt, pt1);
                    g.drawRect(pt1.x, pt1.y, 1, 1);
                }
                for (Polygon poly : polies) {
                    Polygon poly1 = new Polygon();
                    for (int j = 0; j < poly.npoints; j++) {
                        Point pt = new Point(poly.xpoints[j], poly.ypoints[j]);
                        transform.transform(pt, pt);
                        poly1.addPoint(pt.x, pt.y);
                    }
                    g.drawPolygon(poly1);
                }
                return image;
            }

            @Override
            public void scaleChanged() {
            }
        };
        TilesCache cache1 = new TilesCache() {

            @Override
            protected Image renderTile(Point ptKey) {
                Rectangle rect = TileUtils.expandRectFromCell(ptKey, tileSize);
                Image image = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
                Graphics g = image.getGraphics();
                // let's move coordinate system to the center
                g.translate(tileSize / 2 - (rect.x + rect.width / 2), tileSize / 2 - (rect.y + rect.height / 2));
                for (Point pt : points) {
                    Point pt1 = new Point();
                    transform.transform(pt, pt1);
                    g.drawRect(pt1.x-10, pt1.y-10, 20, 20);
                }
                return image;
            }

            @Override
            public void scaleChanged() {
            }
        };
        pane = new JTiledPane(cache, cache1);
        container.add(pane, BorderLayout.CENTER);

        Action leftAction = new LeftAction();
        Action rightAction = new RightAction();
        Action upAction = new UpAction();
        Action downAction = new DownAction();

        pane.getInputMap().put((KeyStroke) leftAction.getValue(Action.ACCELERATOR_KEY), LeftAction.class.getSimpleName());
        pane.getActionMap().put(LeftAction.class.getSimpleName(), leftAction);

        pane.getInputMap().put((KeyStroke) rightAction.getValue(Action.ACCELERATOR_KEY), RightAction.class.getSimpleName());
        pane.getActionMap().put(RightAction.class.getSimpleName(), rightAction);

        pane.getInputMap().put((KeyStroke) upAction.getValue(Action.ACCELERATOR_KEY), UpAction.class.getSimpleName());
        pane.getActionMap().put(UpAction.class.getSimpleName(), upAction);

        pane.getInputMap().put((KeyStroke) downAction.getValue(Action.ACCELERATOR_KEY), DownAction.class.getSimpleName());
        pane.getActionMap().put(DownAction.class.getSimpleName(), downAction);

        fr.setSize(600, 600);
        fr.setVisible(true);
        Thread.sleep(1000);
    }
}

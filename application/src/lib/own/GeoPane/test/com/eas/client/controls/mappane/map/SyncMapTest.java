/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.mappane.map;

import com.eas.client.controls.geopane.JGeoPane;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Style;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author mg
 */
public class SyncMapTest extends MapGraphicTest {

    protected JGeoPane pane;

    protected class LeftAction extends AbstractAction {

        public LeftAction() {
            super();
            putValue(Action.NAME, " < ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                pane.translateGrid(2, 0);
                pane.repaint();
            } catch (Exception ex) {
                Logger.getLogger(SyncMapTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class RightAction extends AbstractAction {

        public RightAction() {
            super();
            putValue(Action.NAME, " > ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                pane.translateGrid(-2, 0);
                pane.repaint();
            } catch (Exception ex) {
                Logger.getLogger(SyncMapTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class UpAction extends AbstractAction {

        public UpAction() {
            super();
            putValue(Action.NAME, " < ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                pane.translateGrid(0, 2);
                pane.repaint();
            } catch (Exception ex) {
                Logger.getLogger(SyncMapTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class DownAction extends AbstractAction {

        public DownAction() {
            super();
            putValue(Action.NAME, " < ");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                pane.translateGrid(0, -2);
                pane.repaint();
            } catch (Exception ex) {
                Logger.getLogger(SyncMapTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void geoToolsBaseSettingsTest() throws InterruptedException, FactoryException, ParseException {
    }

    @Test
    public void geoPaneSyncTest() throws Exception {
        LineString lightweightLine = (LineString) wktReader.read("LINESTRING (-0.8 -1, 2 1)");

        // cartesian view point
        Point2D.Double viewPoint = new Point2D.Double(2 * 111313.83923667614, 0);

        // Create line symbolizer
        LineSymbolizer lineSymb = sb.createLineSymbolizer(Color.RED, 1);
        Style lineStyle = sb.createStyle(lineSymb);

        // Create line symbolizer
        LineSymbolizer lineSymb1 = sb.createLineSymbolizer(Color.lightGray, 1);
        Style lineStyle1 = sb.createStyle(lineSymb1);

        ReferencedEnvelope aoi = new ReferencedEnvelope(new Rectangle2D.Double(viewPoint.x - 100, viewPoint.y - 100, 4 * 111000, 200), projectedCrs); // meters

        MapContent mainContext = new MapContent(projectedCrs);
        mainContext.getViewport().setBounds(aoi);

        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setCRS(dataCrs);
        typeBuilder.setName("Line type");
        typeBuilder.add("graphicRepresentation", LineString.class);
        typeBuilder.add("name", String.class);
        SimpleFeatureType featureType = typeBuilder.buildFeatureType();

        List<SimpleFeature> lst = new ArrayList<>();

        Object[] attrs = new Object[2];
        int lNo = 0;
        for (LineString line : lineStrings) {
            attrs[0] = line;
            String lStringId = String.valueOf(lNo++);
            attrs[1] = "line " + lStringId;
            SimpleFeature feature = SimpleFeatureBuilder.build(featureType, attrs, lStringId);
            lst.add(feature);
        }
        
        FeatureCollection fcollection1 = new ListFeatureCollection(featureType, lst);
        lst.clear();
        Layer layer1 = new FeatureLayer(fcollection1, lineStyle, "Main layer");
        mainContext.addLayer(layer1);

        final MapContent lightContext = new MapContent(projectedCrs);
        lightContext.getViewport().setBounds(aoi);

        attrs[0] = lightweightLine;
        String lStringId = String.valueOf(lNo++);
        attrs[1] = "line " + lStringId;
        SimpleFeature feature = SimpleFeatureBuilder.build(featureType, attrs, lStringId);
        lst.add(feature);

        FeatureCollection fcollection2 = new ListFeatureCollection(featureType, lst);
        
        Layer layer2 = new FeatureLayer(fcollection2, lineStyle1, "Lightweight layer");
        lightContext.addLayer(layer2);

        pane = new JGeoPane(mainContext, lightContext, false);
        pane.scaleView(8e-4, 8e-4, false);
        pane.translateView(-viewPoint.x, -viewPoint.y, true);
        
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

        JFrame fr = new JFrame();
        fr.setTitle("geoPaneSyncTest");
        Container container = fr.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(pane, BorderLayout.CENTER);
        fr.setSize(600, 600);
        fr.setVisible(true);
        Thread.sleep(1000);
        fr.dispose();
    }
}

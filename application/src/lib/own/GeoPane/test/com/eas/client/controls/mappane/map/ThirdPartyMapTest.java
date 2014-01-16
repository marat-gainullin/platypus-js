/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.controls.mappane.map;

import com.eas.client.controls.geopane.JGeoPane;
import com.eas.client.controls.geopane.actions.DownAction;
import com.eas.client.controls.geopane.actions.InfoAction;
import com.eas.client.controls.geopane.actions.LeftAction;
import com.eas.client.controls.geopane.actions.RightAction;
import com.eas.client.controls.geopane.actions.UpAction;
import com.eas.client.controls.geopane.actions.ZoomInAction;
import com.eas.client.controls.geopane.actions.ZoomOutAction;
import com.eas.client.controls.geopane.mousetools.DragPanner;
import com.eas.client.controls.geopane.mousetools.WheelZoomer;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
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
public class ThirdPartyMapTest extends MapGraphicTest{

    protected JGeoPane pane;

    @Override
    public void geoToolsBaseSettingsTest() throws InterruptedException, FactoryException, ParseException {
    }

    @Test
    public void geoPaneAsyncTest() throws Exception {
        LineString lightweightLine  = (LineString) wktReader.read("LINESTRING (-0.8 -1, 2 -1)");

        // Cartesian view point
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

        String baseUrl = "http://vec0%d.maps.yandex.ru/tiles?l=map&v=2.16.0&x=%d&y=%d&z=%d"; // vec01 - vec04
        //String baseUrl = "http://sat0%d.maps.yandex.net/tiles?l=sat&v=1.19.0&x=%d&y=%d&z=%d"; // sat00 - sat04
        //String baseUrl = "http://mt%d.google.com/vt?x=%d&y=%d&z=%d";// mt0 - mt3
        //String baseUrl = "http://khm%d.google.com/kh/v=69&x=%d&y=%d&z=%d"; // khm0 - khm3

        final double MERCATOR_WORLD_LENGTH = 4.007501668557849e+7;

        pane = new JGeoPane(mainContext, lightContext, true);
        pane.setBackingUrl(baseUrl);
        pane.scaleView(8e-4, 8e-4, false);
        //pane.scaleView(256/MERCATOR_WORLD_LENGTH, 256/MERCATOR_WORLD_LENGTH, false);
        pane.translateView(-viewPoint.x, -viewPoint.y, true);

        Point2D.Double pt1 = pane.geo2Cartesian(new Point2D.Double(-180, -89));
        Point2D.Double pt2 = pane.geo2Cartesian(new Point2D.Double(180, 89));

        Action leftAction = new LeftAction(pane);
        Action rightAction = new RightAction(pane);
        Action upAction = new UpAction(pane);
        Action downAction = new DownAction(pane);
        Action zoomInAction = new ZoomInAction(pane);
        Action zoomOutAction = new ZoomOutAction(pane);
        Action infoAction = new InfoAction(pane);

        pane.getInputMap().put((KeyStroke) leftAction.getValue(Action.ACCELERATOR_KEY), LeftAction.class.getSimpleName());
        pane.getActionMap().put(LeftAction.class.getSimpleName(), leftAction);

        pane.getInputMap().put((KeyStroke) rightAction.getValue(Action.ACCELERATOR_KEY), RightAction.class.getSimpleName());
        pane.getActionMap().put(RightAction.class.getSimpleName(), rightAction);

        pane.getInputMap().put((KeyStroke) upAction.getValue(Action.ACCELERATOR_KEY), UpAction.class.getSimpleName());
        pane.getActionMap().put(UpAction.class.getSimpleName(), upAction);

        pane.getInputMap().put((KeyStroke) downAction.getValue(Action.ACCELERATOR_KEY), DownAction.class.getSimpleName());
        pane.getActionMap().put(DownAction.class.getSimpleName(), downAction);

        pane.getInputMap().put((KeyStroke) zoomInAction.getValue(Action.ACCELERATOR_KEY), ZoomInAction.class.getSimpleName());
        pane.getActionMap().put(ZoomInAction.class.getSimpleName(), zoomInAction);

        pane.getInputMap().put((KeyStroke) zoomOutAction.getValue(Action.ACCELERATOR_KEY), ZoomOutAction.class.getSimpleName());
        pane.getActionMap().put(ZoomOutAction.class.getSimpleName(), zoomOutAction);

        pane.getInputMap().put((KeyStroke) infoAction.getValue(Action.ACCELERATOR_KEY), InfoAction.class.getSimpleName());
        pane.getActionMap().put(InfoAction.class.getSimpleName(), infoAction);

        DragPanner panner = new DragPanner(pane);
        pane.addMouseMotionListener(panner);
        pane.addMouseListener(panner);
        WheelZoomer wheelZoomer = new WheelZoomer(pane);
        pane.addMouseWheelListener(wheelZoomer);
        //RectZoomer rectZoomer = new RectZoomer(pane);
        //pane.addMouseMotionListener(rectZoomer);
        //pane.addMouseListener(rectZoomer);

        JFrame fr = new JFrame();
        fr.setTitle("geoPaneAsyncTest");
        Container container = fr.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(pane, BorderLayout.CENTER);
        fr.setSize(1200, 900);
        fr.setVisible(true);
        Thread.sleep(1000);
    }
}

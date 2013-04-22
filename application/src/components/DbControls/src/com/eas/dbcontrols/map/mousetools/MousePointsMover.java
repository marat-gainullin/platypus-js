/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.bearsoft.rowset.Row;
import com.eas.client.controls.geopane.mousetools.MouseToolCapability;
import com.eas.client.geo.GisUtilities;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.map.DbMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.map.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author mg
 */
public class MousePointsMover extends MapTool {

    protected List<SelectionEntry> sEntries2Move;
    protected Point downGeoPt;
    protected Point prevGeoPt;
    protected Point geoPt;

    public MousePointsMover(DbMap aMap) {
        super(aMap);
    }

    protected List<SelectionEntry> obtainSelection2Move() throws Exception {
        if (map.selectedPointHitted(geoPt)) {
            return map.getSelection().getSelection();
        } else {
            return null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            try {
                Point2D.Double cartesianPt = pane.awtScreen2Cartesian(e.getPoint());
                prevGeoPt = GisUtilities.createPoint(pane.cartesian2Geo(cartesianPt));
                downGeoPt = prevGeoPt;
                geoPt = prevGeoPt;
                sEntries2Move = obtainSelection2Move();
            } catch (Exception ex) {
                Logger.getLogger(MousePointsMover.class.getName()).log(Level.SEVERE, null, ex);
                sEntries2Move = null;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (sEntries2Move != null) {
            try {
                prevGeoPt = geoPt;
                Point2D.Double cartesianPt = pane.awtScreen2Cartesian(e.getPoint());
                geoPt = GisUtilities.createPoint(pane.cartesian2Geo(cartesianPt));
                if (!geoPt.equals(downGeoPt)) {
                    if (!prevGeoPt.equals(geoPt)) {
                        moveSelection(geoPt.getX() - prevGeoPt.getX(), geoPt.getY() - prevGeoPt.getY());
                    }
                    map.fireSelectionChanged();
                    // Let's mark all involved rowsets as modified in order to preserve geometries changes
                    for (SelectionEntry entry : sEntries2Move) {
                        ApplicationEntity<?, ?, ?> entitiy = map.getModel().getEntityById(entry.getEntityId());
                        assert entitiy != null;
                        entitiy.getRowset().setModified(true);
                    }
                    // Let's fire feature chaned event to all editable layers
                    MapLayer[] lightLayers = map.getPane().getLightweightMapContext().getLayers();
                    for(MapLayer layer:lightLayers)
                    {
                        FeatureSource<? extends FeatureType, ? extends Feature> source = layer.getFeatureSource();
                        assert source instanceof RowsFeatureSource;
                        RowsFeatureSource rowsSource = (RowsFeatureSource)source;
                        rowsSource.fireFeaturesChanged();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(MousePointsMover.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sEntries2Move = null;
        prevGeoPt = null;
        geoPt = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (sEntries2Move != null) {
            try {
                prevGeoPt = geoPt;
                Point2D.Double cartesianPt = pane.awtScreen2Cartesian(e.getPoint());
                geoPt = GisUtilities.createPoint(pane.cartesian2Geo(cartesianPt));
                if (!prevGeoPt.equals(geoPt)) {
                    moveSelection(geoPt.getX() - prevGeoPt.getX(), geoPt.getY() - prevGeoPt.getY());
                }
                map.fireSelectionChanged();
            } catch (Exception ex) {
                Logger.getLogger(MousePointsMover.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void moveSelection(double dX, double dY) throws Exception {
        assert sEntries2Move != null;
        Map<String, Row> rows = new HashMap<>();
        Map<String, Integer> rowsColIndexes = new HashMap<>();
        Map<String, Geometry> oldGeometries = new HashMap<>();

        for (SelectionEntry sEntry : sEntries2Move) {
            Coordinate sPtCoordinate = sEntry.getViewShape().getCoordinates()[0];
            sEntry.setViewShape(GisUtilities.createPoint(new Coordinate(sPtCoordinate.x + dX, sPtCoordinate.y + dY)));

            Row row = sEntry.getRow();
            Object oGeometry = row.getColumnObject(sEntry.getGeometryColIndex());
            assert oGeometry instanceof Geometry;
            Geometry geometry = (Geometry) oGeometry;
            rows.put(sEntry.getFeatureId(), row);
            oldGeometries.put(sEntry.getFeatureId(), geometry);
            rowsColIndexes.put(sEntry.getFeatureId(), sEntry.getGeometryColIndex());
        }

        Map<String, List<Coordinate[]>> newGeometriesData = new HashMap<>();
        for (SelectionEntry sEntry : sEntries2Move) {
            // data for new geometries
            List<Coordinate[]> g = newGeometriesData.get(sEntry.getFeatureId());
            if (g == null) {
                g = new ArrayList<>();
                newGeometriesData.put(sEntry.getFeatureId(), g);
                Geometry oldGeometry = oldGeometries.get(sEntry.getFeatureId());
                for (int i = 0; i < oldGeometry.getNumGeometries(); i++) {
                    Coordinate[] newCoordinates = oldGeometry.getGeometryN(i).getCoordinates();
                    g.add(newCoordinates);
                }
            }
            // new (moved) points
            if (sEntry.getGeometryOfInterestIndex() < 0) {
                g.get(0)[sEntry.getCoordinateOfInterestIndex()] = sEntry.getViewShape().getCoordinate();
            } else {
                g.get(sEntry.getGeometryOfInterestIndex())[sEntry.getCoordinateOfInterestIndex()] = sEntry.getViewShape().getCoordinate();
            }
        }
        // new geometries construction and assigning
        for (String gKey : newGeometriesData.keySet()) {
            Row row2Update = rows.get(gKey);
            Geometry oldGeometry = oldGeometries.get(gKey);
            row2Update.setColumnObject(rowsColIndexes.get(gKey), GisUtilities.constructGeometry(newGeometriesData.get(gKey), oldGeometry.getClass(), oldGeometry.getSRID()));
        }
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.BUTTONS
                || aCapability == MouseToolCapability.MOTION;
    }
}

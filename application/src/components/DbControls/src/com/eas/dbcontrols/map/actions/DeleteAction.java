/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.actions;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.controls.geopane.actions.GeoPaneAction;
import com.eas.util.gis.GeometryUtils;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.IconCache;
import com.eas.dbcontrols.map.DbMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.geotools.data.FeatureSource;
import org.geotools.map.Layer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author mg
 */
public class DeleteAction extends GeoPaneAction {

    private class DeleteFeature {

        private int geometryIdx;
        private String featureId;
        private List<Integer> shells;
        private Map<Integer, List<Integer>> holes;

        public DeleteFeature(int aGeometryIdx, String aFeatureId) {
            geometryIdx = aGeometryIdx;
            featureId = aFeatureId;
            shells = new ArrayList<>();
            holes = new TreeMap<>(new DescComparator());
        }

        /**
         * @return the geometryIdx
         */
        public int getGeometryIdx() {
            return geometryIdx;
        }

        /**
         * @return the shells
         */
        public List<Integer> getShells() {
            return shells;
        }

        /**
         * @return the holes
         */
        public Map<Integer, List<Integer>> getHoles() {
            return holes;
        }

        /**
         * @return the featureId
         */
        public String getFeatureId() {
            return featureId;
        }
    }
    
    private static class DescComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }
    protected DbMap map;
    private Map<String, Map<Integer, DeleteFeature>> deleteFeatures;

    private void addDeleteFeature(String aFeatureId, int aGeometryIdx, int aHoleIdx, int aCoordinateIdx) {
        Map<Integer, DeleteFeature> dfl = deleteFeatures.get(aFeatureId);
        if (dfl == null) {
            dfl = new TreeMap<>(new DescComparator());
            deleteFeatures.put(aFeatureId, dfl);
        }
        int idx = aGeometryIdx < 0 ? 0 : aGeometryIdx;
        DeleteFeature df = dfl.get(idx);
        if (df == null) {    
            df = new DeleteFeature(aGeometryIdx, aFeatureId);
            dfl.put(idx, df);
        }
        if (aHoleIdx < 0) {
            df.getShells().add(aCoordinateIdx);
        } else {
            List<Integer> coord = df.getHoles().get(aHoleIdx);
            if (coord == null) {
                coord = new ArrayList<>();
                coord.add(aCoordinateIdx);
                df.getHoles().put(aHoleIdx, coord);
            } else {
                coord.add(aCoordinateIdx);
            }
        }
    }

    public DeleteAction(DbMap aMap) {
        super(aMap.getPane());
        map = aMap;
        putValue(Action.NAME, getClass().getSimpleName());
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/delete.png"));
    }

    private Polygon[] processHoles(Map<Integer, List<Integer>> aHoles, Polygon[] aResultHoles) {
        List<Polygon> holes = new ArrayList<>(Arrays.asList(aResultHoles));
        for (Integer key : aHoles.keySet()) {
            List<Integer> deleteCoordinates = aHoles.get(key);
            Collections.sort(deleteCoordinates, new DescComparator());
            Coordinate[] coordinate = GeometryUtils.deletePointsFromCoordinates(aResultHoles[key], deleteCoordinates);
            if (coordinate != null && GeometryUtils.isValidGeometryDataSection(coordinate, Polygon.class)) {
                holes.set(key, (Polygon) GeometryUtils.constructGeometry(coordinate, Polygon.class));
            } else {
                holes.remove(key.intValue());
            }
        }
        Polygon[] res = new Polygon[0];
        return holes.toArray(res);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            List<SelectionEntry> sEntries2Remove = map.getSelection().getSelection();
            assert sEntries2Remove != null;
            Map<String, Row> rows = new HashMap<>();
            Map<String, Integer> rowsColIndexes = new HashMap<>();
            Map<String, Geometry> oldGeometries = new HashMap<>();
            Map<String, ApplicationEntity<?, ?, ?>> features2Entities = new HashMap<>();
            deleteFeatures = new HashMap<>();

            for (SelectionEntry sEntry : sEntries2Remove) {
                Row row = sEntry.getRow();
                Object oGeometry = row.getColumnObject(sEntry.getGeometryColIndex());
                assert oGeometry instanceof Geometry;
                Geometry geometry = (Geometry) oGeometry;
                rows.put(sEntry.getFeatureId(), row);
                oldGeometries.put(sEntry.getFeatureId(), geometry);
                rowsColIndexes.put(sEntry.getFeatureId(), sEntry.getGeometryColIndex());
                features2Entities.put(sEntry.getFeatureId(), sEntry.getEntity());
            }

            // data from old geometries
            Map<String, List<Geometry>> newGeometriesData = new HashMap<>();
            for (SelectionEntry sEntry : sEntries2Remove) {
                List<Geometry> g = newGeometriesData.get(sEntry.getFeatureId());
                if (g == null) {
                    g = new ArrayList<>();
                    newGeometriesData.put(sEntry.getFeatureId(), g);
                    Geometry oldGeometry = oldGeometries.get(sEntry.getFeatureId());
                    for (int i = 0; i < oldGeometry.getNumGeometries(); i++) {
                        g.add(oldGeometries.get(sEntry.getFeatureId()));
                    }
                }
                addDeleteFeature(sEntry.getFeatureId(), sEntry.getGeometryOfInterestIndex(), sEntry.getHoleOfInterestIndex(), sEntry.getCoordinateOfInterestIndex());
            }
            // filtering of geometry data
            for (Map<Integer, DeleteFeature> ldfEntry : deleteFeatures.values()) {
                for (Integer dfKey : ldfEntry.keySet()) {
                    DeleteFeature deleteFeature = ldfEntry.get(dfKey);
                    List<Geometry> g = newGeometriesData.get(deleteFeature.getFeatureId());
                    Geometry geom = g.get(dfKey);
                    if (geom != null) {
                        if (geom instanceof Polygon) {
                            Polygon shell = GeometryUtils.getPolygonShell((Polygon) geom);
                            Polygon[] holes = GeometryUtils.getPolygonHoles((Polygon) geom);
                            if (!deleteFeature.getShells().isEmpty()) {
                                List<Integer> deleteCoordinates = deleteFeature.getShells();
                                Collections.sort(deleteCoordinates, new DescComparator());
                                Coordinate[] coordinate = GeometryUtils.deletePointsFromCoordinates(shell, deleteCoordinates);
                                if (coordinate != null && GeometryUtils.isValidGeometryDataSection(coordinate, Polygon.class)) {
                                    shell = (Polygon) GeometryUtils.constructGeometry(coordinate, Polygon.class);
                                    holes = processHoles(deleteFeature.getHoles(), holes);
                                    g.set(dfKey, GeometryUtils.createPolygonWithHoles(shell, holes));
                                } else {
                                    g.remove(dfKey.intValue());
                                }
                            } else {
                                holes = processHoles(deleteFeature.getHoles(), holes);
                                g.set(dfKey, GeometryUtils.createPolygonWithHoles(shell, holes));
                            }
                        } else {
                            List<Integer> deleteCoordinates = deleteFeature.getShells();
                            Collections.sort(deleteCoordinates, new DescComparator());
                            Coordinate[] coordinate = GeometryUtils.deletePointsFromCoordinates(geom, deleteCoordinates);
                            if (GeometryUtils.isValidGeometryDataSection(coordinate, geom.getClass())) {
                                g.set(dfKey, GeometryUtils.constructGeometry(coordinate, geom.getClass()));
                            } else {
                                g.remove(dfKey.intValue());
                            }
                        }
                    }
                }
            }
            // new geometries construction, assigning and filtering
            Set<String> features2Remove = new HashSet<>();
            for (String gKey : newGeometriesData.keySet()) {
                Geometry oldGeometry = oldGeometries.get(gKey);

                List<Geometry> coordsCloud = newGeometriesData.get(gKey);

                Row row2Update = rows.get(gKey);
                if (GeometryUtils.isValidGeometryData(coordsCloud, oldGeometry.getClass())) {
                    row2Update.setColumnObject(rowsColIndexes.get(gKey), GeometryUtils.constructGeometry(coordsCloud, oldGeometry.getClass(), oldGeometry.getSRID()));
                    features2Entities.get(gKey).getRowset().setModified(true);
                } else {
                    features2Remove.add(gKey);
                }
            }
            if (!features2Remove.isEmpty()) {
                // generate locators
                Map<Long, Locator> entitiesLocators = new HashMap<>();
                Map<Long, Field> entitiesPks = new HashMap<>();
                for (ApplicationEntity<?, ?, ?> entity : features2Entities.values()) {
                    Locator loc = RowsetUtils.generatePkLocator(entity.getRowset());
                    List<Integer> pkFields = loc.getFields();
                    assert pkFields.size() <= 1 : "Multiple primary key per rowset are not supported";
                    assert pkFields.size() > 0 : "Rowsets without primary keys are not supported";
                    entitiesLocators.put(entity.getEntityId(), loc);
                    entitiesPks.put(entity.getEntityId(), loc.getRowset().getFields().get(pkFields.get(0)));
                }
                try {
                    // removing filtered features
                    for (String gKey : features2Remove) {
                        ApplicationEntity<?, ?, ?> ent = features2Entities.get(gKey);
                        Locator loc = entitiesLocators.get(ent.getEntityId());
                        assert loc != null;
                        if (loc.find(gKey)) {
                            if (loc.first()) {
                                ent.getRowset().delete();
                            } else {
                                assert false; // should never happen
                            }
                        } else {
                            assert false; // should never happen
                        }
                    }
                } finally {
                    // remove generated locators
                    for (Locator loc : entitiesLocators.values()) {
                        loc.getRowset().removeLocator(loc);
                    }
                }
            }
            map.getSelection().clear();
            // Let's fire feature chaned event to all editabble layers
            List<Layer> lightLayers = map.getPane().getLightweightMapContext().layers();
            for (Layer layer : lightLayers) {
                FeatureSource<? extends FeatureType, ? extends Feature> source = layer.getFeatureSource();
                assert source instanceof RowsFeatureSource;
                RowsFeatureSource rowsSource = (RowsFeatureSource) source;
                rowsSource.fireFeaturesChanged();
            }
        } catch (Exception ex) {
            Logger.getLogger(DeleteAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

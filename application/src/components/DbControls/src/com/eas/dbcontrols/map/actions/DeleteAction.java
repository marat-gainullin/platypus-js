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
import com.eas.client.geo.GisUtilities;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.IconCache;
import com.eas.dbcontrols.map.DbMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.geotools.data.FeatureSource;
import org.geotools.map.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author mg
 */
public class DeleteAction extends GeoPaneAction {

    protected DbMap map;

    public DeleteAction(DbMap aMap) {
        super(aMap.getPane());
        map = aMap;
        putValue(Action.NAME, getClass().getSimpleName());
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/delete.png"));
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

            for (SelectionEntry sEntry : sEntries2Remove) {
                Row row = sEntry.getRow();
                Object oGeometry = row.getColumnObject(sEntry.getGeometryColIndex());
                assert oGeometry instanceof Geometry;
                Geometry geometry = (Geometry) oGeometry;
                rows.put(sEntry.getFeatureId(), row);
                oldGeometries.put(sEntry.getFeatureId(), geometry);
                rowsColIndexes.put(sEntry.getFeatureId(), sEntry.getGeometryColIndex());
                features2Entities.put(sEntry.getFeatureId(), map.getModel().getEntityById(sEntry.getEntityId()));
            }

            // data from old geometries
            Map<String, List<Coordinate[]>> newGeometriesData = new HashMap<>();
            for (SelectionEntry sEntry : sEntries2Remove) {
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
            }
            // filtering of geometry data
            for (SelectionEntry sEntry : sEntries2Remove) {
                List<Coordinate[]> g = newGeometriesData.get(sEntry.getFeatureId());
                Coordinate[] section = g.get(Math.max(0, sEntry.getGeometryOfInterestIndex()));
                section[sEntry.getCoordinateOfInterestIndex()] = null;
            }
            // new geometries construction, assigning and filtering
            Set<String> features2Remove = new HashSet<>();
            for (String gKey : newGeometriesData.keySet()) {
                Geometry oldGeometry = oldGeometries.get(gKey);

                List<Coordinate[]> coordsCloud = newGeometriesData.get(gKey);
                GisUtilities.packNullsInGeometryData(coordsCloud, oldGeometry.getClass());

                Row row2Update = rows.get(gKey);
                if (GisUtilities.isValidGeometryData(coordsCloud, oldGeometry.getClass())) {
                    row2Update.setColumnObject(rowsColIndexes.get(gKey), GisUtilities.constructGeometry(coordsCloud, oldGeometry.getClass(), oldGeometry.getSRID()));
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
            MapLayer[] lightLayers = map.getPane().getLightweightMapContext().getLayers();
            for (MapLayer layer : lightLayers) {
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

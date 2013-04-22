/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.eas.client.controls.geopane.mousetools.DragPanner;
import com.eas.client.controls.geopane.mousetools.GeoPaneTool;
import com.eas.client.controls.geopane.mousetools.MouseClickAlerter;
import com.eas.client.controls.geopane.mousetools.MouseToolCapability;
import com.eas.client.controls.geopane.mousetools.WheelZoomer;
import com.eas.dbcontrols.map.DbMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class MouseTools {

    public static final Set<Class<? extends GeoPaneTool>> VIEW = new HashSet<>();
    public static final Set<Class<? extends GeoPaneTool>> NAVIGATION = new HashSet<>();
    public static final Set<Class<? extends GeoPaneTool>> SELECTION = new HashSet<>();
    public static final Set<Class<? extends GeoPaneTool>> EDITING = new HashSet<>();
    public static final Set<Class<? extends GeoPaneTool>> VERTICES_EDITING = new HashSet<>();

    static {
        VIEW.add(WheelZoomer.class);
        VIEW.add(DragPanner.class);
        VIEW.add(MouseRectZoomer.class); // drag
        VIEW.add(MouseFocuser.class);
        VIEW.add(MouseCursorer.class);

        NAVIGATION.add(MouseGeometriesMover.class); // drag
        NAVIGATION.add(MouseClickAlerter.class);
        NAVIGATION.add(WheelZoomer.class);
        NAVIGATION.add(DragPanner.class);
        NAVIGATION.add(MouseRectZoomer.class); // drag
        NAVIGATION.add(MouseFocuser.class);
        NAVIGATION.add(MouseCursorer.class);

        SELECTION.add(MouseGeometriesMover.class); // drag
        SELECTION.add(MousePointSelector.class);
        SELECTION.add(WheelZoomer.class);
        SELECTION.add(DragPanner.class);
        SELECTION.add(MouseRectSelector.class); // drag
        SELECTION.add(MouseFocuser.class);
        SELECTION.add(MouseCursorer.class);

        //EDITING.add(MouseGeometriesMover.class); // drag
        //EDITING.add(MousePointSelector.class);
        EDITING.add(WheelZoomer.class);
        EDITING.add(DragPanner.class);
        EDITING.add(MouseClickAlerter.class);
        EDITING.add(MouseFocuser.class);
        EDITING.add(MouseCursorer.class);

        VERTICES_EDITING.add(MousePartialRectSelector.class); // drag
        VERTICES_EDITING.add(MousePointsMover.class); // drag
        VERTICES_EDITING.add(MousePointsAdder.class);
        VERTICES_EDITING.add(MouseSinglePointSelector.class);
        VERTICES_EDITING.add(WheelZoomer.class);
        VERTICES_EDITING.add(DragPanner.class);
        VERTICES_EDITING.add(MouseFocuser.class);
        VERTICES_EDITING.add(MouseCursorer.class);
    }

    protected DbMap map;
    protected List<GeoPaneTool> tools = new ArrayList<>();
    protected Set<Class<? extends GeoPaneTool>> installed;

    public MouseTools(DbMap aMap) {
        super();
        map = aMap;
        tools.add(new MouseRectZoomer(aMap));
        tools.add(new WheelZoomer(aMap.getPane()));
        tools.add(new MouseClickAlerter(aMap.getPane()));
        tools.add(new DragPanner(aMap.getPane()));
        MousePointSelector selector = new MousePointSelector(aMap);
        tools.add(selector);
        tools.add(new MousePointsAdder(aMap, selector));
        tools.add(new MouseGeometriesMover(aMap));
        tools.add(new MousePointsMover(aMap));
        tools.add(new MouseRectSelector(aMap));
        tools.add(new MouseSinglePointSelector(aMap));
        tools.add(new MousePartialRectSelector(aMap));
        tools.add(new MouseFocuser(aMap));
        tools.add(new MouseCursorer(aMap));
    }

    protected void uninstall() {
        for (GeoPaneTool tool : tools) {
            map.getPane().removeMouseListener(tool);
            map.getPane().removeMouseMotionListener(tool);
            map.getPane().removeMouseWheelListener(tool);
        }
    }

    public void install(Set<Class<? extends GeoPaneTool>> aTools) {
        uninstall();
        for (GeoPaneTool tool : tools) {
            if (aTools.contains(tool.getClass())) {
                if (tool.isCapable(MouseToolCapability.BUTTONS)) {
                    map.getPane().addMouseListener(tool);
                }
                if (tool.isCapable(MouseToolCapability.MOTION)) {
                    map.getPane().addMouseMotionListener(tool);
                }
                if (tool.isCapable(MouseToolCapability.WHEEL)) {
                    map.getPane().addMouseWheelListener(tool);
                }
            }
        }
        installed = aTools;
    }

    public Set<Class<? extends GeoPaneTool>> getInstalled() {
        return installed;
    }
}

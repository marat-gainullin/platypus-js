/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var FUNC_TYPE_NAME = 'function';
var APP_LOGGER_NAME = 'Application';
var DEFAULT_ENCODING = "utf-8";

// java imports
Calendar = java.util.Calendar;
Vector = java.util.Vector;
ArrayList = java.util.ArrayList;
Logger = java.util.logging.Logger.getLogger(APP_LOGGER_NAME);
Lock = java.util.concurrent.locks.ReentrantLock;
TreeMap = java.util.TreeMap;

// platypus script imports
Color = com.eas.client.scripts.ScriptColor;
Colors = java.awt.Color;
ScriptCalculator = com.eas.client.scripts.ScriptCalculator;

// platypus misc imports
ExcelReport = com.eas.client.reports.ExcelReport;

var formsPresent = typeof(com.eas.client.forms.FormRunner) == FUNC_TYPE_NAME;

if (formsPresent)
{
    FieldsGrid = com.eas.dbcontrols.grid.EntityFieldsGrid;
}

Style = com.eas.gui.CascadedStyle;
SqlCompiledQuery = com.eas.client.queries.SqlCompiledQuery;
Parameters = com.bearsoft.rowset.metadata.Parameters;
Parameter = com.bearsoft.rowset.metadata.Parameter;
DataTypeInfo = com.bearsoft.rowset.metadata.DataTypeInfo;

// platypus utilities imports
IDGenerator = com.bearsoft.rowset.utils.IDGenerator;
MD5Generator = com.eas.client.login.MD5Generator;
Blob = com.bearsoft.rowset.compacts.CompactBlob;
Clob = com.bearsoft.rowset.compacts.CompactClob;
CANParser = com.eas.sensors.positioning.utils.can.CANDataParser;


// platypus GIS imports
GisUtilities = com.eas.client.geo.GisUtilities;

if (formsPresent)
{
    ViewpointChangedEvent = com.eas.client.controls.geopane.events.ViewpointChangedEvent;
    MapClickedEvent = com.eas.client.controls.geopane.events.MapClickedEvent;
    MapMouseMovedEvent = com.eas.client.controls.geopane.events.MapMouseMovedEvent;
    Tools = com.eas.dbcontrols.map.mousetools.MouseTools;
    /**
     * jFreeChart
     */
    TimeSeriesChart = com.eas.client.chart.TimeSeriesChart;
    PieChart = com.eas.client.chart.PieChart;
    LineChart = com.eas.client.chart.LineChart;
}

WKTReader = com.vividsolutions.jts.io.WKTReader;
Geometry = com.vividsolutions.jts.geom.Geometry;
LineString = com.vividsolutions.jts.geom.LineString;
LinearRing = com.vividsolutions.jts.geom.LinearRing;
MultiLineString = com.vividsolutions.jts.geom.MultiLineString;
MultiPoint = com.vividsolutions.jts.geom.MultiPoint;
MultiPolygon = com.vividsolutions.jts.geom.MultiPolygon;
Point = com.vividsolutions.jts.geom.Point;
Polygon = com.vividsolutions.jts.geom.Polygon;
PointSymbol = com.eas.client.geo.PointSymbol;

//OLE Automation 
ComSession = com.eas.client.scripts.ole.ComSession;
ComObject = com.eas.client.scripts.ole.ComObject;

//Resources
Resource = {};
Object.defineProperty(Resource, "load", {
    get: function() {
        return function(aResName, aOnSuccess, aOnFailure) {
            try{
                var loaded = com.eas.client.scripts.ScriptRunner.PlatypusScriptedResource.load(aResName);
                if (aOnSuccess)
                    aOnSuccess(loaded);
                return loaded;
            }catch(e){
                if(aOnFailure)
                    aOnFailure(e.message?e.message:e);
                else
                    throw e;
            }
        };
    }
});

Object.defineProperty(Resource, "loadText", {
    get: function() {
        return function(aResName, aOnSuccessOrEncoding, aOnSuccessOrOnFailure, aOnFailure) {
            var encoding = null;
            var onSuccess = aOnSuccessOrEncoding;
            var onFailure = aOnSuccessOrOnFailure;
            if(typeof onSuccess != "function"){
                encoding = aOnSuccessOrEncoding;
                onSuccess = aOnSuccessOrOnFailure;
                onFailure = aOnFailure;
            }
            try{
                var _loaded = encoding ? com.eas.client.scripts.ScriptRunner.PlatypusScriptedResource.loadText(aResName, encoding) :
                                         com.eas.client.scripts.ScriptRunner.PlatypusScriptedResource.loadText(aResName);
                if(onSuccess)
                    onSuccess(_loaded);
                else
                    return _loaded;
            }catch(e){
                if(onFailure)
                    onFailure(e.message?e.message:e);
                else
                    throw e;
            }
        };
    }
});

Object.defineProperty(Resource, "applicationPath", {
    get: function() {
        return com.eas.client.scripts.ScriptRunner.PlatypusScriptedResource.getApplicationPath();
    }
});


platypus = {};
platypus.HTML5 = "Html5 client";
platypus.J2SE = "Java SE client";
platypus.agent = platypus.J2SE; 


function getTreadLocal(aName) {
    return com.eas.script.ScriptUtils.getThreadLocal(aName);
}
Thread = java.lang.Thread;

var THREAD_POOL_SIZE = 10;
var FixedThreadPool;

/**
 * The size of thread pool
 * @return thread pool size
 */
function setThreadPoolSize(aSize) {
    THREAD_POOL_SIZE = aSize;
}

/**
 * The size of thread pool
 */
function getThreadPoolSize() {
    return THREAD_POOL_SIZE;
}

/** 
 * Thread - schedules given function in the pool thread
 */
Function.prototype.invokeBackground = function() {
    var func = this;
    var args = arguments;
    if (!FixedThreadPool) {
        FixedThreadPool = java.util.concurrent.Executors.newFixedThreadPool(THREAD_POOL_SIZE, new com.eas.concurrent.DeamonThreadFactory());
    }
    FixedThreadPool.execute(function() {
        func.apply(func, args);
    });
};

/**
 * This is a stub for dynamically loaded modules, since J2SE client
 * allways loads them dynamically and synchronously.
 */
function require(deps, aOnSuccess, aOnFailure) {
    try{
        if (deps) {
            if (Array.isArray(deps)) {
                for (var i = 0; i < deps.length; i++) {
                    com.eas.client.scripts.ScriptRunner.executeResource(deps[i]);
                }
            } else {
                com.eas.client.scripts.ScriptRunner.executeResource(deps);
            }
        }
        if (aOnSuccess) {
            aOnSuccess();
        }
    }catch(e){
        if(aOnFailure)
            aOnFailure(e);
        else
            throw e;
    }
}

function readString(aFileName, aEncoding) {
    var encoding = DEFAULT_ENCODING;
    if (aEncoding) {
        encoding = aEncoding;
    }
    return com.eas.util.FileUtils.readString(new java.io.File(aFileName), encoding);
}

function writeString(aFileName, aText, aEncoding) {
    var encoding = DEFAULT_ENCODING;
    if (aEncoding) {
        encoding = aEncoding;
    }
    com.eas.util.FileUtils.writeString(new java.io.File(aFileName), aText, encoding);
}
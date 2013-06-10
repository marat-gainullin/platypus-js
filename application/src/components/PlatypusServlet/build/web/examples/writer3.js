/*
 * ExtJS 3
 **/
Ext.onReady(function(){
  
  var queryId = 131555739572592;
  
  var store = new Ext.data.JsonStore({
    storeId: 'myStore',
    root: 'data',
    idProperty: 'DUMMY_WEB_DATA_ID',
    batch: false,
    autoLoad: true,
    autoSave: true,
    fields: [
      {name: 'DUMMY_WEB_DATA_ID', type: 'int'},
      {name: 'STRING_ONE', type: 'string'},
      {name: 'STRING_TWO', type: 'string'},
      {name: 'DATE_FIELD', type: 'date'},
      {name: 'NUMERIC', type: 'int'}
    ],
    writer: new Ext.data.JsonWriter({
        encode: true,
        writeAllFields: true
    }),
    proxy: new Ext.data.ScriptTagProxy({
        url: 'http://localhost:8080/PlatypusServlet/json/?queryId=' + queryId
    }),
    baseParams: {main: null}
  });
  
  var grid = new Ext.grid.EditorGridPanel({
    xtype: 'editorgrid',
    store: store,
    region: 'south',
    title: 'dummy grid',
    height: 500,
    width: 800,
    loadMask: true,
    renderTo: Ext.getBody(),
    columns: [
      {
	xtype: 'gridcolumn', 
	header: 'DUMMY_WEB_DATA_ID', 
	sortable: true, 
	width: 100, 
	dataIndex: 'DUMMY_WEB_DATA_ID', 
	id: 'DUMMY_WEB_DATA_ID'
      }, {
	xtype: 'gridcolumn', 
	header: 'STRING_ONE', 
	sortable: true, 
	width: 100, 
	dataIndex: 'STRING_ONE', 
	id: 'STRING_ONE',
	editor: {xtype: 'textfield'}
      }, {
	xtype: 'gridcolumn', 
	header: 'STRING_TWO', 
	sortable: true, 
	width: 100, 
	dataIndex: 'STRING_TWO', 
	id: 'STRING_TWO',
	editor: {xtype: 'textfield'}
      }, {
	xtype: 'gridcolumn', 
	header: 'DATE_FIELD', 
	sortable: true, 
	width: 100, 
	dataIndex: 'DATE_FIELD', 
	id: 'DATE_FIELD'
      }, {
	xtype: 'gridcolumn', 
	header: 'NUMERIC', 
	sortable: true, 
	width: 100, 
	dataIndex: 'NUMERIC', 
	id: 'NUMERIC'
      }
    ],
    viewConfig: {
      forceFit: true
    }
  });
  
});
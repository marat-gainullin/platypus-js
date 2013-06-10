Ext.require(['Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.ModelManager', 'Ext.tip.QuickTipManager']);

Ext.onReady(function(){
  
  var queryId = 131408499123740;
  var url = '/platypus/application/json?queryId=' + queryId;
  alert(url);
   
  var entitiesResponseFields = [
    {name: 'MDENT_ID', type: 'int'},
    {name: 'MDENT_NAME', type: 'string'},
    {name: 'MDENT_ORDER', type: 'int'},
    {name: 'MDENT_PARENT_ID', type: 'int'},
    {name: 'MDENT_TYPE', type: 'int'},
    {name: 'TEST_FLOAT', type: 'float'},
    {name: 'TEST_DATE', type: 'date', dateFormat: 'H:i:s Y/m/d'}
  ];
    
  var entityGridStore = Ext.create('Ext.data.Store', {    
    proxy: {
      type: 'jsonp',
      url: '/platypus/application/json?queryId=' + queryId,
      reader: {
	type: 'json',
	root: 'data',
	totalProperty: true
      },
      writer: {
	url: '/webapps/PlatypusServletJson/',
	type: 'json',
	writeAllFields: false,
	root: 'data'
      }
    },
    autoSync: true,
    idProperty: 'MDENT_ID',
    fields: entitiesResponseFields
  });
  
  var entityTreeStore = Ext.create('Ext.data.TreeStore', {    
    proxy: {
      type: 'jsonp',
      url: '/platypus/application/json?queryId=' + queryId,
      reader: {
	type: 'json',
	root: 'data',
	totalProperty: true
      }
    },
    fields: entitiesResponseFields,
    baseParams: {
      PARENT_ENTITY_ID: null
    } 
  });
  
    
  
  var entityTreeGrid = Ext.create('Ext.tree.Panel', {
    width: 500,
    height: 600,
    renderTo: 'right-col',
    title: 'Entities list',
    store: entityTreeStore,
    useArrows: true,
    rootVisible: false,
    multiSelect: true,
    singleExpand: true,
    columns: [
      {
	xtype: 'treecolumn',
	text: 'id',
	sortable: true,
	dataIndex: 'MDENT_ID',	
	flex: 1
      }, {
	xtype: 'treecolumn',
	text: 'name',
	sortable: true,
	dataIndex: 'MDENT_NAME',
	flex: 3
      }
    ]
  });
  entityTreeStore.load();  
  
  entityTreeGrid.on('beforeitemexpand', function(node){
    entityTreeStore.baseParams.PARENT_ENTITY_ID = node.data.MDENT_ID;
  });
  
  var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
    clicksToMoveEditor: 1,
    autoCancel: false
  });
  
  var grid = Ext.create('Ext.grid.Panel', {
    width: 600,
    height: 300,
    renderTo: 'left-col',
    title: 'Entities list',
    store: entityGridStore,
    columns: [
      {
	text: 'MDENT_ID', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'MDENT_ID'
	
      }, {
	text: 'MDENT_NAME', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'MDENT_NAME',
	editor: {
	  allowBlank: false
	}
	
      }, {
	text: 'TEST_FLOAT', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'TEST_FLOAT', 
	editor: {
	  allowBlank: false,
	  xtype: 'numberfield',
	  decimalPrecision: 18
	}
      }, {
	text: 'TEST_DATE', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'TEST_DATE', 
	renderer: Ext.util.Format.dateRenderer('H:i:s Y/m/d'),
	editor: {
	  xtype: 'datefield',
	  allowBlank: false
	}
      }
    ],
    plugins: [rowEditing]
  });
  entityGridStore.load();
});
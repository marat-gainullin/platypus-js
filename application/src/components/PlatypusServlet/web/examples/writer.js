var platypusJsClient = new PlatypusAPI();
platypusJsClient.setServerUrl("http://localhost:8080/platypus/application");

Ext.require(['Ext.grid.*', 'Ext.data.*', 'Ext.form.*', 'Ext.util.*', 'Ext.ModelManager', 'Ext.tip.QuickTipManager']);

Ext.onReady(function(){
  
  var queryId = 131555739572592;
  var url = '/platypus/application/json?queryId=' + queryId;
  
  Ext.define('PlatypusIdGenerator', {
      extend: 'Ext.data.IdGenerator',
      alias: 'idgen.custom',
      id: 'custom',
      generate: function() {
          return platypusJsClient.genID().toString();
      }
  });
  
  Ext.define('DummyModel', {
    extend: 'Ext.data.Model',
    fields: [
      {name: 'DUMMY_WEB_DATA_ID', type: 'int'},
      {name: 'STRING_ONE', type: 'string'},
      {name: 'STRING_TWO', type: 'string'},
      {name: 'DATE_FIELD', type: 'date', dateFormat: "Y-m-dTH:i:s"},
      {name: 'NUMERIC', type: 'int'}
    ],
    idProperty: 'DUMMY_WEB_DATA_ID'
  });
  
  var store = Ext.create('Ext.data.Store', {
    model: 'DummyModel',
    proxy: {
      type: 'ajax',
      api: {
	read: url,
	create: '/platypus/application/json/create?queryId=' + queryId,
	update: '/platypus/application/json/update?queryId=' + queryId,
	destroy: '/platypus/application/json/delete?queryId=' + queryId
      }, 
      reader: {
	type: 'json',
	successProperty: 'success',
	root: 'data',
        idProperty: 'DUMMY_WEB_DATA_ID'
      },
      writer: {
	type: 'json',
	writeAllFields: false
      }
    },
    autoLoad: true,
    autoSync: true,
    encode: true
  });
  
  var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
    clicksToMoveEditor: 1,
    autoCancel: false
  });
  
  var grid = Ext.create('Ext.grid.Panel', {
    width: 800,
    height: 500,
    renderTo: Ext.getBody(),
    title: 'dummy data',
    store: store,
    columns: [
      {
	text: 'DUMMY_WEB_DATA_ID', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'DUMMY_WEB_DATA_ID'
      }, {
	text: 'STRING_ONE', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'STRING_ONE',
	editor: {allowBlank: false}
      }, {
	text: 'STRING_TWO', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'STRING_TWO',
	editor: {allowBlank: false}
      }, {
	text: 'DATE_FIELD', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'DATE_FIELD',
        renderer : Ext.util.Format.dateRenderer('d.m.Y H:i'),
	editor: {
            xtype: 'datefield',
            format: 'd.m.Y'
        }
      }, {
	text: 'NUMERIC', 
	flex: 1, 
	sortable: true, 
	dataIndex: 'NUMERIC',
	editor: {allowBlank: false}
      }
    ],
    plugins: [rowEditing],
    dockedItems: [
        {
            xtype: 'toolbar',
            items: [
                {
                    text: 'create',
                    scope: this,
                    handler: function(){
                        var rec = new DummyModel({
                            DUMMY_WEB_DATA_ID: platypusJsClient.genID(),
                            STRING_ONE: null,
                            STRING_TWO: null,
                            NUMERIC: null,
                            DATE_FIELD: null
                        });
                        store.insert(0, rec);
                    }
                }, {
                    text: 'delete',
                    scope: this,
                    handler: function(){}
                }, {
                    text: 'commit',
                    scope: this,
                    handler: function(){}
                }, {
                    text: 'rollback',
                    scope: this,
                    handler: function(){}
                }
            ]
        }
    ]
  });
  
});
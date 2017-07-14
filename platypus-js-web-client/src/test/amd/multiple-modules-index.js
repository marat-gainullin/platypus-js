/* global define */

define.amd['modules-index'] = {
    "multiple-modules.js": {
        "modules":[
            "short-name-m1",
            "short-name-m2"
        ]
        //"fetched":[],
        //"global-deps":[],
        //"entities":[],
        //"rpc-stubs":[]
    },
    "fetched/a.js": {
        "modules":[
            "fetched/a"
        ],
        "fetched":[
            "fetched/a.model",
            "fetched/a.layout"
        ]
        //"global-deps":[],
        //"entities":[],
        //"rpc-stubs":[]
    },
    "global-dependencies/a.js": {
        "modules": [
            "GlobalA"
        ],
        //"fetched":[],
        "global-deps":[
            "GlobalB"            
        ]
        //"entities":[],
        //"rpc-stubs":[]
    },
    "global-dependencies/b.js": {
        "modules": [
            "GlobalB"
        ]
        //"fetched":[],
        //"global-deps":[],
        //"entities":[],
        //"rpc-stubs":[]
    },
    "cyclic-global-dependencies/a.js": {
        "modules": [
            "CycleA"
        ],
        //"fetched":[],
        "global-deps":[
            "CycleB"
        ]
        //"entities":[],
        //"rpc-stubs":[]
    },
    "cyclic-global-dependencies/b.js": {
        "modules": [
            "CycleB"
        ],
        //"fetched":[],
        "global-deps":[
            "CycleC"
        ]
        //"entities":[],
        //"rpc-stubs":[]
    },
    "cyclic-global-dependencies/c.js": {
        "modules": [
            "CycleC"
        ],
        //"fetched":[],
        "global-deps":[
            "CycleA"
        ]
        //"entities":[],
        //"rpc-stubs":[]
    }
};